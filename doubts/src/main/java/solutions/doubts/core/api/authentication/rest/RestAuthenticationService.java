/*
 * This file is part of Doubts.
 * Copyright 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.authentication.rest;

import android.util.Log;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import solutions.doubts.core.api.authentication.retrofit.RetrofitAuthenticationService;

public class RestAuthenticationService {

    private RestAdapter restAdapter;

    public RestAuthenticationService(final RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public String authenticate(final String email) {
        final RetrofitAuthenticationService retrofitAuthenticationService = this.restAdapter.create(
                RetrofitAuthenticationService.class);
        //try {
            retrofitAuthenticationService.authenticate(email, new Callback<JsonObject>() {
                @Override
                public void success(JsonObject jsonObject, Response response) {
                    Log.d("", jsonObject.toString());
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        ///}
        return null;
    }

}
