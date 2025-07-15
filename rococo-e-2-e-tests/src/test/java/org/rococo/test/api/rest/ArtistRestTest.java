package org.rococo.test.api.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.ApiLogin;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.jupiter.annotation.TestUser;
import org.rococo.jupiter.annotation.Token;
import org.rococo.model.ArtistJson;
import org.rococo.model.ErrorJson;
import retrofit2.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomArtistName;

public class ArtistRestTest extends BaseRestTest {

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new artist with rococo-gateway service")
    void shouldBeAbleToAddArtist(@Token String token) {
        final String artistName = randomArtistName();

        final ArtistJson artistJson = new ArtistJson(
            null,
            artistName,
            ARTIST_BIO,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.addArtist(token, artistJson);
        final ArtistJson artist = response.body();

        assertEquals(200, response.code());
        assertNotNull(artist.id());
        assertEquals(artistName, artist.name());
        assertEquals(ARTIST_BIO, artist.biography());
        assertEquals(getEncodedImageFromClasspath(ARTIST_IMAGE_PATH), artist.photo());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new artist with name blank with rococo-gateway service")
    void createArtistShouldReturnInvalidArgumentIfNameIsBlank(@Token String token) {
        final String invalidName = "";

        final ArtistJson artistJson = new ArtistJson(
            null,
            invalidName,
            ARTIST_BIO,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.addArtist(token, artistJson);
        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Name must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new artist with bio blank with rococo-gateway service")
    void createArtistShouldReturnInvalidArgumentIfBioIsBlank(@Token String token) {
        final String artistName = randomArtistName();
        final String invalidBio = "";

        final ArtistJson artistJson = new ArtistJson(
            null,
            artistName,
            invalidBio,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.addArtist(token, artistJson);
        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Biography must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @DisplayName("REST: Adding new artist with photo blank with rococo-gateway service")
    void createArtistShouldReturnInvalidArgumentIfPhotoIsBlank(@Token String token) {
        final String artistName = randomArtistName();

        final ArtistJson artistJson = new ArtistJson(
            null,
            artistName,
            ARTIST_BIO,
            ""
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.addArtist(token, artistJson);
        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Photo must not be blank", errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestArtist
    @DisplayName("REST: Updating artist info with rococo-gateway service")
    void shouldBeAbleToUpdateArtist(ArtistJson artistJson, @Token String token) {
        final ArtistJson newArtistJson = new ArtistJson(
            artistJson.id(),
            artistJson.name() + " edited",
            artistJson.biography() + " edited",
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH_NEW)
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.updateArtist(token, newArtistJson);
        final ArtistJson artist = response.body();

        assertEquals(200, response.code());
        assertEquals(artistJson.id(), artist.id());
        assertEquals(artistJson.name() + " edited", artist.name());
        assertEquals(artistJson.biography() + " edited", artist.biography());
        assertEquals(getEncodedImageFromClasspath(ARTIST_IMAGE_PATH_NEW), artist.photo());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Trying to update artist that doesn't exist with rococo-gateway service")
    void updateArtistShouldReturnNotFoundIfArtistNotFound(@Token String token) {
        final UUID notExistingArtistId = UUID.randomUUID();
        final String artistName = randomArtistName();

        final ArtistJson artistJson = new ArtistJson(
            notExistingArtistId,
            artistName,
            ARTIST_BIO,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        final Response<ArtistJson> response = gatewayArtistApiClient.updateArtist(token, artistJson);
        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Artist with id '%s' not found", notExistingArtistId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestArtist
    @DisplayName("REST: Getting artist by ID with rococo-gateway service")
    void shouldBeAbleToGetArtistById(ArtistJson artistJson, @Token String token) {
        final Response<ArtistJson> response = gatewayArtistApiClient.getArtistById(token, artistJson.id().toString());
        final ArtistJson responseArtist = response.body();

        assertEquals(200, response.code());
        assertEquals(responseArtist.id(), artistJson.id());
        assertEquals(responseArtist.name(), artistJson.name());
        assertEquals(responseArtist.biography(), artistJson.biography());
        assertEquals(responseArtist.photo(), artistJson.photo());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Getting artist that doesn't exist by ID with rococo-gateway service")
    void findArtistByIdShouldReturnNotFoundIfArtistNotFound(@Token String token) {
        final String notExistingArtistId = UUID.randomUUID().toString();

        final Response<ArtistJson> response = gatewayArtistApiClient.getArtistById(token, notExistingArtistId);
        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Artist with id '%s' not found", notExistingArtistId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Deleting an artist that doesn't exist with rococo-gateway service")
    void deleteArtistShouldReturnNotFoundIfArtistNotFound(@Token String token) {
        final UUID notExistingArtistId = UUID.randomUUID();
        final String artistName = randomArtistName();

        final ArtistJson artistJson = new ArtistJson(
            notExistingArtistId,
            artistName,
            ARTIST_BIO,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        Response<Void> response = gatewayArtistApiClient.deleteArtist(token, artistJson.id().toString());

        final ErrorJson errorJson = gatewayArtistApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Artist with id '%s' not found", notExistingArtistId),
            errorJson.message());
    }
}
