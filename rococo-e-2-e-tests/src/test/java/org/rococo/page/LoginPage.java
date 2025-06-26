package org.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerButton = $("a[href='/register']");
    private final SelenideElement errorFrom = $("p.form__error");

    @Override
    @Step("Check Login page is loaded")
    @Nonnull
    public LoginPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        return this;
    }

    @Step("Fill in Login page with: username: '{0}', password: {1}")
    @Nonnull
    public LoginPage fillInLoginPage(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        return this;
    }

    @Step("Submit user credentials")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }

    @Nonnull
    public RegisterPage continueToRegister() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("Check error on page: {error}")
    @Nonnull
    public LoginPage checkError(String error) {
        errorFrom.shouldHave(text(error));
        return this;
    }

}
