package org.rococo.service.api;

import org.rococo.grpc.RococoUserdataServiceGrpc;
import org.rococo.grpc.UserRequest;
import org.rococo.grpc.UserResponse;
import org.rococo.grpc.UsernameRequest;
import org.rococo.model.UserJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(UserGrpcClient.class);

    @GrpcClient("grpcUserdataClient")
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataClient;

    public @Nonnull UserJson createNewUserIfNotPresent(String username) {
        try {
            UserResponse current = userdataClient.getCurrent(
                    UsernameRequest.newBuilder()
                            .setUsername(username)
                            .build()
            );

            return UserJson.fromGrpc(current.getUser());

        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }

    public @Nonnull UserJson updateUser(UserJson userJson) {
        try {
            return UserJson.fromGrpc(
                    userdataClient.updateUser(
                            UserRequest.newBuilder()
                                    .setUser(UserJson.toGrpc(userJson))
                                    .build()
                    ).getUser()
            );
        } catch (StatusRuntimeException e) {
            LOG.error("### Error while calling gRPC server ", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
        }
    }
}
