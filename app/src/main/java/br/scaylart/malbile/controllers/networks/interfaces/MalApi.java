package br.scaylart.malbile.controllers.networks.interfaces;

import java.util.ArrayList;
import java.util.Map;

import br.scaylart.malbile.models.Message;
import br.scaylart.malbile.models.MessageList;
import br.scaylart.malbile.models.MyAnimeList;
import br.scaylart.malbile.models.SearchAnime;
import br.scaylart.malbile.models.SearchManga;
import br.scaylart.malbile.models.User;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MalApi {
    @GET("/malappinfo.php")
    MyAnimeList getLibrary(@Query("u") String user, @Query("status") String status, @Query("type") String type);

    @GET("/api/account/verify_credentials.xml")
    User verifyCredentials();

    @GET("/api/anime/search.xml")
    SearchAnime searchAnime(@Query("q") String query);

    @GET("/api/manga/search.xml")
    SearchManga searchManga(@Query("q") String query);

    @GET("/messages")
    MessageList getMessages();

    @FormUrlEncoded
    @POST("/messages")
    Response sendMessage(@Field("username") String username, @Field("subject") String subject,
                         @Field("message") String message);

    @FormUrlEncoded
    @POST("/{type}/reviews")
    Response postReviews(@Path("type") String type, @FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/{type}/recommendations")
    Response postRecommendations(@Path("type") String type, @FieldMap Map<String, String> params);
}
