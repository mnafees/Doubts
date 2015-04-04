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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.api.models.Question;
import solutions.doubts.core.events.NetworkEvent;
import solutions.doubts.core.events.ResourceEvent;
import solutions.doubts.internal.RestConstants;

public class CreateDoubtActivity extends ActionBarActivity {

    private static final String TAG = "CreateDoubtActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FlowLayout mTagsContainer;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mTags;
    private ImageView mImageView;
    private FloatingActionButton mCreateDoubtButton;

    private final List<String> mTagsList = new ArrayList<>();
    private String mLastEnteredTag;
    private int mTagIndex = 1;
    private int mNetworkId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_create_doubt);
        DoubtsApplication.getInstance().getBus().register(this);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }

        mTagsContainer = (FlowLayout)findViewById(R.id.tags_container);

        mTitle = (MaterialEditText)findViewById(R.id.title);
        mDescription = (EditText)findViewById(R.id.description_edit_text);

        mTags = (EditText)findViewById(R.id.tags_edit_text);
        mTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String tag = s.toString();
                if (tag.endsWith(",") && tag.length() > 1) {
                    // new tag added
                    final View v = View.inflate(CreateDoubtActivity.this,
                            R.layout.layout_single_tag, null);
                    final TextView tagView = (TextView) v.findViewById(R.id.tag);
                    final String newTag = mTags.getText().toString()
                            .replace(",", "").replace("#", "");
                    if (mTagsList.contains(newTag)) {
                        // return if tag is already added
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

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTags.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mTags.getText().length() == 0 &&
                            !mTagsList.isEmpty()) {
                        --mTagIndex;
                        mTagsContainer.removeViewAt(mTagIndex);
                        mTags.setText("#" + mLastEnteredTag);
                        mTagsList.remove(mLastEnteredTag);
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

        mCreateDoubtButton = (FloatingActionButton)findViewById(R.id.create_doubt_button);
        mCreateDoubtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Question q = Question.newBuilder()
                        .title(mTitle.getText().toString())
                        .description(mDescription.getText().toString())
                        .tags(mTagsList)
                        .build();
                final NetworkEvent networkEvent = NetworkEvent.newBuilder()
                        .url(RestConstants.API_ENDPOINT + "/api/v1/questions")
                        .operation(NetworkEvent.Operation.CREATE)
                        .clazz(Question.class)
                        .object(q)
                        .build();
                mNetworkId = networkEvent.getId();
                networkEvent.post();
            }
        });
    }

    @Subscribe
    public void onResourceEvent(final ResourceEvent event) {
        if (event.getId() == mNetworkId) {
            if (event.getType() == ResourceEvent.Type.FAILURE) {

            } else {
                // success
                Toast.makeText(this, "New question created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

}
