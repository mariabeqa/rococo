package org.rococo.utils.grpc;

import org.rococo.grpc.Artist;
import org.rococo.grpc.Museum;
import org.rococo.grpc.Painting;
import org.rococo.utils.ImageUtil;

import java.util.UUID;

public class PaintingBuilder {

    public static final UUID PAINTING_ID = UUID.randomUUID();
    public static final String IMAGE_PATH = "img/painting/raduga.jpg";
    public static final String PAINTING_TITLE = "Радуга";
    public static final String PAINTING_DESCRIPTION = "Картина «Радуга» считается одним из шедевров позднего периода творчества Куинджи";

    private final Painting.Builder builder = Painting.newBuilder();

    public PaintingBuilder withDefaults() {
        builder.setId(PAINTING_ID.toString())
            .setTitle(PAINTING_TITLE)
            .setDescription(PAINTING_DESCRIPTION)
            .setContent(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH));
        return this;
    }

    public PaintingBuilder withId(String id) {
        builder.setId(id);
        return this;
    }

    public PaintingBuilder withTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public PaintingBuilder withDescription(String description) {
        builder.setDescription(description);
        return this;
    }

    public PaintingBuilder withContent(String path) {
        builder.setContent(ImageUtil.getEncodedImageFromClasspath(path));
        return this;
    }

    public PaintingBuilder withArtist(Artist artist) {
        builder.setArtist(artist);
        return this;
    }

    public PaintingBuilder withMuseum(Museum museum) {
        builder.setMuseum(museum);
        return this;
    }

    public Painting build() {
        return builder.build();
    }

}
