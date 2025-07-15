package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.MuseumJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;

import static org.rococo.model.MuseumJson.fromGrpc;

@ParametersAreNonnullByDefault
public class MuseumGrpcClient extends GrpcClient {

    public MuseumGrpcClient() {
        super(CFG.museumGrpcAddress(), CFG.museumGrpcPort());
    }

    private final RococoMuseumsServiceGrpc.RococoMuseumsServiceBlockingStub museumStub =
        RococoMuseumsServiceGrpc.newBlockingStub(channel);

    @Step("Add new museum with Museum Grpc service")
    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museumJson) {
        return fromGrpc(
            museumStub.addMuseum(
                    MuseumRequest.newBuilder()
                        .setMuseum(
                            Museum.newBuilder()
                                .setTitle(museumJson.title())
                                .setDescription(museumJson.description())
                                .setPhoto(museumJson.photo())
                                .setGeo(
                                    GeoLocation.newBuilder()
                                        .setCity(museumJson.geo().city())
                                        .setCountry(
                                            Country.newBuilder()
                                                .setId(museumJson.geo().country().id().toString())
                                                .setName(museumJson.geo().country().name())
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .getMuseum()
        );
    }

    @Step("Update museum with rococo-museum Grpc service")
    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museumJson) {
        return fromGrpc(
            museumStub.updateMuseum(
                    MuseumRequest.newBuilder()
                        .setMuseum(
                            Museum.newBuilder()
                                .setId(museumJson.id().toString())
                                .setTitle(museumJson.title())
                                .setDescription(museumJson.description())
                                .setPhoto(museumJson.photo())
                                .setGeo(
                                    GeoLocation.newBuilder()
                                        .setCity(museumJson.geo().city())
                                        .setCountry(
                                            Country.newBuilder()
                                                .setId(museumJson.geo().country().id().toString())
                                                .setName(museumJson.geo().country().name())
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .getMuseum()
        );
    }

    @Step("Get museum by ID with rococo-museum Grpc service")
    public @Nonnull MuseumJson getMuseum(@Nonnull String museumId) {
        return fromGrpc(
            museumStub.findMuseumById(
                    MuseumByIdRequest.newBuilder()
                        .setMuseumId(museumId)
                        .build()
                )
                .getMuseum()
        );
    }

    @Step("Get all museums with rococo-museum Grpc service")
    public @Nonnull List<MuseumJson> getAll(int page, int size, @Nullable String title) {

        List<Museum> museumsList = museumStub.getAll(
                MuseumsPageRequest.newBuilder()
                    .setPage(page)
                    .setSize(size)
                    .setTitle(title == null ? "" : title)
                    .build()
            )
            .getMuseumsList();

        return museumsList
            .stream()
            .map(MuseumJson::fromGrpc)
            .toList();
    }

    @Step("Delete museum with rococo-museum Grpc service")
    public void deleteMuseum(@Nullable String museumId) {
        museumStub.deleteMuseum(
            MuseumByIdRequest.newBuilder()
                .setMuseumId(museumId)
                .build()
        );
    }
}
