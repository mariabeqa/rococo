package org.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorJson(
    @JsonProperty("grpcCode")
    String grpcCode,
    @JsonProperty("error")
    String error,
    @JsonProperty("timestamp")
    String timestamp,
    @JsonProperty("message")
    String message,
    @JsonProperty("status")
    int status)
{
}
