/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.transitions.everywhere.AutoTransition;
import android.transitions.everywhere.ChangeBounds;
import android.transitions.everywhere.Scene;
import android.transitions.everywhere.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import solutions.doubts.R;
import solutions.doubts.core.api.authentication.rest.RestAuthenticationService;
import solutions.doubts.internal.RestConstants;

public class LoginActivity extends ActionBarActivity {

    private RestAuthenticationService restAuthenticationService;
    private EditText email;
    private ViewGroup sceneRoot;
    private CircleImageView profileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setFollowRedirects(true);
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(RestConstants.API_ENDPOINT)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        this.restAuthenticationService = new RestAuthenticationService(restAdapter);

        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);
        final Animation popIn = AnimationUtils.loadAnimation(this, R.anim.login_pop_in);

        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        logoView.startAnimation(popOut);
        this.email = (EditText)findViewById(R.id.email);
        this.email.startAnimation(popOut);
        final Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.startAnimation(popOut);

        this.sceneRoot = (ViewGroup)findViewById(R.id.innerContainer);
        final View sceneView = View.inflate(this, R.layout.layout_login_processing, null);
        final EditText sceneEmail = (EditText)sceneView.findViewById(R.id.email);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.this.email.getText().length() > 0) {
                    final ChangeBounds transition = new ChangeBounds();
                    transition.setDuration(500);
                    final Scene scene = new Scene(sceneRoot, sceneView);
                    scene.setEnterAction(new Runnable() {
                        @Override
                        public void run() {
                            sceneEmail.setText(LoginActivity.this.email.getText());
                            try {
                                final Observable<Response> response = LoginActivity.this.restAuthenticationService
                                        .login(LoginActivity.this.email.getText().toString());
                                response.observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Response>() {
                                    @Override
                                    public void call(Response response) {
                                        if (response.getUrl().contains("/auth/register")) {
                                            proceedWithRegistration(LoginActivity.this.email.getText().toString());
                                        } else {
                                            showEmailSentMessage();
                                        }
                                        //Toast.makeText(LoginActivity.this, "An error occurred during authentication", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (NetworkErrorException e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    TransitionManager.go(scene, transition);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void proceedWithRegistration(final String email) {
        final View sceneView = View.inflate(this, R.layout.layout_register, null);
        final EditText name = (EditText)sceneView.findViewById(R.id.name);
        final EditText username = (EditText)sceneView.findViewById(R.id.username);
        final Button registerButton = (Button)sceneView.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() > 0 && username.getText().length() > 0) {
                    try {
                        final Observable<Response> response = LoginActivity.this.restAuthenticationService
                                .register(email, username.getText().toString(), name.getText().toString());
                        response.observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Response>() {
                                    @Override
                                    public void call(Response response) {
                                        if (response.getStatus() == 200) {
                                            LoginActivity.this.showEmailSentMessage();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "An error occurred during authentication", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (NetworkErrorException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your name and username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.profileImage = (CircleImageView)sceneView.findViewById(R.id.profileImage);
        this.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
            }
        });
        final Scene scene = new Scene(this.sceneRoot, sceneView);
        final AutoTransition transition = new AutoTransition();
        transition.setDuration(500);
        TransitionManager.go(scene, transition);
    }

    private void showEmailSentMessage() {
        final Scene scene = Scene.getSceneForLayout(this.sceneRoot, R.layout.layout_email_sent, this);
        final AutoTransition transition = new AutoTransition();
        transition.setDuration(500);
        TransitionManager.go(scene, transition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            try {
                InputStream stream = getContentResolver().openInputStream(
                        data.getData());
                final Bitmap bm = BitmapFactory.decodeStream(stream);
                stream.close();
                this.profileImage.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
