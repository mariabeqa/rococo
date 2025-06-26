package org.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rococo.grpc.Country;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name) {

    public static @Nonnull CountryJson fromGrpc(@Nonnull Country country) {
        return new CountryJson(
                UUID.fromString(country.getId()),
                country.getName()
        );
    }
}
