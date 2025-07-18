package org.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EncodedAllureResult(@JsonProperty("file_name") String fileName,
                                  @JsonProperty("content_base64") String contentBase64) {
}
