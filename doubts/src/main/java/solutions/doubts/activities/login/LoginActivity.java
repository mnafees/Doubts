/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.transitions.everywhere.ChangeBounds;
import android.transitions.everywhere.Scene;
import android.transitions.everywhere.Transition;
import android.transitions.everywhere.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import solutions.doubts.R;
import solutions.doubts.core.api.authentication.rest.RestAuthenticationService;
import solutions.doubts.core.util.RestAdapterUtil;

public class LoginActivity extends ActionBarActivity {

    private EditText email;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);
        final Animation popIn = AnimationUtils.loadAnimation(this, R.anim.login_pop_in);

        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        logoView.startAnimation(popOut);
        this.email = (EditText)findViewById(R.id.email);
        this.email.startAnimation(popOut);
        this.loginButton = (Button)findViewById(R.id.loginButton);
        this.loginButton.startAnimation(popOut);

        final ViewGroup sceneRoot = (ViewGroup)findViewById(R.id.innerContainer);
        final View sceneView = View.inflate(this, R.layout.layout_login_processing, null);
        final EditText sceneEmail = (EditText)sceneView.findViewById(R.id.email);
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.this.email.getText().length() > 0) {
                    final ChangeBounds transition = new ChangeBounds();
                    transition.setDuration(500);
                    transition.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {}

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            final RestAuthenticationService authenticationService = new RestAuthenticationService(RestAdapterUtil.getRestAdapter());
                            final String message = authenticationService.authenticate(LoginActivity.this.email.getText().toString());
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {}

                        @Override
                        public void onTransitionPause(Transition transition) {}

                        @Override
                        public void onTransitionResume(Transition transition) {}

                    });
                    final Scene scene = new Scene(sceneRoot, sceneView);
                    scene.setEnterAction(new Runnable() {
                        @Override
                        public void run() {
                            sceneEmail.setText(LoginActivity.this.email.getText());
                        }
                    });
                    TransitionManager.go(scene, transition);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
