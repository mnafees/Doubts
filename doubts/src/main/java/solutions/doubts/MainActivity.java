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
import android.widget.Toast;

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
                    data.getPath().contains("/auth/token/")) {
                final int length = data.getPathSegments().size();
                if (length != 5) {
                    // mischief alert!
                    Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                    startLoginActivity();
                } else {
                    try {
                        final int id = Integer.valueOf(data.getPathSegments().get(length - 3));
                        final String username = data.getPathSegments().get(length - 2);
                        final String authToken = data.getPathSegments().get(length - 1);

                        preferencesEditor.putInt("user_id", id)
                                .putString("username", username)
                                .putString("auth_token", authToken)
                                .apply();

                        startFeedActivity();
                    } catch (NumberFormatException e) {
                        // mischief alert!
                        Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                        startLoginActivity();
                    }
                }
            }
        } else {
            if (preferences.contains("user_id") &&
                    preferences.contains("username") &&
                    preferences.contains("auth_token")) {
                startFeedActivity();
            } else {
                startLoginActivity();
            }
        }

        finish();
    }

    private void startLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startFeedActivity() {
        final Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

}
