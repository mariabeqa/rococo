package org.rococo.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.rococo.model.ErrorJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorJson> handleGrpcException(StatusRuntimeException ex) {
        Status.Code code = ex.getStatus().getCode();
        String message = ex.getStatus().getDescription();

        HttpStatus httpStatus = switch (code) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ErrorJson errorJson = new ErrorJson(
            code.name(),
            httpStatus.getReasonPhrase(),
            LocalDateTime.now().toString(),
            message,
            httpStatus.value()
        );

        LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
        return new ResponseEntity<>(errorJson, httpStatus);
    }
}

