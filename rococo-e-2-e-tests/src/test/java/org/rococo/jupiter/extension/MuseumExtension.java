package org.rococo.jupiter.extension;

import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestPainting;
import org.rococo.model.CountryJson;
import org.rococo.model.GeoLocationJson;
import org.rococo.model.MuseumJson;
import org.rococo.utils.ImageUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.rococo.model.Countries.RUSSIA;
import static org.rococo.utils.data.RandomDataUtils.*;

@ParametersAreNonnullByDefault
public class MuseumExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private static final String MUSEUM_PHOTO_PATH = "img/museum/russian_museum.jpg";
    private static final String MUSEUM_CITY = "Санкт-Петербург";
    private final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();

    @Override
    @Step("Create test museum")
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseum.class)
                .ifPresent(museumAnno -> {
                    final String title = "".equals(museumAnno.title()) ?
                            randomMuseumTitle() : museumAnno.title();
                    final String description = "".equals(museumAnno.description()) ?
                            randomMuseumDescription() : museumAnno.description();
                    final String photoPath = "".equals(museumAnno.path()) ?
                            MUSEUM_PHOTO_PATH : museumAnno.path();
                    final String city = "".equals(museumAnno.city()) ?
                            MUSEUM_CITY : museumAnno.city();
                    final String countryName = RUSSIA.equals(museumAnno.country()) ?
                            RUSSIA.getName() : museumAnno.country().getName();
                    final UUID countryId = RUSSIA.equals(museumAnno.country()) ?
                            RUSSIA.getId() : museumAnno.country().getId();

                    MuseumJson museum = new MuseumJson(
                            null,
                            title,
                            description,
                            ImageUtil.getEncodedImageFromClasspath(photoPath),
                            new GeoLocationJson(
                                    city,
                                    new CountryJson(
                                            countryId,
                                            countryName
                                    )
                            )
                    );
                    MuseumJson created = museumGrpcClient.addMuseum(museum);
                    setMuseum(created);

                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    @Step("Remove created test museum")
    public void afterEach(ExtensionContext context) throws Exception {
        Optional<TestPainting> paintingAnno =
                AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class);
        Optional<TestMuseum> museumAnno =
                AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseum.class);
        //Если тест с созданием музея без привязки к картине, то музей можно удалять
        if (paintingAnno.isEmpty()) {
            museumAnno.ifPresent(
                    museum -> {
                        if (museum.removeAfterTest()) {
                            museumGrpcClient.deleteMuseum(Objects.requireNonNull(getMuseum().id().toString()));
                        }
                    }
            );
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public MuseumJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getMuseum();
    }

    public static void setMuseum(MuseumJson museum) {
        ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museum
        );
    }

    @Nullable
    public static MuseumJson getMuseum() {
        ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(), MuseumJson.class
        );
    }
}
