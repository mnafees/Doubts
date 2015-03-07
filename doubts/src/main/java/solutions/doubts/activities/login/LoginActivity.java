/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import solutions.doubts.R;
import solutions.doubts.api.backend.AuthApi;
import solutions.doubts.internal.RestConstants;

public class LoginActivity extends ActionBarActivity {

    private AuthApi mApi;
    private EditText mEmail;
    private ViewGroup mSceneRoot;
    private CircleImageView mProfileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(RestConstants.API_ENDPOINT)
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mApi = restAdapter.create(AuthApi.class);

        /*final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ContentObservable.fromBroadcast(this, filter)
                .subscribe(new Action1<Intent>() {
                    @Override
                    public void call(Intent intent) {

                    }
                });*/

        final Animation popOut = AnimationUtils.loadAnimation(this, R.anim.login_pop_out);

        final ImageView logoView = (ImageView)findViewById(R.id.logoView);
        logoView.startAnimation(popOut);

        mEmail = (EditText)findViewById(R.id.email);
        mEmail.startAnimation(popOut);

        final Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.startAnimation(popOut);

        mSceneRoot = (ViewGroup)findViewById(R.id.innerContainer);
        final View sceneView = View.inflate(this, R.layout.layout_login_processing, null);
        final EditText sceneEmail = (EditText)sceneView.findViewById(R.id.email);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.this.mEmail.getText().length() > 0) {
                    final ChangeBounds transition = new ChangeBounds();
                    transition.setDuration(500);
                    final Scene scene = new Scene(mSceneRoot, sceneView);
                    scene.setEnterAction(new Runnable() {
                        @Override
                        public void run() {
                            sceneEmail.setText(LoginActivity.this.mEmail.getText());
                            final Observable<Response> response = LoginActivity.this.mApi
                                    .login(LoginActivity.this.mEmail.getText().toString())
                                    .cache();
                            response.observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Response>() {
                                        @Override
                                        public void call(Response response) {
                                            if (response.getUrl().contains("/auth/register")) {
                                                proceedWithRegistration(LoginActivity.this.mEmail.getText().toString());
                                            } else {
                                                // TODO: refine
                                                showEmailSentMessage();
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Toast.makeText(LoginActivity.this, throwable.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
                    final Observable<Response> response = LoginActivity.this.mApi
                            .register(encodeEmail(email), email,
                                    username.getText().toString(),
                                    name.getText().toString()).cache();
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
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(LoginActivity.this, throwable.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your name and username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mProfileImage = (CircleImageView)sceneView.findViewById(R.id.profileImage);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 1);
            }
        });

        final Scene scene = new Scene(mSceneRoot, sceneView);
        final AutoTransition transition = new AutoTransition();
        transition.setDuration(500);
        TransitionManager.go(scene, transition);
    }

    private String encodeEmail(final String email) {
        try {
            return URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should never happen
        }
        return null;
    }

    private void showEmailSentMessage() {
        final Scene scene = Scene.getSceneForLayout(mSceneRoot, R.layout.layout_email_sent, this);
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
                mProfileImage.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
