package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.model.UserJson;
import org.rococo.page.LoginPage;
import org.rococo.page.MainPage;

@WebTest
public class LoginWebTest {

    @Test
    @DisplayName("Successful login")
    @TestUser
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .fillInLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Unsuccessful login with invalid password")
    @TestUser
    void userShouldStayOnLoginPageAfterLoginWithInvalidPw(UserJson user) {
        final String incorrectPw = "123";
        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .fillInLoginPage(user.username(), incorrectPw)
                .submit(new LoginPage())
                .checkError("Bad credentials");
    }

    @Test
    @DisplayName("Unsuccessful login with invalid username")
    @TestUser
    void userShouldStayOnLoginPageAfterLoginWithInvalidUsername(UserJson user) {
        final String incorrectPw = "123";
        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .fillInLoginPage(user.username(), incorrectPw)
                .submit(new LoginPage())
                .checkError("Bad credentials");
    }

    @Test
    @DisplayName("Successful logout")
    @TestUser
    void mainPageShouldBeDisplayedAfterSuccessfulLogout(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .fillInLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded()
                .header()
                .profile()
                .signOut()
                .checkThatPageLoaded();
    }
}
