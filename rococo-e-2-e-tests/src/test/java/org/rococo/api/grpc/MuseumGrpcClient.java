package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.MuseumJson;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.model.MuseumJson.fromGrpc;

@ParametersAreNonnullByDefault
public class MuseumGrpcClient extends GrpcClient {

    public MuseumGrpcClient() {
        super(CFG.museumGrpcAddress(), CFG.museumGrpcPort());
    }

    private final RococoMuseumsServiceGrpc.RococoMuseumsServiceBlockingStub museumStub =
            RococoMuseumsServiceGrpc.newBlockingStub(channel);

    @Step("Add new museum with Museum Grpc service")
    public MuseumJson addMuseum(MuseumJson museumJson) {
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

    @Step("Delete museum with Museum Grpc service")
    public void deleteMuseum(MuseumJson museumJson) {
        museumStub.deleteMuseum(
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
                        );
    }
}
