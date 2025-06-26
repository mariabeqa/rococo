package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.ScreenShotTest;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.model.CountryJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;
import org.rococo.page.museum.MuseumPage;
import org.rococo.page.museum.MuseumsPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.rococo.model.Countries.AUSTRALIA;
import static org.rococo.utils.RandomDataUtils.randomMuseumTitle;

@WebTest
public class MuseumWebTest {

    private static final String MUSEUM_DESCRIPTION = "Русский музей – крупнейший в мире музей русского искусства, уникальный архитектурно-художественный комплекс в историческом центре Санкт-Петербурга.";
    private static final String MUSEUM_ADDED_MSG = "Добавлен музей: %s";
    private static final String MUSEUM_NAME_LENGTH_ERROR_MSG = "Название не может быть короче 3 символов";
    private static final String CITY_NAME_LENGTH_ERROR_MSG = "Город не может быть короче 3 символов";
    private static final String DESCRIPTION_LENGTH_ERROR_MSG = "Описание не может быть короче 10 символов";
    private static final String MUSEUM_EDITED_MSG = "Обновлен музей: %s";
    private static final String MUSEUM_PHOTO_PATH = "img/museum/русский_музей.jpg";

    @Test
    @DisplayName("User should be able to add a new Museum")
    @ApiLogin(testUser = @TestUser())
    void shouldBeAbleTAddNewMuseum() {
       final String name = randomMuseumTitle();

        MuseumsPage museumsPage = Selenide.open(MuseumsPage.URL, MuseumsPage.class)
                .addMuseum()
                .setName(name)
                .selectCountry("Россия")
                .setCity("Санкт-Петербург")
                .uploadPhoto(MUSEUM_PHOTO_PATH)
                .setDescription(MUSEUM_DESCRIPTION)
                .submitForm()
                .checkToastMessage(String.format(MUSEUM_ADDED_MSG, name));

        Selenide.refresh();

        museumsPage.checkMuseumExists(name);
    }

    @Test
    @DisplayName("Error message in case museum name length is insufficient")
    @ApiLogin(testUser = @TestUser())
    void shouldShowErrorIfMuseumNameLengthIsInvalid() {
        final String invalidMuseumName = "ab";

        Selenide.open(MuseumsPage.URL, MuseumsPage.class)
                .addMuseum()
                .setName(invalidMuseumName)
                .selectCountry("Россия")
                .setCity("Санкт-Петербург")
                .uploadPhoto(MUSEUM_PHOTO_PATH)
                .setDescription(MUSEUM_DESCRIPTION)
                .submitForm()
                .checkTextFieldErrorMessage(MUSEUM_NAME_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("Error message in case city name length is insufficient")
    @ApiLogin(testUser = @TestUser())
    void shouldShowErrorIfCityNameLengthIsInvalid() {
        final String invalidCityName = "ab";

        Selenide.open(MuseumsPage.URL, MuseumsPage.class)
                .addMuseum()
                .setName(randomMuseumTitle())
                .selectCountry("Россия")
                .setCity(invalidCityName)
                .uploadPhoto(MUSEUM_PHOTO_PATH)
                .setDescription(MUSEUM_DESCRIPTION)
                .submitForm()
                .checkTextFieldErrorMessage(CITY_NAME_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("Error message in case museum description length is insufficient")
    @ApiLogin(testUser = @TestUser())
    void shouldShowErrorIfDescriptionLengthIsInvalid() {
        final String invalidDescriptionText = "abw wer w";

        Selenide.open(MuseumsPage.URL, MuseumsPage.class)
                .addMuseum()
                .setName(randomMuseumTitle())
                .selectCountry("Россия")
                .setCity("Омск")
                .uploadPhoto(MUSEUM_PHOTO_PATH)
                .setDescription(invalidDescriptionText)
                .submitForm()
                .checkTextFieldErrorMessage(DESCRIPTION_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("User should be able to edit Museum title and description")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    void shouldBeAbleToEditMuseumTitleAndDesc(MuseumJson museum) {
        final String museumId = museum.id().toString();
        final String newName = museum.title() + " edited";
        final String newDescription = museum.description() + " edited";

        MuseumPage museumPage = Selenide.open(MuseumPage.url(museumId), MuseumPage.class)
                .editMuseum()
                .setName(newName)
                .setDescription(newDescription)
                .submitForm()
                .checkToastMessage(String.format(MUSEUM_EDITED_MSG, newName));

        Selenide.refresh();

        museumPage.checkMuseumInfo(
                new MuseumJson(
                        museum.id(),
                        newName,
                        newDescription,
                        museum.photo(),
                        museum.geo()
                )
        );
    }

    @Test
    @DisplayName("User should be able to edit Museum geo location")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    void shouldBeAbleToEditMuseumGeoLocation(MuseumJson museum) {
        final String museumId = museum.id().toString();
        final String newCity = "Cидней";
        final String newCountry = AUSTRALIA.getName();

        MuseumPage museumPage = Selenide.open(MuseumPage.url(museumId), MuseumPage.class)
                .editMuseum()
                .setCity(newCity)
                .selectCountry(AUSTRALIA.getName().toString())
                .submitForm()
                .checkToastMessage(String.format(MUSEUM_EDITED_MSG, museum.title()));

        Selenide.refresh();

        museumPage.checkMuseumInfo(
                new MuseumJson(
                        museum.id(),
                        museum.title(),
                        museum.description(),
                        museum.photo(),
                        new GeoLocationJson(
                                newCity,
                                new CountryJson(
                                        AUSTRALIA.getId(),
                                        newCountry
                                )
                        )
                )
        );
    }

    @Test
    @ScreenShotTest(expected = "expected-museumPic.png")
    @DisplayName("User should be able to edit museum picture")
    @ApiLogin(testUser = @TestUser())
    @TestMuseum
    void shouldBeAbleToEditMuseumPicture(BufferedImage expectedAvatar, MuseumJson museum) throws IOException {
        final String museumId = museum.id().toString();
        String path = "img/museum/русский_музей_новая.jpeg";

        MuseumPage museumPage = Selenide.open(MuseumPage.url(museumId), MuseumPage.class)
                .editMuseum()
                .uploadPhoto(path)
                .submitForm()
                .checkToastMessage(String.format(MUSEUM_EDITED_MSG, museum.title()));

        Selenide.refresh();

        museumPage.checkPhotoExist()
                .checkPhoto(expectedAvatar);
    }
}
