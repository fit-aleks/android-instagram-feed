package com.fitaleks.instafeed.network;

import com.fitaleks.instafeed.data.PhotoEntry;
import com.fitaleks.instafeed.data.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by alexander on 21.08.15.
 */
public interface InstagramService {

    @GET("/search")
    List<User> getUserList(@Query("q") String userName, @Query("client_id") String clientId);

    @GET("/{user_id}/media/recent")
    List<PhotoEntry> getPhotos(@Path("user_id") long userId, @Query("count") int count, @Query("client_id") String clientId);
}
