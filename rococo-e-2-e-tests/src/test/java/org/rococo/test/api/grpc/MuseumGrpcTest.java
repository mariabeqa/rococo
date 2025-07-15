package org.rococo.test.api.grpc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.model.CountryJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.rococo.model.Countries.AUSTRALIA;
import static org.rococo.model.Countries.RUSSIA;
import static org.rococo.utils.ImageUtil.getEncodedImageFromClasspath;
import static org.rococo.utils.data.RandomDataUtils.randomMuseumTitle;

public class MuseumGrpcTest extends BaseGrpcTest {

    @Test
    @DisplayName("gRPC: Adding new museum with rococo-museum grpc service")
    void shouldAddNewMuseum() {
        final String museumTitle = randomMuseumTitle();
        MuseumJson museumJson = new MuseumJson(
            null,
            museumTitle,
            MUSEUM_DESCRIPTION,
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            new GeoLocationJson(
                CITY,
                new CountryJson(
                    RUSSIA.getId(),
                    RUSSIA.getName()
                )
            )
        );

        MuseumJson result = museumGrpcClient.addMuseum(museumJson);

        assertEquals(museumTitle, result.title());
        assertEquals(MUSEUM_DESCRIPTION, result.description());
        assertEquals(getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH),
            result.photo());
        assertEquals(CITY, result.geo().city());
        assertEquals( RUSSIA.getId(), result.geo().country().id());
        assertEquals(RUSSIA.getName(), result.geo().country().name());
    }

    @Test
    @DisplayName("gRPC: Updating a museum with rococo-museum grpc service")
    @TestMuseum
    void shouldUpdateMuseum(MuseumJson museumJson) {
        final String newCity = "Петербург";
        MuseumJson newMuseumJson = new MuseumJson(
            museumJson.id(),
            museumJson.title() + " edited",
            museumJson.description() + " edited",
            getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH_NEW),
            new GeoLocationJson(
                newCity,
                new CountryJson(
                    AUSTRALIA.getId(),
                    AUSTRALIA.getName()
                )
            )
        );

        MuseumJson result = museumGrpcClient.updateMuseum(newMuseumJson);

        assertEquals(museumJson.id(), result.id());
        assertEquals(museumJson.title() + " edited", result.title());
        assertEquals(museumJson.description() + " edited", result.description());
        assertEquals(getEncodedImageFromClasspath(MUSEUM_IMAGE_PATH_NEW),
            result.photo());
        assertEquals(newCity, result.geo().city());
        assertEquals(AUSTRALIA.getId(), result.geo().country().id());
        assertEquals(AUSTRALIA.getName(), result.geo().country().name());
    }

    @Test
    @DisplayName("gRPC: Getting museum by ID with rococo-museum grpc service")
    @TestMuseum
    void shouldReturnMuseumByID(MuseumJson museumJson) {

        MuseumJson result = museumGrpcClient.getMuseum(museumJson.id().toString());

        assertEquals(museumJson.id(), result.id());
        assertEquals(museumJson.title(), result.title());
        assertEquals(museumJson.description(), result.description());
        assertEquals(museumJson.photo(), result.photo());
        assertEquals(museumJson.geo().city(), result.geo().city());
        assertEquals(museumJson.geo().country().id(), result.geo().country().id());
        assertEquals(museumJson.geo().country().name(), result.geo().country().name());
    }
}
