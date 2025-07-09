package org.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rococo.grpc.PaintingResponse;
import org.rococo.grpc.PaintingsPageResponse;
import org.rococo.grpc.RococoPaintingsServiceGrpc;
import org.rococo.model.*;
import org.rococo.utils.ImageUtil;
import org.rococo.utils.grpc.ArtistBuilder;
import org.rococo.utils.grpc.MuseumBuilder;
import org.rococo.utils.grpc.PaintingBuilder;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.List;
import java.util.UUID;

import static org.rococo.utils.grpc.ArtistBuilder.*;
import static org.rococo.utils.grpc.MuseumBuilder.*;
import static org.rococo.utils.grpc.PaintingBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

public class PaintingControllerTest extends BaseControllerTest{

    private static final int WIREMOCK_PORT = 8810;

    @BeforeAll
    static void beforeAll() {
        wireMock = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(WIREMOCK_PORT)
                .withRootDirectory("src/test/resources/wiremock")
                .extensions(new Jetty12GrpcExtensionFactory())
        );
        wireMock.start();
    }

    @BeforeEach
    void setUp() {
        wireMockGrpcService = new WireMockGrpcService(
            WireMock.create()
                .port(WIREMOCK_PORT)
                .build(),
            RococoPaintingsServiceGrpc.SERVICE_NAME
        );
    }

    @Test
    void getAllShouldReturnAllPaintings() throws Exception {
        final String secondPaintingId = UUID.randomUUID().toString();
        final String secondPaintingTitle = "Днепр утром";
        final String secondPaintingDescription = "Картина «Днепр утром» была последней картиной, выставленной Куинджи на суд публики, перед тем как он полностью отказался от участия в выставках";
        final String imgPath = "img/painting/dnepr.jpg";

        wireMockGrpcService
            .stubFor(
                method("GetAll")
                    .willReturn(message(
                        PaintingsPageResponse.newBuilder()
                            .addAllPaintings(
                                List.of(
                                    paintingBuilder
                                        .withDefaults()
                                        .withArtist(artistBuilder.withDefaults().build())
                                        .withMuseum(museumBuilder.withDefaults().build())
                                        .build(),
                                    paintingBuilder.withId(secondPaintingId)
                                        .withTitle(secondPaintingTitle)
                                        .withDescription(secondPaintingDescription)
                                        .withContent(imgPath)
                                        .withArtist(artistBuilder.withDefaults().build())
                                        .withMuseum(museumBuilder.withDefaults().build())
                                        .build()
                                )
                            )
                            .build()
                    ))
            );

        mockMvc.perform(get("/api/painting")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content.[0].id").value(PAINTING_ID.toString()))
            .andExpect(jsonPath("$.content.[1].id").value(secondPaintingId))
            .andExpect(jsonPath("$.content.[0].title").value(PAINTING_TITLE))
            .andExpect(jsonPath("$.content.[1].title").value(secondPaintingTitle))
            .andExpect(jsonPath("$.content.[0].description").value(PAINTING_DESCRIPTION))
            .andExpect(jsonPath("$.content.[1].description").value(secondPaintingDescription))
            .andExpect(jsonPath("$.content.[0].content").value(ImageUtil.getEncodedImageFromClasspath(PaintingBuilder.IMAGE_PATH)))
            .andExpect(jsonPath("$.content.[1].content").value(ImageUtil.getEncodedImageFromClasspath(imgPath)))
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andDo(print());
    }

    @Test
    void findPaintingByIdShouldReturnCorrectPainting() throws Exception {

        wireMockGrpcService.stubFor(
            method("FindPaintingById")
                .willReturn(message(
                    PaintingResponse.newBuilder()
                        .setPainting(
                            paintingBuilder
                                .withDefaults()
                                .withArtist(artistBuilder.withDefaults().build())
                                .withMuseum(museumBuilder.withDefaults().build())
                                .build()
                        )
                        .build()
                ))
        );

        mockMvc.perform(get("/api/painting/" + PAINTING_ID)
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(PAINTING_ID.toString()))
            .andExpect(jsonPath("$.title").value(PAINTING_TITLE))
            .andExpect(jsonPath("$.description").value(PAINTING_DESCRIPTION))
            .andExpect(jsonPath("$.content").value(ImageUtil.getEncodedImageFromClasspath(PaintingBuilder.IMAGE_PATH)));
    }

    @Test
    void addPaintingShouldReturnCreatedPainting() throws Exception {

        wireMockGrpcService.stubFor(
            method("AddPainting")
                .willReturn(message(
                    PaintingResponse.newBuilder()
                        .setPainting(
                            paintingBuilder
                                .withDefaults()
                                .withArtist(artistBuilder.withDefaults().build())
                                .withMuseum(museumBuilder.withDefaults().build())
                                .build()
                        )
                        .build()
                ))
        );

        PaintingJson paintingJson = new PaintingJson(
            null,
            PAINTING_TITLE,
            PAINTING_DESCRIPTION,
            ImageUtil.getEncodedImageFromClasspath(PaintingBuilder.IMAGE_PATH),
            new MuseumJson(
                MUSEUM_ID,
                MUSEUM_TITLE,
                MUSEUM_DESCRIPTION,
                ImageUtil.getEncodedImageFromClasspath(MuseumBuilder.IMAGE_PATH),
                new GeoLocationJson(
                    CITY,
                    new CountryJson(
                        UUID.fromString(COUNTRY_ID),
                        COUNTRY_NAME
                    )
                )
            ),
            new ArtistJson(
                ARTIST_ID,
                ARTIST_NAME,
                ARTIST_BIO,
                ImageUtil.getEncodedImageFromClasspath(ArtistBuilder.IMAGE_PATH)
            )
        );

        mockMvc.perform(post("/api/painting")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(paintingJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(PAINTING_ID.toString()))
            .andExpect(jsonPath("$.title").value(PAINTING_TITLE))
            .andExpect(jsonPath("$.description").value(PAINTING_DESCRIPTION))
            .andExpect(jsonPath("$.content").value(ImageUtil.getEncodedImageFromClasspath(PaintingBuilder.IMAGE_PATH)))
            .andExpect(jsonPath("$.museum.id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.museum.title").value(MUSEUM_TITLE))
            .andExpect(jsonPath("$.museum.description").value(MUSEUM_DESCRIPTION))
            .andExpect(jsonPath("$.museum.photo").value(ImageUtil.getEncodedImageFromClasspath(MuseumBuilder.IMAGE_PATH)))
            .andExpect(jsonPath("$.museum.geo.city").value(CITY))
            .andExpect(jsonPath("$.museum.geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.museum.geo.country.id").value(COUNTRY_ID))
            .andExpect(jsonPath("$.artist.id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.artist.name").value(ARTIST_NAME))
            .andExpect(jsonPath("$.artist.biography").value(ARTIST_BIO))
            .andExpect(jsonPath("$.artist.photo").value(ImageUtil.getEncodedImageFromClasspath(ArtistBuilder.IMAGE_PATH)));
    }

    @Test
    void updatePaintingShouldReturnUpdatedPainting() throws Exception {
        final String newPath = "img/painting/dnepr.jpg";

        wireMockGrpcService.stubFor(
            method("UpdatePainting")
                .willReturn(message(
                    PaintingResponse.newBuilder()
                        .setPainting(
                            paintingBuilder.withId(PAINTING_ID.toString())
                                .withTitle(PAINTING_TITLE + " edited")
                                .withDescription(PAINTING_DESCRIPTION + " edited")
                                .withContent(newPath)
                                .withArtist(artistBuilder.withDefaults().build())
                                .withMuseum(museumBuilder.withDefaults().build())
                                .build()
                        )
                        .build()
                ))
        );

        PaintingJson paintingJson = new PaintingJson(
            PAINTING_ID,
            PAINTING_TITLE,
            PAINTING_DESCRIPTION,
            ImageUtil.getEncodedImageFromClasspath(PaintingBuilder.IMAGE_PATH),
            new MuseumJson(
                MUSEUM_ID,
                MUSEUM_TITLE,
                MUSEUM_DESCRIPTION,
                ImageUtil.getEncodedImageFromClasspath(MuseumBuilder.IMAGE_PATH),
                new GeoLocationJson(
                    CITY,
                    new CountryJson(
                        UUID.fromString(COUNTRY_ID),
                        COUNTRY_NAME
                    )
                )
            ),
            new ArtistJson(
                ARTIST_ID,
                ARTIST_NAME,
                ARTIST_BIO,
                ImageUtil.getEncodedImageFromClasspath(ArtistBuilder.IMAGE_PATH)
            )
        );

        mockMvc.perform(patch("/api/painting")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(paintingJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(PAINTING_ID.toString()))
            .andExpect(jsonPath("$.title").value(PAINTING_TITLE + " edited"))
            .andExpect(jsonPath("$.description").value(PAINTING_DESCRIPTION + " edited"))
            .andExpect(jsonPath("$.content").value(ImageUtil.getEncodedImageFromClasspath(newPath)))
            .andExpect(jsonPath("$.museum.id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.museum.title").value(MUSEUM_TITLE))
            .andExpect(jsonPath("$.museum.description").value(MUSEUM_DESCRIPTION))
            .andExpect(jsonPath("$.museum.photo").value(ImageUtil.getEncodedImageFromClasspath(MuseumBuilder.IMAGE_PATH)))
            .andExpect(jsonPath("$.museum.geo.city").value(CITY))
            .andExpect(jsonPath("$.museum.geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.museum.geo.country.id").value(COUNTRY_ID))
            .andExpect(jsonPath("$.artist.id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.artist.name").value(ARTIST_NAME))
            .andExpect(jsonPath("$.artist.biography").value(ARTIST_BIO))
            .andExpect(jsonPath("$.artist.photo").value(ImageUtil.getEncodedImageFromClasspath(ArtistBuilder.IMAGE_PATH)));
    }
}
