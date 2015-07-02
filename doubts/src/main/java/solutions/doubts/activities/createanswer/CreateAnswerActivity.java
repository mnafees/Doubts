/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.createanswer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.activities.createquestion.OnBackKeyDownListener;
import solutions.doubts.activities.createquestion.TagsEditText;
import solutions.doubts.activities.questionview.QuestionCache;
import solutions.doubts.api.models.Answer;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.S3Image;
import solutions.doubts.api.query.Query;
import solutions.doubts.core.events.LogoutEvent;

public class CreateAnswerActivity extends AppCompatActivity {

    private static final String TAG = "CreateAnswerActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // UI elements
    @InjectView(R.id.tags_layout)
    FlowLayout mTagsContainer;
    @InjectView(R.id.title)
    EditText mTitle;
    @InjectView(R.id.tags)
    TagsEditText mTags;
    @InjectView(R.id.create_doubt_button)
    FloatingActionButton mCreateDoubtButton;
    @InjectView(R.id.title_text_input_layout)
    TextInputLayout mTextInputLayout;

    // Other members
    private final List<String> mTagsList = new ArrayList<>();
    private String mLastEnteredTag;
    private int mTagIndex = 1;

    private CameraFragment mCameraFragment;
    private CameraHost mCameraHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_doubt);
        ButterKnife.inject(this);

        setupAppBar();
        initTagEditText();

        // Temporary
        mTextInputLayout.setHint("Answer Title");

        mCameraFragment = (CameraFragment)getFragmentManager().findFragmentById(R.id.camera_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((DoubtsApplication)getApplication()).getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((DoubtsApplication)getApplication()).getBus().unregister(this);
    }

    @Subscribe
    public void onLogoutEvent(LogoutEvent event) {
        finish();
    }

    @OnClick(R.id.tags_layout)
    public void onClickTagsLayout() {
        mTags.requestFocus();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(mTags, 0);
    }

    private void setupAppBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.action_bar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initTagEditText() {
        mTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTags(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // TODO: Implement correctly
        mTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    updateTags(mTags.getText().toString());
                }
            }
        });
        // TODO: Implement correctly
        mTags.setOnBackKeyDownListener(new OnBackKeyDownListener() {
            @Override
            public void onBackKey() {
                if (mTags.getText().length() == 0 &&
                        !mTagsList.isEmpty()) {
                    --mTagIndex;
                    mTagsContainer.removeViewAt(mTagIndex);
                    mTags.setText("#" + mLastEnteredTag);
                    mTagsList.remove(mLastEnteredTag);
                }
            }
        });
    }

    private void updateTags(String tag) {
        if (tag.endsWith(" ") && tag.length() > 1) {
            // new tag added
            final View v = View.inflate(CreateAnswerActivity.this,
                    R.layout.layout_single_tag, null);
            final TextView tagView = (TextView) v.findViewById(R.id.tag);
            final String newTag = mTags.getText().toString()
                    .replace(" ", "").replace("#", "");
            if (mTagsList.contains(newTag)) {
                // return if tag is already added
                mTags.getText().clear();
                Toast.makeText(CreateAnswerActivity.this, "Tag already added", Toast.LENGTH_SHORT)
                        .show();
                return;
            } else {
                // add new tag
                mTagsList.add(newTag);
            }
            mLastEnteredTag = newTag;
            tagView.setText("#" + newTag);
            mTagsContainer.addView(v, mTagIndex);
            mTagIndex++;
            mTags.getText().clear();
        }
    }

    public void createAnswer(S3Image s3image) {
        Question q = new Question();
        q.setId(QuestionCache.getInstance().getLastSelectedQuestion().getId());
        q.setSlug(QuestionCache.getInstance().getLastSelectedQuestion().getSlug());
        Answer a = Answer.newAnswer()
                //.author(QuestionCache.getInstance().getLastSelectedQuestion().getAuthor())
                .question(q)
                .tags(mTagsList)
                .title(mTitle.getText().toString())
                .image(s3image)
                .create();

        Query.with(this)
                .remote(Answer.class)
                .resource("answers")
                .create(a, new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        // TODO: show some kind of progress
                    }
                }, new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e != null) {
                            mCreateDoubtButton.setEnabled(true);
                            Snackbar.make(mCameraFragment.getView(), getString(R.string.network_error_message),
                                    Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.create_doubt_button)
    public void onClickCreateDoubtButton() {
        if (TextUtils.isEmpty(mTitle.getText().toString())) {
            mTitle.setError("Oops, it seems you haven\'t entered anything!");
            return;
        }
        if (mTagsList.size() == 0) {
            Snackbar.make(mTags, "Please enter at least one tag", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        mCreateDoubtButton.setEnabled(false);

        mCameraFragment.takePicture(true, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_doubt_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_discard:
                confirmAndExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmAndExit() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("Doubts")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Are you sure you want to discard this answer?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        confirmAndExit();
    }

}

