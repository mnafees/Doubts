/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api;

import java.util.List;

import solutions.doubts.api.models.Answer;

public class AnswersResource {

    private int count;
    private List<Answer> answers;
    private String time;

    public int getCount() {
        return this.count;
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    public String getTime() {
        return this.time;
    }

}
