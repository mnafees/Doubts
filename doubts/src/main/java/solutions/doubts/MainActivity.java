/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */

package solutions.doubts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import solutions.doubts.activities.feed.FeedActivity;
import solutions.doubts.activities.login.LoginActivity;
import solutions.doubts.internal.StringConstants;

/**
 * The base activity of the app.
 */
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = getSharedPreferences(StringConstants.PREFERENCES_NAME, 0);
        final SharedPreferences.Editor preferencesEditor = preferences.edit();

        if (getIntent().getData() != null) {
            final Uri data = getIntent().getData();
            if (data.getScheme().equals("doubts") &&
                data.getPath().startsWith("doubts.solutions/auth/token/")) {
                final String authSignature = data.toString().replace("doubts://doubts.solutions/auth/token/", "");
                final String[] authParams = authSignature.split("/");

                final int id = Integer.valueOf(authParams[0]);
                final String username = authParams[1];
                final String authToken = authParams[2];

                preferencesEditor.putInt("user_id", id)
                        .putString("username", username)
                        .putString("auth_token", authToken)
                        .apply();

                final Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            }
        } else {
            if (/*preferences.contains("user_id") &&
                    preferences.contains("username") &&*/
                    preferences.contains("auth_token")) {
                final Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            } else {
                final Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }

        finish();
    }

}
