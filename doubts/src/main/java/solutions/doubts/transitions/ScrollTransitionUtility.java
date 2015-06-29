package solutions.doubts.transitions;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.transitions.everywhere.Scene;
import android.transitions.everywhere.Transition;
import android.transitions.everywhere.TransitionManager;
import android.transitions.everywhere.utils.ArrayMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ScrollTransitionUtility {
    TransitionManager transitionManager;
    Transition transition;
    int scrollHeight;

    public ScrollTransitionUtility(Scene initialScene, Scene finalScene, Transition transition) {
        transitionManager = new TransitionManager();
        transitionManager.setTransition(initialScene, finalScene, transition);
        transitionManager.transitionTo(finalScene);
        this.transition = transition;
        transition.pause(initialScene.getSceneRoot());
    }

    private Set<Animator> getAnimators() {
        try {
            Method getRunningAnimators = Transition.class.getDeclaredMethod("getRunningAnimators");
            getRunningAnimators.setAccessible(true);
            ArrayMap<Animator, Transition.AnimationInfo> am =
                    (ArrayMap<Animator, Transition.AnimationInfo>) getRunningAnimators.invoke(null);
            return am.keySet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    public void setScrollPosition(int scrollPosition) {
        for(Animator a : getAnimators()) {
            if(a instanceof ValueAnimator) {
                ((ValueAnimator)a).setCurrentPlayTime(scrollPosition);
            }
        }
    }


    public int getScrollHeight() {
        return scrollHeight;
    }

    public void setScrollHeight(int scrollHeight) {
        this.scrollHeight = scrollHeight;
        for(Animator a : getAnimators()) {
            if(a instanceof ValueAnimator) {
                ((ValueAnimator)a).setDuration(scrollHeight);
            }
        }
    }
}
