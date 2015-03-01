/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.transitions;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.transitions.everywhere.ChangeBounds;
import android.transitions.everywhere.TransitionValues;
import android.transitions.everywhere.utils.AnimatorUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChangeBoundsOnScrollTransition {

    private static final String TAG = "ChangeBoundsOnScrollTransition";

    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    private final List<ObjectAnimator> animatorList = new ArrayList<>();

    private boolean initialised;
    private boolean initialViewPreDraw;
    private boolean finalViewPreDraw;
    private long duration = -1;

    public ChangeBoundsOnScrollTransition(final ViewGroup parentContainer,
                                          final ViewGroup initialView,
                                          final ViewGroup finalView) {
        final ChildViewPreDraw childViewPreDraw = new ChildViewPreDraw();
        childViewPreDraw.setListener(new ChildViewPreDrawListener() {
            @Override
            public void onPreDraw() {
                initialise(initialView, finalView);

                parentContainer.removeAllViews();
                parentContainer.addView(initialView);
            }
        });
        parentContainer.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (!initialised) {
                            parentContainer.removeAllViews();

                            parentContainer.addView(finalView);
                            parentContainer.addView(initialView);

                            initialView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    if (!initialViewPreDraw) {
                                        childViewPreDraw.increment();
                                        initialViewPreDraw = true;
                                    }
                                    return true;
                                }
                            });
                            finalView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    if (!finalViewPreDraw) {
                                        childViewPreDraw.increment();
                                        finalViewPreDraw = true;
                                    }
                                    return true;
                                }
                            });
                        }

                        return true;
                    }
                }
        );
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        if (/*ViewUtils.isLaidOut(view, false) ||*/ view.getWidth() != 0 || view.getHeight() != 0) {
            values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(),
                    view.getRight(), view.getBottom()));
        }
    }

    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void initialise(final ViewGroup initialView, final ViewGroup finalView) {
        if (getAllChildren(initialView).size() != getAllChildren(finalView).size()) {
            ViewGroup moreChildrenViewGroup = null;
            ViewGroup lessChildrenViewGroup = null;
            if (getAllChildren(initialView).size()  > getAllChildren(finalView).size()) {
                moreChildrenViewGroup = initialView;
                lessChildrenViewGroup = finalView;
            } else {
                moreChildrenViewGroup = finalView;
                lessChildrenViewGroup = initialView;
            }

            final List<Integer> childIdList = new ArrayList<>();

            for (int i = 0; i < getAllChildren(moreChildrenViewGroup).size(); ++i) {
                final int childId = getAllChildren(moreChildrenViewGroup).get(i).getId();
                if (childIdList.contains(childId)) {
                    continue;
                }

                childIdList.add(childId);
                if (moreChildrenViewGroup.findViewById(childId) != null &&
                        lessChildrenViewGroup.findViewById(childId) != null) {
                    TransitionValues startValues = new TransitionValues();
                    startValues.view = initialView.findViewById(childId);
                    TransitionValues endValues = new TransitionValues();
                    endValues.view = finalView.findViewById(childId);

                    captureStartValues(startValues);
                    captureEndValues(endValues);

                    setupAnimators(startValues, endValues);
                }
            }
        } else {
            final List<Integer> childIdList = new ArrayList<>();

            for (int i = 0; i < getAllChildren(initialView).size(); ++i) {
                final int childId = getAllChildren(initialView).get(i).getId();
                if (childIdList.contains(childId)) {
                    continue;
                }

                childIdList.add(childId);
                if (initialView.findViewById(childId) != null &&
                        finalView.findViewById(childId) != null) {
                    TransitionValues startValues = new TransitionValues();
                    startValues.view = initialView.findViewById(childId);
                    TransitionValues endValues = new TransitionValues();
                    endValues.view = finalView.findViewById(childId);

                    captureStartValues(startValues);
                    captureEndValues(endValues);

                    setupAnimators(startValues, endValues);
                }
            }
        }
        this.initialised = true;
        setAnimatorsDuration();
    }

    private void setAnimatorsDuration() {
        if (this.duration == -1) {
            throw new IllegalStateException("Transition duration not set.");
        }
        for (Animator animator : this.animatorList) {
            animator.setDuration(this.duration);
        }
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }

        return result;
    }

    private void setupAnimators(TransitionValues startValues,
                                TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return;
        }

        Map<String, Object> startParentVals = startValues.values;
        Map<String, Object> endParentVals = endValues.values;
        final View view = startValues.view;
        Rect startBounds = (Rect) startParentVals.get(PROPNAME_BOUNDS);
        Rect endBounds = (Rect) endParentVals.get(PROPNAME_BOUNDS);
        if (startBounds == null || endBounds == null) {
            return;
        }
        int startLeft = startBounds.left;
        int endLeft = endBounds.left;
        int startTop = startBounds.top;
        int endTop = endBounds.top;
        int startRight = startBounds.right;
        int endRight = endBounds.right;
        int startBottom = startBounds.bottom;
        int endBottom = endBounds.bottom;
        int startWidth = startRight - startLeft;
        int startHeight = startBottom - startTop;
        int endWidth = endRight - endLeft;
        int endHeight = endBottom - endTop;
        int numChanges = 0;
        if ((startWidth != 0 && startHeight != 0) || (endWidth != 0 && endHeight != 0)) {
            if (startLeft != endLeft || startTop != endTop) ++numChanges;
            if (startRight != endRight || startBottom != endBottom) ++numChanges;
        }
        if (numChanges > 0) {
            if (startWidth == endWidth && startHeight == endHeight && !startBounds.equals(endBounds)) {
                // the dimensions don't change but the bounds do change
                ObjectAnimator x = ObjectAnimator.ofFloat(view, "x", startLeft, endLeft);
                ObjectAnimator y = ObjectAnimator.ofFloat(view, "y", startTop, endTop);
                this.animatorList.add(x);
                this.animatorList.add(y);
            } else {
                if (startLeft != endLeft) view.setLeft(startLeft);
                if (startTop != endTop) view.setTop(startTop);
                if (startRight != endRight) view.setRight(startRight);
                if (startBottom != endBottom) view.setBottom(startBottom);

                if (startLeft != endLeft || startTop != endTop) {
                    ObjectAnimator topLeftAnimator = AnimatorUtils.ofInt(new ChangeBounds(), view, "left", "top",
                            startLeft, startTop, endLeft, endTop);
                    this.animatorList.add(topLeftAnimator);
                }
                if (startRight != endRight || startBottom != endBottom) {
                    ObjectAnimator bottomRightAnimator = AnimatorUtils.ofInt(new ChangeBounds(), view, "right", "bottom",
                            startRight, startBottom, endRight, endBottom);
                    this.animatorList.add(bottomRightAnimator);
                }
            }
        }
        for(Animator animator : this.animatorList) {
            animator.setInterpolator(new LinearInterpolator());
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        if (this.duration == -1) {
            throw new IllegalStateException("Transition duration not set.");
        }

        return this.duration;
    }

    public boolean animateOnScroll(long scroll) {
        for (ObjectAnimator animator : this.animatorList) {
            animator.setCurrentPlayTime(scroll);
        }
        return !this.animatorList.isEmpty();
    }

    private class ChildViewPreDraw {

        private ChildViewPreDrawListener listener;
        private int c = 0;

        public void setListener(final ChildViewPreDrawListener listener) {
            this.listener = listener;
        }

        public void increment() {
            c++;
            if (c == 2) {
                this.listener.onPreDraw();
            }
        }

    }

    private interface ChildViewPreDrawListener {

        public void onPreDraw();

    }

}
