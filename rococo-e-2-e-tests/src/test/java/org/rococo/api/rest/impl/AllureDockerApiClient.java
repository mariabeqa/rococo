package org.rococo.api.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.logging.HttpLoggingInterceptor;
import org.rococo.api.rest.AllureDockerApi;
import org.rococo.api.rest.core.RestClient;
import org.rococo.model.allure.AllureProject;
import org.rococo.model.allure.AllureResults;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class AllureDockerApiClient extends RestClient {

    private final AllureDockerApi allureDockerApi;

    public AllureDockerApiClient() {
        super(CFG.allureDockerUrl(), HttpLoggingInterceptor.Level.NONE);
        this.allureDockerApi = create(AllureDockerApi.class);
    }

    public void uploadResults(String projectId,
                              AllureResults results) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.uploadResults(
                    projectId,
                    results
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void createProjectIfNotExist(String projectId) {
        int code;
        try {
            code = allureDockerApi.project(
                    projectId
            ).execute().code();
            if (code == 404) {
                code = allureDockerApi.createProject(
                        new AllureProject(
                                projectId
                        )
                ).execute().code();
                assertEquals(201, code);
            } else {
                assertEquals(200, code);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public void cleanResults(String projectId) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.cleanResults(
                    projectId
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public void generateReport(String projectId) {
        final Response<JsonNode> response;
        try {
            response = allureDockerApi.generateReport(
                    projectId,
                    System.getenv("HEAD_COMMIT_MESSAGE"),
                    System.getenv("BUILD_URL"),
                    System.getenv("EXECUTION_TYPE")
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }
}
