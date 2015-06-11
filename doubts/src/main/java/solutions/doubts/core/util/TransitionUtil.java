/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class TransitionUtil {

    public static void showWithFadeIn(final View view) {
        AlphaAnimation show = new AlphaAnimation(0.0f, 1.0f);
        show.setDuration(100);
        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(show);
    }

    public static void hideWithFadeOut(final View view) {
        AlphaAnimation hide = new AlphaAnimation(1.0f, 0.0f);
        hide.setDuration(100);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.startAnimation(hide);
    }

}
