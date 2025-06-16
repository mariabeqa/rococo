package org.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.rococo.grpc.Museum;

import java.util.UUID;

public record MuseumJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("photo")
    String photo,
    @JsonProperty("geo")
    GeoLocationJson geo) {

    public static @Nonnull MuseumJson fromGrpc(@Nonnull Museum museum) {
        return new MuseumJson(
                UUID.fromString(museum.getId()),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto(),
                new GeoLocationJson(
                        museum.getGeo().getCity(),
                        new CountryJson(
                                UUID.fromString(museum.getGeo().getCountry().getId()),
                                museum.getGeo().getCountry().getName()
                        )
                )
        );
    }
}
