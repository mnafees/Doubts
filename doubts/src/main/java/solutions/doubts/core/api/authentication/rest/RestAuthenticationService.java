/*
 * This file is part of Doubts.
 * Copyright 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */
package solutions.doubts.core.api.authentication.rest;

import android.accounts.NetworkErrorException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import solutions.doubts.core.api.authentication.retrofit.RetrofitAuthenticationService;

public class RestAuthenticationService {

    final static String TAG = "RestAuthenticationServ";

    private RestAdapter restAdapter;

    public RestAuthenticationService(final RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public Observable<Response> login(final String email) throws NetworkErrorException {
        final RetrofitAuthenticationService retrofitAuthenticationService = this.restAdapter.create(
                RetrofitAuthenticationService.class);
        try {
            return retrofitAuthenticationService.login(email);
        } catch (RetrofitError error) {
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                throw new NetworkErrorException("Not connected to the internet.");
            }
        }
        return null; // should not happen
    }

    public Observable<Response> register(final String email, final String username,
                                         final String name)
            throws NetworkErrorException{
        final RetrofitAuthenticationService retrofitAuthenticationService = this.restAdapter
                .create(RetrofitAuthenticationService.class);
        try {
            String encodedEmail = null;
            try {
                encodedEmail = URLEncoder.encode("email", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("Could not encode email " + email);
            }
           return retrofitAuthenticationService.register(encodedEmail,
                    email, username, name);
        } catch (RetrofitError error) {
            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                throw new NetworkErrorException("Not connected to the internet.");
            }
        }
        return null; // should not happen
    }

}
