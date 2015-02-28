/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.profile.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import solutions.doubts.R;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {
    //private Questions dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final Context context;
        public final TextView question;
        public final ImageView imageView;
        public final ListView tagList;
        public final RelativeTimeTextView timeTextView;

        public ViewHolder(final View view) {
            super(view);
            this.context = view.getContext();
            this.question = (TextView)view.findViewById(R.id.title);
            this.imageView = (ImageView)view.findViewById(R.id.imageView);
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /** @TODO: implement this */
                }
            });
            this.tagList = (ListView)view.findViewById(R.id.tagList);
            this.timeTextView = (RelativeTimeTextView)view.findViewById(R.id.timestamp);
        }

    }

    public QuestionsAdapter(String[] myDataset) {
        //this.dataset = myDataset;
    }

    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //final
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
