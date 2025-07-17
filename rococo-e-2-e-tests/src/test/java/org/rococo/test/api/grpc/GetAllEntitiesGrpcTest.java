package org.rococo.test.api.grpc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.data.entity.ArtistEntity;
import org.rococo.data.entity.MuseumEntity;
import org.rococo.data.entity.PaintingEntity;
import org.rococo.data.repository.ArtistRepositoryHibernate;
import org.rococo.data.repository.MuseumRepositoryHibernate;
import org.rococo.data.repository.PaintingRepositoryHibernate;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestPainting;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Isolated
public class GetAllEntitiesGrpcTest  extends BaseGrpcTest {

    @Test
    @DisplayName("gRPC: Getting all artists with rococo-artist grpc service")
    @TestArtist
    void shouldReturnAllArtists() {
        final ArtistRepositoryHibernate artistRepositoryHibernate = new ArtistRepositoryHibernate();
        final int page = 0;
        final int size = 10;

        List<ArtistJson> actual = artistGrpcClient.getAll(page, size, "");

        final List<ArtistEntity> expected = artistRepositoryHibernate.allArtists(page, size, null);

        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("gRPC: Getting all museums with rococo-museum grpc service")
    @TestMuseum
    void shouldReturnAllMuseums() {
        final MuseumRepositoryHibernate museumRepository = new MuseumRepositoryHibernate();
        final int page = 0;
        final int size = 10;

        List<MuseumJson> actual = museumGrpcClient.getAll(page, size, "");

        final List<MuseumEntity> expected = museumRepository.allMuseums(page, size, null);

        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("gRPC: Getting all museums filtered by title with rococo-museum grpc service")
    @TestMuseum
    void shouldReturnAllMuseumsFilteredByTitle() {
        final MuseumRepositoryHibernate museumRepository = new MuseumRepositoryHibernate();
        final int page = 0;
        final int size = 10;
        final String title = "Ру";

        List<MuseumJson> actual = museumGrpcClient.getAll(page, size, title);

        final List<MuseumEntity> expected = museumRepository.allMuseums(page, size, title);

        assertEquals(expected.size(), actual.size());
    }
    @Test
    @DisplayName("gRPC: Getting all paintings with rococo-painting grpc service")
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldReturnAllPaintings() {
        final PaintingRepositoryHibernate paintingRepository = new PaintingRepositoryHibernate();
        final int page = 0;
        final int size = 10;

        List<PaintingJson> actual = paintingGrpcClient.allPaintings(page, size, "");

        final List<PaintingEntity> expected = paintingRepository.allPaintings(page, size, null);

        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("gRPC: Getting all paintings filtered by title with rococo-painting grpc service")
    @TestMuseum
    @TestArtist
    @TestPainting
    void shouldReturnAllPaintingsFilteredByTitle() {
        final PaintingRepositoryHibernate paintingRepository = new PaintingRepositoryHibernate();
        final int page = 0;
        final int size = 10;
        final String name = "Ра";

        List<PaintingJson> actual = paintingGrpcClient.allPaintings(page, size, name);

        final List<PaintingEntity> expected = paintingRepository.allPaintings(page, size, name);

        assertEquals(expected.size(), actual.size());
    }
}
