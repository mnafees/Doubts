/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.user.retrofit;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import solutions.doubts.core.api.user.domain.User;

/**
 * Retrofit backend to fetch the profile information of the user.
 */
public interface RetrofitUserService {

    @GET("/users/{id}/{username}")
    public User fetchUser(@Path("id") final int id,
                             @Path("username") final String username);

    @POST("/users")
    public void createNewUser(final User user);

    @PUT("/users/{id}/{username}")
    public void updateUser(@Path("id") final int id,
                           @Path("username") final String username,
                           final User user);



}
