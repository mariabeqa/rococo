package org.rococo.test.api.grpc;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.rococo.api.grpc.ArtistGrpcClient;
import org.rococo.api.grpc.CountryGrpcClient;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.api.grpc.PaintingGrpcClient;
import org.rococo.jupiter.annotation.meta.GrpcTest;
import org.rococo.jupiter.extension.ApiLoginExtension;
import org.rococo.model.Countries;

import java.util.UUID;

import static org.rococo.model.Countries.AUSTRALIA;
import static org.rococo.model.Countries.RUSSIA;

@GrpcTest
public class BaseGrpcTest {

    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    public static final String MUSEUM_IMAGE_PATH = "img/museum/russian_museum.jpg";
    public static final String MUSEUM_IMAGE_PATH_NEW = "img/museum/russian_museum_new.jpeg";
    public static final String MUSEUM_DESCRIPTION = "Российский государственный художественный музей в Санкт-Петербурге, крупнейшее в мире собрание русского изобразительного искусства.";
    public static final String CITY = "Санкт-Петербург";

    public static final String ARTIST_IMAGE_PATH = "img/artist/kuindzhi.jpg";
    public static final String ARTIST_IMAGE_PATH_NEW = "img/artist/kuindzhi_new.jpg";
    public static final String ARTIST_BIO = "Биография Архипа Куинджи";

    public static final String PAINTING_IMAGE_PATH = "img/painting/raduga.jpg";
    public static final String PAINTING_IMAGE_PATH_NEW = "img/painting/dnepr.jpg";
    public static final String PAINTING_DESCRIPTION = "Картина «Радуга» считается одним из шедевров позднего периода творчества Куинджи";

    protected final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();
    protected final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    protected final PaintingGrpcClient paintingGrpcClient = new PaintingGrpcClient();
    protected final CountryGrpcClient countryGrpcClient = new CountryGrpcClient();
    protected final UUID RUSSIA_COUNTRY_ID = countryGrpcClient.getCountryByName(RUSSIA.getName()).id();
    protected final UUID AUSTRALIA_COUNTRY_ID = countryGrpcClient.getCountryByName(AUSTRALIA.getName()).id();
}
