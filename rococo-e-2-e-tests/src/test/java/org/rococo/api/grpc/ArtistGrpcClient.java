package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.ArtistJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.rococo.model.ArtistJson.fromGrpc;

@ParametersAreNonnullByDefault
public class ArtistGrpcClient extends GrpcClient {

    public ArtistGrpcClient() {
        super(CFG.artistGrpcAddress(), CFG.artistGrpcPort());
    }

    private final RococoArtistsServiceGrpc.RococoArtistsServiceBlockingStub artistStub =
        RococoArtistsServiceGrpc.newBlockingStub(channel);

    @Step("Add new artist with Artist Grpc service")
    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artistJson) {
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

    @Step("Update an artist with Artist Grpc service")
    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artistJson) {
        return fromGrpc(
            artistStub.updateArtist(
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
                )
                .getArtist()
        );
    }

    @Step("Find artist by ID with Artist Grpc service")
    public @Nonnull ArtistJson getArtist(@Nonnull String artistId) {
        return ArtistJson.fromGrpc(
            artistStub.findArtistById(
                    ArtistByIdRequest.newBuilder()
                        .setArtistId(artistId)
                        .build()
                )
                .getArtist()
        );
    }

    @Step("Get all artists with rococo-artist Grpc service")
    public @Nonnull List<ArtistJson> getAll(int page, int size, @Nullable String name) {
        List<Artist> artistList = artistStub.getAll(
                ArtistsPageRequest.newBuilder()
                    .setPage(page)
                    .setSize(size)
                    .setTitle(name == null ? "" : name)
                    .build()
            )
            .getArtistsList();

        return artistList
            .stream()
            .map(ArtistJson::fromGrpc)
            .toList();
    }

    @Step("Delete artist with Artist Grpc service")
    public void deleteArtist(@Nullable String artistId) {
        artistStub.deleteArtist(
            ArtistByIdRequest.newBuilder()
                .setArtistId(artistId)
                .build()
        );
    }
}
