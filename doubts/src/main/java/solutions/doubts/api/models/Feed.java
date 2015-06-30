/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.LinkedList;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.QuestionsResource;
import solutions.doubts.api.query.Query;
import solutions.doubts.core.events.FeedUpdatedEvent;

public class Feed {

    private static final String TAG = "Feed";

    private LinkedList<Question> mFeedItems;
    private Context mContext;
    private int mOffset;

    public Feed(Context context) {
        mContext = context;
        mFeedItems = new LinkedList<>();
    }

    public void fetchNext(boolean firstUpdate) {
        if (firstUpdate && mFeedItems.size() > 0) {
            DoubtsApplication.getInstance().getBus().post(new FeedUpdatedEvent());
        } else {
            Query.with(mContext)
                    .remote(QuestionsResource.class)
                    .resource("questions")
                    .getAll(null, null, mOffset,
                            new FutureCallback<Response<QuestionsResource>>() {
                                @Override
                                public void onCompleted(Exception e, Response<QuestionsResource> result) {
                                    if (e == null) {
                                        if (result.getHeaders().code() == 200) {
                                            mFeedItems.addAll(result.getResult().getQuestions());
                                            mOffset += 10;
                                            DoubtsApplication.getInstance().getBus().post(
                                                    new FeedUpdatedEvent()
                                            );
                                        }
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
