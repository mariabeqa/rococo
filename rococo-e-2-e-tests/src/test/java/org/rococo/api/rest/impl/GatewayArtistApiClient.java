package org.rococo.api.rest.impl;

import io.qameta.allure.Step;
import org.rococo.api.rest.GatewayApi;
import org.rococo.api.rest.core.RestClient;
import org.rococo.model.ArtistJson;
import org.rococo.model.pageable.RestResponsePage;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class GatewayArtistApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayArtistApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send POST request /api/artist to rococo-gateway")
    @Nonnull
    public Response<ArtistJson> addArtist(String token, ArtistJson artistJson) {
        final Response<ArtistJson> response;

        try {
            response = gatewayApi.addArtist(token, artistJson)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send PATCH request /api/artist to rococo-gateway")
    @Nonnull
    public Response<ArtistJson> updateArtist(String token, ArtistJson artistJson) {
        final Response<ArtistJson> response;

        try {
            response = gatewayApi.updateArtist(token, artistJson)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/artist/{id} to rococo-gateway")
    @Nonnull
    public Response<ArtistJson> getArtistById(String token, String id) {
        final Response<ArtistJson> response;

        try {
            response = gatewayApi.findArtistById(token, id)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/artist to rococo-gateway")
    @Nonnull
    public Response<RestResponsePage<ArtistJson>> allArtists(String token,
                                                             int page,
                                                             int size,
                                                             @Nullable String name) {
        final Response<RestResponsePage<ArtistJson>> response;
        try {
            response = gatewayApi.getArtists(
                    token,
                    page,
                    size,
                    name)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send DELETE request /api/artist/ to rococo-gateway")
    @Nonnull
    public Response<Void> deleteArtist(String token, String artistId) {
        final Response<Void> response;
        try {
            response = gatewayApi.deleteArtist(token, artistId)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }
        return response;
    }
}
