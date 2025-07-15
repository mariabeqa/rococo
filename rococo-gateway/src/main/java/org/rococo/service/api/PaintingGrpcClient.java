package org.rococo.service.api;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.*;
import org.rococo.model.PaintingJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.rococo.model.PaintingJson.fromGrpc;

@Component
public class PaintingGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(PaintingGrpcClient.class);
    private final ArtistGrpcClient artistGrpcClient;

    @GrpcClient("grpcPaintingClient")
    private RococoPaintingsServiceGrpc.RococoPaintingsServiceBlockingStub paintingsClient;

    public PaintingGrpcClient(ArtistGrpcClient artistGrpcClient) {
        this.artistGrpcClient = artistGrpcClient;
    }

    public @Nonnull Page<PaintingJson> getAll(@Nullable String title,
                                              @Nonnull Pageable pageable) {

        PaintingsPageRequest request = PaintingsPageRequest.newBuilder()
            .setTitle(title != null ? title : "")
            .setPage(pageable.getPageNumber())
            .setSize(pageable.getPageSize())
            .build();

        PaintingsPageResponse response = paintingsClient.getAll(request);
        List<PaintingJson> countries = response.getPaintingsList()
            .stream()
            .map(PaintingJson::fromGrpc)
            .toList();

        return new PageImpl<>(countries, PageRequest.of(request.getPage(), request.getSize()), response.getTotalElements());

    }

    public @Nonnull PaintingJson findPaintingById(@Nonnull String paintingId) {

        PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
            .setPaintingId(paintingId)
            .build();
        return fromGrpc(paintingsClient.findPaintingById(request).getPainting());

    }

    public @Nonnull Page<PaintingJson> findPaintingByAuthorId(@Nonnull String authorId,
                                                              @Nonnull Pageable pageable) {

        PaintingByAuthorIdPageRequest request = PaintingByAuthorIdPageRequest.newBuilder()
            .setAuthorId(authorId)
            .setPage(pageable.getPageNumber())
            .setSize(pageable.getPageSize())
            .build();
        PaintingsPageResponse response = paintingsClient.findPaintingByAuthorId(request);

        List<PaintingJson> paintings = response.getPaintingsList()
            .stream()
            .map(PaintingJson::fromGrpc)
            .toList();

        return new PageImpl<>(paintings, PageRequest.of(request.getPage(), request.getSize()), response.getTotalElements());

    }

    public @Nonnull PaintingJson updatePainting(@Nonnull PaintingJson painting) {

        PaintingRequest request = PaintingRequest.newBuilder()
            .setPainting(
                Painting.newBuilder()
                    .setId(painting.id().toString())
                    .setTitle(painting.title())
                    .setDescription(painting.description())
                    .setContent(painting.content())
                    .setMuseum(
                        Museum.newBuilder()
                            .setId(painting.museum().id().toString())
                            .build()
                    )
                    .setArtist(
                        Artist.newBuilder()
                            .setId(painting.artist().id().toString())
                            .build()
                    )
                    .build()
            )
            .build();
        return PaintingJson.fromGrpc(paintingsClient.updatePainting(request).getPainting());

    }

    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson painting) {

        AddPaintingRequest request = AddPaintingRequest.newBuilder()
            .setTitle(painting.title())
            .setDescription(painting.description())
            .setContent(painting.content())
            .setArtistId(
                ArtistId.newBuilder()
                    .setId(painting.artist().id().toString())
                    .build()
            )
            .setMuseumId(
                MuseumId.newBuilder()
                    .setId(painting.museum().id().toString())
                    .build()
            )
            .build();
        return PaintingJson.fromGrpc(paintingsClient.addPainting(request).getPainting());

    }

    public void deletePainting(@Nonnull String paintingId) {
        PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
            .setPaintingId(paintingId)
            .build();
        paintingsClient.deletePainting(request);
    }
}
