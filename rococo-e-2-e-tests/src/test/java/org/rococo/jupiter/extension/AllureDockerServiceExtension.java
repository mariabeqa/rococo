package org.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.api.rest.impl.AllureDockerApiClient;
import org.rococo.config.Config;
import org.rococo.model.allure.AllureResults;
import org.rococo.model.allure.EncodedAllureResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureDockerServiceExtension implements SuiteExtension {

    private static final Config CFG = Config.getInstance();
    private static final boolean IN_DOCKER = "docker".equals(System.getProperty("test.env"));
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Path PATH_TO_RESULTS = Path.of("./rococo-e-2-e-tests/build/allure-results");
    private static final AllureDockerApiClient allureApiClient = new AllureDockerApiClient();

    @Override
    public void beforeSuite(ExtensionContext context) {
        if (IN_DOCKER) {
            allureApiClient.createProjectIfNotExist(CFG.projectId());
            allureApiClient.clean(CFG.projectId());
        }
    }

    @Override
    public void afterSuite() {
        if (IN_DOCKER) {
            try (Stream<Path> allureResults = Files.walk(PATH_TO_RESULTS).filter(Files::isRegularFile)) {
                List<EncodedAllureResult> encodedAllureResults = new ArrayList<>();

                for (Path path : allureResults.toList()) {
                    try(InputStream is = Files.newInputStream(path)) {
                        encodedAllureResults.add(
                                new EncodedAllureResult(
                                        path.getFileName().toString(),
                                        ENCODER.encodeToString(is.readAllBytes())
                                )
                        );
                    }
                }

                allureApiClient.uploadResults(
                        CFG.projectId(),
                        new AllureResults(encodedAllureResults)
                );

                allureApiClient.generateReport(CFG.projectId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
