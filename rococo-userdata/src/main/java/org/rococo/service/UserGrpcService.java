package org.rococo.service;

import org.rococo.data.UserEntity;
import org.rococo.data.repository.UserRepository;
import org.rococo.exception.NotFoundException;
import org.rococo.grpc.RococoUserdataServiceGrpc;
import org.rococo.grpc.UserRequest;
import org.rococo.grpc.UserResponse;
import org.rococo.grpc.UsernameRequest;
import org.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;

import static org.rococo.data.UserEntity.toGrpc;
import static java.nio.charset.StandardCharsets.UTF_8;


@GrpcService
public class UserGrpcService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(UserGrpcService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserGrpcService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("### Kafka consumer record: {}", cr.toString());

                            UserEntity userDataEntity = new UserEntity();
                            userDataEntity.setUsername(user.username());
                            UserEntity userEntity = userRepository.save(userDataEntity);

                            LOG.info(
                                    "### User '{}' successfully saved to database with id: {}",
                                    user.username(),
                                    userEntity.getId()
                            );
                        }
                );
    }


    @Override
    public void getCurrent(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity byUsername = userRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new NotFoundException(String.format("User with username '%s' not found", request.getUsername()))
        );

        responseObserver.onNext(
                UserResponse.newBuilder()
                        .setUser(toGrpc(byUsername))
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userToUpdate = userRepository.findByUsername(request.getUser().getUsername()).orElseThrow(
                () -> new NotFoundException(String.format("User with username '%s' not found", request.getUser().getUsername()))
        );

        userToUpdate.setFirstname(request.getUser().getFirstname());
        userToUpdate.setLastname(request.getUser().getLastname());
        userToUpdate.setAvatar(request.getUser().getAvatar().isEmpty() ? new byte[0] : request.getUser().getAvatar().getBytes(UTF_8));
        UserEntity savedUser = userRepository.save(userToUpdate);

        responseObserver.onNext(
                UserResponse.newBuilder()
                        .setUser(toGrpc(savedUser))
                        .build()
        );
        responseObserver.onCompleted();
    }

}
