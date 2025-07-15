package org.rococo.test.api.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.Token;
import org.rococo.model.UserJson;
import org.rococo.utils.ImageUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.rococo.utils.data.RandomDataUtils.randomName;
import static org.rococo.utils.data.RandomDataUtils.randomSurname;

public class UserRestTest extends BaseRestTest {

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Getting user by ID with rococo-gateway service")
    void shouldBeAbleToGetUserByID(@Token String token, UserJson expected) {
        UserJson response = gatewayUserApiClient.getCurrent(token);

        assertEquals(expected.id(), response.id());
        assertEquals(expected.username(), response.username());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Updating user with rococo-gateway service")
    void shouldBeAbleToUpdateUser(@Token String token, UserJson expected) {
        final String firstName = randomName();
        final String lastName = randomSurname();
        final String imgPath = "img/profile/profilePic.jpg";

        UserJson newUserJson = new UserJson(
                expected.id(),
                expected.username(),
                firstName,
                lastName,
                ImageUtil.getEncodedImageFromClasspath(imgPath),
                expected.testData()
        );

        UserJson response = gatewayUserApiClient.updateUser(token, newUserJson);

        assertEquals(expected.id(), response.id());
        assertEquals(expected.username(), response.username());
        assertEquals(firstName, response.firstname());
        assertEquals(lastName, response.lastname());
        assertEquals(ImageUtil.getEncodedImageFromClasspath(imgPath),
                response.avatar());
    }
}
