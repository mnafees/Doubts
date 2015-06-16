/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.LinkedList;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.query.QuestionsResource;
import solutions.doubts.api.query.RemoteQuery;
import solutions.doubts.core.events.FeedUpdatedEvent;

public class Feed {

    private static final String TAG = "Feed";

    private final Handler mHandler = new Handler(DoubtsApplication.getInstance().getMainLooper());
    private LinkedList<Question> mFeedItems;
    private RemoteQuery<QuestionsResource> mRemoteQuery;
    private int mOffset;

    public Feed(final Context context) {
        mFeedItems = new LinkedList<>();
        mRemoteQuery = new RemoteQuery<>(QuestionsResource.class);
        mRemoteQuery.setContext(context);
    }

    public void fetchNext(boolean firstUpdate) {
        if (firstUpdate && mFeedItems.size() > 0) {
            DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
        } else {
            mRemoteQuery.getAll(null, null, mOffset).setCallback(
                    new FutureCallback<Response<QuestionsResource>>() {
                        @Override
                        public void onCompleted(Exception e, Response<QuestionsResource> result) {
                            if (e != null) {
                                Toast.makeText(mRemoteQuery.getContext(), e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                mFeedItems.addAll(result.getResult().getQuestions());
                                mOffset += 10;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
                                    }
                                });
                            }
                        }
                    });
        }
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
