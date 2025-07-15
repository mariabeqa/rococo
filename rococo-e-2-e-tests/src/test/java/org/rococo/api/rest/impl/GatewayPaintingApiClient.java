package org.rococo.api.rest.impl;

import io.qameta.allure.Step;
import org.rococo.api.rest.GatewayApi;
import org.rococo.api.rest.core.RestClient;
import org.rococo.model.PaintingJson;
import org.rococo.model.pageable.RestResponsePage;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public class GatewayPaintingApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayPaintingApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Send POST request /api/painting to rococo-gateway")
    @Nonnull
    public Response<PaintingJson> addPainting(String token, PaintingJson painting) {
        final Response<PaintingJson> response;

        try {
            response = gatewayApi.addPainting(token, painting)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send PATCH request /api/painting to rococo-gateway")
    @Nonnull
    public Response<PaintingJson> updatePainting(String token, PaintingJson painting) {
        final Response<PaintingJson> response;

        try {
            response = gatewayApi.updatePainting(token, painting)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/painting/{id} to rococo-gateway")
    @Nonnull
    public Response<PaintingJson> getPaintingById(String token, String id) {
        final Response<PaintingJson> response;

        try {
            response = gatewayApi.findPaintingById(token, id)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/painting/{id} to rococo-gateway")
    @Nonnull
    public Response<RestResponsePage<PaintingJson>> getPaintingByAuthorId(String token,
                                                        int page,
                                                        int size,
                                                        String id) {
        final Response<RestResponsePage<PaintingJson>> response;

        try {
            response = gatewayApi.findPaintingByAuthorId(token, page, size, id)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send GET request /api/painting/ to rococo-gateway")
    @Nonnull
    public Response<RestResponsePage<PaintingJson>> allPaintings(String token,
                                                             int page,
                                                             int size,
                                                             @Nullable String title) {
        final Response<RestResponsePage<PaintingJson>> response;
        try {
            response = gatewayApi.getPaintings(
                    token,
                    page,
                    size,
                    title
                )
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }

        return response;
    }

    @Step("Send DELETE request /api/painting/{id} to rococo-gateway")
    @Nonnull
    public Response<Void> deletePainting(String token, String paintingId) {
        final Response<Void> response;
        try {
            response = gatewayApi.deletePainting(token, paintingId)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to call gateway API", e);
        }
        return response;
    }

}
