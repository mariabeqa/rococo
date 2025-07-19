package org.rococo.api.rest;

import org.rococo.model.*;
import org.rococo.model.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;

public interface GatewayApi {

    @GET("api/user")
    Call<UserJson> getCurrentUser(@Header("Authorization") String bearerToken);

    @PATCH("api/user")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                              @Body UserJson user);

    @POST("api/museum")
    Call<MuseumJson> addMuseum(@Header("Authorization") String bearerToken,
                                 @Body MuseumJson museum);

    @PATCH("api/museum")
    Call<MuseumJson> updateMuseum(@Header("Authorization") String bearerToken,
                               @Body MuseumJson museum);

    @GET("api/museum/{id}")
    Call<MuseumJson> findMuseumById(@Header("Authorization") String bearerToken,
                                    @Path("id") String museumId);

    @GET("api/museum")
    Call<RestResponsePage<MuseumJson>> getMuseums(@Header("Authorization") String bearerToken,
                                                  @Query("page") int page,
                                                  @Query("size") int size,
                                                  @Query("searchQuery") @Nullable String title);

    @DELETE("api/museum")
    Call<Void> deleteMuseum(@Header("Authorization") String bearerToken,
                            @Query("museumId") String museumId);

    @POST("api/artist")
    Call<ArtistJson> addArtist(@Header("Authorization") String bearerToken,
                               @Body ArtistJson artist);

    @PATCH("api/artist")
    Call<ArtistJson> updateArtist(@Header("Authorization") String bearerToken,
                                  @Body ArtistJson artist);

    @GET("api/artist/{id}")
    Call<ArtistJson> findArtistById(@Header("Authorization") String bearerToken,
                                    @Path("id") String artistId);

    @GET("api/artist")
    Call<RestResponsePage<ArtistJson>> getArtists(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Query("name") @Nullable String name);

    @DELETE("api/artist")
    Call<Void> deleteArtist(@Header("Authorization") String bearerToken,
                            @Query("artistId") String artistId);

    @POST("api/painting")
    Call<PaintingJson> addPainting(@Header("Authorization") String bearerToken,
                               @Body PaintingJson painting);

    @PATCH("api/painting")
    Call<PaintingJson> updatePainting(@Header("Authorization") String bearerToken,
                                  @Body PaintingJson painting);

    @GET("api/painting/{id}")
    Call<PaintingJson> findPaintingById(@Header("Authorization") String bearerToken,
                                    @Path("id") String paintingId);

    @GET("api/painting/{id}")
    Call<RestResponsePage<PaintingJson>> findPaintingByAuthorId(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Path("id") String authorId);

    @GET("api/painting")
    Call<RestResponsePage<PaintingJson>> getPaintings(@Header("Authorization") String bearerToken,
                                                      @Query("page") int page,
                                                      @Query("size") int size,
                                                      @Query("title") @Nullable String title);
    @DELETE("api/painting")
    Call<Void> deletePainting(@Header("Authorization") String bearerToken,
                            @Query("paintingId") String paintingId);

    @GET("api/country/name/{name}")
    Call<CountryJson> findCountryByName(@Header("Authorization") String bearerToken,
                                        @Path("name") String name);

}
