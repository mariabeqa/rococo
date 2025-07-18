package org.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AllureProject(@JsonProperty("id") String id) {
}
