package org.rococo.test.api.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.Token;
import org.rococo.model.CountryJson;
import org.rococo.model.ErrorJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;
import retrofit2.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.model.Countries.AUSTRALIA;
import static org.rococo.model.Countries.RUSSIA;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomMuseumDescription;
import static org.rococo.utils.data.RandomDataUtils.randomMuseumTitle;

public class MuseumRestTest extends BaseRestTest {

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new museum with rococo-gateway service")
    void shouldBeAbleToAddMuseum(@Token String token) {
        final String museumTitle = randomMuseumTitle();

        final MuseumJson museumJson = new MuseumJson(
            null,
            museumTitle,
            MUSEUM_DESCRIPTION,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.addMuseum(token, museumJson);
        final MuseumJson museum = response.body();

        assertEquals(200, response.code());
        assertNotNull(museum.id());
        assertEquals(museumTitle, museum.title());
        assertEquals(MUSEUM_DESCRIPTION, museum.description());
        assertEquals(getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH), museum.photo());
        assertEquals(CITY, museum.geo().city());
        assertEquals(RUSSIA.getId(), museum.geo().country().id());
        assertEquals(RUSSIA.getName(), museum.geo().country().name());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new museum with title blank with rococo-gateway service")
    void createMuseumShouldReturnInvalidArgumentIfTitleIsBlank(@Token String token) {
        final String invalidTitle = "";

        final MuseumJson museumJson = new MuseumJson(
            null,
            invalidTitle,
            MUSEUM_DESCRIPTION,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.addMuseum(token, museumJson);
        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Title must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new museum with description blank with rococo-gateway service")
    void createMuseumShouldReturnInvalidArgumentIfDescriptionIsBlank(@Token String token) {
        final String museumTitle = randomMuseumTitle();
        final String invalidDescription = "";

        final MuseumJson museumJson = new MuseumJson(
            null,
            museumTitle,
            invalidDescription,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.addMuseum(token, museumJson);
        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Description must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new museum with photo blank with rococo-gateway service")
    void createMuseumShouldReturnInvalidArgumentIfPhotoIsBlank(@Token String token) {
        final String museumTitle = randomMuseumTitle();

        final MuseumJson museumJson = new MuseumJson(
            null,
            museumTitle,
            MUSEUM_DESCRIPTION,
            "",
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.addMuseum(token, museumJson);
        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Photo must not be blank", errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum
    @DisplayName("REST: Updating museum with rococo-gateway service")
    void shouldBeAbleToUpdateMuseum(MuseumJson museumJson, @Token String token) {
        final String newMuseumTitle = randomMuseumTitle();
        final String newMuseumDescription = randomMuseumDescription();
        final String newCity = "Сидней";

        final MuseumJson newMuseumJson = new MuseumJson(
            museumJson.id(),
            newMuseumTitle,
            newMuseumDescription,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH_NEW),
            new GeoLocationJson(
                newCity,
                new CountryJson(
                    AUSTRALIA.getId(),
                    AUSTRALIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.updateMuseum(token, newMuseumJson);
        final MuseumJson museum = response.body();

        assertEquals(200, response.code());
        assertEquals(museumJson.id(), museum.id());
        assertEquals(newMuseumTitle, museum.title());
        assertEquals(newMuseumDescription, museum.description());
        assertEquals(getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH_NEW), museum.photo());
        assertEquals(newCity, museum.geo().city());
        assertEquals(AUSTRALIA.getId(), museum.geo().country().id());
        assertEquals(AUSTRALIA.getName(), museum.geo().country().name());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Trying to update museum that doesn't exist with rococo-gateway service")
    void updateMuseumShouldReturnNotFoundIfMuseumNotFound(@Token String token) {
        final UUID notExistingMuseumId = UUID.randomUUID();

        final MuseumJson museumJson = new MuseumJson(
            notExistingMuseumId,
            MUSEUM_TITLE,
            MUSEUM_DESCRIPTION,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        final Response<MuseumJson> response = gatewayMuseumApiClient.updateMuseum(token, museumJson);
        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Museum with id '%s' not found", notExistingMuseumId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum
    @DisplayName("REST: Getting museum by ID with rococo-gateway service")
    void shouldBeAbleToGetMuseumById(MuseumJson museumJson, @Token String token) {
        final Response<MuseumJson> response = gatewayMuseumApiClient.getMuseumById(token, museumJson.id().toString());
        final MuseumJson responseMuseum = response.body();

        assertEquals(200, response.code());
        assertEquals(museumJson.id(), responseMuseum.id());
        assertEquals(museumJson.title(), responseMuseum.title());
        assertEquals(museumJson.description(), responseMuseum.description());
        assertEquals(museumJson.photo(), responseMuseum.photo());
        assertEquals(museumJson.geo().city(), responseMuseum.geo().city());
        assertEquals(museumJson.geo().country().id(), responseMuseum.geo().country().id());
        assertEquals(museumJson.geo().country().name(), responseMuseum.geo().country().name());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Getting museum that doesn't exist by ID with rococo-gateway service")
    void findMuseumByIdShouldReturnNotFoundIfMuseumNotFound(@Token String token) {
        final String notExistingMuseumId = UUID.randomUUID().toString();

        final Response<MuseumJson> response = gatewayMuseumApiClient.getMuseumById(token, notExistingMuseumId);
        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Museum with id '%s' not found", notExistingMuseumId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Deleting a museum that doesn't exist with rococo-gateway service")
    void deleteMuseumShouldReturnNotFoundIfMuseumNotFound(@Token String token) {
        final UUID notExistingMuseumId = UUID.randomUUID();

        final MuseumJson museumJson = new MuseumJson(
            notExistingMuseumId,
            MUSEUM_TITLE,
            MUSEUM_DESCRIPTION,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        Response<Void> response = gatewayMuseumApiClient.deleteMuseum(token, museumJson.id().toString());

        final ErrorJson errorJson = gatewayMuseumApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Museum with id '%s' not found", notExistingMuseumId),
            errorJson.message());
    }
}
