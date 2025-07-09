package org.rococo.utils.grpc;

import org.rococo.grpc.Artist;
import org.rococo.utils.ImageUtil;

import java.util.UUID;

public class ArtistBuilder {

    public static final UUID ARTIST_ID = UUID.randomUUID();
    public static final String IMAGE_PATH = "img/artist/kuindzhi.jpg";
    public static final String ARTIST_NAME = "Архип Куинджи";
    public static final String ARTIST_BIO = "Биография Архипа Куинджи";

    private final Artist.Builder builder = Artist.newBuilder();

    public ArtistBuilder withDefaults() {
        builder.setId(String.valueOf(ARTIST_ID))
            .setName(ARTIST_NAME)
            .setBio(ARTIST_BIO)
            .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH));
        return this;
    }

    public ArtistBuilder withId(String id) {
        builder.setId(id);
        return this;
    }

    public ArtistBuilder withName(String name) {
        builder.setName(name);
        return this;
    }

    public ArtistBuilder withBio(String bio) {
        builder.setBio(bio);
        return this;
    }

    public ArtistBuilder withPhoto(String path) {
        builder.setPhoto(ImageUtil.getEncodedImageFromClasspath(path));
        return this;
    }

    public Artist build() {
        return builder.build();
    }

}
