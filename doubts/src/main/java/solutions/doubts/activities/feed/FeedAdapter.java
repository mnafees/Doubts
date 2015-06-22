/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.profile.ProfileActivity;
import solutions.doubts.activities.profile.UserCache;
import solutions.doubts.activities.questionview.QuestionCache;
import solutions.doubts.activities.questionview.QuestionViewActivity;
import solutions.doubts.api.models.Entity;
import solutions.doubts.api.models.Feed;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.S3Image;
import solutions.doubts.core.events.FeedUpdatedEvent;
import solutions.doubts.core.util.MaterialColorsUtil;
import solutions.doubts.core.util.StringUtil;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static final String TAG = "FeedAdapter";
    private Context mContext;
    private Feed mFeed;
    private MaterialColorsUtil mMaterialColorsUtil;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.username)
        TextView username;
        @InjectView(R.id.doubt_image)
        SimpleDraweeView doubtImage;
        @InjectView(R.id.author_image)
        SimpleDraweeView authorImage;
        @InjectView(R.id.tags_layout)
        FlowLayout tagsLayout;
        @InjectView(R.id.timestamp)
        RelativeTimeTextView time;

        public ViewHolder(final View view) {
            super(view);
            ButterKnife.inject(this, view);
            this.view = view;
        }

    }

    public FeedAdapter(final Context context) {
        mContext = context;
        DoubtsApplication.getInstance().getBus().register(this);
        mFeed = DoubtsApplication.getInstance().getFeedInstance();
        mMaterialColorsUtil = new MaterialColorsUtil();
        update(true /* first update */);
    }

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        final ImageButton bookmarkButton = (ImageButton)view.findViewById(R.id.bookmark_button);
//        final BitmapDrawable hollowBookmarkDrawable = (BitmapDrawable)bookmarkButton.getDrawable();
//        hollowBookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
//        bookmarkButton.setImageDrawable(hollowBookmarkDrawable);
//        final BitmapDrawable solidBookmarkDrawable = (BitmapDrawable)mContext.getResources().getDrawable(R.drawable.ic_bookmark_black_36dp);
//        solidBookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        final TransitionDrawable bookmarkDrawable = (TransitionDrawable) bookmarkButton.getDrawable();
        bookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ViewCompat.animate(v).cancel();
                ViewCompat.animate(v)
                        .scaleX(1.5f)
                        .scaleY(1.5f)
                        .setDuration(150)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                bookmarkDrawable.reverseTransition(150);
                            }
                        })
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                v.animate().scaleY(1.0f).scaleX(1.0f);
                            }
                        });
            }
        });
        view.findViewById(R.id.question_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionCache.getInstance().setLastSelectedQuestion((Question) view.getTag());
                Intent intent = new Intent(mContext, QuestionViewActivity.class);
                mContext.startActivity(intent);
            }
        });
        final View authorContainer = view.findViewById(R.id.author_container);
        authorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCache.getInstance().setLastSelectedUser(((Question)view.getTag()).getAuthor());
                final Intent intent = new Intent(FeedAdapter.this.mContext,
                        ProfileActivity.class);
                FeedAdapter.this.mContext.startActivity(intent);
            }
        });
        return new ViewHolder(view);
    }

    @Subscribe
    public void onFeedUpdatedEvent(final FeedUpdatedEvent event) {
        notifyDataSetChanged();
    }

    public void update(boolean firstUpdate) {
        mFeed.fetchNext(firstUpdate);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Question q = mFeed.getItem(position);
        holder.view.setTag(q);
        holder.title.setText(q.getTitle());
        holder.username.setText(q.getAuthor().getUsername());
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        long time = formatter.parseMillis(q.getCreated());
        holder.time.setReferenceTime(time);
        // FIXME: later tags will be limited to at most 5
        holder.tagsLayout.removeAllViews();
        for (Entity tag : q.getTags()) {
            final View v = View.inflate(mContext, R.layout.layout_single_tag, null);
            final TextView textView = (TextView)v.findViewById(R.id.tag);
            textView.setText("#" + tag.getName().replace(" ", "").toLowerCase());
            holder.tagsLayout.addView(v);
        }
        holder.authorImage.setImageURI(Uri.parse(StringUtil.getProfileImageUrl(q.getAuthor())));
        RoundingParams params = new RoundingParams();
        params.setRoundAsCircle(true);
        holder.authorImage.getHierarchy().setRoundingParams(params);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources())
                .setFadeDuration(300)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        S3Image s3i = q.getImage();
        String doubtImage = s3i != null && s3i.getUrl() != null ? s3i.getUrl() : StringUtil.getDoubtImageUrl(q);
        holder.doubtImage.setHierarchy(hierarchy);
        holder.doubtImage.setImageURI(Uri.parse(doubtImage));
    }

    @Override
    public int getItemCount() {
        return mFeed.getLength();
    }
}
