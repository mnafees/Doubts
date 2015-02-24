/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transitions.everywhere.ChangeBounds;
import android.transitions.everywhere.TransitionValues;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.neokree.materialtabs.MaterialTabHost;
import solutions.doubts.R;
import solutions.doubts.activities.profile.fragments.AboutFragment;
import solutions.doubts.core.util.PaletteHelperUtil;
import solutions.doubts.core.util.PaletteHelperUtilListener;

public class ProfileActivity extends ActionBarActivity implements PaletteHelperUtilListener,
        ObservableScrollViewCallbacks {

    private CircleImageView imageView;
    private CircleImageView smallImageView;
    private Toolbar topContainer;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private TextView name;
    private TextView bio;

    private final PaletteHelperUtil paletteHelperUtil = new PaletteHelperUtil();

    private int actionBarSize;
    private int separationNameProfilePic;
    private int initialNameX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_profile);

        this.paletteHelperUtil.setPaletteHelperUtilListener(this);

        this.name = (TextView)findViewById(R.id.name);
        this.bio = (TextView)findViewById(R.id.bio);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }

        final List<Fragment> fragments = new ArrayList<>();
        final AboutFragment aboutFragment = new AboutFragment();
        fragments.add(aboutFragment);

        final ProfilePagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager(), fragments);
        adapter.setIconForTab(0, getResources().getDrawable(R.drawable.ic_account_circle_purple_24dp));
        adapter.setIconForTab(1, getResources().getDrawable(R.drawable.ic_live_help_purple_24dp));
        adapter.setIconForTab(2, getResources().getDrawable(R.drawable.ic_spell_check_purple_24dp));

        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.viewPager.setAdapter(adapter);
        this.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        final ObservableScrollView scrollView = (ObservableScrollView)findViewById(R.id.scrollView);
        scrollView.setScrollViewCallbacks(this);

        this.actionBarSize = getActionBarSize();
        this.separationNameProfilePic = getResources().getDimensionPixelSize(R.dimen.profile_action_bar_separation_name_profile);

        this.tabHost = (MaterialTabHost)findViewById(R.id.materialTabHost);
        for (int i = 0; i < adapter.getCount(); ++i) {
            tabHost.addTab(
                    tabHost.newTab().setIcon(adapter.getIconForTab(i))
            );
        }

        this.imageView = (CircleImageView)findViewById(R.id.profileImage);
        this.smallImageView = (CircleImageView)findViewById(R.id.profileImageSmall);
        this.smallImageView.setAlpha(0.0f);
        setImage();

        this.topContainer = (Toolbar)findViewById(R.id.topContainer);
    }

    @Override
    public void onResume() {
        super.onResume();

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

                        Picasso.with(context)
                                .load("https://avatars3.githubusercontent.com/u/1763885?v=3&s=460")
                                .resize(smallImageView.getWidth(), smallImageView.getHeight())
                                .centerCrop()
                                .into(smallImageView);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
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
    public void onPaletteGenerated(Palette palette) {
        if (palette.getLightVibrantSwatch() != null &&
                palette.getVibrantSwatch() != null &&
                palette.getDarkVibrantSwatch() != null) {
            // change the color of the action bar
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(palette.getVibrantSwatch().getRgb()));

            // change the color of the status bar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(palette.getDarkVibrantSwatch().getRgb());
            }

            // change the color of the main toolbar
            this.topContainer.setBackgroundColor(palette.getVibrantSwatch().getRgb());
            this.name.setTextColor(palette.getVibrantSwatch().getBodyTextColor());
            this.bio.setTextColor(palette.getVibrantSwatch().getTitleTextColor());

            // change the color of the tab host
            this.tabHost.setPrimaryColor(palette.getLightVibrantSwatch().getRgb());
            this.tabHost.setIconColor(palette.getLightVibrantSwatch().getBodyTextColor());
            this.tabHost.setAccentColor(palette.getLightVibrantSwatch().getTitleTextColor());
            this.tabHost.setSelectedNavigationItem(this.viewPager.getCurrentItem());
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        final int minOverlayTransitionY = this.actionBarSize - this.topContainer.getHeight();
        ViewHelper.setTranslationY(this.tabHost, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        final float separator = -(float)scrollY/(float)minOverlayTransitionY;
        ViewHelper.setAlpha(this.smallImageView, separator);
        float titleToMoveByX = this.initialNameX - this.smallImageView.getX() - this.smallImageView.getWidth() -
                this.separationNameProfilePic;
        ViewHelper.setTranslationX(this.name, separator*titleToMoveByX);
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

}
