/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transitions.everywhere.AutoTransition;
import android.transitions.everywhere.Scene;
import android.transitions.everywhere.Slide;
import android.transitions.everywhere.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import solutions.doubts.R;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL_HINT = "What's your email address?";
    private static final String EMPTY_ERROR = "Oops, looks like you haven't entered anything.";

    // UI elements
    @InjectView(R.id.main_container) CoordinatorLayout mMainContainer;
    @InjectView(R.id.text_input_layout) TextInputLayout mTextInputLayout;
    @InjectView(R.id.email_username) AppCompatAutoCompleteTextView mEmailUsername;
    @InjectView(R.id.button) Button mButton;
    @InjectView(R.id.throbber) ProgressBar mThrobber;
    @InjectView(R.id.email_message) TextView mEmailMessage;
    @InjectViews({R.id.email_username, R.id.button}) List<View> mLoginElements;
    @InjectViews({R.id.email_username, R.id.button}) List<View> mRegisterElements;

    // Other private members
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private String mEmail;
    private Snackbar mNetworkErrorSnackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        ButterKnife.inject(this);

        mThrobber.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);

        mTextInputLayout.setErrorEnabled(true);
        mTextInputLayout.setHint(EMAIL_HINT);

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        ArrayList<String> emails = new ArrayList<>();
        for (Account account: list) {
            if (account.name.contains("@") && !emails.contains(account.name)) {
                emails.add(account.name);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, emails);
        mEmailUsername.setAdapter(adapter);
        mEmailUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTextInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNetworkErrorSnackbar = Snackbar.make(mMainContainer, R.string.network_error_message, Snackbar.LENGTH_LONG);
        mNetworkErrorSnackbar.setAction("Close", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetworkErrorSnackbar.dismiss();
            }
        });
    }

    static final ButterKnife.Setter<View, Boolean> SHOW = new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
            view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        }
    };

    @OnClick(R.id.button)
    public void onButtonClicked() {
        proceedWithRegistration();
        /*if (mButton.getText().equals(getString(R.string.login_button_text))) { // login
            if (!validateText(mEmailUsername)) {
                mTextInputLayout.setError(EMPTY_ERROR);
                return;
            }
            mEmail = mEmailUsername.getText().toString();
            ButterKnife.apply(mLoginElements, SHOW, false);
            show(mThrobber);

            Ion.with(this)
                    .load(RestConstants.API_ENDPOINT + "/auth/login")
                    .setBodyParameter("email", mEmail)
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {
                            if (e != null) {
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        hide(mThrobber);
                                        mNetworkErrorSnackbar.show();
                                        ButterKnife.apply(mLoginElements, SHOW, true);
                                    }
                                });
                                return;
                            }

                            if (result.getHeaders().code() == 200) {
                                if (result.getRequest().getUri().getLastPathSegment().equals("register")) {
                                    // user needs to register
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            proceedWithRegistration();
                                        }
                                    });
                                } else {
                                    // login
                                    mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showEmailSentMessage();
                                        }
                                    });
                                }
                            } else {
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        hide(mThrobber);
                                        Snackbar.make(mMainContainer, R.string.network_error_message, Snackbar.LENGTH_SHORT)
                                                .show();
                                        ButterKnife.apply(mLoginElements, SHOW, true);
                                    }
                                });
                            }
                        }
                    });
        } else { // register
            if (!validateText(mEmailUsername)) {
                Snackbar.make(mMainContainer, "Please enter your name", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            ButterKnife.apply(mRegisterElements, SHOW, false);
            show(mThrobber);

            Ion.with(this)
                    .load(RestConstants.API_ENDPOINT + "/auth/register")
                    .setBodyParameter("email", mEmail)
                    .setBodyParameter("username", mEmailUsername.getText().toString())
                    .asString()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<String>>() {
                        @Override
                        public void onCompleted(Exception e, Response<String> result) {

                        }
                    });

            /*final RequestBody requestBody = new FormEncodingBuilder()
                    .add("email", mEmail)
                    .add("name", mEmailName.getText().toString())
                    .add("username", mUsername.getText().toString())
                    .build();
            final Request req = new Request.Builder()
                    .url(RestConstants.API_ENDPOINT + "/auth/register")
                    .post(requestBody)
                    .build();
            mOkHttpClient.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, final IOException e) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            hide(mThrobber);
                            Toast.makeText(LoginActivity.this, "Sorry, we could not process your request. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                            ButterKnife.apply(mRegisterElements, SHOW, true);
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.code() == 200) {
                        mMainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showEmailSentMessage();
                            }
                        });
                    } else {
                        onFailure(response.request(), null);
                    }
                }
            });*/
        //}
    }

    private void proceedWithRegistration() {
        View regView = View.inflate(this, R.layout.layout_register, null);
        Scene scene = new Scene((ViewGroup)findViewById(R.id.scene_root), regView);
        Slide slide = new Slide(Gravity.RIGHT);
        slide.setInterpolator(new AccelerateDecelerateInterpolator());
        TransitionManager.go(scene, new AutoTransition());
        ExpandableGridView competitiveExams = (ExpandableGridView)regView.findViewById(R.id.competetive_exams);
        competitiveExams.setExpanded(true);
        InterestsAdapter competitiveExamsAdapter = new InterestsAdapter(this);
        competitiveExamsAdapter.setInterests(new String[]{"IIT JEE" ,"AIPMT" ,"SAT", "CAT", "GMAT", "UPSC", "CLAT",
                "GRE", "GMAT", "XAT", "CA", "Others"});
        competitiveExams.setAdapter(competitiveExamsAdapter);
        /*mEmailUsername.setHint("Username");
        mEmailUsername.setText("");
        mButton.setText("Register");
        hide(mThrobber);
        ButterKnife.apply(mRegisterElements, SHOW, true);*/
    }

    private void showEmailSentMessage() {
        mEmailMessage.setText("Please check " + mEmail + " and click on the link in the email we've " +
                "sent you to log in.");
        hide(mThrobber);
        show(mEmailMessage);
    }

    private boolean validateText(final EditText editText) {
        return !(TextUtils.isEmpty(editText.getText()) ||
                editText.getText().toString().split(" ").length == 0);
    }

    private void hide(final View view) {
        view.setVisibility(View.INVISIBLE);
    }

    private void show(final View view) {
        view.setVisibility(View.VISIBLE);
    }

}
