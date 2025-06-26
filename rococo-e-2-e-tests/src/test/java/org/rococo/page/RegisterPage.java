package org.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement loginButton = $("a.form__link");
    private final SelenideElement proceedLoginButton = $("a.form__submit");
    private final SelenideElement toMainPageBtn = $(By.xpath("//a[text() = 'На главную страницу']"));
    private final SelenideElement errorForm = $(".form__error");

    @Override
    @Step("Check RegisterPage page is loaded")
    @Nonnull
    public RegisterPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        passwordSubmitInput.should(visible);
        return this;
    }

    @Step("Fill on Register page with username: '{0}', password: {1}, submit password: {2}")
    @Nonnull
    public RegisterPage fillRegisterPage(String username, String password, String passwordSubmit) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    @Step("Submit registration form")
    @Nonnull
    public MainPage successSubmit() {
        submitButton.click();
        proceedLoginButton.click();
        toMainPageBtn.click();
        return new MainPage();
    }

    @Step("Submit registration form")
    @Nonnull
    public RegisterPage errorSubmit() {
        submitButton.click();
        return this;
    }

    @Nonnull
    public RegisterPage checkErrorMessage(String errorMessage) {
        errorForm.shouldHave(text(errorMessage));
        return this;
    }
}
