/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import solutions.doubts.R;
import solutions.doubts.activities.common.QuestionsAdapter;
import solutions.doubts.activities.profile.ProfileActivity;
import solutions.doubts.api.models.Question;
import solutions.doubts.core.util.ColorHolder;
import solutions.doubts.core.util.PaletteHelperUtil;
import solutions.doubts.core.util.PaletteHelperUtilListener;

public class FeedActivity extends ActionBarActivity implements PaletteHelperUtilListener {

    private RelativeLayout drawer;
    private RecyclerView content;
    private CircleImageView profileImageView;
    private TextView name;
    private TextView bio;
    private ViewGroup topProfileContainer;

    private ColorHolder colorHolder;
    private final PaletteHelperUtil paletteHelperUtil = new PaletteHelperUtil();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_feed_parent_drawer);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        this.paletteHelperUtil.setPaletteHelperUtilListener(this);

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDark));

        this.drawer = (RelativeLayout)findViewById(R.id.drawer);
        this.content = (RecyclerView)findViewById(R.id.content);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        //manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.content.setLayoutManager(manager);
        QuestionsAdapter questionsAdapter = new QuestionsAdapter();
        this.content.setAdapter(questionsAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        final List<DrawerListViewItem> list = new ArrayList<>();
        final DrawerListViewItem item = new DrawerListViewItem(R.drawable.ic_settings_24dp, "Settings");
        list.add(item);
        final DrawerListViewArrayAdapter adapter = new DrawerListViewArrayAdapter(this, list);
        final ListView listView = (ListView)findViewById(R.id.drawerListView);
        listView.setAdapter(adapter);

        final Intent intent = new Intent(this, ProfileActivity.class);
        this.topProfileContainer = (ViewGroup)findViewById(R.id.top_profile_view);
        this.profileImageView = (CircleImageView)this.topProfileContainer.findViewById(R.id.profileImage);
        this.name = (TextView)this.topProfileContainer.findViewById(R.id.name);
        this.bio = (TextView)this.topProfileContainer.findViewById(R.id.bio);
        setImage();
        this.topProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                //overridePendingTransition(R.anim.change_bounds, R.anim.change_bounds);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feed_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // do something here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{android.R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    public void setImage(/* final String url */) {
        final Context context = this;
        this.profileImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Picasso.with(context)
                                .load("https://avatars3.githubusercontent.com/u/1763885?v=3&s=460")
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
    public void onPaletteGenerated(ColorHolder colorHolder) {
        if (this.colorHolder != null) {
            // prevent change of color holder values once already set
            return;
        }

        this.colorHolder = colorHolder;
        setThemeColors(this.colorHolder);
    }

    private void setThemeColors(ColorHolder colorHolder) {
        // change color of image view border
        this.profileImageView.setBorderColor(colorHolder.bodyText);

        // change the color of the main toolbar
        this.name.setTextColor(colorHolder.bodyText);
        this.bio.setTextColor(colorHolder.titleText);

        this.topProfileContainer.setBackgroundColor(colorHolder.background);
    }

}
