package org.rococo.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rococo.grpc.RococoUserdataServiceGrpc;
import org.rococo.grpc.User;
import org.rococo.grpc.UserResponse;
import org.rococo.model.UserJson;
import org.rococo.utils.ImageUtil;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

public class UserdataControllerTest extends BaseControllerTest{

    private static final String IMAGE_PATH = "img/profile/profilePic.jpg";
    private static final int WIREMOCK_PORT = 8802;

    private final UUID userId = UUID.randomUUID();
    private final String username = "mariamur";
    private final String firstName = "Maria";
    private final String lastName = "Murashkina";

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
            RococoUserdataServiceGrpc.SERVICE_NAME
        );
    }

    @Test
    void getCurrentShouldReturnCorrectUser() throws Exception {
        wireMockGrpcService.stubFor(
            method("GetCurrent")
                .willReturn(message(
                    UserResponse.newBuilder()
                        .setUser(
                            User.newBuilder()
                                .setId(userId.toString())
                                .setUsername(username)
                                .setFirstname(firstName)
                                .setLastname(lastName)
                                .setAvatar(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                                .build()
                        )
                        .build()
                ))
        );

        mockMvc.perform(get("/api/user")
                .with(jwt().jwt(c -> c.claim("sub", username)))
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.firstname").value(firstName))
            .andExpect(jsonPath("$.lastname").value(lastName))
            .andExpect(jsonPath("$.avatar").value(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH)));
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws Exception {
        String newFirstName = "newFirstName";
        String newLastName = "newLastName";
        String newAvatar = "img/profile/madonna.jpg";

        wireMockGrpcService.stubFor(
            method("UpdateUser")
                .willReturn(message(
                    UserResponse.newBuilder()
                        .setUser(
                            User.newBuilder()
                                .setId(userId.toString())
                                .setUsername(username)
                                .setFirstname(newFirstName)
                                .setLastname(newLastName)
                                .setAvatar(ImageUtil.getEncodedImageFromClasspath(newAvatar))
                                .build()
                        )
                        .build()
                ))
        );

        final UserJson userJson = new UserJson(
            userId,
            username,
            newFirstName,
            newLastName,
            ImageUtil.getEncodedImageFromClasspath(newAvatar)
        );

        mockMvc.perform(patch("/api/user")
                .with(jwt().jwt(c -> c.claim("sub", username)))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(userJson)
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.firstname").value(newFirstName))
            .andExpect(jsonPath("$.lastname").value(newLastName))
            .andExpect(jsonPath("$.avatar").value(ImageUtil.getEncodedImageFromClasspath(newAvatar)));
    }

    @Test
    void updateUserShouldReturnForbiddenIfUsernameIsDifferent() throws Exception {
        String invalidUsername = "maria";
        String newFirstName = "newFirstName";
        String newLastName = "newLastName";
        String newAvatar = "img/profile/madonna.jpg";

        wireMockGrpcService.stubFor(
            method("UpdateUser")
                .willReturn(message(
                    UserResponse.newBuilder()
                        .setUser(
                            User.newBuilder()
                                .setId(userId.toString())
                                .setUsername(username)
                                .setFirstname(newFirstName)
                                .setLastname(newLastName)
                                .setAvatar(ImageUtil.getEncodedImageFromClasspath(newAvatar))
                                .build()
                        )
                        .build()
                ))
        );

        final UserJson userJson = new UserJson(
            userId,
            username,
            newFirstName,
            newLastName,
            ImageUtil.getEncodedImageFromClasspath(newAvatar)
        );

        mockMvc.perform(patch("/api/user")
                .with(jwt().jwt(c -> c.claim("sub", invalidUsername)))
                .contentType(APPLICATION_JSON)
                .content(
                    om.writeValueAsString(userJson)
                )
            )
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Access to another user is forbidden"));
    }
}
