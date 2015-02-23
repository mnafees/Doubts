/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.graphics.Palette;

import java.io.Serializable;

public class PaletteHelperUtil {
    public class ColorHolder implements Serializable {
        public int bodyText, titleText, primary, icon, accent, background, backgroundSecondary;
    }

    private PaletteHelperUtilListener listener;
    private ColorHolder mColorHolder = new ColorHolder();

    public void setPaletteHelperUtilListener(final PaletteHelperUtilListener listener) {
        this.listener = listener;
    }

    public PaletteHelperUtilListener paletteHelperUtilListener() {
        if (this.listener == null) {
            throw new IllegalStateException("PaletteHelperUtilListener is null.");
        }

        return this.listener;
    }

    public void generatePalette(final Bitmap bm) {
        Palette.generateAsync(bm, new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch(),
                        darkVibrant = palette.getDarkVibrantSwatch(),
                        lightVibrant = palette.getLightVibrantSwatch();

                if (vibrant != null && darkVibrant != null && lightVibrant != null) {

                    mColorHolder.background = vibrant.getRgb();
                    mColorHolder.backgroundSecondary = darkVibrant.getRgb();
                    mColorHolder.bodyText = vibrant.getBodyTextColor();
                    mColorHolder.titleText = vibrant.getTitleTextColor();
                    mColorHolder.primary = lightVibrant.getRgb();
                    mColorHolder.icon = lightVibrant.getBodyTextColor();
                    mColorHolder.accent = lightVibrant.getTitleTextColor();
                }
                listener.onPaletteGenerated(mColorHolder);
            }
        });
    }
}
