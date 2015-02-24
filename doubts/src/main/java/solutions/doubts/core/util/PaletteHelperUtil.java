/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public class PaletteHelperUtil {

    private PaletteHelperUtilListener listener;
    private final ColorHolder colorHolder = new ColorHolder();

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

                    colorHolder.background = vibrant.getRgb();
                    colorHolder.backgroundSecondary = darkVibrant.getRgb();
                    colorHolder.bodyText = vibrant.getBodyTextColor();
                    colorHolder.titleText = vibrant.getTitleTextColor();
                    colorHolder.primary = lightVibrant.getRgb();
                    colorHolder.icon = lightVibrant.getBodyTextColor();
                    colorHolder.accent = lightVibrant.getTitleTextColor();
                }
                listener.onPaletteGenerated(colorHolder);
            }
        });
    }
}
