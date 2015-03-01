/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import solutions.doubts.R;

public class LoginActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);
        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);
        logoView.startAnimation(popOut);
        final EditText emailUsername = (EditText)findViewById(R.id.emailUsername);
        emailUsername.startAnimation(popOut);
        final Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.startAnimation(popOut);
    }

}
