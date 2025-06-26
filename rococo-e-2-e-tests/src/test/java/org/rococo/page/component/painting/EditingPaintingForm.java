package org.rococo.page.component.painting;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.page.component.BaseComponent;
import org.rococo.page.component.Select;
import org.rococo.page.painting.PaintingPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditingPaintingForm extends BaseComponent<EditingPaintingForm> {

    private final SelenideElement image = self.$("img");
    private final SelenideElement imageInput = self.$("input[type='file']");
    private final SelenideElement nameInput = self.$("input[name='title']");
    private final SelenideElement authorInput = self.$("select[name='authorId']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement museumInput = self.$("select[name='museumId']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement saveBtn = self.$("button[type='submit']");

    private final Select museumSelect = new Select(museumInput);
    private final Select authorSelect = new Select(authorInput);

    public EditingPaintingForm() {
        super($("div.card form"));
        self.should(visible);
    }

    @Step("Set new painting name: '{0}'")
    @Nonnull
    public EditingPaintingForm setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload painting from classpath: '{0}'")
    @Nonnull
    public EditingPaintingForm uploadImage(String path) {
        imageInput.should(visible).uploadFromClasspath(path);
        Selenide.sleep(2000);
        return this;
    }

    @Step("Set new painting description: '{0}'")
    @Nonnull
    public EditingPaintingForm setDescription(String text) {
        description.clear();
        description.setValue(text);
        return this;
    }

    @Step("Set new painting's museum: '{0}'")
    @Nonnull
    public EditingPaintingForm selectMuseum(String museum) {
        museumSelect.selectItem(museum);
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public PaintingPage submitForm() {
        saveBtn.scrollIntoView(false);
        saveBtn.click();
        return new PaintingPage();
    }
}
