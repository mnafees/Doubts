/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import org.apmem.tools.layouts.FlowLayout;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.R;
import solutions.doubts.activities.fullscreenimageview.FullscreenImageViewActivity;
import solutions.doubts.api.models.Entity;
import solutions.doubts.api.models.Question;
import solutions.doubts.core.util.StringUtil;

public class QuestionViewActivity extends AppCompatActivity {

    // UI elements
    @InjectView(R.id.bookmark_button)
    ImageButton mBookmarkButton;

    // Other private members
    private Question mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_question_view);
        ButterKnife.inject(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_16dp));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mQuestion = QuestionCache.getInstance().getLastSelectedQuestion();

        RoundingParams params = new RoundingParams();
        params.setRoundAsCircle(true);
        ((SimpleDraweeView)findViewById(R.id.author_image)).getHierarchy().setRoundingParams(params);
        ((SimpleDraweeView)findViewById(R.id.author_image)).setImageURI(Uri.parse(
                StringUtil.getProfileImageUrl(mQuestion.getAuthor())
        ));
        ((TextView)findViewById(R.id.username)).setText(mQuestion.getAuthor().getUsername());
        ((TextView)findViewById(R.id.title)).setText(mQuestion.getTitle());
        ((SimpleDraweeView)findViewById(R.id.doubt_image)).setImageURI(Uri.parse(
                StringUtil.getDoubtImageUrl(mQuestion)
        ));
        FlowLayout tagsLayout = (FlowLayout)findViewById(R.id.tags_layout);
        for (Entity tag : mQuestion.getTags()) {
            View v = View.inflate(this, R.layout.layout_single_tag, null);
            TextView textView = (TextView)v.findViewById(R.id.tag);
            textView.setText("#" + tag.getName().replace(" ", "").toLowerCase());
            tagsLayout.addView(v);
        }
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        long time = formatter.parseMillis(mQuestion.getCreated());
        ((RelativeTimeTextView)findViewById(R.id.timestamp)).setReferenceTime(time);

        final TransitionDrawable bookmarkDrawable = (TransitionDrawable)mBookmarkButton.getDrawable();
        bookmarkDrawable.setColorFilter(Color.parseColor("#F44336"), PorterDuff.Mode.SRC_IN);
        mBookmarkButton.setOnClickListener(new View.OnClickListener() {
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
    }

    @OnClick(R.id.doubt_image)
    public void onClickDoubtImage() {
        Intent intent = new Intent(this, FullscreenImageViewActivity.class);
        intent.putExtra("doubt_image_url", StringUtil.getDoubtImageUrl(mQuestion));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_question_view_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
