/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.common;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.LinkedList;
import java.util.List;

import solutions.doubts.R;
import solutions.doubts.api.models.Question;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    private static final String TAG = "QuestionsAdapter";
    private List<Question> dataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final View view;
        public final TextView question, username;
        public final ImageView imageView;
        public final ListView tagList;
        public final RelativeTimeTextView timeTextView;

        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            this.context = view.getContext();
            this.question = (TextView)view.findViewById(R.id.title);
            this.username = (TextView)view.findViewById(R.id.username);
            this.imageView = (ImageView)view.findViewById(R.id.image);
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** @TODO: implement this */
                }
            });
            this.tagList = null;//(ListView)view.findViewById(R.id.tagList);
            this.timeTextView = (RelativeTimeTextView)view.findViewById(R.id.timestamp);
        }

    }

    public QuestionsAdapter(final Context context) {
        this.context = context;
        this.dataset = new LinkedList<Question>();
        update();
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                          int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(QuestionsAdapter.this.context,
                        QuestionViewActivity.class);
                intent.putExtra("question", (Question)view.getTag());
                QuestionsAdapter.this.context.startActivity(intent);
            }
        });
        return new ViewHolder(view);
    }

    public void update() {
        /*final Observable<Question> oq = Question.getRemote()
                .get(1, "question0");
        oq.observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Question>() {
            @Override
            public void call(Question question) {
                QuestionsAdapter.this.dataset.add(question);
                QuestionsAdapter.this.dataset.add(question);
                QuestionsAdapter.this.dataset.add(question);
                QuestionsAdapter.this.dataset.add(question);
                QuestionsAdapter.this.dataset.add(question);
                QuestionsAdapter.this.dataset.add(question);

                notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder");
        Log.d(TAG, Integer.toString(position));
        Question q = QuestionsAdapter.this.dataset.get(position);
        holder.view.setTag(q);
        holder.question.setText(q.getTitle());
        holder.username.setText(q.getAuthor().getUsername());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        Log.d(TAG, Integer.toString(this.dataset.size()));
        return this.dataset.size();
    }
}
