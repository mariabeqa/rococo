package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.CountryByNameRequest;
import org.rococo.grpc.RococoCountriesServiceGrpc;
import org.rococo.model.CountryJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.model.CountryJson.fromGrpc;

@ParametersAreNonnullByDefault
public class CountryGrpcClient extends GrpcClient{

    public CountryGrpcClient() {
        super(CFG.museumGrpcAddress(), CFG.museumGrpcPort());
    }

    private final RococoCountriesServiceGrpc.RococoCountriesServiceBlockingStub countryStub =
            RococoCountriesServiceGrpc.newBlockingStub(channel);

    @Step("Get country by Name: {countryName} with rococo-museum Grpc service")
    public @Nonnull CountryJson getCountryByName(@Nonnull String countryName) {
        return fromGrpc(
                countryStub.findCountryByName(
                        CountryByNameRequest.newBuilder()
                                .setName(countryName)
                                .build()
                ).getCountry()
        );
    }
}
