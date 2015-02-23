/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>
 */

package solutions.doubts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import solutions.doubts.activities.profile.ProfileActivity;

/**
 * The base activity of the app.
 */
public class MainActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.layout_login);
        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);
        logoView.startAnimation(popOut);
        final EditText emailUsername = (EditText)findViewById(R.id.emailUsername);
        emailUsername.startAnimation(popOut);
        final Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.startAnimation(popOut);*/
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
