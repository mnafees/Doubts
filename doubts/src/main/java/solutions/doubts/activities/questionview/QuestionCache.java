/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import solutions.doubts.api.models.Question;

public class QuestionCache {

    private static QuestionCache sInstance;
    private Question mLastSelectedQuestion;

    private QuestionCache() {}

    public void setLastSelectedQuestion(Question question) {
        mLastSelectedQuestion = question;
    }

    public Question getLastSelectedQuestion() {
        return mLastSelectedQuestion;
    }

    public static QuestionCache getInstance() {
        synchronized (QuestionCache.class) {
            if (sInstance == null) {
                sInstance = new QuestionCache();
            }
        }
        return sInstance;
    }

}
