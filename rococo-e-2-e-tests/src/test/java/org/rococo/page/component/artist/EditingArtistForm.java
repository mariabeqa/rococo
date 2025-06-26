package org.rococo.page.component.artist;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.page.artist.ArtistPage;
import org.rococo.page.component.BaseComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditingArtistForm extends BaseComponent<EditingArtistForm> {

    private final SelenideElement image = self.$("img");
    private final SelenideElement imageInput = self.$("input[type='file']");
    private final SelenideElement nameInput = self.$("input[name='name']");
    private final SelenideElement bio = self.$("textarea[name='biography']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement saveBtn = self.$("button[type='submit']");

    public EditingArtistForm() {
        super($("div.card form"));
    }

    @Step("Set new artist name: '{0}'")
    @Nonnull
    public EditingArtistForm setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload photo from classpath: '{0}'")
    @Nonnull
    public EditingArtistForm uploadPhoto(String path) {
        imageInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Set new artist biography: '{0}'")
    @Nonnull
    public EditingArtistForm setBio(String text) {
        bio.clear();
        bio.setValue(text);
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public ArtistPage submitForm() {
        saveBtn.click();
        return new ArtistPage();
    }
}
