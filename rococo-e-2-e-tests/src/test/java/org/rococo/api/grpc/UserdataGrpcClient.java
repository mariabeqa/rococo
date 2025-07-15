package org.rococo.api.grpc;

import io.qameta.allure.Step;
import org.rococo.grpc.RococoUserdataServiceGrpc;
import org.rococo.grpc.UsernameRequest;
import org.rococo.model.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.rococo.model.UserJson.fromGrpc;

@ParametersAreNonnullByDefault
public class UserdataGrpcClient extends GrpcClient {

    public UserdataGrpcClient() {
        super(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort());
    }

    private final RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub userdataStub =
            RococoUserdataServiceGrpc.newBlockingStub(channel);


    @Step("Get current user '{0}' from Userdata Grpc service")
    public @Nonnull UserJson getCurrent(@Nonnull String username) {
        return fromGrpc(
                userdataStub.getCurrent(
                        UsernameRequest.newBuilder()
                                .setUsername(username)
                                .build()
                        ).getUser()
        );
    }
}
