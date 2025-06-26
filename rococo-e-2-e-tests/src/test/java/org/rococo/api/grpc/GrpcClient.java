package org.rococo.api.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import org.rococo.config.Config;
import org.rococo.utils.GrpcConsoleInterceptor;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class GrpcClient {

    protected static final Config CFG = Config.getInstance();
    private final String address;
    private final int port;
    protected final Channel channel;

    public GrpcClient(String address, int port) {
        this.address = address;
        this.port = port;
        this.channel = ManagedChannelBuilder
                .forAddress(address, port)
                .intercept(new GrpcConsoleInterceptor())
                .intercept(new AllureGrpc())
                .usePlaintext()
                .maxInboundMessageSize(Integer.MAX_VALUE)
                .build();
    }
}
