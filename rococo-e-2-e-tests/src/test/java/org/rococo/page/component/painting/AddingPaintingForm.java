package org.rococo.page.component.painting;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.page.BasePage;
import org.rococo.page.component.BaseComponent;
import org.rococo.page.component.Select;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AddingPaintingForm extends BaseComponent<AddingPaintingForm> {

    private final SelenideElement nameInput = self.$("input[name='title']");
    private final SelenideElement imageInput = self.$("input[type='file']");
    private final SelenideElement authorInput = self.$("select[name='authorId']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement museumInput = self.$("select[name='museumId']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement addBtn = self.$("button[type='submit']");

    private final Select authorSelect = new Select(authorInput);
    private final Select museumSelect = new Select(museumInput);

    public AddingPaintingForm() {
        super($("div.card form"));
    }

    @Step("Set painting name: '{0}'")
    @Nonnull
    public AddingPaintingForm setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload painting from classpath: '{0}'")
    @Nonnull
    public AddingPaintingForm uploadPainting(String path) {
        imageInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Select painting author: '{0}'")
    @Nonnull
    public AddingPaintingForm selectAuthor(String author) {
        authorSelect.selectItem(author);
        return this;
    }

    @Step("Set painting description: '{0}'")
    @Nonnull
    public AddingPaintingForm setDescription(String text) {
        description.clear();
        description.setValue(text);
        return this;
    }

    @Step("Select painting's Museum: '{museumName}'")
    @Nonnull
    public AddingPaintingForm selectMuseum(String museumName) {
        museumSelect.selectItem(museumName);
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public <T extends BasePage<?>> T submitForm(T page) {
        addBtn.click();
        return page;
    }
}
