/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.common.FeedAdapter;
import solutions.doubts.activities.createdoubt.CreateDoubtActivity;
import solutions.doubts.activities.profile.ProfileActivity;
import solutions.doubts.core.ConnectivityChangeReceiver;
import solutions.doubts.core.events.ConnectivityChangedEvent;
import solutions.doubts.core.events.LogoutEvent;
import solutions.doubts.core.util.TransitionUtil;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";

    @InjectView(R.id.feed)
    RecyclerView mRecyclerView;
    private CircleImageView mProfileImage;
    private TextView mName;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mConnectivityError;

    private final ConnectivityChangeReceiver mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
    private final IntentFilter mIntentFilter = new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feed);
        ButterKnife.inject(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setElevation(3.0f);

        ((DoubtsApplication)getApplication()).getBus().register(this);

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);

        mConnectivityError = (TextView)findViewById(R.id.no_internet_textview);

        mRecyclerView = (RecyclerView)findViewById(R.id.feed);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        final FeedAdapter feedAdapter = new FeedAdapter(this);
        mRecyclerView.setAdapter(feedAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedAdapter.update();
            }
        });
        feedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                swipeRefreshLayout.setRefreshing(false);
                super.onChanged();
            }
        });

        final FloatingActionButton addQuestionButton = (FloatingActionButton)findViewById(R.id.add_question_button);
        addQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(FeedActivity.this, CreateDoubtActivity.class);
                startActivity(intent);
            }
        });
        addQuestionButton.attachToRecyclerView(mRecyclerView);

        final List<DrawerListViewItem> list = new ArrayList<>();
        list.add(new DrawerListViewItem(R.drawable.ic_bookmark_grey600_24dp, "Bookmarked posts"));
        list.add(new DrawerListViewItem(-1, "Logout"));

        /* BETA - Add Settings option */

        final DrawerListViewArrayAdapter adapter = new DrawerListViewArrayAdapter(this, list);
        final ListView listView = (ListView)findViewById(R.id.drawerListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == list.size() - 1) {
                    // Logout
                    final AlertDialog alertDialog = new AlertDialog.Builder(FeedActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setMessage("Are you sure you want to logout?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((DoubtsApplication)getApplication()).logout();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });
        listView.setAdapter(adapter);

        final Intent intent = new Intent(this, ProfileActivity.class);
        ViewGroup topProfileContainer = (ViewGroup) findViewById(R.id.top_profile_view);
        this.mProfileImage = (CircleImageView) topProfileContainer.findViewById(R.id.profileImage);
        //this.mName = (TextView)this.topProfileContainer.findViewById(R.id.email_name);
        setImage();
        topProfileContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(mConnectivityChangeReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(mConnectivityChangeReceiver);
    }

    @Subscribe
    public void onLogoutEvent(final LogoutEvent event) {
        finish();
    }

    @Subscribe
    public void onConnectivityChangedEvent(final ConnectivityChangedEvent event) {
        if (event.isConnected()) {
            TransitionUtil.hideWithFadeOut(mConnectivityError);
        } else {
            TransitionUtil.showWithFadeIn(mConnectivityError);
        }
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

    public void setImage(/* final String url */) {
        /*final Context context = this;
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
        );*/
    }

}
