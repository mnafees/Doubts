/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.LinkedList;
import java.util.List;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.core.events.FeedUpdatedEvent;
import solutions.doubts.internal.ApiConstants;

public class Feed {

    private static final String TAG = "Feed";

    private final Handler mHandler = new Handler(DoubtsApplication.getInstance().getMainLooper());
    private Context mContext;
    private LinkedList<Question> mFeedItems;

    public Feed(final Context context) {
        mContext = context;
        mFeedItems = new LinkedList<>();

        DoubtsApplication.getInstance().getBus().register(this);
    }

    public void fetchNext() {
        Ion.with(mContext)
                .load(ApiConstants.API_ENDPOINT + "/api/v1/questions")
                .setHeader(ApiConstants.HEADER_AUTHORIZATION, DoubtsApplication.getInstance().getAuthToken().toString())
                .as(new TypeToken<List<Question>>(){})
                .setCallback(new FutureCallback<List<Question>>() {
                    @Override
                    public void onCompleted(Exception e, List<Question> result) {
                        if (e != null) {

                        } else {
                            mFeedItems.addAll(result);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    DoubtsApplication.getInstance().getBus().post(
                                            new FeedUpdatedEvent()
                                    );
                                }
                            });
                        }
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
