package org.rococo.utils.grpc;

import org.rococo.grpc.Country;
import org.rococo.grpc.GeoLocation;
import org.rococo.grpc.Museum;
import org.rococo.utils.ImageUtil;

import java.util.UUID;

public class MuseumBuilder {

    public static final String IMAGE_PATH = "img/museum/russian_museum.jpg";
    public static final String MUSEUM_TITLE = "Русский Государственный музей";
    public static final String MUSEUM_DESCRIPTION = "Российский государственный художественный музей в Санкт-Петербурге, крупнейшее в мире собрание русского изобразительного искусства.";
    public static final String CITY = "Санкт-Петербург";
    public static final String COUNTRY_NAME = "Россия";
    public static final String COUNTRY_ID = "11f0525d-71e5-30c5-aec0-0242ac110004";
    public static final UUID MUSEUM_ID = UUID.randomUUID();

    private final Museum.Builder builder = Museum.newBuilder();

    public MuseumBuilder withDefaults() {
        builder.setId(String.valueOf(MUSEUM_ID))
                .setTitle(MUSEUM_TITLE)
                .setDescription(MUSEUM_DESCRIPTION)
                .setPhoto(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                .setGeo(
                    GeoLocation.newBuilder()
                        .setCity(CITY)
                        .setCountry(
                            Country.newBuilder()
                                .setId(COUNTRY_ID)
                                .setName(COUNTRY_NAME)
                                .build()
                        )
                        .build()
                );
        return this;
    }

    public MuseumBuilder withId(String id) {
        builder.setId(id);
        return this;
    }

    public MuseumBuilder withTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public MuseumBuilder withDescription(String description) {
        builder.setDescription(description);
        return this;
    }

    public MuseumBuilder withPhoto(String photo) {
        builder.setPhoto(photo);
        return this;
    }

    public MuseumBuilder withDefaultGeo() {
        builder.setGeo(
            GeoLocation.newBuilder()
                .setCity(CITY)
                .setCountry(
                    Country.newBuilder()
                        .setId(COUNTRY_ID)
                        .setName(COUNTRY_NAME)
                        .build()
                )
                .build()
        );
        return this;
    }


    public Museum build() {
        return builder.build();
    }
}
