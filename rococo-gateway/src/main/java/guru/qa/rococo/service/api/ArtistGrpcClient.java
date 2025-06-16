package guru.qa.rococo.service.api;

import guru.qa.rococo.grpc.*;
import guru.qa.rococo.model.ArtistJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static guru.qa.rococo.model.ArtistJson.fromGrpc;

@Component
public class ArtistGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistGrpcClient.class.getName());

    @GrpcClient("grpcArtistClient")
    private RococoArtistsServiceGrpc.RococoArtistsServiceBlockingStub artistClient;

    public @Nonnull Page<ArtistJson> getAll(@Nullable String title,
                                            @Nonnull Pageable pageable) {
        try {
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

        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull ArtistJson findArtistById(@Nonnull String artistId) {
        try {
            ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
                    .setArtistId(artistId)
                    .build();
            return fromGrpc(artistClient.findArtistById(request).getArtist());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        try {
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
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artist) {
        try {
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
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
