package org.rococo.service.api;

import org.rococo.grpc.*;
import org.rococo.model.MuseumJson;
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

import static org.rococo.model.MuseumJson.fromGrpc;

@Component
public class MuseumGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(MuseumGrpcClient.class);

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumsServiceGrpc.RococoMuseumsServiceBlockingStub museumsClient;

    public @Nonnull Page<MuseumJson> getAll(@Nullable String title,
                                            @Nonnull Pageable pageable) {
        try {
            MuseumsPageRequest request = MuseumsPageRequest.newBuilder()
                    .setTitle(title != null ? title : "")
                    .setPage(pageable.getPageNumber())
                    .setSize(pageable.getPageSize())
                    .build();

            MuseumsPageResponse response = museumsClient.getAll(request);
            List<MuseumJson> countries = response.getMuseumsList()
                    .stream()
                    .map(MuseumJson::fromGrpc)
                    .toList();

            return new PageImpl<>(countries, PageRequest.of(request.getPage(), request.getSize()), response.getTotalElements());

        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull MuseumJson findMuseumById(@Nonnull String museumId) {
        try {
            MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
                    .setMuseumId(museumId)
                    .build();
            return fromGrpc(museumsClient.findMuseumById(request).getMuseum());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        try {
            MuseumRequest request = MuseumRequest.newBuilder()
                    .setMuseum(
                            Museum.newBuilder()
                                    .setId(museum.id().toString())
                                    .setTitle(museum.title())
                                    .setDescription(museum.description())
                                    .setPhoto(museum.photo())
                                    .setGeo(GeoLocation.newBuilder()
                                            .setCity(museum.geo().city())
                                            .setCountry(Country.newBuilder()
                                                    .setId(museum.geo().country().id().toString())
                                                    .setName(museum.geo().country().name() == null ? "" :
                                                            museum.geo().country().name())
                                            )
                                    )
                                    .build()
                    )
                    .build();
            return fromGrpc(museumsClient.updateMuseum(request).getMuseum());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        try {
            MuseumRequest request = MuseumRequest.newBuilder()
                    .setMuseum(
                            Museum.newBuilder()
                                    .setTitle(museum.title())
                                    .setDescription(museum.description())
                                    .setPhoto(museum.photo())
                                    .setGeo(
                                            GeoLocation.newBuilder()
                                                    .setCity(museum.geo().city())
                                                    .setCountry(
                                                            Country.newBuilder()
                                                                    .setId(museum.geo().country().id().toString())
                                                                    .setName(museum.geo().country().name() == null ? "" :
                                                                            museum.geo().country().name())
                                                    )
                                    )
                                    .build()
                    )
                    .build();
            return fromGrpc(museumsClient.addMuseum(request).getMuseum());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
