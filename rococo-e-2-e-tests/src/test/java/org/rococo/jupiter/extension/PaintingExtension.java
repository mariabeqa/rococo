package org.rococo.jupiter.extension;

import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.rococo.api.grpc.ArtistGrpcClient;
import org.rococo.api.grpc.MuseumGrpcClient;
import org.rococo.api.grpc.PaintingGrpcClient;
import org.rococo.jupiter.annotation.TestArtist;
import org.rococo.jupiter.annotation.TestMuseum;
import org.rococo.jupiter.annotation.TestPainting;
import org.rococo.model.ArtistJson;
import org.rococo.model.MuseumJson;
import org.rococo.model.PaintingJson;
import org.rococo.utils.ImageUtil;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.utils.RandomDataUtils.randomPaintingDescription;
import static org.rococo.utils.RandomDataUtils.randomPaintingName;

@ParametersAreNonnullByDefault
public class PaintingExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private static final String PAINTING_PHOTO_PATH = "img/painting/dnepr.jpg";
    private final PaintingGrpcClient paintingClient = new PaintingGrpcClient();
    private final MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();
    private final ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class)
                .ifPresent(paintingAnno -> {
                    final String title = "".equals(paintingAnno.title()) ?
                            randomPaintingName() : paintingAnno.title();
                    final String description = "".equals(paintingAnno.description()) ?
                            randomPaintingDescription() : paintingAnno.description();
                    final String path = "".equals(paintingAnno.path()) ?
                            PAINTING_PHOTO_PATH : paintingAnno.path();
                    final MuseumJson museum = MuseumExtension.getMuseum();
                    final ArtistJson artist = ArtistExtension.getArtist();

                    PaintingJson painting = new PaintingJson(
                            null,
                            title,
                            description,
                            ImageUtil.getEncodedImageFromClasspath(path),
                            museum,
                            artist
                    );

                    PaintingJson created = paintingClient.addPainting(painting);

                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    @Step("Remove created test painting")
    public void afterEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestPainting.class)
                .ifPresent(paintingAnno -> {
                    paintingClient.deletePainting(
                            context.getStore(NAMESPACE).get(context.getUniqueId(), PaintingJson.class)
                    );
                });

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestMuseum.class)
                .ifPresent(museumAnno -> {
                            if (museumAnno.removeAfterTest()) {
                                ExtensionContext ctx = TestMethodContextExtension.context();
                                museumGrpcClient.deleteMuseum(
                                        ctx.getStore(MuseumExtension.NAMESPACE).get(ctx.getUniqueId(), MuseumJson.class)
                                );
                            }
                        }
                );

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), TestArtist.class)
                .ifPresent(artistAnno -> {
                            if (artistAnno.removeAfterTest()) {
                                ExtensionContext ctx = TestMethodContextExtension.context();
                                artistGrpcClient.deleteArtist(
                                        ctx.getStore(ArtistExtension.NAMESPACE).get(ctx.getUniqueId(), ArtistJson.class)
                                );
                            }
                        }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public PaintingJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), PaintingJson.class);
    }
}
