package org.rococo.config;


import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8080/";
  }

  @Nonnull
  @Override
  public String databaseAddress() {
    return "localhost:3306";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String museumGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String artistGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String paintingGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }
}
