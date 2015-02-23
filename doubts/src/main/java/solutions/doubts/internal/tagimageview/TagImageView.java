/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.internal.tagimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TagImageView extends View {

    private final ImageView imageView;
    private final Paint paint;
    private float lastTouchX;
    private float lastTouchY;

    private final Path path = new Path();

    public TagImageView(Context context) {
        this(context, null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.imageView = new ImageView(context);
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(10.0f);
        this.paint.setColor(Color.BLUE);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @TargetApi(21)
    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.imageView = new ImageView(context);
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(10.0f);
        this.paint.setColor(Color.BLUE);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                this.lastTouchX = x;
                this.lastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();

                this.path.moveTo(lastTouchX, lastTouchY);
                this.path.lineTo(x, y);

                this.lastTouchX = x;
                this.lastTouchY = y;

                // Invalidate to request a redraw
                invalidate();
                break;
            }

        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawPath(this.path, this.paint);
        canvas.restore();
    }
}
