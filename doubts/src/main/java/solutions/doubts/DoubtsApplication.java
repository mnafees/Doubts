/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import solutions.doubts.api.models.AuthToken;
import solutions.doubts.core.events.LogoutEvent;
import solutions.doubts.core.events.ResourceEvent;
import solutions.doubts.internal.StringConstants;

public class DoubtsApplication extends Application {

    private static final String TAG = "DoubtsApplication";

    private SharedPreferences mSharedPreferences;
    private AuthToken mAuthToken;
    private Bus mBus;

    private static DoubtsApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;
        mBus = new Bus(ThreadEnforcer.MAIN);
        mBus.register(this);
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


    public Bus getBus() {
        return mBus;
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

    public int getUserId() {
        return mSharedPreferences.getInt("user_id", -1);
    }

    public String getUsername() {
        return mSharedPreferences.getString("username", "");
    }

    @Subscribe
    public void onResourceEvent(final ResourceEvent event) {
        if (event.getType() == ResourceEvent.Type.UNAUTHORISED) {
            Toast.makeText(this, "Please login again to continue", Toast.LENGTH_LONG).show();
            logout();
        }
    }

    public void logout() {
        // clear the Shared Preferences
        mSharedPreferences.edit().clear().apply();
        // clear Realm
        //Realm.getInstance(this).de

        mBus.post(new LogoutEvent());

        // start MainActivity
        final Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
