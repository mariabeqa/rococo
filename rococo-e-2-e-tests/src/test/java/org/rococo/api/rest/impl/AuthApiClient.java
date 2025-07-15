package org.rococo.api.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.rococo.api.rest.AuthApi;
import org.rococo.api.rest.core.CodeInterceptor;
import org.rococo.api.rest.core.RestClient;
import org.rococo.api.rest.core.ThreadSafeCookieStore;
import org.rococo.jupiter.extension.ApiLoginExtension;
import org.rococo.utils.auth.OAuthUtils;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

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
    public @Nullable String login(String username, String password) {
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
