/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.fullscreenimageview;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import solutions.doubts.R;
import uk.co.senab.photoview.PhotoViewAttacher;

public class FullscreenImageViewActivity extends ActionBarActivity {

    private static final String TAG = "FullscreenImageView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_image_fullscreen);

        final ImageView imageView = (ImageView)findViewById(R.id.fullScreenImageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        new PhotoViewAttacher(imageView);
    }

}
