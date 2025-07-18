package org.rococo.config;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://client.rococo.dc/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.rococo.dc:9000/";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rococo.dc:8080/";
  }

  @Nonnull
  @Override
  public String databaseAddress() {
    return "rococo-db:3306";
  }

  @Nonnull
  @Override
  public String userdataGrpcAddress() {
    return "userdata.rococo.dc";
  }

  @Nonnull
  @Override
  public String museumGrpcAddress() {
    return "museum.rococo.dc";
  }

  @Nonnull
  @Override
  public String artistGrpcAddress() {
    return "artist.rococo.dc";
  }

  @Nonnull
  @Override
  public String paintingGrpcAddress() {
    return "painting.rococo.dc";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/selenoid/";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    final String allureDockerApi = System.getenv("ALLURE_DOCKER_API");
    return allureDockerApi == null
            ? "http://allure:5050/"
            : allureDockerApi;
  }
}
