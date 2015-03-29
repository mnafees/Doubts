/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.os.Handler;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.core.events.FeedUpdatedEvent;
import solutions.doubts.core.events.NetworkEvent;
import solutions.doubts.core.events.PageableDataEvent;
import solutions.doubts.internal.RestConstants;

public class Feed {

    private static final String TAG = "Feed";

    private LinkedList<Question> mFeedItems;
    private int mNetworkEventId;
    private Gson mGson;

    public Feed() {
        mFeedItems = new LinkedList<>();
        mGson = new GsonBuilder()
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
                .create();

        DoubtsApplication.getInstance().getBus().register(this);
    }

    public boolean loadLocalRealmData() {
        final Realm realm = Realm.getInstance(DoubtsApplication.getInstance());
        final RealmResults<Question> persistentQuestions = realm.where(Question.class).findAll();
        if (persistentQuestions.size() == 0) {
            return false;
        }
        for (int i = 0; i < persistentQuestions.size(); ++i) {
            Collections.addAll(mFeedItems, persistentQuestions.get(i));
        }
        postFeedUpdateEvent();
        return true;
    }

    public void fetchNext() {
        final NetworkEvent networkEvent = NetworkEvent.newBuilder()
                .operation(NetworkEvent.Operation.GETALL)
                .url(RestConstants.API_ENDPOINT + "/api/v1/questions")
                .clazz(Question.class)
                .build();
        mNetworkEventId = networkEvent.getId();
        networkEvent.post();
    }

    @Subscribe
    public void onPageableDataEvent(final PageableDataEvent event) {
        if (event.getId() == mNetworkEventId) {
            final List<Question> questions = mGson.fromJson(event.getPageableData(),
                    new TypeToken<List<Question>>(){}.getType());

            final Realm realm = Realm.getInstance(DoubtsApplication.getInstance());
            realm.beginTransaction();
            realm.clear(Question.class);
            realm.copyToRealm(questions);
            realm.commitTransaction();

            mFeedItems.addAll(questions);
            postFeedUpdateEvent();
        }
    }

    private void postFeedUpdateEvent() {
        final Handler handler = new Handler(DoubtsApplication.getInstance().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
            }
        });
    }

    public void reset() {
        mFeedItems.clear();
    }

    public int getLength() {
        return mFeedItems.size();
    }

    public Question getItem(int index) {
        return mFeedItems.get(index);
    }

}
