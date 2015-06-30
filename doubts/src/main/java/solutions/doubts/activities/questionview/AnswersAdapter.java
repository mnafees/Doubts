/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import solutions.doubts.R;
import solutions.doubts.activities.profile.ProfileActivity;
import solutions.doubts.activities.newprofile.UserCache;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.S3Image;
import solutions.doubts.core.util.StringUtil;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.ViewHolder> {

    private static final String TAG = "AnswersAdapter";
    private Context mContext;
    private Answers mAnswers;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.username)
        TextView username;
        @InjectView(R.id.answer_image)
        SimpleDraweeView answerImage;
        @InjectView(R.id.author_image)
        SimpleDraweeView authorImage;
        @InjectView(R.id.timestamp)
        RelativeTimeTextView time;

        public ViewHolder(final View view) {
            super(view);
            ButterKnife.inject(this, view);
            this.view = view;
        }

    }

    public AnswersAdapter(Context context, int questionId) {
        mContext = context;
        mAnswers = new Answers(context, questionId);
    }

    public void update(final Answers.UpdateCallback callback) {
        mAnswers.fetchNext(new Answers.UpdateCallback() {
            @Override
            public void onUpdate(int answerCount) {
                notifyDataSetChanged();
                callback.onUpdate(answerCount);
            }
        });
    }

    @Override
    public AnswersAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_answer, parent, false);
        final View authorContainer = view.findViewById(R.id.author_container);
        authorContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserCache.getInstance().setLastSelectedUser(((Answer)view.getTag()).getAuthor());
                final Intent intent = new Intent(AnswersAdapter.this.mContext,
                        ProfileActivity.class);
                AnswersAdapter.this.mContext.startActivity(intent);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Answer answer = mAnswers.getItem(position);
        holder.view.setTag(answer);
        holder.title.setText(answer.getTitle());
        holder.name.setText(answer.getAuthor().getName());
        holder.username.setText("@" + answer.getAuthor().getUsername());
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        long time = formatter.parseMillis(answer.getCreated());
        holder.time.setReferenceTime(time);
        holder.authorImage.setImageURI(Uri.parse(StringUtil.getProfileImageUrl(answer.getAuthor())));
        RoundingParams params = new RoundingParams();
        params.setRoundAsCircle(true);
        holder.authorImage.getHierarchy().setRoundingParams(params);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(mContext.getResources())
                .setFadeDuration(300)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        S3Image s3i = answer.getImage();
        String answerImage = s3i != null && s3i.getUrl() != null ? s3i.getUrl() : StringUtil.getAnswerImageUrl(answer);
        holder.answerImage.setHierarchy(hierarchy);
        holder.answerImage.setImageURI(Uri.parse(answerImage));
    }

    @Override
    public int getItemCount() {
        return mAnswers.getLength();
    }

}
