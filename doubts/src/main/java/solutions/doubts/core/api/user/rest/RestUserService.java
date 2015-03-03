/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */
package solutions.doubts.core.api.user.rest;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import solutions.doubts.core.api.user.domain.User;
import solutions.doubts.core.api.user.retrofit.RetrofitUserService;

public class RestUserService {

    private RestAdapter restAdapter;

    public User fetchProfile(final int id, final String username) {
        final RetrofitUserService retrofitUserService = this.restAdapter.create(RetrofitUserService.class);
        User response = null;
        try {
            response = retrofitUserService.fetchUser(id, username);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exists
            }
        }
        return response;
    }

    public void createNewProfile(final int id, final String username, final User user) {
        final RetrofitUserService retrofitUserService = this.restAdapter.create(RetrofitUserService.class);
        try {
            retrofitUserService.createNewUser(user);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exist
            }
        }
    }

    public void updateProfile(final int id, final String username, final User user) {
        final RetrofitUserService retrofitUserService = this.restAdapter.create(RetrofitUserService.class);
        try {
            retrofitUserService.updateUser(id, username, user);
        } catch (RetrofitError error) {
            if (error.getResponse().getStatus() == 404) {
                // user does not exist
            }
        }
    }

}
