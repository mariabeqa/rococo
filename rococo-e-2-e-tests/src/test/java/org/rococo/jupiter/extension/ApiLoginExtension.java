package org.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import org.rococo.api.rest.AuthApiClient;
import org.rococo.api.rest.core.ThreadSafeCookieStore;
import org.rococo.config.Config;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.model.UserJson;
import org.rococo.page.MainPage;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback {

    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension api() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userByExtension = TestUserExtension.createdUser();
                    final String username;
                    final String password;
                    final String token;

                    if (("".equals(apiLogin.username()) || "".equals(apiLogin.password()))) {
                        if (apiLogin.testUser() == null) {
                            throw new IllegalArgumentException("@TestUser or username/password should be provided for @ApiLogin");
                        }
                    } else if (apiLogin.testUser() != null && (!("".equals(apiLogin.username()) || "".equals(apiLogin.password())))) {
                        throw new IllegalArgumentException("Both @TestUser and username/password should not be provided for @ApiLogin");
                    }

                    if (apiLogin.testUser() != null && userByExtension != null) {
                        username = userByExtension.username();
                        password = userByExtension.testData().password();
                    } else {
                        username = apiLogin.username();
                        password = apiLogin.password();
                    }

                    token = authApiClient.login(
                            username,
                            password
                    );
                    setToken(token);

                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                getJsessionIdCookie()
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
                    }
                });
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
