/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.fullscreenimageview.FullscreenImageViewActivity;
import solutions.doubts.api.models.Entity;
import solutions.doubts.api.models.Feed;
import solutions.doubts.api.models.Question;
import solutions.doubts.core.events.FeedUpdatedEvent;
import solutions.doubts.core.util.DateTimeUtil;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String TAG = "FeedAdapter";
    private Context mContext;
    private Feed mFeed;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView question, username;
        public final ImageView imageView;
        public final FlowLayout tagList;
        public final RelativeTimeTextView time;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            this.question = (TextView)view.findViewById(R.id.title);
            this.username = (TextView)view.findViewById(R.id.username);
            this.imageView = (ImageView)view.findViewById(R.id.image);
            this.tagList = (FlowLayout)view.findViewById(R.id.tagList);
            this.time = (RelativeTimeTextView)view.findViewById(R.id.timestamp);
        }

    }

    public FeedAdapter(final Context context) {
        mContext = context;
        DoubtsApplication.getInstance().getBus().register(this);
        mFeed = new Feed();
        //if (!mFeed.loadLocalRealmData()) {
            update();
        //}
    }

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                          int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        final ImageButton bookmarkButton = (ImageButton)view.findViewById(R.id.bookmarkIconButton);
        final BitmapDrawable hollowBookmarkDrawable = (BitmapDrawable)bookmarkButton.getDrawable();
        hollowBookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        bookmarkButton.setImageDrawable(hollowBookmarkDrawable);
        final BitmapDrawable solidBookmarkDrawable = (BitmapDrawable)mContext.getResources().getDrawable(R.drawable.ic_bookmark_black_36dp);
        solidBookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarkButton.getDrawable().equals(hollowBookmarkDrawable)) {
                    bookmarkButton.setImageDrawable(solidBookmarkDrawable);
                } else {
                    bookmarkButton.setImageDrawable(hollowBookmarkDrawable);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final Intent intent = new Intent(FeedAdapter.this.mContext,
                        QuestionViewActivity.class);
                intent.putExtra("question", (Question)view.getTag());
                FeedAdapter.this.mContext.startActivity(intent);*/
            }
        });
        return new ViewHolder(view);
    }

    @Subscribe
    public void onFeedUpdatedEvent(final FeedUpdatedEvent event) {
        notifyDataSetChanged();
    }

    public void update() {
        mFeed.fetchNext();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Question q = mFeed.getItem(position);
        holder.view.setTag(q);
        holder.question.setText(q.getTitle());
        holder.username.setText(q.getAuthor().getUsername());
        holder.time.setReferenceTime(DateTimeUtil.getMillis(q.getCreated()));
        // FIXME: later tags will be limited to at most 5
        holder.tagList.removeAllViews();
        for (Entity tag : q.getTags()) {
            final View v = View.inflate(mContext, R.layout.layout_single_tag, null);
            final TextView textView = (TextView)v.findViewById(R.id.tag);
            textView.setText("#" + tag.getName().replace(" ", ""));
            holder.tagList.addView(v);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(FeedAdapter.this.mContext,
                        FullscreenImageViewActivity.class);
                FeedAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeed.getLength();
    }
}
