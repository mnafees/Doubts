/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */

package solutions.doubts;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import solutions.doubts.activities.feed.FeedActivity;
import solutions.doubts.activities.login.LoginActivity;

import static android.support.v4.app.NotificationManagerCompat.*;

/**
 * The base activity of the app.
 */
public class MainActivity extends ActionBarActivity {
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getData() != null) {
            if (getIntent().getData().getScheme().equals("doubts") &&
                getIntent().getData().getPath().contains("/auth/token/")) {
                getSharedPreferences("solutions.doubts.internal_data", 0).edit()
                        .putString("auth_token", getIntent().getData().getLastPathSegment())
                        .commit();

                final Intent intent = new Intent(this, FeedActivity.class);
                startActivity(intent);
            }
        } else {
            if (getSharedPreferences("solutions.doubts.internal_data", 0).contains("auth_token")) {
                Log.d(TAG, getSharedPreferences("solutions.doubts.internal_data", 0).getString("auth_token", ""));
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
