package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.*;
import org.rococo.model.PaintingJson;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.model.PaintingJson.fromGrpc;


@ParametersAreNonnullByDefault
public class PaintingGrpcClient extends GrpcClient{

    public PaintingGrpcClient() {
        super(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort());
    }

    private final RococoPaintingsServiceGrpc.RococoPaintingsServiceBlockingStub paintingStub =
            RococoPaintingsServiceGrpc.newBlockingStub(channel);

    @Step("Add new painting with Painting Grpc service")
    public PaintingJson addPainting(PaintingJson paintingJson) {
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

    @Step("Add new painting with Painting Grpc service")
    public void deletePainting(PaintingJson paintingJson) {
        paintingStub.deletePainting(
                                DeletePaintingRequest.newBuilder()
                                        .setId(paintingJson.id().toString())
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
                        );
    }
}
