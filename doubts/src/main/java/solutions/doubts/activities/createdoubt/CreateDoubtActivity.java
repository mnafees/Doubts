/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.createdoubt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.google.gson.JsonObject;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import solutions.doubts.DoubtsApplication;
import solutions.doubts.R;
import solutions.doubts.api.ServerResponseCallback;
import solutions.doubts.api.models.Question;
import solutions.doubts.api.models.S3Image;
import solutions.doubts.api.query.Query;
import solutions.doubts.core.events.LogoutEvent;

public class CreateDoubtActivity extends AppCompatActivity {

    private static final String TAG = "CreateDoubtActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // UI elements
    @InjectView(R.id.tags_layout)
    FlowLayout mTagsContainer;
    @InjectView(R.id.title)
    EditText mTitle;
    @InjectView(R.id.description)
    EditText mDescription;
    @InjectView(R.id.tags)
    EditText mTags;
    @InjectView(R.id.doubt_image)
    ImageView mDoubtImage;
    @InjectView(R.id.create_doubt_button)
    FloatingActionButton mCreateDoubtButton;

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

        setSupportActionBar((Toolbar)findViewById(R.id.action_bar));

        mCameraFragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_fragment);

        setSupportActionBar((Toolbar) findViewById(R.id.action_bar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
            getSupportActionBar().setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000ff")));
        }

        mTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String tag = s.toString();
                if (tag.endsWith(" ") && tag.length() > 1) {
                    // new tag added
                    final View v = View.inflate(CreateDoubtActivity.this,
                            R.layout.layout_single_tag, null);
                    final TextView tagView = (TextView) v.findViewById(R.id.tag);
                    final String newTag = mTags.getText().toString()
                            .replace(" ", "").replace("#", "");
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
            public void afterTextChanged(Editable s) {}
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
    }

    @OnClick(R.id.doubt_image)
    public void onClickDoubtImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @SuppressWarnings("unchecked")
    protected void createDoubt(S3Image s3image) {
        Question q = Question.newQuestion()
                .title(mTitle.getText().toString())
                .description(mDescription.getText().toString())
                .tags(mTagsList)
                .image(s3image)
                .create();
        Query.with(this)
                .remote(Question.class)
                .setServerResponseCallback(new ServerResponseCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            mCreateDoubtButton.setEnabled(true);
                            Snackbar.make(mDescription, getString(R.string.network_error_message), Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            Log.d(TAG, result.toString());
                            finish();
                        }
                    }
                })
                .create(q, null);
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
                .setMessage("Are you sure you want to discard this doubt?")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            mDoubtImage.setScaleType(ImageView.ScaleType.FIT_XY);
            mDoubtImage.setImageBitmap(imageBitmap);
        }
    }

}
