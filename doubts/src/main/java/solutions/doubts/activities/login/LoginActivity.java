/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import solutions.doubts.R;

public class LoginActivity extends ActionBarActivity {

    private EditText email;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);
        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);
        logoView.startAnimation(popOut);
        this.email = (EditText)findViewById(R.id.email);
        this.email.startAnimation(popOut);
        this.loginButton = (Button)findViewById(R.id.loginButton);
        this.loginButton.startAnimation(popOut);

        final ViewGroup sceneRoot = (ViewGroup)findViewById(R.id.innerContainer);
        final View sceneView = View.inflate(this, R.layout.layout_login_processing, null);
        final Context context = this;
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChangeBounds transition = new ChangeBounds();
                transition.setDuration(500);
                transition.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        new CountDownTimer(3000, 1000) {

                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                final Transition transition1 = new android.transitions.everywhere.AutoTransition();
                                final Scene scene = Scene.getSceneForLayout(sceneRoot, R.layout.layout_register, context);
                                TransitionManager.go(scene, transition1);
                            }
                        }.start();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });
                final Scene scene = new Scene(sceneRoot, sceneView);
                TransitionManager.go(scene, transition);
            }
        });
    }

}
