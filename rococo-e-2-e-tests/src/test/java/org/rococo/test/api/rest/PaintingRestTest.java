package org.rococo.test.api.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.*;
import org.rococo.model.ArtistJson;
import org.rococo.model.ErrorJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;
import retrofit2.Response;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.constant.DefaultData.*;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomPaintingDescription;
import static org.rococo.utils.data.RandomDataUtils.randomPaintingName;

public class PaintingRestTest extends BaseRestTest {

    @ApiLogin
    @TestUser
    @Test
    @TestMuseum(removeAfterTest = false)
    @TestArtist(removeAfterTest = false)
    @DisplayName("REST: Adding new painting with rococo-painting service")
    void shouldBeAbleToAddPainting(MuseumJson museumJson,
                                   ArtistJson artistJson,
                                   @Token String token) {
        final String paintingTitle = randomPaintingName();

        final PaintingJson paintingJson = new PaintingJson(
            null,
            paintingTitle,
            PAINTING_DESCRIPTION,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH),
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.addPainting(token, paintingJson);
        final PaintingJson painting = response.body();

        assertEquals(200, response.code());
        assertNotNull(painting.id());
        assertEquals(paintingTitle, painting.title());
        assertEquals(PAINTING_DESCRIPTION, painting.description());
        assertEquals(getEncodedImageFromClasspath(PAINTING_IMAGE_PATH), painting.content());
        assertEquals(museumJson.id(), painting.museum().id());
        assertEquals(museumJson.title(), painting.museum().title());
        assertEquals(museumJson.description(), painting.museum().description());
        assertEquals(museumJson.photo(), painting.museum().photo());
        assertEquals(museumJson.geo().city(), painting.museum().geo().city());
        assertEquals(museumJson.geo().country().name(), painting.museum().geo().country().name());
        assertEquals(museumJson.geo().country().id(), painting.museum().geo().country().id());
        assertEquals(artistJson.id(), painting.artist().id());
        assertEquals(artistJson.name(), painting.artist().name());
        assertEquals(artistJson.biography(), painting.artist().biography());
        assertEquals(artistJson.photo(), painting.artist().photo());
    }

