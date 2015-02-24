/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import solutions.doubts.R;
import solutions.doubts.activities.profile.fragments.AboutFragment;
import solutions.doubts.activities.profile.fragments.AnswersFragment;
import solutions.doubts.activities.profile.fragments.QuestionsFragment;
import solutions.doubts.core.util.ColorHolder;
import solutions.doubts.core.util.PaletteHelperUtil;
import solutions.doubts.core.util.PaletteHelperUtilListener;

public class ProfileActivity extends ActionBarActivity implements PaletteHelperUtilListener,
        ObservableScrollViewCallbacks {
    public static final String TAG = "ProfileActivity";

    private CircleImageView imageView;
    private CircleImageView smallImageView;
    private Toolbar actionBar;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private TextView name;
    private TextView bio;
    private ColorHolder colorHolder;
    private View topPanel;
    private Menu menu;

    private final PaletteHelperUtil paletteHelperUtil = new PaletteHelperUtil();

    private int actionBarSize;
    private int separationNameProfilePic;
    private int initialNameX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_profile);
        topPanel = findViewById(R.id.top_panel);

        this.paletteHelperUtil.setPaletteHelperUtilListener(this);

        this.name = (TextView)findViewById(R.id.name);
        this.bio = (TextView)findViewById(R.id.bio);

        this.actionBar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(this.actionBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }

        final List<Fragment> fragments = new ArrayList<>();
        final AboutFragment aboutFragment = new AboutFragment();
        fragments.add(aboutFragment);
        final QuestionsFragment questionsFragment = new QuestionsFragment();
        fragments.add(questionsFragment);
        final AnswersFragment answersFragment = new AnswersFragment();
        fragments.add(answersFragment);

        final ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), fragments);
        adapter.setIconForTab(0, getResources().getDrawable(R.drawable.ic_account_circle_purple_24dp));
        adapter.setIconForTab(1, getResources().getDrawable(R.drawable.ic_live_help_purple_24dp));
        adapter.setIconForTab(2, getResources().getDrawable(R.drawable.ic_spell_check_purple_24dp));

        this.viewPager = (ProfileViewPager)findViewById(R.id.viewPager);
        this.viewPager.setAdapter(adapter);
        this.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
        Log.d(TAG, Integer.toString(topPanel.getMeasuredHeight()));
        Log.d(TAG, Integer.toString(topPanel.getHeight()));
        ViewHelper.setTranslationY(this.viewPager, topPanel.getMeasuredHeight());

        final ObservableScrollView scrollView = (ObservableScrollView)findViewById(R.id.scrollView);
        scrollView.setScrollViewCallbacks(this);

        this.actionBarSize = getActionBarSize();
        this.separationNameProfilePic = getResources().getDimensionPixelSize(R.dimen.profile_action_bar_separation_name_profile);

        this.tabHost = (MaterialTabHost)findViewById(R.id.materialTabHost);
        for (int i = 0; i < adapter.getCount(); ++i) {
            tabHost.addTab(
                    tabHost.newTab()
                           .setIcon(adapter.getIconForTab(i))
                           .setTabListener(new MaterialTabListener() {
                               @Override
                               public void onTabSelected(MaterialTab materialTab) {
                                   viewPager.setCurrentItem(materialTab.getPosition());
                               }

                               @Override
                               public void onTabReselected(MaterialTab materialTab) {}

                               @Override
                               public void onTabUnselected(MaterialTab materialTab) {}
                           })
            );
        }

        this.imageView = (CircleImageView)findViewById(R.id.profileImage);
        setImage();

        if(savedInstanceState != null) {
            this.colorHolder = (ColorHolder) savedInstanceState.getSerializable("colorHolder");
            setThemeColors(this.colorHolder);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.layout_topbar_collapsed, null);
        v.measure(500, 500);
        View img = v.findViewById(R.id.profileImage);
        Log.d(TAG, "X = " + Float.toString(img.getX()) + ", Y = " + Float.toString(img.getY()));
        Log.d(TAG, "W = " + Float.toString(img.getMeasuredWidth()) + ", Y = " + Float.toString(img.getMeasuredHeight()));

        this.initialNameX = (int)this.name.getX();
    }

    public void setImage(/* final String url */) {
        final Context context = this;
        this.imageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Picasso.with(context)
                                .load("https://avatars3.githubusercontent.com/u/1763885?v=3&s=460")
                                .resize(imageView.getWidth(), imageView.getHeight())
                                .centerCrop()
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        paletteHelperUtil.generatePalette(source);
                                        return source;
                                    }

                                    @Override
                                    public String key() {
                                        return "DBTS";
                                    }
                                })
                                .into(imageView);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        if (this.colorHolder != null) {
            final Drawable editButton = getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
            editButton.mutate().setColorFilter(this.colorHolder.bodyText, PorterDuff.Mode.SRC_IN);
            this.menu.findItem(R.id.action_edit_profile).setIcon(editButton);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                // do something here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPaletteGenerated(ColorHolder colorHolder) {
        this.colorHolder = colorHolder;
        setThemeColors(this.colorHolder);
    }

    private void setThemeColors(ColorHolder colorHolder) {
        // change color of action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorHolder.background));
        final Drawable backButton = getResources().getDrawable(R.drawable.ic_arrow_back_white_16dp);
        backButton.mutate().setColorFilter(colorHolder.bodyText, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(backButton);
        if (this.menu != null) {
            final Drawable editButton = getResources().getDrawable(R.drawable.ic_mode_edit_white_24dp);
            editButton.mutate().setColorFilter(colorHolder.bodyText, PorterDuff.Mode.SRC_IN);
            this.menu.findItem(R.id.action_edit_profile).setIcon(editButton);
        }

        // change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorHolder.backgroundSecondary);
            getWindow().setNavigationBarColor(colorHolder.backgroundSecondary);
        }

        // change the color of the main toolbar
        this.actionBar.setBackgroundColor(colorHolder.background);
        this.name.setTextColor(colorHolder.bodyText);
        this.bio.setTextColor(colorHolder.titleText);

        // change the color of the tab host
        this.tabHost.setPrimaryColor(colorHolder.primary);
        this.tabHost.setIconColor(colorHolder.icon);
        this.tabHost.setAccentColor(colorHolder.accent);
        this.tabHost.setSelectedNavigationItem(this.viewPager.getCurrentItem());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        final int minOverlayTransitionY = this.actionBarSize - this.topContainer.getHeight();
//        ViewHelper.setTranslationY(this.tabHost, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
//        final float separator = -(float)scrollY/(float)minOverlayTransitionY;
//        ViewHelper.setAlpha(this.smallImageView, separator);
//        float titleToMoveByX = this.initialNameX - this.smallImageView.getX() - this.smallImageView.getWidth() -
//                this.separationNameProfilePic;
//        /** @TODO: fix this */
//        ViewHelper.setTranslationX(this.name, separator*titleToMoveByX);

        Log.d(TAG, Integer.toString(topPanel.getMeasuredHeight()));
        Log.d(TAG, Integer.toString(topPanel.getHeight()));
        ViewHelper.setTranslationY(this.topPanel, scrollY);
        ViewHelper.setTranslationY(this.viewPager, topPanel.getMeasuredHeight());
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("colorHolder", this.colorHolder);
        super.onSaveInstanceState(outState);
    }
}
