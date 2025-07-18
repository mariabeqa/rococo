package org.rococo.test.api.grpc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestPainting;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.constant.DefaultData.*;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomPaintingDescription;
import static org.rococo.utils.data.RandomDataUtils.randomPaintingName;

public class PaintingGrpcTest extends BaseGrpcTest{

    @Test
    @TestMuseum(removeAfterTest = false)
    @TestArtist(removeAfterTest = false)
    @DisplayName("gRPC: Adding new painting with rococo-painting grpc service")
    void shouldAddNewPainting(MuseumJson museumJson, ArtistJson artistJson) {
        final String paintingTitle = randomPaintingName();

        PaintingJson paintingJson = new PaintingJson(
            null,
            paintingTitle,
            PAINTING_DESCRIPTION,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH),
            museumJson,
            artistJson
        );

        PaintingJson result = paintingGrpcClient.addPainting(paintingJson);

        assertNotNull(result.id());
        assertEquals(paintingTitle, result.title());
        assertEquals(PAINTING_DESCRIPTION, result.description());
        assertEquals(getEncodedImageFromClasspath(PAINTING_IMAGE_PATH), result.content());
        assertEquals(museumJson.id(), result.museum().id());
        assertEquals(museumJson.title(), result.museum().title());
        assertEquals(museumJson.description(), result.museum().description());
        assertEquals(museumJson.photo(), result.museum().photo());
        assertEquals(museumJson.geo().city(), result.museum().geo().city());
        assertEquals(museumJson.geo().country().name(), result.museum().geo().country().name());
        assertEquals(museumJson.geo().country().id(), result.museum().geo().country().id());
        assertEquals(artistJson.id(), result.artist().id());
        assertEquals(artistJson.name(), result.artist().name());
        assertEquals(artistJson.biography(), result.artist().biography());
        assertEquals(artistJson.photo(), result.artist().photo());
    }

    @Test
    @DisplayName("gRPC: Updating painting with rococo-painting grpc service")
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldUpdatePainting(MuseumJson museumJson,
                              ArtistJson artistJson,
                              PaintingJson paintingJson) {
        final String newPaintingTitle = randomPaintingName();
        final String newPaintingDescription = randomPaintingDescription();

        PaintingJson newPaintingJson = new PaintingJson(
            paintingJson.id(),
            newPaintingTitle,
            newPaintingDescription,
            getEncodedImageFromClasspath(PAINTING_IMAGE_PATH_NEW),
            museumJson,
            artistJson
        );
        PaintingJson result = paintingGrpcClient.updatePainting(newPaintingJson);

        assertEquals(paintingJson.id(), result.id());
        assertEquals(newPaintingTitle, result.title());
        assertEquals(newPaintingDescription, result.description());
        assertEquals(getEncodedImageFromClasspath(PAINTING_IMAGE_PATH_NEW), result.content());
        assertEquals(museumJson.id(), result.museum().id());
        assertEquals(museumJson.title(), result.museum().title());
        assertEquals(museumJson.description(), result.museum().description());
        assertEquals(museumJson.photo(), result.museum().photo());
        assertEquals(museumJson.geo().city(), result.museum().geo().city());
        assertEquals(museumJson.geo().country().name(), result.museum().geo().country().name());
        assertEquals(museumJson.geo().country().id(), result.museum().geo().country().id());
        assertEquals(artistJson.id(), result.artist().id());
        assertEquals(artistJson.name(), result.artist().name());
        assertEquals(artistJson.biography(), result.artist().biography());
        assertEquals(artistJson.photo(), result.artist().photo());
    }

    @Test
    @DisplayName("gRPC: Getting painting by ID with rococo-painting grpc service")
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldReturnPaintingByID(PaintingJson paintingJson) {
        PaintingJson result = paintingGrpcClient.findPaintingById(paintingJson.id().toString());

        assertEquals(paintingJson.id(), result.id());
        assertEquals(paintingJson.title(), result.title());
        assertEquals(paintingJson.description(), result.description());
        assertEquals(paintingJson.content(), result.content());
        assertEquals(paintingJson.museum().id(), result.museum().id());
        assertEquals(paintingJson.artist().id(), result.artist().id());
    }

    @Test
    @DisplayName("gRPC: Getting painting by author ID with rococo-painting grpc service")
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldReturnPaintingByAuthorID(ArtistJson artistJson, PaintingJson paintingJson) {
        final int page = 0;
        final int size = 10;
        String artistId = artistJson.id().toString();
        List<PaintingJson> result = paintingGrpcClient.findPaintingByAuthorId(
            artistId,
            page,
            size
        );

        assertEquals(1, result.size());
        assertEquals(paintingJson.id(), result.getFirst().id());
        assertEquals(paintingJson.title(), result.getFirst().title());
        assertEquals(paintingJson.description(), result.getFirst().description());
        assertEquals(paintingJson.content(), result.getFirst().content());
        assertEquals(paintingJson.museum().id(), result.getFirst().museum().id());
        assertEquals(paintingJson.artist().id(), result.getFirst().artist().id());
    }
}
