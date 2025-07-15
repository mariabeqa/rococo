package org.rococo.service.api;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.*;
import org.rococo.model.ArtistJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.rococo.model.ArtistJson.fromGrpc;

@Component
public class ArtistGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistGrpcClient.class.getName());

    @GrpcClient("grpcArtistClient")
    private RococoArtistsServiceGrpc.RococoArtistsServiceBlockingStub artistClient;

    public @Nonnull Page<ArtistJson> getAll(@Nullable String title,
                                            @Nonnull Pageable pageable) {
        ArtistsPageRequest request = ArtistsPageRequest.newBuilder()
            .setTitle(title != null ? title : "")
            .setPage(pageable.getPageNumber())
            .setSize(pageable.getPageSize())
            .build();

        ArtistsPageResponse response = artistClient.getAll(request);
        List<ArtistJson> artists = response.getArtistsList()
            .stream()
            .map(ArtistJson::fromGrpc)
            .toList();

        return new PageImpl<>(artists, PageRequest.of(request.getPage(), request.getSize()), response.getTotalElements());
    }

    public @Nonnull ArtistJson findArtistById(@Nonnull String artistId) {
        ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
            .setArtistId(artistId)
            .build();
        return fromGrpc(artistClient.findArtistById(request).getArtist());
    }

    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        ArtistRequest request = ArtistRequest.newBuilder()
            .setArtist(
                Artist.newBuilder()
                    .setId(artist.id().toString())
                    .setName(artist.name())
                    .setBio(artist.biography())
                    .setPhoto(artist.photo())
                    .build()
            )
            .build();
        return fromGrpc(artistClient.updateArtist(request).getArtist());
    }

    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artist) {
        ArtistRequest request = ArtistRequest.newBuilder()
            .setArtist(
                Artist.newBuilder()
                    .setName(artist.name())
                    .setBio(artist.biography())
                    .setPhoto(artist.photo())
                    .build()
            )
            .build();
        return fromGrpc(artistClient.addArtist(request).getArtist());
    }

    public void deleteArtist(@Nonnull String artistId) {
        ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
            .setArtistId(artistId)
            .build();
        artistClient.deleteArtist(request);
    }
}
