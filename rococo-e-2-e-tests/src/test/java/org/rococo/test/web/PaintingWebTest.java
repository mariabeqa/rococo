package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.*;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;
import org.rococo.page.painting.PaintingPage;
import org.rococo.page.painting.PaintingsPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.rococo.utils.data.RandomDataUtils.randomPaintingDescription;
import static org.rococo.utils.data.RandomDataUtils.randomPaintingName;

@WebTest
public class PaintingWebTest {

    private static final String PAINTING_PATH = "img/painting/pool.jpeg";
    private static final String PAINTING_ADDED_MSG = "Добавлена картина: %s";
    private static final String NAME_LENGTH_ERROR_MSG = "Название не может быть короче 3 символов";
    private static final String DESCRIPTION_LENGTH_ERROR_MSG = "Описание не может быть короче 10 символов";
    private static final String PAINTING_EDITED_MSG = "Обновлена картина: %s";

    @Test
    @DisplayName("WEB: User should be able to add a new Painting")
    @TestMuseum(removeAfterTest = false)
    @TestArtist(removeAfterTest = false)
    @ApiLogin(testUser = @TestUser())
    void shouldBeAbleTAddNewPainting(MuseumJson museum, ArtistJson artist) {
        final String paintingName = randomPaintingName();
        final String paintingDescription = randomPaintingDescription();

        PaintingsPage paintingsPage = Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .addPainting()
                .setName(paintingName)
                .uploadPainting(PAINTING_PATH)
                .selectAuthor(artist.name())
                .setDescription(paintingDescription)
                .selectMuseum(museum.title())
                .submitForm(new PaintingsPage())
                .checkToastMessage(String.format(PAINTING_ADDED_MSG, paintingName));

        Selenide.refresh();

        paintingsPage.checkPaintingExists(paintingName);
    }

    @Test
    @DisplayName("WEB: User should be able to edit Painting title and description")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldBeAbleToEditPaintingTitleAndDesc(MuseumJson museum, ArtistJson artist, PaintingJson painting) {
        final String newName = painting.title() + " edited";
        final String newDescription = painting.description() + " edited";

        PaintingPage paintingPage = Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .searchPainting(painting.title())
                .openPainting(painting.title())
                .editPainting()
                .setName(newName)
                .setDescription(newDescription)
                .submitForm()
                .checkToastMessage(String.format(PAINTING_EDITED_MSG, newName));

        Selenide.refresh();

        paintingPage.checkPaintingInfo(
                new PaintingJson(
                        painting.id(),
                        painting.title(),
                        painting.description(),
                        painting.content(),
                        museum,
                        artist
                )
        );
    }

    @Test
    @DisplayName("WEB: Error message in case painting name length is insufficient")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    @TestArtist
    void shouldShowErrorIfPaintingNameLengthIsInvalid(MuseumJson museum, ArtistJson artist) {
        final String invalidPaintingName = "ab";

        Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .addPainting()
                .setName(invalidPaintingName)
                .uploadPainting(PAINTING_PATH)
                .selectAuthor(artist.name())
                .setDescription(randomPaintingDescription())
                .selectMuseum(museum.title())
                .submitForm(new PaintingsPage())
                .checkTextFieldErrorMessage(NAME_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("WEB: Error message in case painting description length is insufficient")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    @TestArtist
    void shouldShowErrorIfPaintingDescriptionLengthIsInvalid(MuseumJson museum, ArtistJson artist) {
        final String paintingName = randomPaintingName();
        final String invalidDescriptionText = "abw wer w";

        Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .addPainting()
                .setName(paintingName)
                .uploadPainting(PAINTING_PATH)
                .selectAuthor(artist.name())
                .setDescription(invalidDescriptionText)
                .selectMuseum(museum.title())
                .submitForm(new PaintingsPage())
                .checkTextFieldErrorMessage(DESCRIPTION_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("WEB: User should be able to edit Painting's Museum")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldBeAbleToEditPaintingMuseum(MuseumJson museum, ArtistJson artist, PaintingJson painting) {

        PaintingPage paintingPage = Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .searchPainting(painting.title())
                .openPainting(painting.title())
                .editPainting()
                .selectMuseum(museum.title())
                .submitForm()
                .checkToastMessage(String.format(PAINTING_EDITED_MSG, painting.title()));

        Selenide.refresh();

        paintingPage.checkPaintingInfo(
                new PaintingJson(
                        painting.id(),
                        painting.title(),
                        painting.description(),
                        painting.content(),
                        museum,
                        artist
                )
        );
    }

    @Test
    @ScreenShotTest(expected = "expected-paintingPic.png")
    @DisplayName("WEB: User should be able to edit painting picture")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    @TestArtist
    @TestPainting(path = "img/painting/forest.png")
    void shouldBeAbleToEditPaintingPicture(BufferedImage expectedAvatar, PaintingJson painting) throws IOException {
        String path = "img/painting/nighthawks.jpg";

        PaintingPage paintingPage = Selenide.open(PaintingsPage.URL, PaintingsPage.class)
                .searchPainting(painting.title())
                .openPainting(painting.title())
                .editPainting()
                .uploadImage(path)
                .submitForm()
                .checkToastMessage(String.format(PAINTING_EDITED_MSG, painting.title()));

        Selenide.refresh();

        paintingPage.checkPhotoExist()
                .checkPhoto(expectedAvatar);
    }
}
