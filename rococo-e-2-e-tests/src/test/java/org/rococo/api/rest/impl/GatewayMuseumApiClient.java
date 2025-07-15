package org.rococo.api.rest.impl;

import io.qameta.allure.Step;
import org.rococo.api.rest.GatewayApi;
import org.rococo.api.rest.core.RestClient;
import org.rococo.model.MuseumJson;
import org.rococo.model.pageable.RestResponsePage;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class GatewayMuseumApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayMuseumApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send POST request /api/museum to rococo-gateway")
    @Nonnull
    public Response<MuseumJson> addMuseum(String token, MuseumJson museum) {
        final Response<MuseumJson> response;

        try {
            response = gatewayApi.addMuseum(token, museum)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send PATCH request /api/museum to rococo-gateway")
    @Nonnull
    public Response<MuseumJson> updateMuseum(String token, MuseumJson museum) {
        final Response<MuseumJson> response;

        try {
            response = gatewayApi.updateMuseum(token, museum)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/museum/{id} to rococo-gateway")
    @Nonnull
    public Response<MuseumJson> getMuseumById(String token, String id) {
        final Response<MuseumJson> response;

        try {
            response = gatewayApi.findMuseumById(token, id)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/museum/ to rococo-gateway")
    @Nonnull
    public Response<RestResponsePage<MuseumJson>> allMuseums(String token,
                                                   int page,
                                                   int size,
                                                   @Nullable String title) {
        final Response<RestResponsePage<MuseumJson>> response;
        try {
            response = gatewayApi.getMuseums(token,
                    page,
                    size,
                    title)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send DELETE request /api/museum/{id} to rococo-gateway")
    @Nonnull
    public Response<Void> deleteMuseum(String token, String museumId) {
        final Response<Void> response;
        try {
            response = gatewayApi.deleteMuseum(token, museumId)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }
        return response;
    }
}
