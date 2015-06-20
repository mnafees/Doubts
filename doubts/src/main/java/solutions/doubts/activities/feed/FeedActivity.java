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
import android.net.Uri;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.koushikdutta.ion.Ion;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.authentication.AuthenticationActivity;
import solutions.doubts.activities.createdoubt.CreateDoubtActivity;
import solutions.doubts.core.ConnectivityChangeReceiver;
import solutions.doubts.core.events.ConnectivityChangedEvent;
import solutions.doubts.core.events.FeedUpdatedEvent;
import solutions.doubts.core.events.LogoutEvent;
import solutions.doubts.core.events.SessionUpdatedEvent;
import solutions.doubts.core.util.StringUtil;
import solutions.doubts.internal.AuthToken;
import solutions.doubts.internal.Session;

public class FeedActivity extends AppCompatActivity {

    private static final String TAG = "FeedActivity";
    private static final String SEARCHVIEW_TAG = "SearchView";

    // UI elements
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.feed)
    RecyclerView mRecyclerView;
    SimpleDraweeView mDrawerHeaderProfileImage;
    TextView mDrawerHeaderName;

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

        final Uri data = getIntent().getData();
        if (data != null) {
            if ((data.getScheme().equals("http") ||
                    data.getScheme().equals("https")) &&
                    data.getPath().contains("/auth/token/")) {
                final int length = data.getPathSegments().size();
                if (length != 5) {
                    // mischief alert!
                    Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                    startAuthenticationActivity();
                    return;
                } else {
                    try {
                        final int id = Integer.valueOf(data.getPathSegments().get(length - 3));
                        final String username = data.getPathSegments().get(length - 2);
                        final String token = data.getPathSegments().get(length - 1);

                        Session session = new Session(DoubtsApplication.getInstance(),
                                DoubtsApplication.getInstance().getPreferences());
                        AuthToken authToken = new AuthToken(id, username, token);
                        session.setAuthToken(authToken);
                        DoubtsApplication.getInstance().setSession(session);
                    } catch (NumberFormatException e) {
                        // mischief alert!
                        Toast.makeText(this, "Invalid authentication URL", Toast.LENGTH_LONG).show();
                        startAuthenticationActivity();
                        return;
                    }
                }
            }
        }
        if (DoubtsApplication.getInstance().getSession() == null) {
            startAuthenticationActivity();
            return;
        }

        Fresco.initialize(this);
        setContentView(R.layout.layout_feed);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setTransitionName("toolbarTransition");
        }
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
        View drawerHeader = navigationView.inflateHeaderView(R.layout.layout_feed_drawer_profile_top);
        mDrawerHeaderProfileImage = (SimpleDraweeView)drawerHeader.findViewById(R.id.profile_image);
        mDrawerHeaderName = (TextView)drawerHeader.findViewById(R.id.name);
        DoubtsApplication.getInstance().getSession().getLoggedInUser();

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

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedAdapter.update(false);
            }
        });
        mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean exec = true;
            @Override
            public void onGlobalLayout() {
                if(exec) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        mSwipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    exec = !exec;
                }
            }
        });

        FloatingActionButton addQuestionButton = (FloatingActionButton)findViewById(R.id.add_doubt_button);
        addQuestionButton.attachToRecyclerView(mRecyclerView);

        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    private void startAuthenticationActivity() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onFeedUpdatedEvent(FeedUpdatedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onSessionUpdatedEvent(SessionUpdatedEvent event) {
        mDrawerHeaderName.setText(event.getUser().getName());
        mDrawerHeaderProfileImage.setImageURI(Uri.parse(StringUtil.getProfileImageUrl(event.getUser())));
    }

    @OnClick(R.id.add_doubt_button)
    public void onClickAddDoubtButton() {
        Intent intent = new Intent(this, CreateDoubtActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DoubtsApplication)getApplication()).getBus().register(this);
        registerReceiver(mConnectivityChangeReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((DoubtsApplication)getApplication()).getBus().unregister(this);
        unregisterReceiver(mConnectivityChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Ion.getDefault(this).cancelAll();
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
