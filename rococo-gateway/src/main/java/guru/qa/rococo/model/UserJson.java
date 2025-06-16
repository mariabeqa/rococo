package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.grpc.User;
import jakarta.annotation.Nonnull;

import java.util.UUID;


public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("lastname")
    String lastname,
    @JsonProperty("avatar")
    String avatar) {

    public static @Nonnull UserJson fromGrpc(@Nonnull User user) {
        return new UserJson(
                !user.getId().isEmpty() ? UUID.fromString(user.getId()) : null,
                user.getUsername(),
                !user.getFirstname().isEmpty() ? user.getFirstname() : null,
                !user.getLastname().isEmpty() ? user.getLastname() : null,
                !user.getAvatar().isEmpty() ? user.getAvatar() : null
        );
    }

    public static @Nonnull User toGrpc(@Nonnull UserJson userJson) {
        return User.newBuilder()
                .setId(userJson.id() != null ? userJson.id().toString() : "")
                .setUsername(userJson.username())
                .setFirstname(userJson.firstname() != null ? userJson.firstname() : "")
                .setLastname(userJson.lastname() != null ? userJson.lastname() : "")
                .setAvatar(userJson.avatar() != null ? userJson.avatar() : "")
                .build();
    }
}
