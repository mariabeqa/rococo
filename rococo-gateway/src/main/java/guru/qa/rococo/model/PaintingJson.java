package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.grpc.Painting;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("artist")
        ArtistJson artist) {

    public static @Nonnull PaintingJson fromGrpc(@Nonnull Painting painting) {
        return new PaintingJson(
                UUID.fromString(painting.getId()),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                new MuseumJson(
                        UUID.fromString(painting.getMuseum().getId()),
                        painting.getMuseum().getTitle(),
                        painting.getMuseum().getDescription(),
                        painting.getMuseum().getPhoto(),
                        new GeoLocationJson(
                                painting.getMuseum().getGeo().getCity(),
                                new CountryJson(
                                        UUID.fromString(painting.getMuseum().getGeo().getCountry().getId()),
                                        painting.getMuseum().getGeo().getCountry().getName()
                                )
                        )
                ),
                new ArtistJson(
                        UUID.fromString(painting.getArtist().getId()),
                        painting.getArtist().getName(),
                        painting.getArtist().getBio(),
                        painting.getArtist().getPhoto()
                )
        );
    }
}
