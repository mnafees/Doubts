package solutions.doubts.activities.newprofile;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.R;
import solutions.doubts.api.models.User;
import solutions.doubts.core.util.StringUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @InjectView(R.id.viewpager) ViewPager mViewPager;
    @InjectView(R.id.tablayout) TabLayout mTabLayout;
    @InjectView(R.id.action_bar) Toolbar mToolbar;
    private TabAdapter mTabAdapter;

    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.bio) TextView bio;
    @InjectView(R.id.author_image) SimpleDraweeView userImage;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.inject(this, v);
        initUi(savedInstanceState);
        updateUi();

        return v;
    }

    private void updateUi() {
        User user = UserCache.getInstance().getLastSelectedUser();
        name.setText(user.getName());
        bio.setText(user.getBio());
        if(user.getImage().getUrl() != null)
            userImage.setImageURI(Uri.parse(user.getImage().getUrl()));
        else
            userImage.setImageURI(Uri.parse(StringUtil.getProfileImageUrl(user)));
    }

    private void initUi(Bundle savedInstanceState) {
        ((ProfileActivity)getActivity()).setSupportActionBar(mToolbar);
        ((ProfileActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTabAdapter = new TabAdapter(this.getActivity(), getFragmentManager());

        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageSelected(int position) {
                mTabAdapter.onUserChanged(UserCache.getInstance().getLastSelectedUser());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mTabAdapter.onUserChanged(UserCache.getInstance().getLastSelectedUser());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