    @ApiLogin
    @TestUser
    @Test
    @TestMuseum()
    @TestArtist()
    @DisplayName("REST: Adding new painting with title blank with rococo-gateway service")
    void createPaintingShouldReturnInvalidArgumentIfTitleIsBlank(MuseumJson museumJson,
                                                                 ArtistJson artistJson,
                                                                 @Token String token) {
        final String invalidTitle = "";

        final PaintingJson paintingJson = new PaintingJson(
            null,
            invalidTitle,
            PAINTING_DESCRIPTION,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH),
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.addPainting(token, paintingJson);
        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Title must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @TestMuseum()
    @TestArtist()
    @DisplayName("REST: Adding new painting with description blank with rococo-gateway service")
    void createPaintingShouldReturnInvalidArgumentIfDescriptionIsBlank(MuseumJson museumJson,
                                                                       ArtistJson artistJson,
                                                                       @Token String token) {
        final String paintingTitle = randomPaintingName();
        final String invalidDescription = "";

        final PaintingJson newPaintingJson = new PaintingJson(
            null,
            paintingTitle,
            invalidDescription,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH),
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.addPainting(token, newPaintingJson);
        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Description must not be blank", errorJson.message());
    }

    @ApiLogin
    @TestUser
    @Test
    @TestMuseum()
    @TestArtist()
    @DisplayName("REST: Adding new painting with photo blank with rococo-gateway service")
    void createPaintingShouldReturnInvalidArgumentIfPhotoIsBlank(MuseumJson museumJson,
                                                                 ArtistJson artistJson,
                                                                 @Token String token) {
        final String paintingTitle = randomPaintingName();

        final PaintingJson newPaintingJson = new PaintingJson(
            null,
            paintingTitle,
            PAINTING_DESCRIPTION,
            "",
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.addPainting(token, newPaintingJson);
        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(400, response.code());
        assertEquals("INVALID_ARGUMENT", errorJson.grpcCode());
        assertEquals("Bad Request", errorJson.error());
        assertEquals("Content must not be blank", errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum()
    @TestArtist()
    @TestPainting
    @DisplayName("REST: Updating painting with rococo-gateway service")
    void shouldBeAbleToUpdateMuseum(MuseumJson museumJson,
                                    ArtistJson artistJson,
                                    PaintingJson paintingJson,
                                    @Token String token) {
        final String newPaintingTitle = randomPaintingName();
        final String newPaintingDescription = randomPaintingDescription();

        final PaintingJson newPaintingJson = new PaintingJson(
            paintingJson.id(),
            newPaintingTitle,
            newPaintingDescription,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH_NEW),
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.updatePainting(token, newPaintingJson);
        final PaintingJson painting = response.body();

        assertEquals(200, response.code());
        assertEquals(paintingJson.id(), painting.id());
        assertEquals(newPaintingTitle, painting.title());
        assertEquals(newPaintingDescription, painting.description());
        assertEquals(getEncodedImageFromClasspath(PAINTING_IMAGE_PATH_NEW), painting.content());
        assertEquals(museumJson.id(), painting.museum().id());
        assertEquals(museumJson.title(), painting.museum().title());
        assertEquals(museumJson.description(), painting.museum().description());
        assertEquals(museumJson.photo(), painting.museum().photo());
        assertEquals(museumJson.geo().city(), painting.museum().geo().city());
        assertEquals(museumJson.geo().country().name(), painting.museum().geo().country().name());
        assertEquals(museumJson.geo().country().id(), painting.museum().geo().country().id());
        assertEquals(artistJson.id(), painting.artist().id());
        assertEquals(artistJson.name(), painting.artist().name());
        assertEquals(artistJson.biography(), painting.artist().biography());
        assertEquals(artistJson.photo(), painting.artist().photo());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum()
    @TestArtist()
    @DisplayName("REST: Trying to update painting that doesn't exist with rococo-gateway service")
    void updateMuseumShouldReturnNotFoundIfMuseumNotFound(MuseumJson museumJson,
                                                          ArtistJson artistJson,
                                                          @Token String token) {
        final UUID notExistingPaintingId = UUID.randomUUID();
        final String newPaintingTitle = randomPaintingName();

        final PaintingJson newPaintingJson = new PaintingJson(
            notExistingPaintingId,
            newPaintingTitle,
            PAINTING_DESCRIPTION,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH),
            museumJson,
            artistJson
        );

        final Response<PaintingJson> response = gatewayPaintingApiClient.updatePainting(token, newPaintingJson);
        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Painting with id '%s' not found", notExistingPaintingId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum()
    @TestArtist()
    @TestPainting
    @DisplayName("REST: Getting painting by ID with rococo-gateway service")
    void shouldBeAbleToGetPaintingById(MuseumJson museumJson,
                                       ArtistJson artistJson,
                                       PaintingJson paintingJson,
                                       @Token String token) {
        final Response<PaintingJson> response = gatewayPaintingApiClient.getPaintingById(token, paintingJson.id().toString());
        final PaintingJson responsePainting = response.body();

        assertEquals(200, response.code());
        assertEquals(responsePainting.id(), responsePainting.id());
        assertEquals(paintingJson.title(), responsePainting.title());
        assertEquals(paintingJson.description(), responsePainting.description());
        assertEquals(paintingJson.content(), responsePainting.content());
        assertEquals(museumJson.id(), responsePainting.museum().id());
        assertEquals(museumJson.title(), responsePainting.museum().title());
        assertEquals(museumJson.description(), responsePainting.museum().description());
        assertEquals(museumJson.photo(), responsePainting.museum().photo());
        assertEquals(museumJson.geo().city(), responsePainting.museum().geo().city());
        assertEquals(museumJson.geo().country().name(), responsePainting.museum().geo().country().name());
        assertEquals(museumJson.geo().country().id(), responsePainting.museum().geo().country().id());
        assertEquals(artistJson.id(), responsePainting.artist().id());
        assertEquals(artistJson.name(), responsePainting.artist().name());
        assertEquals(artistJson.biography(), responsePainting.artist().biography());
        assertEquals(artistJson.photo(), responsePainting.artist().photo());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Getting painting that doesn't exist by ID with rococo-gateway service")
    void findPaintingByIdShouldReturnNotFoundIfPaintingNotFound(@Token String token) {
        final String notExistingPaintingId = UUID.randomUUID().toString();

        final Response<PaintingJson> response = gatewayPaintingApiClient.getPaintingById(token, notExistingPaintingId);
        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Painting with id '%s' not found", notExistingPaintingId),
            errorJson.message());
    }

    @Test
    @ApiLogin
    @TestUser
    @DisplayName("REST: Deleting a painting that doesn't exist with rococo-gateway service")
    void deletePaintingShouldReturnNotFoundIfPaintingNotFound(@Token String token) {
        final String notExistingPaintingId = UUID.randomUUID().toString();

        Response<Void> response = gatewayPaintingApiClient.deletePainting(token, notExistingPaintingId);

        final ErrorJson errorJson = gatewayPaintingApiClient.parseError(response);

        assertEquals(404, response.code());
        assertEquals("NOT_FOUND", errorJson.grpcCode());
        assertEquals("Not Found", errorJson.error());
        assertEquals(String.format("Painting with id '%s' not found", notExistingPaintingId),
            errorJson.message());
    }
}
