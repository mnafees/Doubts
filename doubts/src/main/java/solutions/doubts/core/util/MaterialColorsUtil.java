/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.graphics.Color;

import java.util.Random;

public class MaterialColorsUtil {

    private String[] mColors = new String[] {"#F44336", "#E91E63", "#673AB7", "#2196F3",
            "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B",
            "#FFC107", "#FF9800", "#FF5722"};
    private String mLastSelectedColor;

    public int generateColor() {
        int random = new Random().nextInt(mColors.length);
        String color = mColors[random];
        while (color.equals(mLastSelectedColor)) {
            random = new Random().nextInt(mColors.length);
            color = mColors[random];
        }
        mLastSelectedColor = color;
        return Color.parseColor(color);
    }

}
