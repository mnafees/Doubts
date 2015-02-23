/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.retrofit;

import com.google.gson.JsonObject;

import retrofit.http.GET;

/**
 * Retrofit interface for authentication.
 */
public interface RetrofitAuthenticationService {

    /**
     * Initiates an authentication request.
     * @param email the email address of the user
     * @return a JSON with the response string
     */
    @GET("/")
    public JsonObject authenticate(final String email);

}
