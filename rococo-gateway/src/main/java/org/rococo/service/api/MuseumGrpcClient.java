package org.rococo.service.api;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.*;
import org.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.rococo.model.MuseumJson.fromGrpc;

@Component
public class MuseumGrpcClient {

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumsServiceGrpc.RococoMuseumsServiceBlockingStub museumsClient;

    public @Nonnull Page<MuseumJson> getAll(@Nullable String title,
                                            @Nonnull Pageable pageable) {
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
    }

    public @Nonnull MuseumJson findMuseumById(@Nonnull String museumId) {
        MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
            .setMuseumId(museumId)
            .build();
        return fromGrpc(museumsClient.findMuseumById(request).getMuseum());
    }

    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        MuseumRequest request = MuseumRequest.newBuilder()
            .setMuseum(
                Museum.newBuilder()
                    .setId(String.valueOf(museum.id()))
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
    }

    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
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
    }

    public void deleteMuseum(@Nonnull String museumId) {
        MuseumByIdRequest request = MuseumByIdRequest.newBuilder()
            .setMuseumId(museumId)
            .build();
        museumsClient.deleteMuseum(request);
    }
}
