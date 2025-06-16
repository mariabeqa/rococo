package org.rococo.service.api;

import org.rococo.grpc.*;
import org.rococo.model.PaintingJson;
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

import static org.rococo.model.PaintingJson.fromGrpc;

@Component
public class PaintingGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(PaintingGrpcClient.class);

    @GrpcClient("grpcPaintingClient")
    private RococoPaintingsServiceGrpc.RococoPaintingsServiceBlockingStub paintingsClient;

    public @Nonnull Page<PaintingJson> getAll(@Nullable String title,
                                              @Nonnull Pageable pageable) {
        try {
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

        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull PaintingJson findPaintingById(@Nonnull String paintingId) {
        try {
            PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
                    .setPaintingId(paintingId)
                    .build();
            return fromGrpc(paintingsClient.findPaintingById(request).getPainting());
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull Page<PaintingJson> findPaintingByAuthorId(@Nonnull String authorId,
                                                              @Nonnull Pageable pageable) {
        try {
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
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        try {
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
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson painting) {
        try {
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
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
