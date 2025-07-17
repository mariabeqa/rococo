package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.*;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.page.artist.ArtistPage;
import org.rococo.page.artist.ArtistsPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.rococo.utils.data.RandomDataUtils.*;

@WebTest
public class ArtistWebTest {

    private static final String ARTIST_ADDED_MSG = "Добавлен художник: %s";
    private static final String PAINTING_ADDED_MSG = "Добавлена картина: %s";
    private static final String ARTIST_NAME_LENGTH_ERROR_MSG = "Имя не может быть короче 3 символов";
    private static final String ARTIST_BIO_LENGTH_ERROR_MSG = "Биография не может быть короче 10 символов";
    private static final String ARTIST_EDITED_MSG = "Обновлен художник: %s";
    private static final String ARTIST_PHOTO_PATH = "img/artist/kuindzhi.jpg";
    private static final String ARTIST_PAINTING_PATH = "img/painting/raduga.jpg";
    private static final String ARTIST_BIO = "Русский музей – крупнейший в мире музей русского искусства, уникальный архитектурно-художественный комплекс в историческом центре Санкт-Петербурга.";

    @Test
    @DisplayName("WEB: User should be able to add a new Artist")
    @ApiLogin(testUser = @TestUser())
    void shouldBeAbleToAddNewArtist() {
        final String name = randomArtistName();

        Selenide.open(ArtistsPage.URL, ArtistsPage.class)
                .addArtist()
                .setName(name)
                .uploadPhoto(ARTIST_PHOTO_PATH)
                .setBiography(ARTIST_BIO)
                .submitForm()
                .checkToastMessage(String.format(ARTIST_ADDED_MSG, name))
                .checkArtistExists(name);
    }

    @Test
    @DisplayName("WEB: Error message in case artist name length is insufficient")
    @ApiLogin(testUser = @TestUser())
    void shouldShowErrorIfArtistNameLengthIsInvalid() {
        final String invalidArtistName = "ab";

        Selenide.open(ArtistsPage.URL, ArtistsPage.class)
                .addArtist()
                .setName(invalidArtistName)
                .uploadPhoto(ARTIST_PHOTO_PATH)
                .setBiography(ARTIST_BIO)
                .submitForm()
                .checkTextFieldErrorMessage(ARTIST_NAME_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("WEB: Error message in case artist bio length is insufficient")
    @ApiLogin(testUser = @TestUser())
    void shouldShowErrorIfArtistBioLengthIsInvalid() {
        final String invalidArtistBio = "abf kjf j";

        Selenide.open(ArtistsPage.URL, ArtistsPage.class)
                .addArtist()
                .setName(randomArtistName())
                .uploadPhoto(ARTIST_PHOTO_PATH)
                .setBiography(invalidArtistBio)
                .submitForm()
                .checkTextFieldErrorMessage(ARTIST_BIO_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("WEB: User should be able to edit Artist name and bio")
    @ApiLogin(testUser = @TestUser())
    @TestArtist
    void shouldBeAbleToEditArtistNameAndBio(ArtistJson artist) {
        final String newName = artist.name() + " edited";
        final String newBio = artist.biography() + " edited";
        final String artistId = artist.id().toString();

        ArtistPage artistPage = Selenide.open(ArtistPage.url(artistId), ArtistPage.class)
                .editArtist()
                .setName(newName)
                .setBio(newBio)
                .submitForm()
                .checkToastMessage(String.format(ARTIST_EDITED_MSG, newName));

        Selenide.refresh();

        artistPage.checkArtistInfo(
                new ArtistJson(
                        artist.id(),
                        newName,
                        newBio,
                        artist.photo()
                )
        );
    }

    @Test
    @ScreenShotTest(expected = "expected-artistPic.png")
    @DisplayName("WEB: User should be able to edit artist photo")
    @ApiLogin(testUser = @TestUser())
    @TestArtist
    void shouldBeAbleToEditArtistPhoto(BufferedImage expectedAvatar, ArtistJson artist) throws IOException {
        String path = "img/artist/kuindzhi_new.jpg";
        final String artistId = artist.id().toString();

        ArtistPage artistPage = Selenide.open(ArtistPage.url(artistId), ArtistPage.class)
                .editArtist()
                .uploadPhoto(path)
                .submitForm()
                .checkToastMessage(String.format(ARTIST_EDITED_MSG, artist.name()));

        Selenide.refresh();

        artistPage.checkPhotoExist()
                .checkPhoto(expectedAvatar);
    }

    @Test
    @DisplayName("WEB: User should be able to add a new painting on Artist page")
    @ApiLogin(testUser = @TestUser())
    @TestArtist(removeAfterTest = false)
    @TestMuseum(removeAfterTest = false)
    void shouldBeAbleToAddNewPaintingOnArtistPage(ArtistJson artist, MuseumJson museum) {
        final String artistId = artist.id().toString();
        final String paintingName = randomPaintingName();
        final String paintingDescription = randomPaintingDescription();

        ArtistPage artistPage = Selenide.open(ArtistPage.url(artistId), ArtistPage.class)
                .addPainting()
                .setName(paintingName)
                .uploadPainting(ARTIST_PAINTING_PATH)
                .setDescription(paintingDescription)
                .selectMuseum(museum.title())
                .submitForm(new ArtistPage())
                .checkToastMessage(String.format(PAINTING_ADDED_MSG, paintingName));

        Selenide.refresh();

        artistPage.checkPaintingExist()
                .checkPaintingName(paintingName);
    }
}
