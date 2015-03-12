/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import de.hdodenhof.circleimageview.CircleImageView;
import solutions.doubts.R;
import solutions.doubts.activities.profile.fragments.AboutFragment;
import solutions.doubts.activities.profile.fragments.AnswersFragment;
import solutions.doubts.activities.profile.fragments.QuestionsFragment;
import solutions.doubts.core.util.ColorHolder;
import solutions.doubts.core.util.PaletteHelperUtil;
import solutions.doubts.core.util.PaletteHelperUtilListener;
import solutions.doubts.thirdparty.ObservableVerticalScrollView;
import solutions.doubts.thirdparty.SlidingTabLayout;
import solutions.doubts.transitions.ChangeBoundsOnScrollTransition;

public class ProfileActivity extends ActionBarActivity implements PaletteHelperUtilListener,
        ObservableVerticalScrollView.OnScrollCallback {
    public static final String TAG = ProfileActivity.class.getName();

    private View mTopPanelContainer;

    private CircleImageView mProfileImage;
    private EditText mName;
    private EditText mBio;

    private Menu mMenu;
    private ObservableVerticalScrollView mScrollView;

    private ChangeBoundsOnScrollTransition mTopPanelTransition;
    private ColorHolder mColorHolder;
    private final PaletteHelperUtil mPaletteHelperUtil = new PaletteHelperUtil();
    private SlidingTabLayout mTabsLayout;
    private TabsAdapter mTabsAdapter;
    private View mTopPanel;

    private boolean mEditingMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_profile);

        mTopPanelContainer = findViewById(R.id.topPanelContainer);
        final View expandedTopPanel = getLayoutInflater().inflate(R.layout.layout_topbar_expanded, (ViewGroup) this.mTopPanelContainer, false);
        final View collapsedTopPanel = getLayoutInflater().inflate(R.layout.layout_topbar_collapsed, (ViewGroup) this.mTopPanelContainer, false);
        mTopPanel = expandedTopPanel.findViewById(R.id.top_panel);

        mTopPanelTransition = new ChangeBoundsOnScrollTransition((ViewGroup)this.mTopPanelContainer,
                (ViewGroup) expandedTopPanel, (ViewGroup) collapsedTopPanel);
        mTopPanelTransition.setDuration(getResources()
                .getDimensionPixelSize(R.dimen.profile_actionbar_expanded_height) - getActionBarSize());

        mPaletteHelperUtil.setPaletteHelperUtilListener(this);

        mName = (EditText) expandedTopPanel.findViewById(R.id.name);
        mName.setFocusable(false);
        mName.setFocusableInTouchMode(false);
        mName.setClickable(false);
        mBio = (EditText) expandedTopPanel.findViewById(R.id.bio);
        mBio.setFocusable(false);
        mBio.setFocusableInTouchMode(false);
        mBio.setClickable(false);

        setSupportActionBar((Toolbar) expandedTopPanel.findViewById(R.id.action_bar));
        setupActionBar();

        mScrollView = (ObservableVerticalScrollView)findViewById(R.id.scrollView);
        mScrollView.setOnScrollCallback(this);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mTabsAdapter);

        mTabsAdapter.addTab("About ", new AboutFragment(), 0);
        mTabsAdapter.addTab("Questions", new QuestionsFragment(), 1);
        mTabsAdapter.addTab("Answers", new AnswersFragment(), 2);
        mTabsAdapter.addTab("Followers", new AnswersFragment(), 3);

        mTabsLayout = (SlidingTabLayout)expandedTopPanel.findViewById(R.id.sliding_tab_layout);
        mTabsLayout.setCustomTabView(R.layout.layout_tab_strip, android.R.id.text1);
        mTabsLayout.setDistributeEvenly(true);
        mTabsLayout.setViewPager(viewPager);

        mTopPanelContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (ProfileActivity.this.mTopPanelTransition.animateOnScroll(mScrollView.getScrollY())) {
                    ProfileActivity.this.mTopPanelContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });

        mProfileImage = (CircleImageView) expandedTopPanel.findViewById(R.id.profileImage);
        setImage();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_16dp));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }
    }

    public void setImage(/* final String url */) {
        mProfileImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Picasso.with(ProfileActivity.this)
                                .load("https://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-xfp1/v/t1.0-9/10405660_766257123461357_6260509899989635471_n.jpg?oh=cf71870bfc7130ff2e1b4923d8088a3f&oe=557EDD1A&__gda__=1438388115_facbd34c0394105ab06d1e6f650c2a39")
                                .resize(mProfileImage.getWidth(), mProfileImage.getHeight())
                                .centerCrop()
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        mPaletteHelperUtil.generatePalette(source);
                                        return source;
                                    }

                                    @Override
                                    public String key() {
                                        return "DBTS";
                                    }
                                })
                                .into(mProfileImage);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        if (mColorHolder != null) {
            setInEditingMode(mEditingMode);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                setInEditingMode(!mEditingMode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPaletteGenerated(ColorHolder colorHolder) {
        mColorHolder = colorHolder;
        setThemeColors(mColorHolder);
    }

    private void setThemeColors(final ColorHolder colorHolder) {
        // change color of the top panel and the icons
        mTopPanel.setBackgroundColor(colorHolder.background);
        final Drawable backButton = getResources().getDrawable(R.drawable.ic_arrow_back_white_16dp);
        backButton.mutate().setColorFilter(colorHolder.bodyText, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(backButton);
        if (mMenu != null) {
            final Drawable editButton = getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
            editButton.mutate().setColorFilter(colorHolder.bodyText, PorterDuff.Mode.SRC_IN);
            mMenu.findItem(R.id.action_edit_profile).setIcon(editButton);
        }

        // change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorHolder.backgroundSecondary);
            getWindow().setNavigationBarColor(colorHolder.backgroundSecondary);
        }

        // change color of image view border
        mProfileImage.setBorderColor(colorHolder.bodyText);

        // change the color of the main toolbar
        mName.setTextColor(colorHolder.bodyText);
        mBio.setTextColor(colorHolder.titleText);

        // change color of the tab layout
        mTabsLayout.setTextColor(colorHolder.bodyText);
        mTabsLayout.setSelectedIndicatorColors(colorHolder.backgroundSecondary);
    }

    @Override
    public void onScrollChanged(int yScroll) {
        mTopPanelTransition.animateOnScroll(yScroll);
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mColorHolder = (ColorHolder) savedInstanceState.getSerializable("colorHolder");
        mEditingMode = savedInstanceState.getBoolean("editingMode");

        setThemeColors(mColorHolder);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("colorHolder", mColorHolder);
        outState.putBoolean("editingMode", mEditingMode);

        super.onSaveInstanceState(outState);
    }

    private void setInEditingMode(final boolean editingMode) {
        if (editingMode) {
            final Drawable icon = getResources().getDrawable(R.drawable.ic_done_white_24dp);
            icon.mutate().setColorFilter(mColorHolder.bodyText, PorterDuff.Mode.SRC_IN);
            mMenu.findItem(R.id.action_edit_profile).setIcon(icon);
            mName.setBackgroundColor(mColorHolder.backgroundSecondary);
            mName.setFocusable(true);
            mName.setFocusableInTouchMode(true);
            mName.setClickable(true);
            mName.requestFocus();
            mBio.setBackgroundColor(mColorHolder.backgroundSecondary);
            mBio.setFocusable(true);
            mBio.setFocusableInTouchMode(true);
            mBio.setClickable(true);
            mEditingMode = true;
        } else {
            final Drawable icon = getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
            icon.mutate().setColorFilter(mColorHolder.bodyText, PorterDuff.Mode.SRC_IN);
            mMenu.findItem(R.id.action_edit_profile).setIcon(icon);
            mName.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mName.setFocusable(false);
            mName.setFocusableInTouchMode(false);
            mName.setClickable(false);
            mBio.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mBio.setFocusable(false);
            mBio.setFocusableInTouchMode(false);
            mBio.setClickable(false);
            mEditingMode = false;
        }
    }

}
