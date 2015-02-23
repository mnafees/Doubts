/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import de.hdodenhof.circleimageview.CircleImageView;
import it.neokree.materialtabs.MaterialTabHost;
import solutions.doubts.R;
import solutions.doubts.core.util.ColorHolder;
import solutions.doubts.core.util.PaletteHelperUtil;
import solutions.doubts.core.util.PaletteHelperUtilListener;

public class ProfileActivity extends ActionBarActivity implements PaletteHelperUtilListener {
    public static final String TAG = "ProfileActivity";

    private CircleImageView imageView;
    private Toolbar topContainer;
    private MaterialTabHost tabHost;
    private ViewPager viewPager;
    private TextView name;
    private TextView bio;
    private ColorHolder mColorHolder;

    private final PaletteHelperUtil paletteHelperUtil = new PaletteHelperUtil();

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

        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.tabHost = (MaterialTabHost)findViewById(R.id.materialTabHost);
        final ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        adapter.setIconForTab(0, getResources().getDrawable(R.drawable.ic_account_circle_purple_24dp));
        adapter.setIconForTab(1, getResources().getDrawable(R.drawable.ic_live_help_purple_24dp));
        adapter.setIconForTab(2, getResources().getDrawable(R.drawable.ic_spell_check_purple_24dp));
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });
        for (int i = 0; i < adapter.getCount(); ++i) {
            tabHost.addTab(
                    tabHost.newTab().setIcon(adapter.getIconForTab(i))
            );
        }

        this.imageView = (CircleImageView)findViewById(R.id.profile_image);
        setImage();

        this.topContainer = (Toolbar)findViewById(R.id.topContainer);

        if(savedInstanceState != null) {
            mColorHolder = (ColorHolder) savedInstanceState.getSerializable("colorHolder");
            setThemeColors(mColorHolder);
        }
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
    public void onPaletteGenerated(ColorHolder colorHolder) {
        mColorHolder = colorHolder;
        setThemeColors(mColorHolder);
    }

    private void setThemeColors(ColorHolder colorHolder) {
        // change the color of the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorHolder.backgroundSecondary);
            getWindow().setNavigationBarColor(colorHolder.backgroundSecondary);
        }

        // change the color of the main toolbar
        this.topContainer.setBackgroundColor(colorHolder.background);
        this.name.setTextColor(colorHolder.bodyText);
        this.bio.setTextColor(colorHolder.titleText);

        // change the color of the tab host
        this.tabHost.setPrimaryColor(colorHolder.primary);
        this.tabHost.setIconColor(colorHolder.icon);
        this.tabHost.setAccentColor(colorHolder.accent);
        this.tabHost.setSelectedNavigationItem(this.viewPager.getCurrentItem());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("colorHolder", mColorHolder);
        super.onSaveInstanceState(outState);
    }
}
