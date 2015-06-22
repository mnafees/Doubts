/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.LinkedList;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.ServerResponseCallback;
import solutions.doubts.api.query.Query;
import solutions.doubts.api.query.QuestionsResource;
import solutions.doubts.core.events.FeedUpdatedEvent;

public class Feed {

    private static final String TAG = "Feed";

    private final Handler mHandler = new Handler(DoubtsApplication.getInstance().getMainLooper());
    private LinkedList<Question> mFeedItems;
    private Context mContext;
    private int mOffset;

    public Feed(final Context context) {
        mContext = context;
        mFeedItems = new LinkedList<>();
    }

    public void fetchNext(boolean firstUpdate) {
        if (firstUpdate && mFeedItems.size() > 0) {
            DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
        } else {
            Query.with(mContext)
                    .remote(QuestionsResource.class)
                    .setServerResponseCallback(new ServerResponseCallback<QuestionsResource>() {
                        @Override
                        public void onCompleted(Exception e, QuestionsResource result) {
                            if (e != null) {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                mFeedItems.addAll(result.getQuestions());
                                mOffset += 10;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
                                    }
                                });
                            }
                        }
                    })
                    .getAll(null, null, mOffset);
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
