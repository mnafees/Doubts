/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.createdoubt.CreateDoubtActivity;
import solutions.doubts.core.ConnectivityChangeReceiver;
import solutions.doubts.core.events.ConnectivityChangedEvent;
import solutions.doubts.core.events.LogoutEvent;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";
    private static final String SEARCHVIEW_TAG = "SearchView";

    // UI elements
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.feed)
    RecyclerView mRecyclerView;
    private TextView mName;

    // Other members
    private final ConnectivityChangeReceiver mConnectivityChangeReceiver = new ConnectivityChangeReceiver();
    private final IntentFilter mIntentFilter = new IntentFilter();
    private int mPreviousTotal;
    private boolean mLoading = true;
    private int mVisibleThreshold = 5;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_feed);
        ButterKnife.inject(this);
        ((DoubtsApplication)getApplication()).getBus().register(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTransitionName("toolbarTransition");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        View titleView = View.inflate(this, R.layout.layout_title_action_bar, null);
        TextView title = (TextView)titleView.findViewById(R.id.brand_text);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf"));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(titleView);

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.logout:
                                AlertDialog dialog = new AlertDialog.Builder(FeedActivity.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                                        .setTitle("Doubts")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setMessage("Are you sure you want to logout?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DoubtsApplication.getInstance().logout();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        })
                                        .create();
                                dialog.show();
                        }
                        return true;
                    }
                });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        final FeedAdapter feedAdapter = new FeedAdapter(this);
        mRecyclerView.setAdapter(feedAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = recyclerView.getChildCount();
                mTotalItemCount = linearLayoutManager.getItemCount();
                mFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (mTotalItemCount > mPreviousTotal) {
                        mLoading = false;
                        mPreviousTotal = mTotalItemCount;
                    }
                }
                if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                        <= (mFirstVisibleItem + mVisibleThreshold)) {
                    mLoading = true;
                    feedAdapter.update(false);
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedAdapter.update(false);
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

        } else {

        }
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
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                enterSearchUi();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enterSearchUi() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        SearchViewFragment fragment = (SearchViewFragment)getFragmentManager().findFragmentByTag(SEARCHVIEW_TAG);
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.addSharedElement(findViewById(R.id.toolbar), "toolbarTransition");
        if (fragment == null) {
            fragment = new SearchViewFragment();
            fragment.setOnBackListener(new SearchViewFragment.OnBackListener() {
                @Override
                public void backPressed() {
                    exitSearchUi();
                }
            });
            transaction.add(R.id.drawer_layout, fragment, SEARCHVIEW_TAG);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void exitSearchUi() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        transaction.remove(getFragmentManager().findFragmentByTag(SEARCHVIEW_TAG));
        transaction.commit();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

}
