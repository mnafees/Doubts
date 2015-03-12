/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import solutions.doubts.api.models.AuthToken;
import solutions.doubts.internal.StringConstants;

public class DoubtsApplication extends Application {

    private static final String TAG = "DoubtsApplication";
    private SharedPreferences mSharedPreferences;
    private AuthToken mAuthToken;

    private static DoubtsApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
        mSharedPreferences = getSharedPreferences(StringConstants.PREFERENCES_NAME, 0);
        int userId = mSharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            mAuthToken = new AuthToken(userId,
                    mSharedPreferences.getString("username", ""),
                    mSharedPreferences.getString("auth_token", ""));
        }
    }

    public static DoubtsApplication getInstance() {
        return INSTANCE;
    }

    public AuthToken getAuthToken() {
        if (mSharedPreferences.getInt("user_id", -1) == -1) {
            return null;
        }

        return mAuthToken;
    }

    public void setAuthToken(AuthToken authToken) {
        mSharedPreferences.edit()
                .putInt("user_id", authToken.getUserId())
                .putString("username", authToken.getUsername())
                .putString("auth_token", authToken.getToken())
                .apply();
        mAuthToken = authToken;
    }

    public void logOut() {
        // clear the Shared Preferences
        mSharedPreferences.edit().clear().apply();

        // start MainActivity
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
