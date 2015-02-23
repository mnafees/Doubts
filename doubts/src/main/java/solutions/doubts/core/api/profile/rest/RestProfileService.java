/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */
package solutions.doubts.core.api.profile.rest;

import android.content.Context;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

import solutions.doubts.core.api.profile.domain.Profile;
import solutions.doubts.core.api.profile.retrofit.RetrofitProfileService;
import solutions.doubts.core.exceptions.LoginException;

public class RestProfileService {

    private RestAdapter restAdapter;
    private Context context;

    public RestProfileService(final Context context) {
        this.context = context;
    }

    public Profile fetchProfile(final int id, final String username) {
        final RetrofitProfileService retrofitProfileService = this.restAdapter.create(RetrofitProfileService.class);
        Profile response = null;
        try {
            response = retrofitProfileService.fetchProfile(id, username);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exists
                throw new LoginException(LoginException.Type.USER_DOES_NOT_EXIST);
            }
        }
        return response;
    }

    public void createNewProfile(final int id, final String username, final Profile profile) {
        final RetrofitProfileService retrofitProfileService = this.restAdapter.create(RetrofitProfileService.class);
        try {
            retrofitProfileService.createNewProfile(id, username, profile);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exist
                throw new LoginException(LoginException.Type.USER_DOES_NOT_EXIST);
            }
        }
    }

    public void updateProfile(final int id, final String username, final Profile profile) {
        final RetrofitProfileService retrofitProfileService = this.restAdapter.create(RetrofitProfileService.class);
        try {
            retrofitProfileService.updateProfile(id, username, profile);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exist
                throw new LoginException(LoginException.Type.USER_DOES_NOT_EXIST);
            }
        }
    }

}
