package org.rococo.test.api.grpc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.data.entity.ArtistEntity;
import org.rococo.data.repository.ArtistRepositoryHibernate;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.model.ArtistJson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomArtistName;

public class ArtistGrpcTest extends BaseGrpcTest{

    @Test
    @DisplayName("gRPC: Adding new artist with rococo-artist grpc service")
    void shouldAddNewArtist() {
        final String artistName = randomArtistName();

        ArtistJson artistJson = new ArtistJson(
            null,
            artistName,
            ARTIST_BIO,
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH)
        );

        ArtistJson result = artistGrpcClient.addArtist(artistJson);

        assertNotNull(result.id());
        assertEquals(artistName, result.name());
        assertEquals(ARTIST_BIO, result.biography());
        assertEquals(getEncodedImageFromClasspath(ARTIST_IMAGE_PATH), result.photo());
    }

    @Test
    @DisplayName("gRPC: Updating an artist with rococo-artist grpc service")
    @TestArtist
    void shouldUpdateArtist(ArtistJson testArtist) {
        ArtistJson newArtistJson = new ArtistJson(
            testArtist.id(),
            testArtist.name() + " edited",
            testArtist.biography() + " edited",
            getEncodedImageFromClasspath(ARTIST_IMAGE_PATH_NEW)
        );

        ArtistJson result = artistGrpcClient.updateArtist(newArtistJson);

        assertEquals(testArtist.id(), result.id());
        assertEquals(testArtist.name() + " edited", result.name());
        assertEquals(testArtist.biography() + " edited", result.biography());
        assertEquals(getEncodedImageFromClasspath(ARTIST_IMAGE_PATH_NEW),
            result.photo());
    }

    @Test
    @DisplayName("gRPC: Getting artist by ID with rococo-artist grpc service")
    @TestArtist
    void shouldReturnArtistByID(ArtistJson testArtist) {

        ArtistJson result = artistGrpcClient.getArtist(testArtist.id().toString());

        assertEquals(testArtist.id(), result.id());
        assertEquals(testArtist.name(), result.name());
        assertEquals(testArtist.biography(), result.biography());
        assertEquals(testArtist.photo(), result.photo());
    }

    @Test
    @DisplayName("gRPC: Getting all artists filtered by name with rococo-artist grpc service")
    @TestArtist
    void shouldReturnAllArtistsFilteredByName(ArtistJson testArtist) {
        final ArtistRepositoryHibernate artistRepositoryHibernate = new ArtistRepositoryHibernate();
        final int page = 0;
        final int size = 10;
        final String name = testArtist.name();

        List<ArtistJson> actual = artistGrpcClient.getAll(page, size, name);

        final List<ArtistEntity> expected = artistRepositoryHibernate.allArtists(page, size, name);

        assertEquals(expected.size(), actual.size());
    }
}
