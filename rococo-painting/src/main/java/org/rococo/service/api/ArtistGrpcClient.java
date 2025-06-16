package org.rococo.service.api;

import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class ArtistGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistGrpcClient.class);

    @GrpcClient("grpcArtistClient")
    private RococoArtistsServiceGrpc.RococoArtistsServiceBlockingStub artistClient;

    public @Nonnull Optional<ArtistResponse> findArtistById(@Nonnull String artistId) {
        try {
            ArtistByIdRequest request = ArtistByIdRequest.newBuilder()
                    .setArtistId(artistId)
                    .build();
            return Optional.of(artistClient.findArtistById(request));
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
