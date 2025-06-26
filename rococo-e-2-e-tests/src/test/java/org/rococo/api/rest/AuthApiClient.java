package org.rococo.api.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.SneakyThrows;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.api.rest.core.CodeInterceptor;
import org.rococo.api.rest.core.ThreadSafeCookieStore;
import org.rococo.config.Config;
import org.rococo.jupiter.extension.ApiLoginExtension;
import org.rococo.utils.OAuthUtils;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

@ParametersAreNonnullByDefault
public class AuthApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .followRedirects(true)
            .addNetworkInterceptor(new CodeInterceptor())
            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .addNetworkInterceptor(
                    new AllureOkHttp3()
                            .setRequestTemplate("http-request.ftl")
                            .setResponseTemplate("http-response.ftl")
            )
            .cookieJar(
                    new JavaNetCookieJar(
                            new CookieManager(
                                    ThreadSafeCookieStore.INSTANCE,
                                    CookiePolicy.ACCEPT_ALL
                            )
                    )
            )
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = retrofit.create(AuthApi.class);

    @Step("Register user with username '{0}' using REST API")
    public void createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallange(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getCode(),
                redirectUri,
                clientId,
                codeVerifier,
                "authorization_code"
        ).execute();

        return tokenResponse.body().get("id_token").asText();
    }
}
