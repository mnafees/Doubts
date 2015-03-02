/*
 * This file is part of Doubts.
 * Copyright 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.rest;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import solutions.doubts.core.api.retrofit.RetrofitAuthenticationService;

public class RestAuthenticationService {

    private RestAdapter restAdapter;
    private Context context;

    public RestAuthenticationService(final Context context) {
        this.context = context;
    }

    public void authenticate(final String email) {
        final RetrofitAuthenticationService retrofitAuthenticationService = this.restAdapter.create(
                RetrofitAuthenticationService.class);
        try {
            final JsonObject response = retrofitAuthenticationService.authenticate(email);
        } catch (RetrofitError error) {
            switch (error.getKind()) {
                case NETWORK:
                    Toast.makeText(this.context, "Please connect to the internet.", Toast.LENGTH_LONG).show();
                    break;
                case HTTP:

                    break;
                case CONVERSION:

                    break;
                case UNEXPECTED:
                    Toast.makeText(this.context, "An unexpected error has occurred.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}
