/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import solutions.doubts.R;

public class SearchViewFragment extends Fragment {

    private OnBackListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.layout_feed_search_view, null);
        ImageButton backButton = (ImageButton)v.findViewById(R.id.back_button);
        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTransitionName("toolbarTransition");
        }
        EditText query = (EditText)v.findViewById(R.id.query);
        query.requestFocus();
        Drawable backIcon = backButton.getDrawable();
        backIcon.setColorFilter(getActivity().getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);
        backButton.setImageDrawable(backIcon);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.backPressed();
            }
        });
        return v;
    }

    public void setOnBackListener(OnBackListener listener) {
        mListener = listener;
    }

    public interface OnBackListener {

        public void backPressed();

    }

}
