package org.rococo.service.api;

import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.RococoUserdataServiceGrpc;
import org.rococo.grpc.UserRequest;
import org.rococo.grpc.UserResponse;
import org.rococo.grpc.UsernameRequest;
import org.rococo.model.UserJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserGrpcClient {

    private static final Logger LOG = LoggerFactory.getLogger(UserGrpcClient.class);

    @GrpcClient("grpcUserdataClient")
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataClient;

    public @Nonnull UserJson createNewUserIfNotPresent(String username) {
        UserResponse current = userdataClient.getCurrent(
                UsernameRequest.newBuilder()
                        .setUsername(username)
                        .build()
        );

        return UserJson.fromGrpc(current.getUser());
    }

    public @Nonnull UserJson updateUser(UserJson userJson) {
        return UserJson.fromGrpc(
                userdataClient.updateUser(
                        UserRequest.newBuilder()
                                .setUser(UserJson.toGrpc(userJson))
                                .build()
                ).getUser()
        );
    }
}
