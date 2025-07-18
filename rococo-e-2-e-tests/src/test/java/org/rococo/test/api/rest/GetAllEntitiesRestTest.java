package org.rococo.test.api.rest;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.data.entity.ArtistEntity;
import org.rococo.data.entity.MuseumEntity;
import org.rococo.data.entity.PaintingEntity;
import org.rococo.data.repository.ArtistRepositoryHibernate;
import org.rococo.data.repository.MuseumRepositoryHibernate;
import org.rococo.data.repository.PaintingRepositoryHibernate;
import org.rococo.jupiter.annotation.*;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;
import org.rococo.model.pageable.RestResponsePage;
import retrofit2.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Isolated
public class GetAllEntitiesRestTest extends BaseRestTest {

    private final ArtistRepositoryHibernate artistRepository = new ArtistRepositoryHibernate();
    private final MuseumRepositoryHibernate museumRepository = new MuseumRepositoryHibernate();
    private final PaintingRepositoryHibernate paintingRepository = new PaintingRepositoryHibernate();

    @Test
    @ApiLogin
    @TestUser
    @TestArtist
    @DisplayName("REST: Getting all artists with rococo-gateway service")
    void shouldBeAbleToGetAllArtists(@Token String token) {
        final int page = 0;
        final int size = 10;

        final Response<RestResponsePage<ArtistJson>> response = gatewayArtistApiClient.allArtists(
            token,
            page,
            size,
            null
        );

        final List<ArtistJson> artists = response.body().getContent();

        final List<ArtistEntity> expected = artistRepository.allArtists(page, size, null);

        assertEquals(200, response.code());
        assertEquals(expected.size(), artists.size());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestArtist
    @DisplayName("REST: Getting all artists filtered by title with rococo-gateway service")
    void shouldBeAbleToGetAllArtistsFilteredByName(ArtistJson artistJson, @Token String token) {
        final int page = 0;
        final int size = 10;
        final String title = artistJson.name();

        final Response<RestResponsePage<ArtistJson>> response = gatewayArtistApiClient.allArtists(
            token,
            page,
            size,
            title
        );

        final List<ArtistJson> artists = response.body().getContent();

        final List<ArtistEntity> expected = artistRepository.allArtists(page, size, title);

        assertEquals(200, response.code());
        assertEquals(expected.size(), artists.size());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum
    @DisplayName("REST: Getting all museums with rococo-gateway service")
    void shouldBeAbleToGetAllMuseums(@Token String token) {
        final int page = 0;
        final int size = 10;

        final Response<RestResponsePage<MuseumJson>> response = gatewayMuseumApiClient.allMuseums(
            token,
            page,
            size,
            null
        );

        final List<MuseumJson> museums = response.body().getContent();

        final List<MuseumEntity> expected = museumRepository.allMuseums(page, size, null);

        assertEquals(200, response.code());
        assertEquals(expected.size(), museums.size());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum
    @DisplayName("REST: Getting all museums filtered by title with rococo-gateway service")
    void shouldBeAbleToGetAllMuseumsIfTitleIsPassed(MuseumJson museumJson, @Token String token) {
        final int page = 0;
        final int size = 10;
        final String title = museumJson.title().substring(0,4);

        final Response<RestResponsePage<MuseumJson>> response = gatewayMuseumApiClient.allMuseums(
            token,
            page,
            size,
            title
        );

        final List<MuseumJson> museums = response.body().getContent();

        final List<MuseumEntity> expected = museumRepository.allMuseums(page, size, title);

        assertEquals(200, response.code());
        assertEquals(expected.size(), museums.size());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum()
    @TestArtist()
    @TestPainting
    @DisplayName("REST: Getting all paintings with rococo-gateway service")
    void shouldBeAbleToGetAllPaintings(@Token String token) {
        final int page = 0;
        final int size = 10;

        final Response<RestResponsePage<PaintingJson>> response = gatewayPaintingApiClient.allPaintings(
            token,
            page,
            size,
            null
        );

        final List<PaintingJson> paintings = response.body().getContent();

        final List<PaintingEntity> expected = paintingRepository.allPaintings(page, size, null);

        assertEquals(200, response.code());
        assertEquals(expected.size(), paintings.size());
    }

    @Test
    @ApiLogin
    @TestUser
    @TestMuseum
    @TestArtist()
    @TestPainting()
    @DisplayName("REST: Getting all painting filtered by title with rococo-gateway service")
    void shouldBeAbleToGetAllPaintingsIfTitleIsPassed(PaintingJson painting, @Token String token) {
        final int page = 0;
        final int size = 10;
        final String title = painting.title().substring(0,5);

        final Response<RestResponsePage<PaintingJson>> response = gatewayPaintingApiClient.allPaintings(
            token,
            page,
            size,
            title
        );

        final List<PaintingJson> paintings = response.body().getContent();

        final List<PaintingEntity> expected = paintingRepository.allPaintings(page, size, title);

        assertEquals(200, response.code());
        assertEquals(expected.size(), paintings.size());
    }
}
