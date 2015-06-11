/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.authentication;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.future.ResponseFuture;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.RealmList;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.api.models.Entity;
import solutions.doubts.internal.ApiConstants;

public class RegisterFragment extends Fragment implements InterestsAdapter.InterestToggleCallback {

    // UI elements
    @InjectView(R.id.main_container)
    View mMainContainer;
    @InjectView(R.id.name)
    MaterialEditText mName;
    @InjectView(R.id.username)
    MaterialEditText mUsername;
    @InjectView(R.id.competitive_exams)
    ExpandableGridView mCompetitiveExams;
    @InjectView(R.id.subjects)
    ExpandableGridView mSubjects;
    @InjectView(R.id.competitions)
    ExpandableGridView mCompetitions;

    // Other private members
    private String mEmail;
    private ArrayList<ResponseFuture<JsonObject>> mUsernameCheckFutures;
    private final RealmList<Entity> mInterests = new RealmList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DoubtsApplication.getInstance().getBus().register(this);
        mUsernameCheckFutures = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.layout_register, container, false);
        ButterKnife.inject(this, v);

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                cancelAllPendingUsernameCheckRequests();
                ResponseFuture<JsonObject> responseFuture = Ion.with(getActivity())
                        .load(ApiConstants.API_ENDPOINT + "/api/v1/users")
                        .addQuery("username", mUsername.getText().toString())
                        .asJsonObject();
                mUsernameCheckFutures.clear();
                mUsernameCheckFutures.add(responseFuture);
                responseFuture.setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            int count = result.get("count").getAsInt();
                            if (count > 0) {
                                mUsername.setError("This username already exists");
                            }
                        }
                    }
                });
            }
        });

        mCompetitiveExams.setExpanded(true);
        final InterestsAdapter competitiveExamsAdapter = new InterestsAdapter(getActivity());
        competitiveExamsAdapter.setInterestToggleCallback(this);
        competitiveExamsAdapter.setInterests(new String[]{"IIT JEE" ,"AIPMT" ,"SAT", "CAT", "UPSC", "CLAT",
                "GRE", "GMAT", "XAT", "CA", "Others"});
        mCompetitiveExams.setAdapter(competitiveExamsAdapter);

        mSubjects.setExpanded(true);
        InterestsAdapter subjectsAdapter = new InterestsAdapter(getActivity());
        subjectsAdapter.setInterestToggleCallback(this);
        subjectsAdapter.setInterests(new String[]{"Maths", "Physics", "Chemistry", "Computer Science", "Biology", "Economics",
                "Accounts", "Commerce" ,"History", "Geography", "Arts", "Others"});
        mSubjects.setAdapter(subjectsAdapter);

        mCompetitions.setExpanded(true);
        InterestsAdapter competitionsAdapter = new InterestsAdapter(getActivity());
        competitionsAdapter.setInterestToggleCallback(this);
        competitionsAdapter.setInterests(new String[]{"ACM ICPC", "IOI", "KVPY", "IMO", "NTSE", "NCO", "NSO", "Others"});
        mCompetitions.setAdapter(competitionsAdapter);

        return v;
    }

    @Subscribe
    public void onUserRegistrationEvent(final AuthenticationActivity.UserRegistrationEvent event) {
        mEmail = event.getEmail();
    }

    @OnClick(R.id.button)
    public void onButtonClicked() {
        if (mName.getError() != null || mUsername.getError() != null) {
            return;
        }

        if (TextUtils.isEmpty(mName.getText().toString())) {
            mName.setError("Oops! It seems you haven't entered anything");
            return;
        } else if (TextUtils.isEmpty(mUsername.getText().toString())) {
            mUsername.setError("Oops! It seems you haven't entered anything");
            return;
        } else if (mInterests.size() == 0) {
            Snackbar.make(getView(), "Please select at least one interest", Snackbar.LENGTH_LONG).show();
            return;
        }

        mMainContainer.setEnabled(false);
        Ion.with(getActivity())
                .load(ApiConstants.API_ENDPOINT + "/auth/register")
                .setBodyParameter("name", mName.getText().toString())
                .setBodyParameter("username", mUsername.getText().toString())
                .setBodyParameter("email", mEmail)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            Snackbar.make(getView(), getString(R.string.network_error_message), Snackbar.LENGTH_LONG)
                                    .show();
                            mMainContainer.setEnabled(true);
                        } else {
                            if (result.getHeaders().code() == 200) {
                                // temporary
                                Snackbar.make(getView(), "Registered", Snackbar.LENGTH_LONG)
                                        .show();
                            } else {
                                Snackbar.make(getView(), getString(R.string.network_error_message), Snackbar.LENGTH_LONG)
                                        .show();
                                mMainContainer.setEnabled(true);
                            }
                        }
                    }
                });
    }

    private void cancelAllPendingUsernameCheckRequests() {
        for (ResponseFuture<JsonObject> responseFuture : mUsernameCheckFutures) {
            responseFuture.cancel();
        }
    }

    @Override
    public void interestSelected(final String interest) {
        mInterests.add(Entity.newEntity().name(interest).create());
    }

    @Override
    public void interestedDeselected(final String interest) {
        for (Entity entity : mInterests) {
            if (entity.getName().equals(interest)) {
                mInterests.remove(entity);
                break;
            }
        }
    }

}
