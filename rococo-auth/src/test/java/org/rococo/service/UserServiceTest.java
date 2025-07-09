package org.rococo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.data.Authority;
import org.rococo.data.AuthorityEntity;
import org.rococo.data.UserEntity;
import org.rococo.data.repository.UserRepository;
import org.rococo.model.UserJson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final String username = "maria";
    private final String pw = "12345";
    private UserEntity userEntity;
    private List<AuthorityEntity> authorityEntities;

    @InjectMocks
    private UserService userService;

    @Mock
    private KafkaTemplate<String, UserJson> kafkaTemplate;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void initMockRepository() {
        AuthorityEntity read = new AuthorityEntity();
        read.setUser(userEntity);
        read.setAuthority(Authority.read);
        AuthorityEntity write = new AuthorityEntity();
        write.setUser(userEntity);
        write.setAuthority(Authority.write);
        authorityEntities = List.of(read, write);

        userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setAuthorities(authorityEntities);
        userEntity.setEnabled(true);
        userEntity.setPassword(pw);
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setId(UUID.randomUUID());
    }

    @Test
    void registerUserShouldReturnCreatedUsername() {
        String encodedPassword = "encodedPassword12345";

        when(passwordEncoder.encode(pw)).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        String result = userService.registerUser(username, pw);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();

        assertEquals(username, result);
        assertEquals(username, savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void registerUserShouldSendKafkaMessage() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        userService.registerUser(username, pw);

        verify(kafkaTemplate).send(eq("users"), argThat(userJson ->
            userJson != null && username.equals(userJson.username())));
    }
}
