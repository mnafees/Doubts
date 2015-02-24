/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private final Map<Integer, Drawable> tabIconMap = new HashMap<>();
    private List<Fragment> fragments;

    public ProfilePagerAdapter(final FragmentManager manager,
                               final List<Fragment> fragments) {
        super(manager);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    public void setIconForTab(int index, Drawable icon) {
        this.tabIconMap.put(index, icon);
    }

    public Drawable getIconForTab(int index) {
        return this.tabIconMap.get(index);
    }

}
