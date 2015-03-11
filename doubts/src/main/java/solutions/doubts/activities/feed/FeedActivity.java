/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import solutions.doubts.R;
import solutions.doubts.activities.common.QuestionsAdapter;
import solutions.doubts.activities.createdoubt.CreateDoubtActivity;
import solutions.doubts.activities.profile.ProfileActivity;
import solutions.doubts.activities.settings.SettingsActivity;

public class FeedActivity extends ActionBarActivity {

    private RecyclerView content;
    private CircleImageView profileImageView;
    private TextView name;
    private ViewGroup topProfileContainer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_feed);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDark));
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);

        final RelativeLayout drawer = (RelativeLayout) findViewById(R.id.drawer);
        this.content = (RecyclerView)findViewById(R.id.content);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        this.content.setLayoutManager(manager);
        QuestionsAdapter questionsAdapter = new QuestionsAdapter(this);
        this.content.setAdapter(questionsAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        final AddFloatingActionButton addQuestionButton = (AddFloatingActionButton)findViewById(R.id.add_question_button);
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(FeedActivity.this, CreateDoubtActivity.class);
                startActivity(intent);
            }
        });

        final List<DrawerListViewItem> list = new ArrayList<>();

        /* BETA - Add Settings option */

        final DrawerListViewArrayAdapter adapter = new DrawerListViewArrayAdapter(this, list);
        final ListView listView = (ListView)findViewById(R.id.drawerListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(FeedActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);

        final Intent intent = new Intent(this, ProfileActivity.class);
        this.topProfileContainer = (ViewGroup)findViewById(R.id.top_profile_view);
        this.profileImageView = (CircleImageView)this.topProfileContainer.findViewById(R.id.profileImage);
        this.name = (TextView)this.topProfileContainer.findViewById(R.id.name);
        setImage();
        this.topProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feed_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
                                .into(profileImageView);
                    }
                }
        );
    }

}
