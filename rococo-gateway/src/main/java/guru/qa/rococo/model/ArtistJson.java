package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.grpc.Artist;
import jakarta.annotation.Nonnull;

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
                UUID.fromString(artist.getId()),
                artist.getName(),
                artist.getBio(),
                artist.getPhoto()
        );
    }
}
