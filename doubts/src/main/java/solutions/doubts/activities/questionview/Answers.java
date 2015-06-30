/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.LinkedList;
import java.util.List;

import solutions.doubts.api.AnswersResource;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.query.Query;

public class Answers {

    private LinkedList<Answer> mAnswers;
    private Context mContext;
    private int mQuestionId;

    public Answers(Context context, int questionId) {
        mContext = context;
        mAnswers = new LinkedList<>();
        mQuestionId = questionId;
    }

    public void fetchNext(final UpdateCallback callback) {
        final int previousLength = mAnswers.size();
        Query.with(mContext)
                .remote(AnswersResource.class)
                .resource("answers")
                .filterBy("question.id", Integer.toString(mQuestionId),
                        new FutureCallback<Response<AnswersResource>>() {
                            @Override
                            public void onCompleted(Exception e, Response<AnswersResource> result) {
                                if (e == null) {
                                    if (result.getHeaders().code() == 200) {
                                        List<Answer> answers = result.getResult().getAnswers();
                                        mAnswers.clear();
                                        for (Answer a : answers) {
                                            mAnswers.add(a);
                                        }
                                        if (previousLength != mAnswers.size())
                                            callback.onUpdate(result.getResult().getCount());
                                    }
                                }
                            }
                        });
    }

    public Answer getItem(int position) {
        return mAnswers.get(position);
    }

    public int getLength() {
        return mAnswers.size();
    }

    public interface UpdateCallback {

        void onUpdate(int answerCount);

    }

}



