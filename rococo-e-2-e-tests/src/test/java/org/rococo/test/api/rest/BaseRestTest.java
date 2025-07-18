package org.rococo.test.api.rest;

import org.junit.jupiter.api.extension.RegisterExtension;
import org.rococo.api.rest.impl.*;
import org.rococo.data.repository.ArtistRepositoryHibernate;
import org.rococo.data.repository.MuseumRepositoryHibernate;
import org.rococo.data.repository.PaintingRepositoryHibernate;
import org.rococo.jupiter.annotation.meta.RestTest;
import org.rococo.jupiter.extension.ApiLoginExtension;

@RestTest
public class BaseRestTest {

    @RegisterExtension
    protected static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

    protected final GatewayMuseumApiClient gatewayMuseumApiClient = new GatewayMuseumApiClient();
    protected final GatewayArtistApiClient gatewayArtistApiClient = new GatewayArtistApiClient();
    protected final GatewayPaintingApiClient gatewayPaintingApiClient = new GatewayPaintingApiClient();
    protected final GatewayUserApiClient gatewayUserApiClient = new GatewayUserApiClient();
    protected final AuthApiClient authApiClient = new AuthApiClient();

    protected final ArtistRepositoryHibernate artistRepository = new ArtistRepositoryHibernate();
    protected final MuseumRepositoryHibernate museumRepository = new MuseumRepositoryHibernate();
    protected final PaintingRepositoryHibernate paintingRepository = new PaintingRepositoryHibernate();
}

