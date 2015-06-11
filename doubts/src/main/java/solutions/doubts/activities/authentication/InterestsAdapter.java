/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.authentication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Random;

import me.grantland.widget.AutofitTextView;
import solutions.doubts.R;

public class InterestsAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mInterests;
    private String[] mColors = new String[] {"#F44336", "#E91E63", "#673AB7", "#2196F3",
            "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B",
            "#FFC107", "#FF9800", "#FF5722"};
    private int[] mTileColors;
    private boolean[] mChecked;
    private InterestToggleCallback mCallback;

    public InterestsAdapter(final Context context) {
        mContext = context;
    }

    public void setInterests(final String[] interests) {
        mInterests = interests;
        mTileColors = new int[mInterests.length];
        mChecked = new boolean[mInterests.length];
    }

    @Override
    public Object getItem(int position) {
        return mInterests[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mInterests.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_registeration_interests, null);
            AutofitTextView textView = (AutofitTextView)convertView.findViewById(R.id.text);
            if (mInterests[position].contains(" ") && mInterests[position].length() > 10) {
                textView.setSingleLine(false);
                textView.setMaxLines(2);
            }
            textView.setText(mInterests[position]);
            convertView.setBackgroundColor(randomColor(position));
            final ImageView check = (ImageView)convertView.findViewById(R.id.check);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (check.isShown()) {
                        mChecked[position] = false;
                        ScaleAnimation shrink = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        shrink.setFillAfter(true);
                        shrink.setDuration(100);
                        shrink.setInterpolator(new BounceInterpolator());
                        shrink.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                check.setVisibility(View.INVISIBLE);
                                mCallback.interestedDeselected(mInterests[position].replace(" ", "").toLowerCase());
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        check.startAnimation(shrink);
                    } else {
                        mChecked[position] = true;
                        ScaleAnimation grow = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        grow.setFillAfter(true);
                        grow.setDuration(100);
                        grow.setInterpolator(new BounceInterpolator());
                        grow.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                check.setVisibility(View.VISIBLE);
                                mCallback.interestSelected(mInterests[position].replace(" ", "").toLowerCase());
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        check.startAnimation(grow);
                    }
                }
            });
        }

        return convertView;
    }

    public void setInterestToggleCallback(final InterestToggleCallback callback) {
        mCallback = callback;
    }

    private int randomColor(final int position) {
        if (mTileColors[position] == 0) {
            int random = new Random().nextInt(getCount());
            if (position != 0 && position != getCount() - 1) {
                while (random == mTileColors[position - 1] && random == mTileColors[position + 1]) {
                    random = new Random().nextInt(getCount());
                }
            }
            mTileColors[position] = Color.parseColor(mColors[random]);
        }

        return mTileColors[position];
    }

    public interface InterestToggleCallback {

        void interestSelected(String interest);

        void interestedDeselected(String interest);

    }

}
