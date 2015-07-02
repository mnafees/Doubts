package solutions.doubts.activities.newprofile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import solutions.doubts.R;
import solutions.doubts.activities.feed.QuestionFragment;
import solutions.doubts.api.models.User;
import solutions.doubts.fragments.UserListFragment;


public class TabAdapter extends FragmentPagerAdapter implements UserObserver {
    private SparseArray<Pair<String, Fragment>> mFragments = new SparseArray<>();

    public TabAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragments.append(0,
                Pair.create(context.getString(R.string.profile_about),
                        (Fragment) new AboutFragment()));
        mFragments.append(1,
                Pair.create(context.getString(R.string.profile_questions),
                        (Fragment)new QuestionFragment()));
        mFragments.append(2,
                Pair.create(context.getString(R.string.profile_answers),
                        (Fragment)new AboutFragment()));
        mFragments.append(3,
                Pair.create(context.getString(R.string.profile_following),
                        (Fragment)new UserListFragment()));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position).second;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragments.get(position).first;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public void onUserChanged(User user) {
        for(int i=0; i<mFragments.size(); i++) {
            ((UserObserver)mFragments.get(i).second).onUserChanged(user);
        }
    }
}
