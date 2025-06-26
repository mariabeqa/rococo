package org.rococo.jupiter.extension;

import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.rococo.api.grpc.ArtistGrpcClient;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.jupiter.annotation.TestPainting;
import org.rococo.model.ArtistJson;
import org.rococo.utils.ImageUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;

import static org.rococo.utils.RandomDataUtils.randomArtistBio;
import static org.rococo.utils.RandomDataUtils.randomArtistName;

@ParametersAreNonnullByDefault
public class ArtistExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private static final String ARTIST_PHOTO_PATH = "img/artist/куинджи.jpg";
    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();

    @Override
    @Step("Create test artist")
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtist.class)
                .ifPresent(artistAnno -> {
                    final String name = "".equals(artistAnno.name()) ?
                            randomArtistName() : artistAnno.name();
                    final String bio = "".equals(artistAnno.bio()) ?
                            randomArtistBio() : artistAnno.bio();
                    final String photoPath = "".equals(artistAnno.photoPath()) ?
                            ARTIST_PHOTO_PATH : artistAnno.photoPath();

                    ArtistJson artist = new ArtistJson(
                            null,
                            name,
                            bio,
                            ImageUtil.getEncodedImageFromClasspath(photoPath)
                    );
                    ArtistJson created = artistGrpcClient.addArtist(artist);
                    setArtist(created);

                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    @Step("Remove created test artist")
    public void afterEach(ExtensionContext context) throws Exception {
        Optional<TestPainting> paintingAnno =
                AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class);
        Optional<TestArtist> artistAnno =
                AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtist.class);
        //Если тест с созданием художника без привязки к картине, то художника можно удалять
        if (paintingAnno.isEmpty()) {
            artistAnno.ifPresent(artist -> {
                if (artist.removeAfterTest()) {
                    artistGrpcClient.deleteArtist(Objects.requireNonNull(getArtist()));
                }
            });
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getArtist();
    }

    @Nullable
    public static ArtistJson getArtist() {
        final ExtensionContext context = TestMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(
                context.getUniqueId(), ArtistJson.class
        );
    }

    public static void setArtist(ArtistJson artist) {
        final ExtensionContext context = TestMethodContextExtension.context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                artist
        );
    }
}
