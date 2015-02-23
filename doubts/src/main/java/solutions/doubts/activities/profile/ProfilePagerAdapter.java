/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.doubts.R;

public class ProfilePagerAdapter extends PagerAdapter {

    final int totalPages = 3;
    final Map<Integer, Drawable> tabIconMap = new HashMap<>(getCount());
    final List<View> views = new ArrayList<>(getCount());

    public ProfilePagerAdapter(Context context) {
        views.add(View.inflate(context, R.layout.layout_profile_about, null));
        views.add(View.inflate(context, R.layout.layout_profile_about, null));
        views.add(View.inflate(context, R.layout.layout_profile_about, null));
    }

    @Override
    public int getCount() {
        return this.totalPages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), position);
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeViewAt(position);
    }

    public void setIconForTab(int index, Drawable icon) {
        this.tabIconMap.put(index, icon);
    }

    public Drawable getIconForTab(int index) {
        return this.tabIconMap.get(index);
    }

}
