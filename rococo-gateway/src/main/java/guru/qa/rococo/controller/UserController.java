package guru.qa.rococo.controller;


import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.UserGrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;


@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserGrpcClient grpcUserClient;

  @Autowired
  public UserController(UserGrpcClient grpcUserClient) {
    this.grpcUserClient = grpcUserClient;
  }

  @GetMapping()
  public UserJson getCurrent(@AuthenticationPrincipal Jwt principal) {
    return grpcUserClient.createNewUserIfNotPresent(principal.getClaim("sub"));
  }

  @PatchMapping()
  public UserJson updateUser(@RequestBody UserJson user, @AuthenticationPrincipal Jwt principal) {
    String username = principal.getClaim("sub");
    if (!Objects.equals(username, user.username())) {
      throw new ResponseStatusException(FORBIDDEN, "Access to another user is forbidden");
    }
    return grpcUserClient.updateUser(user);
  }
}
