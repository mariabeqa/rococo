package org.rococo.page.component.artist;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.page.artist.ArtistsPage;
import org.rococo.page.component.BaseComponent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AddingArtistForm extends BaseComponent<AddingArtistForm> {

    private final SelenideElement nameInput = self.$("input[name='name']");
    private final SelenideElement photoInput = self.$("input[type='file']");
    private final SelenideElement bio = self.$("textarea[name='biography']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement addBtn = self.$("button[type='submit']");

    public AddingArtistForm() {
        super($("div.card form"));
    }

    @Step("Set artist name: '{0}'")
    @Nonnull
    public AddingArtistForm setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Upload photo from classpath: '{0}'")
    @Nonnull
    public AddingArtistForm uploadPhoto(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Set artist biography: '{0}'")
    @Nonnull
    public AddingArtistForm setBiography(String text) {
        bio.clear();
        bio.setValue(text);
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public ArtistsPage submitForm() {
        addBtn.click();
        return new ArtistsPage();
    }
}
