package org.rococo.page.component.museum;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.page.component.BaseComponent;
import org.rococo.page.component.Select;
import org.rococo.page.museum.MuseumPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditingMuseumForm extends BaseComponent<EditingMuseumForm> {

    private final SelenideElement image = self.$("img");
    private final SelenideElement imageInput = self.$("input[type='file']");
    private final SelenideElement nameInput = self.$("input[name='title']");
    private final SelenideElement countrySelect = self.$("select[name='countryId']");
    private final SelenideElement cityInput = self.$("input[name='city']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement saveBtn = self.$("button[type='submit']");

    private final Select select = new Select(countrySelect);

    public EditingMuseumForm() {
        super($("div.card form"));
    }

    @Step("Set new museum name: '{0}'")
    @Nonnull
    public EditingMuseumForm setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload photo from classpath: '{0}'")
    @Nonnull
    public EditingMuseumForm uploadPhoto(String path) {
        imageInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Select new museum's country: '{0}'")
    @Nonnull
    public EditingMuseumForm selectCountry(String countryName) {
        select.selectItem(countryName);
        return this;
    }

    @Step("Set new city: '{0}'")
    @Nonnull
    public EditingMuseumForm setCity(String cityName) {
        cityInput.clear();
        cityInput.setValue(cityName);
        return this;
    }

    @Step("Set new museum description: '{0}'")
    @Nonnull
    public EditingMuseumForm setDescription(String text) {
        description.clear();
        description.setValue(text);
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public MuseumPage submitForm() {
        saveBtn.click();
        return new MuseumPage();
    }
}
