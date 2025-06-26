package org.rococo.config;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
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
}
