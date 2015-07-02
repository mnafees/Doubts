/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tumblr.bookends.Bookends;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.R;
import solutions.doubts.activities.newprofile.UserObserver;
import solutions.doubts.api.models.Feed;
import solutions.doubts.api.models.User;
import solutions.doubts.api.query.Query;

public class QuestionFragment extends Fragment implements UserObserver {

    private static final String TAG = "QuestionFragment";

    // UI elemtents
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private FeedAdapter mFeedAdapter;
    private Feed.UpdateCallback mCallback;
    private Bookends<FeedAdapter> mBookends;
    private int mPreviousTotal;
    private boolean mLoading = true;
    private int mVisibleThreshold = 5;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question, container, false);

        ButterKnife.inject(this, v);
        initUi();

        return v;
    }

    public void setSortAndOrderWithOffset(Query.Order order, String sort) {
        mFeedAdapter.setSortAndOrderWithOffset(order, sort);
    }

    public void filterBy(String parameter, String value) {
        mFeedAdapter.filterBy(parameter, value);
    }

    public void filterBy(Map<String, List<String>> queryMap) {
        mFeedAdapter.filterBy(queryMap);
    }

    public void setUpdateCallback(Feed.UpdateCallback callback) {
        mCallback = callback;
    }

    private void initUi() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mFeedAdapter = new FeedAdapter(getActivity());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mVisibleItemCount = recyclerView.getChildCount();
                mTotalItemCount = layoutManager.getItemCount();
                mFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (mTotalItemCount > mPreviousTotal) {
                        mLoading = false;
                        mPreviousTotal = mTotalItemCount;
                    }
                }
                if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                        <= (mFirstVisibleItem + mVisibleThreshold)) {
                    mLoading = true;
                    update(false);
                }
            }
        });

        mBookends = new Bookends<>(mFeedAdapter);
        mRecyclerView.setAdapter(mBookends);
    }

    public void update(boolean firstUpdate) {
        mFeedAdapter.update(firstUpdate, new Feed.UpdateCallback() {
            @Override
            public void onUpdated() {
                mBookends.notifyDataSetChanged();
                if (mCallback != null)
                    mCallback.onUpdated();
            }
        });
    }

    public Bookends<FeedAdapter> getBookends() {
        return mBookends;
    }

    @Override
    public void onUserChanged(User user) {
        Log.d(TAG, "onUserChanged");
        mFeedAdapter.filterBy("author.username", user.getUsername());
        update(true);
    }

}
