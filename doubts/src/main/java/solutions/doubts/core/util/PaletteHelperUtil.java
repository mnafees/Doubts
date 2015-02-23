/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

public class PaletteHelperUtil {

    private PaletteHelperUtilListener listener;
    private Palette lastKnownPalette;

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
                lastKnownPalette = palette;
                listener.onPaletteGenerated(palette);
            }
        });
    }

    public Palette lastKnownPalette() {
        return this.lastKnownPalette;
    }

}
