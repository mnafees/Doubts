/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.api;

import java.util.List;

import solutions.doubts.api.models.Question;

public class QuestionsResource {

    private int count;
    private List<Question> questions;
    private String time;

    public int getCount() {
        return this.count;
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public String getTime() {
        return this.time;
    }

}
