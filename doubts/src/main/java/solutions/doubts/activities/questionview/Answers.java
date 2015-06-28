/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.api.ServerResponseCallback;
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
                .remote(Answer.class)
                .setServerResponseCallback(new ServerResponseCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            if (result.has("answers")) {
                                List<Answer> answers = DoubtsApplication.getInstance().getGson().fromJson(result.get("answers"),
                                        new TypeToken<List<Answer>>() {
                                        }.getType());
                                for (Answer a : answers) {
                                    if (mAnswers.contains(a)) continue;
                                    mAnswers.add(a);
                                }
                                if (previousLength != mAnswers.size())
                                    callback.onUpdate(result.get("count").getAsInt());
                            }
                        }
                    }
                })
                .filterBy("question.id", Integer.toString(mQuestionId));
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



