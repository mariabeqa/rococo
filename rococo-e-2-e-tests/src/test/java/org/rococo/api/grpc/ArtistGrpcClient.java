package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.ArtistJson;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.model.ArtistJson.fromGrpc;

@ParametersAreNonnullByDefault
public class ArtistGrpcClient extends GrpcClient{

    public ArtistGrpcClient() {
        super(CFG.artistGrpcAddress(), CFG.artistGrpcPort());
    }

    private final RococoArtistsServiceGrpc.RococoArtistsServiceBlockingStub artistStub =
            RococoArtistsServiceGrpc.newBlockingStub(channel);

    @Step("Add new artist with Artist Grpc service")
    public ArtistJson addArtist(ArtistJson artistJson) {
        return fromGrpc(
                artistStub.addArtist(
                                ArtistRequest.newBuilder()
                                        .setArtist(
                                                Artist.newBuilder()
                                                        .setName(artistJson.name())
                                                        .setBio(artistJson.biography())
                                                        .setPhoto(artistJson.photo())
                                                        .build()
                                        )
                                        .build()
                        )
                        .getArtist()
        );
    }

    @Step("Delete artist with Artist Grpc service")
    public void deleteArtist(ArtistJson artistJson) {
        artistStub.deleteArtist(
                ArtistRequest.newBuilder()
                        .setArtist(
                                Artist.newBuilder()
                                        .setId(artistJson.id().toString())
                                        .setName(artistJson.name())
                                        .setBio(artistJson.biography())
                                        .setPhoto(artistJson.photo())
                                        .build()
                        )
                        .build()
        );
    }
}
