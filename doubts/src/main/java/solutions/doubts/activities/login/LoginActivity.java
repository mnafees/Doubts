/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.transitions.everywhere.AutoTransition;
import android.transitions.everywhere.ChangeImageTransform;
import android.transitions.everywhere.Scene;
import android.transitions.everywhere.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;
import solutions.doubts.R;
import solutions.doubts.internal.RestConstants;

public class LoginActivity extends ActionBarActivity {

    private EditText mEmail;
    private ViewGroup mSceneRoot;
    private CircleImageView mProfileImage;

    private OkHttpClient mOkHttpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_login);

        mOkHttpClient = new OkHttpClient();

        mEmail = (EditText)findViewById(R.id.email);
        final Button loginButton = (Button)findViewById(R.id.loginButton);

        mSceneRoot = (ViewGroup)findViewById(R.id.innerContainer);
        final View sceneView = View.inflate(this, R.layout.layout_login_processing, null);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.this.mEmail.getText().length() > 0) {
                    final Scene scene = new Scene(mSceneRoot, sceneView);
                    scene.setEnterAction(new Runnable() {
                        @Override
                        public void run() {
                            final String email = LoginActivity.this.mEmail.getText().toString();
                            final RequestBody requestBody = new FormEncodingBuilder()
                                    .add("email", email)
                                    .build();
                            final Request req = new Request.Builder()
                                    .url(RestConstants.API_ENDPOINT + "/auth/login")
                                    .post(requestBody)
                                    .build();

                            mOkHttpClient.newCall(req).enqueue(new Callback() {
                                // obtain the main looper since we need to make changes to the UI
                                final Handler mainHandler = new Handler(LoginActivity.this.getMainLooper());

                                @Override
                                public void onFailure(Request request, final IOException e) {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Response response) throws IOException {
                                    if (response.request().urlString().contains("register")) {
                                        // user needs to register
                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                proceedWithRegistration(email);
                                            }
                                        });
                                    } else {
                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showEmailSentMessage();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                    TransitionManager.go(scene);
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
        TransitionManager.go(scene, new ChangeImageTransform());
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
        TransitionManager.go(scene);
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
