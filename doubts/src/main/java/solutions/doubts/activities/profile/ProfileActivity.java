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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
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
import solutions.doubts.transitions.ChangeBoundsOnScrollTransition;

public class ProfileActivity extends ActionBarActivity implements PaletteHelperUtilListener,
        ObservableScrollViewCallbacks {
    public static final String TAG = ProfileActivity.class.getName();

    private View topPanelContainer;

    // expanded top panel view
    private View expandedTopPanel;
    private CircleImageView profileImageView;
    private MaterialTabHost tabHost;
    private TextView name;
    private TextView bio;

    // collapsed top panel view
    private View collapsedTopPanel;

    // editables
    private EditText nameEditable;

    private Menu menu;
    private ViewPager viewPager;
    private ObservableScrollView scrollView;

    private ChangeBoundsOnScrollTransition topPanelTransition;
    private ColorHolder colorHolder;
    private final PaletteHelperUtil paletteHelperUtil = new PaletteHelperUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_profile);

        this.topPanelContainer = findViewById(R.id.topPanelContainer);
        this.expandedTopPanel = getLayoutInflater().inflate(R.layout.layout_topbar_expanded, (ViewGroup)this.topPanelContainer, false);
        this.collapsedTopPanel = getLayoutInflater().inflate(R.layout.layout_topbar_collapsed, (ViewGroup)this.topPanelContainer, false);

        this.topPanelTransition = new ChangeBoundsOnScrollTransition((ViewGroup)this.topPanelContainer,
            (ViewGroup)this.expandedTopPanel, (ViewGroup)this.collapsedTopPanel);
        this.topPanelTransition.setDuration(getResources()
                    .getDimensionPixelSize(R.dimen.profile_actionbar_expanded_height) - getActionBarSize());

        this.paletteHelperUtil.setPaletteHelperUtilListener(this);

        this.name = (TextView)this.expandedTopPanel.findViewById(R.id.name);
        //this.nameEditable = (EditText)this.expandedTopPanel.findViewById(R.id.editText);
        this.bio = (TextView)this.expandedTopPanel.findViewById(R.id.bio);

        setSupportActionBar((Toolbar)this.expandedTopPanel.findViewById(R.id.action_bar));
        setupActionBar();

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

        this.scrollView = (ObservableScrollView)findViewById(R.id.scrollView);
        this.scrollView.setScrollViewCallbacks(this);
        if (savedInstanceState != null) {
            this.scrollView.setScrollY(savedInstanceState.getInt("scrollY"));
        }

        this.topPanelContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (ProfileActivity.this.topPanelTransition.animateOnScroll(scrollView.getScrollY())) {
                    ProfileActivity.this.topPanelContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return false;
            }
        });

        this.tabHost = (MaterialTabHost)this.expandedTopPanel.findViewById(R.id.materialTabHost);
        for (int i = 0; i < adapter.getCount(); ++i) {
            this.tabHost.addTab(
                    this.tabHost.newTab()
                                .setIcon(adapter.getIconForTab(i))
                                .setTabListener(new MaterialTabListener() {
                                @Override
                                public void onTabSelected(MaterialTab materialTab) {
                                    viewPager.setCurrentItem(materialTab.getPosition());
                                }

                                @Override
                                public void onTabReselected(MaterialTab materialTab) {
                                }

                                @Override
                                public void onTabUnselected(MaterialTab materialTab) {
                                }
                            })
            );
        }

        this.profileImageView = (CircleImageView)this.expandedTopPanel.findViewById(R.id.profileImage);
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
        final Context context = this;
        this.profileImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Picasso.with(context)
                                .load("https://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-xap1/v/t1.0-9/10888920_10204764009974199_7024438969916164500_n.jpg?oh=6943916b9bb2cfd8d3cf79f115696a9f&oe=557BCE32&__gda__=1434962535_a9a811b424b34dbfa950c2ce9de84e78")
                                .resize(profileImageView.getWidth(), profileImageView.getHeight())
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
                                .into(profileImageView);
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
                this.nameEditable.setText(this.name.getText());
                this.name.setVisibility(View.INVISIBLE);
                this.nameEditable.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPaletteGenerated(ColorHolder colorHolder) {
        Log.d(TAG, "here");
        /*if (this.colorHolder != null) {
            // prevent change of color holder values once already set
            return;
        }*/

        //this.profileImageView.setEditingMode(true);
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

        // change color of image view border
        this.profileImageView.setBorderColor(colorHolder.bodyText);

        // change the color of the main toolbar
        this.name.setTextColor(colorHolder.bodyText);
        this.nameEditable.setTextColor(colorHolder.bodyText);
        this.bio.setTextColor(colorHolder.titleText);

        // change the color of the tab host
        this.tabHost.setPrimaryColor(colorHolder.primary);
        this.tabHost.setIconColor(colorHolder.icon);
        this.tabHost.setAccentColor(colorHolder.accent);
        this.tabHost.setSelectedNavigationItem(this.viewPager.getCurrentItem());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        this.topPanelTransition.animateOnScroll(scrollY);
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.colorHolder = (ColorHolder) savedInstanceState.getSerializable("colorHolder");
        setThemeColors(this.colorHolder);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("colorHolder", this.colorHolder);
        outState.putInt("scrollY", this.scrollView.getScrollY());

        super.onSaveInstanceState(outState);
    }
}
