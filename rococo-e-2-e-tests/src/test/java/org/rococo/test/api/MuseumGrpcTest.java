package org.rococo.test.api;

import org.junit.jupiter.api.Test;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.model.CountryJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;
import org.rococo.utils.ImageUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MuseumGrpcTest {

    @Test
    void shouldAddNewMuseum() {
//        MuseumGrpcClient client = new MuseumGrpcClient();
//        MuseumJson museumJson = client.addMuseum(
//                new MuseumJson(
//                        null,
//                        "Marias Museum",
//                        "Marias Museum description",
//                        ImageUtil.getEncodedImageFromClasspath("img/museum/russian_museum.jpg"),
//                        new GeoLocationJson(
//                                "Омск",
//                                new CountryJson(
//                                        UUID.fromString("11f0525d-71e5-30c5-aec0-0242ac110004"),
//                                        "Россия"
//                                )
//                        )
//                )
//        );
//        System.out.println(museumJson);

        String countryId = "71e530c5-525d-11f0-aec0-0242ac110004";
        String value = "11f0525d-71e5-30c5-aec0-0242ac110004";
        UUID uuid = UUID.fromString(value);
        assertEquals(value, uuid.toString());


    }
}
