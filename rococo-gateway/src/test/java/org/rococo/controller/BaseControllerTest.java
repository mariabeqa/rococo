package org.rococo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.rococo.utils.grpc.ArtistBuilder;
import org.rococo.utils.grpc.MuseumBuilder;
import org.rococo.utils.grpc.PaintingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseControllerTest {

    protected static WireMockServer wireMock;
    protected final ArtistBuilder artistBuilder = new ArtistBuilder();
    protected final MuseumBuilder museumBuilder = new MuseumBuilder();
    protected final PaintingBuilder paintingBuilder = new PaintingBuilder();
    protected WireMockGrpcService wireMockGrpcService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper om;

    @AfterEach
    void afterEach() {
        wireMock.resetAll();
        wireMockGrpcService.resetAll();
    }

    @AfterAll
    static void afterAll() {
        wireMock.stop();
    }
}
