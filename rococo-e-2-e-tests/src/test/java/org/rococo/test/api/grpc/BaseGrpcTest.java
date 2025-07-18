package org.rococo.test.api.grpc;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.rococo.api.grpc.ArtistGrpcClient;
import org.rococo.api.grpc.CountryGrpcClient;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.api.grpc.PaintingGrpcClient;
import org.rococo.jupiter.annotation.meta.GrpcTest;
import org.rococo.jupiter.extension.ApiLoginExtension;

import java.util.UUID;

import static org.rococo.model.Countries.AUSTRALIA;
import static org.rococo.model.Countries.RUSSIA;

@GrpcTest
public class BaseGrpcTest {

    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    protected final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();
    protected final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    protected final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();
    protected final CountryGrpcClient countryGrpcClient = new CountryGrpcClient();
    protected final UUID RUSSIA_COUNTRY_ID = countryGrpcClient.getCountryByName(RUSSIA.getName()).id();
    protected final UUID AUSTRALIA_COUNTRY_ID = countryGrpcClient.getCountryByName(AUSTRALIA.getName()).id();
}
