/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.core.util.TransitionUtil;
import solutions.doubts.internal.ApiConstants;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private static final String EMAIL_HINT = "What's your email address?";
    private static final String EMPTY_ERROR = "Oops, looks like you haven't entered anything.";

    // UI elements
    @InjectView(R.id.main_container)
    View mMainContainer;
    @InjectView(R.id.text_input_layout)
    TextInputLayout mTextInputLayout;
    @InjectView(R.id.email)
    AppCompatAutoCompleteTextView mEmail;
    @InjectView(R.id.throbber)
    ProgressBar mThrobber;
    @InjectView(R.id.email_message)
    TextView mEmailMessage;
    @InjectView(R.id.login_elements)
    View mLoginElementsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DoubtsApplication.getInstance().getBus().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.layout_login, container, false);
        ButterKnife.inject(this, v);

        mThrobber.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);

        mTextInputLayout.setErrorEnabled(true);
        mTextInputLayout.setHint(EMAIL_HINT);

        AccountManager manager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        ArrayList<String> emails = new ArrayList<>();
        for (Account account: list) {
            if (account.name.contains("@") && !emails.contains(account.name)) {
                emails.add(account.name);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, emails);
        mEmail.setAdapter(adapter);
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTextInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return v;
    }

    @OnClick(R.id.button)
    public void onButtonClicked() {
        if (!validateText(mEmail)) {
            mTextInputLayout.setError(EMPTY_ERROR);
            return;
        }
        final String email = mEmail.getText().toString();
        TransitionUtil.hideWithFadeOut(mLoginElementsContainer);
        TransitionUtil.showWithFadeIn(mThrobber);

        Ion.with(this)
                .load(ApiConstants.API_ENDPOINT + "/auth/login")
                .setBodyParameter("email", email)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            TransitionUtil.hideWithFadeOut(mThrobber);
                            Snackbar.make(mMainContainer, R.string.network_error_message, Snackbar.LENGTH_SHORT)
                                    .show();
                            TransitionUtil.showWithFadeIn(mLoginElementsContainer);
                            return;
                        }

                        if (result.getHeaders().code() == 200) {
                            if (result.getRequest().getUri().getLastPathSegment().equals("register")) {
                                // user needs to register
                                TransitionUtil.hideWithFadeOut(mThrobber);
                                TransitionUtil.showWithFadeIn(mLoginElementsContainer);
                                DoubtsApplication.getInstance().getBus().post(new
                                        AuthenticationActivity.UserRegistrationEvent(email));
                            } else {
                                // login
                                showEmailSentMessage();
                            }
                        } else {
                            TransitionUtil.hideWithFadeOut(mThrobber);
                            Snackbar.make(mMainContainer, R.string.network_error_message, Snackbar.LENGTH_SHORT)
                                    .show();
                            TransitionUtil.showWithFadeIn(mLoginElementsContainer);
                        }
                    }
                });
    }

    private void showEmailSentMessage() {
        mEmailMessage.setText("Please check " + mEmail.getText().toString() + " and click on the link in the email we've " +
                "sent you to log in.");
        TransitionUtil.hideWithFadeOut(mThrobber);
        TransitionUtil.showWithFadeIn(mEmailMessage);
    }

    private boolean validateText(final EditText editText) {
        return !(TextUtils.isEmpty(editText.getText()) ||
                editText.getText().toString().split(" ").length == 0);
    }

    // http://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception-can-not-perform-this-action-after-onsa
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

}
