/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.profile.retrofit;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import solutions.doubts.core.api.profile.domain.Profile;

/**
 * Retrofit interface to fetch the profile information of the user.
 */
public interface RetrofitProfileService {

    @GET("/users/{id}/{username}")
    public Profile fetchProfile(@Path("id") final int id,
                                @Path("username") final String username);

    @POST("/users/{id}/{username}")
    public void createNewProfile(@Path("id") final int id,
                                 @Path("username") final String username,
                                 final Profile profile);

    @PUT("/users/{id}/{username}")
    public void updateProfile(@Path("id") final int id,
                              @Path("username") final String username,
                              final Profile profile);

}
