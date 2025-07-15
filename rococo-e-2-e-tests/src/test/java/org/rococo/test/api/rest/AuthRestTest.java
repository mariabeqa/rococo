package org.rococo.test.api.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.model.UserJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.utils.data.RandomDataUtils.randomPassword;
import static org.rococo.utils.data.RandomDataUtils.randomUsername;

public class AuthRestTest extends BaseRestTest{

    @Test
    @DisplayName("REST: Create user with rococo-gateway service")
    void shouldBeAbleToCreateNewUser() {
        final String username = randomUsername();
        final String pw = randomPassword();

        authApiClient.createUser(username, pw);
        String token = "Bearer " + authApiClient.login(username, pw);

        UserJson current = gatewayUserApiClient.getCurrent(token);

        assertEquals(username, current.username());
        assertNotNull(current.id());
    }

    @Test
    @TestUser
    @DisplayName("REST: Log in existing user with rococo-gateway service")
    void shouldBeAbleToLogIn(UserJson user) {

        String idToken = authApiClient.login(user.username(), user.testData().password());

        assertNotNull(idToken);
    }
}
