/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.questionview;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.tumblr.bookends.Bookends;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.fullscreenimageview.FullscreenImageViewActivity;
import solutions.doubts.api.models.Question;
import solutions.doubts.core.util.StringUtil;

public class QuestionViewActivity extends AppCompatActivity {

    // UI elements
    private SimpleDraweeView mAuthorImage;
    private TextView mName, mUsername, mTitle, mAnswerCount;
    private RelativeTimeTextView mTime;
    private ImageButton mBookmarkButton;

    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.answers)
    RecyclerView mRecyclerView;
    @InjectView(R.id.doubt_image)
    SimpleDraweeView mDoubtImage;

    // Other private members
    private Question mQuestion;
    private ClipboardManager mClipboardManager;
    private AnswersAdapter mAnswersAdapter;
    private Bookends<AnswersAdapter> mBookends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_question_view);
        ButterKnife.inject(this);

        mQuestion = QuestionCache.getInstance().getLastSelectedQuestion();
        mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        setupAppBar();
        initUi();
        updateUi();
        fetchAnswers();
    }

    private void setupAppBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back_white_16dp));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initUi() {
        mSwipeRefreshLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean exec = true;
            @Override
            public void onGlobalLayout() {
                if (exec) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mSwipeRefreshLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    exec = !exec;
                }
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAnswersAdapter = new AnswersAdapter(this, mQuestion.getId());
        mBookends = new Bookends<>(mAnswersAdapter);

        View question = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_question, mRecyclerView, false);
        RoundingParams params = new RoundingParams();
        params.setRoundAsCircle(true);
        mAuthorImage = ButterKnife.findById(question, R.id.author_image);
        mName = ButterKnife.findById(question, R.id.name);
        mUsername = ButterKnife.findById(question, R.id.username);
        mTitle = ButterKnife.findById(question, R.id.title);
        mTime = ButterKnife.findById(question, R.id.timestamp);
        mBookmarkButton = ButterKnife.findById(question, R.id.bookmark_button);

        mAnswerCount = (TextView)LayoutInflater.from(getBaseContext())
                .inflate(R.layout.layout_text_view, mRecyclerView, false);

        mBookends.addHeader(question);
        mBookends.addHeader(mAnswerCount);
        mRecyclerView.setAdapter(mBookends);
    }

    private void updateUi() {
        RoundingParams params = new RoundingParams();
        params.setRoundAsCircle(true);
        mAuthorImage.getHierarchy().setRoundingParams(params);
        mAuthorImage.setImageURI(Uri.parse(
                StringUtil.getProfileImageUrl(mQuestion.getAuthor())
        ));
        mName.setText(mQuestion.getAuthor().getName());
        mUsername.setText("@" + mQuestion.getAuthor().getUsername());
        mTitle.setText(mQuestion.getTitle());
        mDoubtImage.setImageURI(Uri.parse(
                StringUtil.getDoubtImageUrl(mQuestion)
        ));
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeParser();
        long time = formatter.parseMillis(mQuestion.getCreated());
        mTime.setReferenceTime(time);
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

    private void fetchAnswers() {
        mSwipeRefreshLayout.setRefreshing(true);
        mAnswersAdapter.update(new Answers.UpdateCallback() {
            @Override
            public void onUpdate(int answerCount) {
                mSwipeRefreshLayout.setRefreshing(false);
                mAnswerCount.setText(Integer.toString(answerCount) + " Answer" +
                        (answerCount > 1 ? "s" : ""));
                mBookends.notifyDataSetChanged();
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
        if (mQuestion.getAuthor().getId() != DoubtsApplication.getInstance().getSession().getAuthToken().getUserId()) {
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                fetchAnswers();
                return true;
            case R.id.action_edit:

                return true;
            case R.id.action_delete:

                return true;
            case R.id.action_link:

                return true;
            case R.id.action_copy_text:
                ClipData clipData = ClipData.newPlainText("Question", mQuestion.getTitle());
                mClipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Text copied to clipboard.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
