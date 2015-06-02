/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.login;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import solutions.doubts.R;

public class InterestsAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mInterests;
    private String[] mTileColors = new String[] {"#F44336", "#E91E63", "#9C27B0", "#673AB7",
            "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
            "#FFEB3B", "#FFC107", "#FF9800", "#FF5722"};

    public InterestsAdapter(final Context context) {
        mContext = context;
    }

    public void setInterests(final String[] interests) {
        mInterests = interests;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_registeration_interests, null);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.text);
        textView.setText(mInterests[position]);
        int w = convertView.getWidth();
        textView.setTextSize(w - 60);

        return convertView;
    }

}
