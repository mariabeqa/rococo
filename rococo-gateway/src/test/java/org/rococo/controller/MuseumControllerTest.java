package org.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rococo.grpc.MuseumResponse;
import org.rococo.grpc.MuseumsPageResponse;
import org.rococo.grpc.RococoMuseumsServiceGrpc;
import org.rococo.model.CountryJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;
import org.rococo.utils.ImageUtil;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.List;
import java.util.UUID;

import static org.rococo.utils.grpc.MuseumBuilder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

public class MuseumControllerTest extends BaseControllerTest{

    private static final int WIREMOCK_PORT = 8806;

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
            RococoMuseumsServiceGrpc.SERVICE_NAME
        );
    }

    @Test
    void getAllShouldReturnAllMuseums() throws Exception {
        final String secondMuseumId = UUID.randomUUID().toString();
        final String museumTitle = "Эрмитаж";
        final String museumDescription = "Российский государственный художественный и культурно-исторический музей в Санкт-Петербурге";

        wireMockGrpcService
            .stubFor(
                method("GetAll")
                    .willReturn(message(
                        MuseumsPageResponse.newBuilder()
                            .addAllMuseums(
                                List.of(
                                    museumBuilder.withDefaults().build(),
                                    museumBuilder
                                        .withId(secondMuseumId)
                                        .withTitle(museumTitle)
                                        .withDescription(museumDescription)
                                        .withPhoto("")
                                        .build()
                                )
                            )
                            .build()
                    ))
            );

        mockMvc.perform(get("/api/museum")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content.[0].id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.content.[1].id").value(secondMuseumId))
            .andExpect(jsonPath("$.content.[0].title").value(MUSEUM_TITLE))
            .andExpect(jsonPath("$.content.[1].title").value(museumTitle))
            .andExpect(jsonPath("$.content.[0].description").value(MUSEUM_DESCRIPTION))
            .andExpect(jsonPath("$.content.[1].description").value(museumDescription))
            .andExpect(jsonPath("$.content.[0].photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)))
            .andExpect(jsonPath("$.content.[1].photo").value(""))
            .andExpect(jsonPath("$.content.[0].geo.city").value(CITY))
            .andExpect(jsonPath("$.content.[1].geo.city").value(CITY))
            .andExpect(jsonPath("$.content.[0].geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.content.[1].geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.content.[0].geo.country.id").value(COUNTRY_ID))
            .andExpect(jsonPath("$.content.[1].geo.country.id").value(COUNTRY_ID))
            .andExpect(jsonPath("$.numberOfElements").value(2))
            .andDo(print());
    }

    @Test
    void findMuseumByIdShouldReturnCorrectMuseum() throws Exception {

        wireMockGrpcService.stubFor(
            method("FindMuseumById")
                .willReturn(message(
                    MuseumResponse.newBuilder()
                        .setMuseum(museumBuilder.withDefaults().build())
                        .build()
                ))
        );

        mockMvc.perform(get("/api/museum/" + MUSEUM_ID)
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.title").value(MUSEUM_TITLE))
            .andExpect(jsonPath("$.description").value(MUSEUM_DESCRIPTION))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)))
            .andExpect(jsonPath("$.geo.city").value(CITY))
            .andExpect(jsonPath("$.geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.geo.country.id").value(COUNTRY_ID));
    }

    @Test
    void addMuseumShouldReturnCreatedMuseum() throws Exception {

        wireMockGrpcService.stubFor(
            method("AddMuseum")
                .willReturn(message(
                    MuseumResponse.newBuilder()
                        .setMuseum(museumBuilder.withDefaults().build())
                        .build()
                ))
        );

        MuseumJson museumJson = new MuseumJson(
            null,
            MUSEUM_TITLE,
            MUSEUM_DESCRIPTION,
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    UUID.fromString(COUNTRY_ID),
                    COUNTRY_NAME
                )
            )
        );

        mockMvc.perform(post("/api/museum")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(museumJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.title").value(MUSEUM_TITLE))
            .andExpect(jsonPath("$.description").value(MUSEUM_DESCRIPTION))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)))
            .andExpect(jsonPath("$.geo.city").value(CITY))
            .andExpect(jsonPath("$.geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.geo.country.id").value(COUNTRY_ID));
    }

    @Test
    void updateMuseumShouldReturnUpdatedMuseum() throws Exception {
        final String newPath = "img/artist/kuindzhi_new.jpg";

        wireMockGrpcService.stubFor(
            method("UpdateMuseum")
                .willReturn(message(
                    MuseumResponse.newBuilder()
                        .setMuseum(
                            museumBuilder
                                .withId(String.valueOf(MUSEUM_ID))
                                .withTitle(MUSEUM_TITLE + " edited")
                                .withDescription(MUSEUM_DESCRIPTION + " edited")
                                .withPhoto(ImageUtil.getEncodedImageFromClasspath(newPath))
                                .withDefaultGeo()
                                .build()
                        )
                        .build()
                ))
        );

        MuseumJson museumJson = new MuseumJson(
            MUSEUM_ID,
            MUSEUM_TITLE,
            MUSEUM_DESCRIPTION,
            ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    UUID.fromString(COUNTRY_ID),
                    COUNTRY_NAME
                )
            )
        );

        mockMvc.perform(patch("/api/museum")
                .with(jwt().jwt(c -> c.claim("sub", "maria")))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(museumJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(String.valueOf(MUSEUM_ID)))
            .andExpect(jsonPath("$.title").value(MUSEUM_TITLE + " edited"))
            .andExpect(jsonPath("$.description").value(MUSEUM_DESCRIPTION + " edited"))
            .andExpect(jsonPath("$.photo").value(ImageUtil.getEncodedImageFromClasspath(newPath)))
            .andExpect(jsonPath("$.geo.city").value(CITY))
            .andExpect(jsonPath("$.geo.country.name").value(COUNTRY_NAME))
            .andExpect(jsonPath("$.geo.country.id").value(COUNTRY_ID));
    }
}
