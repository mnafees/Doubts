/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.R;
import solutions.doubts.api.models.User;

public class AboutFragment extends Fragment implements UserInfoView {

    @InjectView(R.id.about_header)
    TextView mAboutHeader;

    @InjectView(R.id.question_count)
    TextView mQuestionCount;

    @InjectView(R.id.answer_count)
    TextView mAnswersCount;
    private User mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.layout_profile_about_feed, container, false);
        ButterKnife.inject(this, v);
        updateUi();
        //final ObservableScrollView scrollView = (ObservableScrollView)v.findViewById(R.id.scrollView);
        //scrollView.setScrollViewCallbacks((ProfileActivity)getActivity());
        return v;
    }

    @Override
    public void setUser(User user) {
        mUser = user;
        updateUi();
    }

    private void updateUi() {
        if(getView() != null) {
            mAboutHeader.setText(getActivity().getString(R.string.about_header_prefix) + mUser.getName());
            mQuestionCount.setText(Integer.toString(mUser.getQuestionCount()));
            mAnswersCount.setText(Integer.toString(mUser.getAnswerCount()));
        }
    }
}
