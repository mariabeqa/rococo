package org.rococo.page.artist;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.model.ArtistJson;
import org.rococo.page.BasePage;
import org.rococo.page.component.Image;
import org.rococo.page.component.artist.EditingArtistForm;
import org.rococo.page.component.painting.AddingPaintingForm;
import org.rococo.page.painting.PaintingPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistPage extends BasePage<ArtistPage> {

    public static final String URL = CFG.frontUrl() + "artist/";

    private final SelenideElement artistName = $("article header");
    private final SelenideElement artistPhoto = $("figure img");
    private final SelenideElement bio = $("article p");
    private final SelenideElement editBtn = $("button[data-testid='edit-artist']");
    private final SelenideElement addPaintingBtn = $(By.xpath("//button[text()='Добавить картину']"));
    private final SelenideElement grid = $("ul.grid");
    private final ElementsCollection paintings = grid.$$("li");

    private final Image image = new Image(artistPhoto);

    @Override
    public ArtistPage checkThatPageLoaded() {
        artistName.should(visible);
        artistPhoto.should(visible);
        editBtn.should(visible);
        return this;
    }

    @Step("Open a painting called '{0}'")
    @Nonnull
    public PaintingPage openPainting(String paintingName) {
        paintings.find(text(paintingName)).click();
        return new PaintingPage();
    }

    @Step("Start adding a painting")
    @Nonnull
    public AddingPaintingForm addPainting() {
        addPaintingBtn.click();
        return new AddingPaintingForm();
    }

    @Step("Start editing an artist")
    @Nonnull
    public EditingArtistForm editArtist() {
        editBtn.click();
        return new EditingArtistForm();
    }

    @Step("Check artist info: {expected}")
    @Nonnull
    public ArtistPage checkArtistInfo(ArtistJson expected) {
        artistName.should(visible).shouldHave(text(expected.name()));
        bio.should(visible).shouldHave(text(expected.biography()));
        return this;
    }

    @Step("Check photo on Artist page")
    @Nonnull
    public ArtistPage checkPhoto(BufferedImage expected) throws IOException {
        image.checkPhoto(expected);
        return this;
    }

    @Step("Check artist photo exist")
    @Nonnull
    public ArtistPage checkPhotoExist() {
        image.checkPhotoExist();
        return this;
    }

    @Step("Check artist painting exist")
    @Nonnull
    public ArtistPage checkPaintingExist() {
        paintings.first().$("img").should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Check artist painting name: {name}")
    @Nonnull
    public ArtistPage checkPaintingName(String name) {
        paintings.first().$("div").should(visible).shouldHave(text(name));
        return this;
    }

    public static String url(String id) {
        return URL + id;
    }
}
