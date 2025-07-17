package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.ScreenShotTest;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.page.MainPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.rococo.utils.data.RandomDataUtils.randomName;

@WebTest
public class ProfileWebTest {

    private static final String PROFILE_MSG = "Профиль обновлен";

    @Test
    @DisplayName("WEB: User should be able to update firstname and lastname in profile")
    @ApiLogin(testUser = @TestUser())
    void shouldUpdateProfileWithFirstAndLastNames() {
        final String firstName = randomName();
        final String lastName = randomName();

        MainPage mainPage =
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .profile()
                .setFirstName(firstName)
                .setLastName(lastName)
                .save()
                .checkToastMessage(PROFILE_MSG);

        Selenide.refresh();

        mainPage.header()
                .profile()
                .checkFirstName(firstName)
                .checkLastName(lastName);
    }

    @Test
    @ScreenShotTest(expected = "expected-profilePic.png")
    @DisplayName("WEB: User should be able to upload profile image")
    @ApiLogin(testUser = @TestUser())
    void shouldUpdateProfileWithAnImage(BufferedImage expectedAvatar) throws IOException {
        String path = "img/profile/profilePic.jpg";

        MainPage mainPage = Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .profile()
                .uploadPhoto(path)
                .save()
                .checkToastMessage(PROFILE_MSG);

        Selenide.refresh();

        mainPage.header()
                .profile()
                .checkPhotoExist()
                .checkPhoto(expectedAvatar);
    }
}
