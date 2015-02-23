/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.internal.tagimageview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class TagPoint extends PointF {

    private final PointF neighbour;
    private int width;

    public TagPoint(final float x, final float y,
                    final PointF neighbour, final int width) {
        super(x, y);
        this.neighbour = neighbour;
        this.width = width;
    }

    public void draw(final Canvas canvas, final Paint paint) {
        canvas.drawLine(this.x, this.y, this.neighbour.x, this.neighbour.y, paint);
        canvas.drawCircle(this.x, this.y, this.width/2, paint);
    }

}
