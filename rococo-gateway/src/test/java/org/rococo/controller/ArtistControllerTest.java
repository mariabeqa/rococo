package org.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rococo.grpc.ArtistResponse;
import org.rococo.grpc.ArtistsPageResponse;
import org.rococo.grpc.RococoArtistsServiceGrpc;
import org.rococo.model.ArtistJson;
import org.rococo.utils.ImageUtil;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.List;
import java.util.UUID;

import static org.rococo.utils.grpc.ArtistBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

public class ArtistControllerTest extends BaseControllerTest{

    private static final int WIREMOCK_PORT = 8808;

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
            RococoArtistsServiceGrpc.SERVICE_NAME
        );
    }

    @Test
    void getAllShouldReturnAllArtists() throws Exception {
        final String secondArtistId = UUID.randomUUID().toString();
        final String artistName = "Иван Шишкин";
        final String artistBio = "Биография Ивана Шишкина";
        final String imgPath = "img/artist/malevich.jpg";

        wireMockGrpcService
            .stubFor(
                method("GetAll")
                    .willReturn(message(
                        ArtistsPageResponse.newBuilder()
                            .addAllArtists(
                                List.of(
                                    artistBuilder.withDefaults().build(),
                                    artistBuilder
                                        .withId(secondArtistId)
                                        .withName(artistName)
                                        .withBio(artistBio)
                                        .withPhoto(imgPath)
                                        .build()
                                )
                            )
                            .build()
                    ))
            );

        mockMvc.perform(get("/api/artist")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content.[0].id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.content.[1].id").value(secondArtistId))
            .andExpect(jsonPath("$.content.[0].name").value(ARTIST_NAME))
            .andExpect(jsonPath("$.content.[1].name").value(artistName))
            .andExpect(jsonPath("$.content.[0].biography").value(ARTIST_BIO))
            .andExpect(jsonPath("$.content.[1].biography").value(artistBio))
            .andExpect(jsonPath("$.content.[0].photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)))
            .andExpect(jsonPath("$.content.[1].photo").value(ImageUtil.getEncodedImageFromClasspath(imgPath)))
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andDo(print());
    }

    @Test
    void findArtistByIdShouldReturnCorrectArtist() throws Exception {

        wireMockGrpcService.stubFor(
            method("FindArtistById")
                .willReturn(message(
                    ArtistResponse.newBuilder()
                        .setArtist(
                            artistBuilder.withDefaults().build()
                        )
                        .build()
                ))
        );

        mockMvc.perform(get("/api/artist/" + ARTIST_ID)
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.name").value(ARTIST_NAME))
            .andExpect(jsonPath("$.biography").value(ARTIST_BIO))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)));
    }

    @Test
    void addArtistShouldReturnCreatedArtist() throws Exception {

        wireMockGrpcService.stubFor(
            method("AddArtist")
                .willReturn(message(
                    ArtistResponse.newBuilder()
                        .setArtist(
                            artistBuilder.withDefaults().build()
                        )
                        .build()
                ))
        );

        ArtistJson artistJson = new ArtistJson(
            null,
            ARTIST_NAME,
            ARTIST_BIO,
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)
        );

        mockMvc.perform(post("/api/artist")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(artistJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.name").value(ARTIST_NAME))
            .andExpect(jsonPath("$.biography").value(ARTIST_BIO))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)));
    }

    @Test
    void updateArtistShouldReturnUpdatedArtist() throws Exception {
        final String newArtistName = "Архип Иванович Куинджи";
        final String newArtistBio = "Биография Архипа Ивановича Куинджи";
        final String newPath = "img/artist/kuindzhi_new.jpg";

        wireMockGrpcService.stubFor(
            method("UpdateArtist")
                .willReturn(message(
                    ArtistResponse.newBuilder()
                        .setArtist(
                            artistBuilder
                                .withId(ARTIST_ID.toString())
                                .withName(newArtistName)
                                .withBio(newArtistBio)
                                .withPhoto(newPath)
                                .build()
                        )
                        .build()
                ))
        );

        ArtistJson artistJson = new ArtistJson(
            ARTIST_ID,
            newArtistName,
            newArtistBio,
            ImageUtil.getEncodedImageFromClasspath(newPath)
        );

        mockMvc.perform(patch("/api/artist")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(artistJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ARTIST_ID.toString()))
            .andExpect(jsonPath("$.name").value(newArtistName))
            .andExpect(jsonPath("$.biography").value(newArtistBio))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(newPath)));
    }

}
