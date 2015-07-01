/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.fullscreenimageview;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;

import solutions.doubts.R;
import solutions.doubts.thirdparty.facebook.fresco.ZoomableDraweeView;

public class FullscreenImageViewActivity extends AppCompatActivity {

    private static final String TAG = "FullscreenImageView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.layout_image_fullscreen);

        String url = getIntent().getStringExtra("image_url");
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(url))
                .setTapToRetryEnabled(true)
                .build();
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        ZoomableDraweeView imageView = (ZoomableDraweeView)findViewById(R.id.doubt_image);
        imageView.setController(controller);
        imageView.setHierarchy(hierarchy);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }



}
