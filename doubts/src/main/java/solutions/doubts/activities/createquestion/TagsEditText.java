/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.createquestion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class TagsEditText extends EditText {

    private OnBackKeyDownListener mListener;

    public TagsEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TagsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsEditText(Context context) {
        super(context);
    }

    public void setOnBackKeyDownListener(OnBackKeyDownListener listener) {
        mListener = listener;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        TagsInputConnection inputConnection = new TagsInputConnection(super.onCreateInputConnection(outAttrs),
                true);
        inputConnection.setOnBackKeyDownListener(mListener);
        return inputConnection;
    }

    private class TagsInputConnection extends InputConnectionWrapper {

        private OnBackKeyDownListener mListener;

        public TagsInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        public void setOnBackKeyDownListener(OnBackKeyDownListener listener) {
            mListener = listener;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                mListener.onBackKey();
            }
            return super.sendKeyEvent(event);
        }

    }

}
