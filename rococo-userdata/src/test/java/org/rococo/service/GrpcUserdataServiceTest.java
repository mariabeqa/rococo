package org.rococo.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.data.UserEntity;
import org.rococo.data.repository.UserRepository;
import org.rococo.grpc.User;
import org.rococo.grpc.UserRequest;
import org.rococo.grpc.UserResponse;
import org.rococo.grpc.UsernameRequest;
import org.rococo.utils.ImageUtil;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrpcUserdataServiceTest {

    private static final String IMAGE_PATH = "img/user/avatar.jpg";

    private UserGrpcService userGrpcService;

    @Mock
    private UserRepository userRepository;

    private final UUID firstUserId = UUID.randomUUID();
    private final String firstUsername = "maria";
    private UserEntity firstTestUser;

    private StreamRecorder<UserResponse> responseObserver;

    @BeforeEach
    void init() {
        firstTestUser = new UserEntity();
        firstTestUser.setId(firstUserId);
        firstTestUser.setUsername(firstUsername);

        responseObserver = StreamRecorder.create();
        userGrpcService = new UserGrpcService(userRepository);
    }

    @Test
    void getCurrentShouldReturnCorrectUser() throws ExecutionException, InterruptedException, TimeoutException {
        final UsernameRequest request = UsernameRequest.newBuilder()
            .setUsername(firstUsername)
            .build();

        when(userRepository.findByUsername(eq(firstUsername)))
            .thenReturn(Optional.of(firstTestUser));

        userGrpcService.getCurrent(request, responseObserver);

        User user = responseObserver.firstValue().get(1, TimeUnit.SECONDS).getUser();

        assertEquals(firstUserId.toString(), user.getId());
        assertEquals(firstUsername, user.getUsername());

        verify(userRepository, times(1)).findByUsername(eq(firstUsername));
    }

    @Test
    void getCurrentShouldReturnNewUserIfUserNotFound() throws ExecutionException, InterruptedException, TimeoutException {
        final String notExistingUsername = "not-existing-user";

        final UsernameRequest request = UsernameRequest.newBuilder()
            .setUsername(notExistingUsername)
            .build();

        when(userRepository.findByUsername(eq(notExistingUsername)))
            .thenAnswer(invocationOnMock -> {
                UserEntity entity = new UserEntity();
                entity.setUsername(notExistingUsername);
                return Optional.of(entity);
            });

        userGrpcService.getCurrent(request, responseObserver);

        User user = responseObserver.firstValue().get(1, TimeUnit.SECONDS).getUser();

        assertNull(responseObserver.getError());
        assertEquals(notExistingUsername, user.getUsername());
        verify(userRepository, times(1)).findByUsername(eq(notExistingUsername));
    }

    @Test
    void getCurrentShouldReturnInvalidArgumentIfUsernameIsBlank() {
        final UsernameRequest request = UsernameRequest.newBuilder()
            .setUsername("")
            .build();

        userGrpcService.getCurrent(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ex.getStatus().getCode());
        assertEquals("Username must not be blank",
            ex.getStatus().getDescription());
    }

    @Test
    void updateUserShouldReturnUpdatedUser() throws ExecutionException, InterruptedException, TimeoutException {
        final String firstName = "Maria";
        final String lastName = "Murashkina";
        final UserRequest request = UserRequest.newBuilder()
            .setUser(
                User.newBuilder()
                    .setId(firstUserId.toString())
                    .setUsername(firstUsername)
                    .setFirstname(firstName)
                    .setLastname(lastName)
                    .setAvatar(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                    .build()
            )
                .build();

        when(userRepository.findByUsername(eq(firstUsername)))
            .thenReturn(Optional.of(firstTestUser));

        when(userRepository.save(any(UserEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        userGrpcService.updateUser(request, responseObserver);

        User user = responseObserver.firstValue().get(1, TimeUnit.SECONDS).getUser();


        assertEquals(firstUserId.toString(), user.getId());
        assertEquals(firstUsername, user.getUsername());
        assertEquals(firstName, user.getFirstname());
        assertEquals(lastName, user.getLastname());
        assertEquals(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH), user.getAvatar());

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByUsername(eq(firstUsername));
    }

    @Test
    void updateUserShouldReturnNotFoundIfUserNotFound() {
        final String notExistingUser = "not-existing-user";
        final UserRequest request = UserRequest.newBuilder()
            .setUser(
                User.newBuilder()
                    .setId(firstUserId.toString())
                    .setUsername(notExistingUser)
                    .setAvatar(ImageUtil.getEncodedImageFromClasspath(IMAGE_PATH))
                    .build()
            )
            .build();

        when(userRepository.findByUsername(eq(notExistingUser)))
            .thenReturn(Optional.empty());

        userGrpcService.updateUser(request, responseObserver);

        assertInstanceOf(StatusRuntimeException.class, responseObserver.getError());
        StatusRuntimeException ex = (StatusRuntimeException) responseObserver.getError();
        assertEquals(Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
        assertEquals(String.format("User with username '%s' not found", notExistingUser),
            ex.getStatus().getDescription());
    }
}
