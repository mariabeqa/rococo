package org.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rococo.grpc.Artist;
import org.rococo.grpc.Museum;

import javax.annotation.Nonnull;
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
                !museum.getId().isEmpty() ? UUID.fromString(museum.getId()) : null,
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

    public static @Nonnull Museum toGrpc(@Nonnull MuseumJson museumJson) {
        return Museum.newBuilder()
                .setId(museumJson.id() != null ? museumJson.id().toString() : "")
                .setTitle(museumJson.title())
                .setDescription(museumJson.description())
                .setPhoto(museumJson.photo())
                .build();
    }
}
