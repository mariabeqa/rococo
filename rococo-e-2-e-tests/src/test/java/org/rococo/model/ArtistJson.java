package org.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rococo.grpc.Artist;

import javax.annotation.Nonnull;
import java.util.UUID;

public record ArtistJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("biography")
    String biography,
    @JsonProperty("photo")
    String photo) {

    public static @Nonnull ArtistJson fromGrpc(@Nonnull Artist artist) {
        return new ArtistJson(
                artist.getId().isEmpty() ? null : UUID.fromString(artist.getId()),
                artist.getName(),
                artist.getBio(),
                artist.getPhoto()
        );
    }

    public static @Nonnull Artist toGrpc(@Nonnull ArtistJson artistJson) {
        return Artist.newBuilder()
                .setId(artistJson.id() != null ? artistJson.id().toString() : "")
                .setName(artistJson.name())
                .setBio(artistJson.biography())
                .setPhoto(artistJson.photo())
                .build();
    }
}
