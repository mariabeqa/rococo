package org.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage> {

    protected static final Config CFG = Config.getInstance();

    private final SelenideElement toastMessage = $(".toast");
    private final String fieldValidationError = "//span[@class='text-error-400'][text()='%s']";

    public abstract T checkThatPageLoaded();

    @Step("Check that toast message has text: {expectedText}")
    @Nonnull
    public T checkToastMessage(String expectedText) {
        toastMessage.should(visible).should(Condition.text(expectedText));
        return (T) this;
    }

    @Step("Check that field validation has error message: {expectedText}")
    @Nonnull
    public T checkTextFieldErrorMessage(String expectedText) {
        $(By.xpath(String.format(fieldValidationError, expectedText))).should(visible);
        return (T) this;
    }
}
