/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;

public class TabsAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {

    private final HashMap<Integer, Fragment> mFragments;
    private final ArrayList<Integer> mTabNums;
    private final ArrayList<CharSequence> mTabTitles;

    @SuppressLint("UseSparseArrays")
    public TabsAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new HashMap<>();
        mTabNums = new ArrayList<>();
        mTabTitles = new ArrayList<>();
    }

    public void addTab(String tabTitle, Fragment newFragment, int tabId) {
        mTabTitles.add(tabTitle);
        mFragments.put(tabId, newFragment);
        mTabNums.add(tabId);
        notifyDataSetChanged();
    }

    public Fragment getTabFragment(int tabNum) {
        if (mFragments.containsKey(tabNum)) {
            return mFragments.get(tabNum);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(mTabNums.get(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
