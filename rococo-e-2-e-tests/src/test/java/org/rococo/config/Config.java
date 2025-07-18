package org.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

  static @Nonnull Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
  }

  @Nonnull
  default String projectId() {
    return "mariamur-rococo";
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  public String databaseAddress();

  @Nonnull
  String userdataGrpcAddress();

  default int userdataGrpcPort() {
    return 8802;
  }

  @Nonnull
  String museumGrpcAddress();

  default int museumGrpcPort() {
    return 8806;
  }

  default int countryGrpcPort() {
    return 8806;
  }

  @Nonnull
  String artistGrpcAddress();

  default int artistGrpcPort() {
    return 8808;
  }

  @Nonnull
  String paintingGrpcAddress();

  default int paintingGrpcPort() {
    return 8810;
  }

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String allureDockerUrl();

}
