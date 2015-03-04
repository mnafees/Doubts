/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.common;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import solutions.doubts.R;
import solutions.doubts.api.models.Question;

public class QuestionViewActivity extends ActionBarActivity {

    private Question question;
    private TextView questionTitle;
    private TextView user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_question);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.question = (Question)getIntent().getSerializableExtra("question");

        this.questionTitle = (TextView)findViewById(R.id.questionTitle);
        this.questionTitle.setText(this.question.getTitle());

        this.user = (TextView)findViewById(R.id.user);
        this.user.setText(this.question.getAuthor().getName());
    }

}
