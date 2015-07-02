/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api.models;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import solutions.doubts.api.QuestionsResource;
import solutions.doubts.api.query.Query;

public class Feed {

    private static final String TAG = "Feed";

    private LinkedList<Question> mFeedItems;
    private Context mContext;
    private Query.Order mOrder;
    private String mSort;
    private int mOffset;
    private String mFilterByParameter;
    private String mFilterByValue;
    private Map<String, List<String>> mFilterByMap;

    public Feed(Context context) {
        mContext = context;
        mFeedItems = new LinkedList<>();
        mOrder = Query.Order.desc;
        mSort = "id";
    }

    public void setSortAndOrderWithOffset(Query.Order order, String sort) {
        mOrder = order;
        mSort = sort;
    }

    public void filterBy(String parameter, String value) {
        mFilterByParameter = parameter;
        mFilterByValue = value;
    }

    public void filterBy(Map<String, List<String>> queryMap) {
        mFilterByMap = queryMap;
    }

    public void fetchNext(boolean firstUpdate, final UpdateCallback callback) {
        if (firstUpdate && mFeedItems.size() > 0) {
            callback.onUpdated();
        } else {
            if (mFilterByMap != null) {
                Query.with(mContext)
                        .remote(QuestionsResource.class)
                        .resource("questions")
                        .sortAndOrderWithOffset(mOrder, mSort, mOffset)
                        .filterBy(mFilterByMap, new FutureCallback<Response<QuestionsResource>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<QuestionsResource> result) {
                                        if (e == null) {
                                            if (result.getHeaders().code() == 200) {
                                                mFeedItems.addAll(result.getResult().getQuestions());
                                                mOffset += 10;
                                                callback.onUpdated();
                                            }
                                        }
                                    }
                                });
            } else {
                Query.with(mContext)
                        .remote(QuestionsResource.class)
                        .resource("questions")
                        .sortAndOrderWithOffset(mOrder, mSort, mOffset)
                        .filterBy(mFilterByParameter, mFilterByValue,
                                new FutureCallback<Response<QuestionsResource>>() {
                                    @Override
                                    public void onCompleted(Exception e, Response<QuestionsResource> result) {
                                        if (e == null) {
                                            if (result.getHeaders().code() == 200) {
                                                mFeedItems.addAll(result.getResult().getQuestions());
                                                mOffset += 10;
                                                callback.onUpdated();
                                            }
                                        }
                                    }
                                });
            }
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

    public interface UpdateCallback {

        void onUpdated();

    }

}
