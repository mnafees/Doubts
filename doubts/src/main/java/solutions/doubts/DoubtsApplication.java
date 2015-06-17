/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import io.realm.Realm;
import io.realm.RealmObject;
import solutions.doubts.activities.feed.FeedActivity;
import solutions.doubts.api.models.Feed;
import solutions.doubts.api.models.User;
import solutions.doubts.core.events.LogoutEvent;
import solutions.doubts.core.events.ResourceEvent;
import solutions.doubts.internal.Session;
import solutions.doubts.internal.StringConstants;

public class DoubtsApplication extends Application {

    private static final String TAG = "DoubtsApplication";

    private SharedPreferences mSharedPreferences;
    private Bus mBus;
    private RefWatcher mRefWatcher;
    private Feed mFeed;
    private Session mSession;

    private static DoubtsApplication INSTANCE;

    public static RefWatcher getRefWatcher(Context context) {
        return INSTANCE.mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mRefWatcher = LeakCanary.install(this);
        INSTANCE = this;
        mBus = new Bus(ThreadEnforcer.MAIN);
        mBus.register(this);
        mSharedPreferences = getSharedPreferences(StringConstants.PREFERENCES_NAME, 0);
        int userId = mSharedPreferences.getInt("user_id", -1);
        if (userId != -1) {
            mSession = new Session(this, mSharedPreferences);
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return (f.getName().equals("id") ||
                                f.getName().equals("slug") ||
                                f.getName().equals("question_count"));
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        Ion.getDefault(this).configure().setGson(gson);
        Ion.getDefault(this).configure().setLogging("Ion Logs", Log.DEBUG);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Ion.getDefault(this).cancelAll();
    }

    public static DoubtsApplication getInstance() {
        return INSTANCE;
    }

    public SharedPreferences getPreferences() {
        return mSharedPreferences;
    }

    public void setSession(Session session) {
        if (mSession == null) {
            mSession = session;
        }
    }

    public Session getSession() {
        return mSession;
    }

    public Bus getBus() {
        return mBus;
    }

    public Feed getFeedInstance() {
        if (mSession != null) {
            synchronized (DoubtsApplication.class) {
                if (mFeed == null) {
                    mFeed = new Feed(this);
                }
            }
        }
        return mFeed;
    }

    public int getUserId() {
        return mSharedPreferences.getInt("user_id", -1);
    }

    public String getUsername() {
        return mSharedPreferences.getString("username", "");
    }

    @Subscribe
    public void onResourceEvent(final ResourceEvent event) {
        if (event.getType() == ResourceEvent.Type.UNAUTHORISED) {
            Toast.makeText(this, "Please login again to continue", Toast.LENGTH_LONG).show();
            logout();
        }
    }

    public void logout() {
        // clear the Shared Preferences
        mSharedPreferences.edit().clear().apply();
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        realm.clear(User.class);
        realm.commitTransaction();
        mSession = null;

        mBus.post(new LogoutEvent());

        final Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
