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
public class MuseumGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(MuseumGrpcClient.class);

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumsServiceGrpc.RococoMuseumsServiceBlockingStub museumsClient;

    public @Nonnull Optional<MuseumResponse> findMuseumById(@Nonnull String museumId) {
        try {
            MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
                    .setMuseumId(museumId)
                    .build();
            return Optional.of(museumsClient.findMuseumById(request));
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
