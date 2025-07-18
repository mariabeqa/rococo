package org.rococo.test.api.grpc;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.rococo.api.grpc.ArtistGrpcClient;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.api.grpc.PaintingGrpcClient;
import org.rococo.jupiter.annotation.meta.GrpcTest;
import org.rococo.jupiter.extension.ApiLoginExtension;


@GrpcTest
public class BaseGrpcTest {

    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    protected final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();
    protected final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    protected final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();
}
