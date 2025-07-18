package org.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.rococo.api.rest.impl.AllureDockerApiClient;
import org.rococo.config.Config;
import org.rococo.model.allure.AllureResults;
import org.rococo.model.allure.EncodedAllureResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureDockerServiceExtension implements SuiteExtension {

    private static final Logger LOG = LoggerFactory.getLogger(AllureDockerServiceExtension.class);

    private static final Config CFG = Config.getInstance();
    private static final boolean IN_DOCKER = "docker".equals(System.getProperty("test.env"));
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Path PATH_TO_RESULTS = Path.of("./rococo-e-2-e-tests/build/allure-results");
    private static final AllureDockerApiClient allureApiClient = new AllureDockerApiClient();

    @Override
    public void beforeSuite(ExtensionContext context) {
        if (IN_DOCKER) {
            allureApiClient.createProjectIfNotExist(CFG.projectId());
            allureApiClient.cleanResults(CFG.projectId());
            LOG.info("### Allure project created and results are cleaned");
        }
    }

    @Override
    public void afterSuite() {
        if (IN_DOCKER) {
            if (!Files.exists(PATH_TO_RESULTS)) {
                LOG.warn("### ALLURE: Allure results folder does not exist: {}", PATH_TO_RESULTS);
                return;
            }

            try (Stream<Path> allureResultsStream = Files.walk(PATH_TO_RESULTS).filter(Files::isRegularFile)) {
                List<Path> allureResults = allureResultsStream.toList();

                for (Path path : allureResults) {

                    try (InputStream is = Files.newInputStream(path)) {

                        EncodedAllureResult encodedResult = new EncodedAllureResult(
                                ENCODER.encodeToString(is.readAllBytes()),
                                path.getFileName().toString()
                        );

                        allureApiClient.uploadResults(
                                CFG.projectId(),
                                new AllureResults(List.of(encodedResult))
                        );
                    }
                }
                LOG.info("### ALLURE: Allure results uploaded successfully");

                allureApiClient.generateReport(CFG.projectId());
                LOG.info("### ALLURE: allure report generated successfully");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
