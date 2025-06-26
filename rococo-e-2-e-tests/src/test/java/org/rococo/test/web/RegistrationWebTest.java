package org.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.meta.WebTest;
import org.rococo.model.UserJson;
import org.rococo.page.MainPage;

import static org.rococo.utils.RandomDataUtils.randomPassword;
import static org.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationWebTest {

    private static final String USERNAME_ALREADY_EXISTS_ERROR_MSG = "Username `%s` already exists";
    private static final String PW_LENGTH_ERROR_MSG = "Allowed password length should be from 3 to 12 characters";
    private static final String USERNAME_LENGTH_ERROR_MSG = "Allowed username length should be from 3 to 50 characters";
    private static final String PW_SHOULD_BE_EQUAL_ERROR_MSG = "Passwords should be equal";

    @Test
    @DisplayName("Successful registration")
    void shouldRegisterNewUser() {
        final String username = randomUsername();
        final String pw = randomPassword();

        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .continueToRegister()
                .fillRegisterPage(username, pw, pw)
                .successSubmit()
                .toLoginPage()
                .fillInLoginPage(username, pw)
                .submit(new MainPage())
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Unsuccessful registration with existing username")
    @TestUser
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        final String existingUsername = user.username();
        final String pw = user.testData().password();

        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .continueToRegister()
                .fillRegisterPage(existingUsername, pw, pw)
                .errorSubmit()
                .checkErrorMessage(String.format(USERNAME_ALREADY_EXISTS_ERROR_MSG, existingUsername));
    }

    @Test
    @DisplayName("Unsuccessful registration in case confirm password doesn't match")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        final String newUsername = randomUsername();
        final String password = randomPassword();
        final String passwordSubmit = "bad";

        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .continueToRegister()
                .fillRegisterPage(newUsername, password, passwordSubmit)
                .errorSubmit()
                .checkErrorMessage(PW_SHOULD_BE_EQUAL_ERROR_MSG);
    }

    @Test
    @DisplayName("Unsuccessful registration in case password length is incorrect")
    void shouldShowErrorIfPasswordLengthIsInvalid() {
        final String newUsername = randomUsername();
        final String password = "12";
        final String passwordSubmit = "12";

        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .continueToRegister()
                .fillRegisterPage(newUsername, password, passwordSubmit)
                .errorSubmit()
                .checkErrorMessage(PW_LENGTH_ERROR_MSG);
    }

    @Test
    @DisplayName("Unsuccessful registration in case username length is incorrect")
    void shouldShowErrorIfUsernameLengthIsInvalid() {
        final String newUsername = "ab";
        final String password = randomPassword();

        Selenide.open(MainPage.URL, MainPage.class)
                .toLoginPage()
                .continueToRegister()
                .fillRegisterPage(newUsername, password, password)
                .errorSubmit()
                .checkErrorMessage(USERNAME_LENGTH_ERROR_MSG);
    }
}
