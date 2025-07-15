package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.rococo.model.PaintingJson.fromGrpc;


@ParametersAreNonnullByDefault
public class PaintingGrpcClient extends GrpcClient {

    public PaintingGrpcClient() {
        super(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort());
    }

    private final RococoPaintingsServiceGrpc.RococoPaintingsServiceBlockingStub paintingStub =
        RococoPaintingsServiceGrpc.newBlockingStub(channel);

    @Step("Add new painting with Painting Grpc service")
    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson paintingJson) {
        return fromGrpc(
            paintingStub.addPainting(
                    AddPaintingRequest.newBuilder()
                        .setTitle(paintingJson.title())
                        .setDescription(paintingJson.description())
                        .setContent(paintingJson.content())
                        .setArtistId(
                            ArtistId.newBuilder()
                                .setId(paintingJson.artist().id().toString())
                                .build()
                        )
                        .setMuseumId(
                            MuseumId.newBuilder()
                                .setId(paintingJson.museum().id().toString())
                                .build()
                        )
                        .build()
                )
                .getPainting()
        );
    }

    @Step("Update painting with Painting Grpc service")
    public @Nonnull PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        return fromGrpc(
            paintingStub.updatePainting(
                    PaintingRequest.newBuilder()
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
                        .build()
                )
                .getPainting()
        );
    }

    public @Nonnull PaintingJson findPaintingById(@Nonnull String paintingId) {
        PaintingByIdRequest request = PaintingByIdRequest.newBuilder()
            .setPaintingId(paintingId)
            .build();
        return PaintingJson.fromGrpc(paintingStub.findPaintingById(request).getPainting());
    }

    public @Nonnull List<PaintingJson> findPaintingByAuthorId(@Nonnull String authorId,
                                                              int page,
                                                              int size) {
        PaintingByAuthorIdPageRequest request = PaintingByAuthorIdPageRequest.newBuilder()
            .setAuthorId(authorId)
            .setPage(page)
            .setSize(size)
            .build();
        List<Painting> paintingsList = paintingStub.findPaintingByAuthorId(request)
            .getPaintingsList();

        return paintingsList
            .stream()
            .map(PaintingJson::fromGrpc)
            .toList();
    }

    public @Nonnull List<PaintingJson> allPaintings(int page, int size,
                                                    @Nullable String title) {
        PaintingsPageRequest request = PaintingsPageRequest.newBuilder()
            .setTitle(title != null ? title : "")
            .setPage(page)
            .setSize(size)
            .build();

        List<Painting> paintingsList = paintingStub.getAll(request).getPaintingsList();
        return paintingsList
            .stream()
            .map(PaintingJson::fromGrpc)
            .toList();
    }

    @Step("Delete painting with Painting Grpc service")
    public void deletePainting(@Nullable String paintingId) {
        paintingStub.deletePainting(
            PaintingByIdRequest.newBuilder()
                .setPaintingId(paintingId)
                .build()
        );
    }
}
