package org.rococo.api.rest.impl;

import io.qameta.allure.Step;
import org.rococo.api.rest.GatewayApi;
import org.rococo.api.rest.core.RestClient;
import org.rococo.model.UserJson;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayUserApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayUserApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send GET request /api/user to rococo-gateway")
    @Nonnull
    public UserJson getCurrent(String token) {
        final Response<UserJson> response;

        try {
            response = gatewayApi.getCurrentUser(token)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }
        assertEquals(200, response.code());

        assert response.body() != null;
        return response.body();
    }

    @Step("Send PATCH request /api/user to rococo-gateway")
    @Nonnull
    public UserJson updateUser(String token, UserJson user) {
        final Response<UserJson> response;

        try {
            response = gatewayApi.updateUser(token, user)
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }
        assertEquals(200, response.code());

        assert response.body() != null;
        return response.body();
    }

}
