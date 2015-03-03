/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.authentication.retrofit;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface RetrofitAuthenticationService {

    @FormUrlEncoded
    @POST("/auth/login")
    public void authenticate(@Field("email") final String email,
                                   Callback<JsonObject> callback);

}
