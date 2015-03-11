/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import solutions.doubts.api.models.AuthToken;
import solutions.doubts.internal.StringConstants;

public class DoubtsApplication extends Application {
    private static final String TAG = "DoubtsApplication";
    private SharedPreferences mSharedPreferences;
    private AuthToken mAuthToken;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(StringConstants.PREFERENCES_NAME, 0);
        int userId = mSharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            mAuthToken = new AuthToken(userId,
                    mSharedPreferences.getString("username", ""),
                    mSharedPreferences.getString("auth_token", ""));
        }
    }

    public AuthToken getAuthToken() {
        Log.d(TAG, mAuthToken.getUsername());
        Log.d(TAG, mAuthToken.getToken());
        return mAuthToken;
    }

    public void setAuthToken(AuthToken authToken) {
        mSharedPreferences.edit().putInt("user_id", authToken.getUserId())
                .putString("username", authToken.getUsername())
                .putString("auth_token", authToken.getToken())
                .apply();
        mAuthToken = authToken;
    }
}
