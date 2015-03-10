/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.createdoubt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import solutions.doubts.R;

public class CreateDoubtActivity extends ActionBarActivity {

    private static final String TAG = "CreateDoubtActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private LinearLayout mTagsContainer;
    private EditText mTagsEditText;
    private ImageView mImageView;

    private final List<String> mTags = new ArrayList<>();
    private String mLastEnteredTag;
    private int mTagIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_create_doubt);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }

        mTagsContainer = (LinearLayout)findViewById(R.id.tags_container);

        mTagsEditText = (EditText)findViewById(R.id.tags_edit_text);
        mTagsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String tag = s.toString();
                if (tag.endsWith(",") && tag.length() > 1) {
                    // new tag added
                    final View v = View.inflate(CreateDoubtActivity.this,
                            R.layout.layout_single_tag, null);
                    final TextView tagView = (TextView)v.findViewById(R.id.tag);
                    final String newTag = mTagsEditText.getText().toString()
                            .replace(",", "").replace("#", "");
                    if (mTags.contains(newTag)) {
                        // return if tag is already added
                        return;
                    } else {
                        // add new tag
                        mTags.add(newTag);
                    }
                    mLastEnteredTag = newTag;
                    tagView.setText("#" + newTag);
                    mTagsContainer.addView(v, mTagIndex);
                    mTagIndex++;
                    mTagsEditText.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mTagsEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mTagsEditText.getText().length() == 0 &&
                            !mTags.isEmpty()) {
                        Log.d(TAG, "here");
                        --mTagIndex;
                        mTagsContainer.removeViewAt(mTagIndex);
                        mTagsEditText.setText("#" + mLastEnteredTag);
                        mTags.remove(mLastEnteredTag);
                    }
                }
                return false;
            }
        });

        mImageView = (ImageView)findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final Bundle extras = data.getExtras();
            final Bitmap imageBitmap = (Bitmap)extras.get("data");
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

}
