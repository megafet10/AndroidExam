/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2017 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 *
 */

package com.borqs.ime.inputmodes;

import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import com.borqs.ime.NativeAlphaInput;
import com.borqs.ime.R;

public class NumericInputMode extends InputMode {

    protected SpannableStringBuilder mInlineWord = new SpannableStringBuilder();
    private static final InputMode.InputStates currentInputStateEnum =
            InputMode.InputStates.INPUT_STATE_123;
    private static final InputMode.InputStates nextInputStateEnum =
            InputMode.InputStates.INPUT_STATE_Abc;
    private static final int mStatusBarIcon = R.drawable.ic_stat_notify_123;
    private final String TAG = "NumericInputMode";
    private IInputModeCallback mInputModeCallback;
    private int mInputType = 0;

    @Override
    public InputMode.InputStates getNextInputStateEnum() {
        if (mInputType == InputType.TYPE_CLASS_NUMBER
                || mInputType ==  InputType.TYPE_CLASS_DATETIME
                || mInputType ==  InputType.TYPE_CLASS_PHONE)
            return currentInputStateEnum;
        else
            return nextInputStateEnum;
    }

    public InputStates getCurrentInputStateEnum(){
        return currentInputStateEnum;
    }

    public NumericInputMode(IInputModeCallback cb) {
        this(cb, 0);
    }

    public NumericInputMode(IInputModeCallback cb, int inputType) {
        mInputModeCallback = cb;
        NativeAlphaInput.xt9input_setInputMode(currentInputStateEnum.ordinal());
        cb.updateStatusBarIcon(mStatusBarIcon);
        mInputType = inputType & InputType.TYPE_MASK_CLASS;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_STAR:
                return true;
            case KeyEvent.KEYCODE_POUND :
                if (mInputType == InputType.TYPE_CLASS_PHONE) {
                    mInlineWord.append('#');
                    mInputModeCallback.getInputConnection().commitText(mInlineWord, 1);
                    mInlineWord.clear();
                    return true;
                }
                if (mInputType == InputType.TYPE_CLASS_NUMBER) {
                    mInlineWord.append('.');
                    mInputModeCallback.getInputConnection().commitText(mInlineWord, 1);
                    mInlineWord.clear();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                mInlineWord.append(String.valueOf((char)
                        (keyCode + '0' - KeyEvent.KEYCODE_0)));
                mInputModeCallback.getInputConnection().commitText(mInlineWord, 1);
                mInlineWord.clear();
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_STAR: {
                handleBackSpaceToDelete();
                return true;
            }
            case KeyEvent.KEYCODE_1:
                if(mInputType == InputType.TYPE_CLASS_PHONE){
                    if (!event.isTracking() && event.getRepeatCount() == 0) {
                        event.startTracking();
                    }
                    return true;
                }
                case KeyEvent.KEYCODE_POUND:
                    if (mInputType == InputType.TYPE_CLASS_NUMBER
                            || mInputType == InputType.TYPE_CLASS_DATETIME
                            || mInputType == InputType.TYPE_CLASS_PHONE) {
                        return true;
                    } else if (!event.isTracking() && event.getRepeatCount() == 0) {
                            event.startTracking();
                        return true;
                    }
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                return true;
        }
        return false;
    }



    public void handleBackSpaceToDelete() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        final int length = mInlineWord.length();
        CharSequence cSeqBefore = ic.getTextBeforeCursor(1, 0);
        if (cSeqBefore != null) {
            checkAndDeleteEmojiChar(cSeqBefore);
            ic.deleteSurroundingText(1, 0);
        }
    }

    private void checkAndDeleteEmojiChar(CharSequence cSeqBefore) {
        if(cSeqBefore.length() > 0 && Character.UnicodeBlock.of(cSeqBefore.charAt(0))
                == Character.UnicodeBlock.LOW_SURROGATES){
            //This is 32-bit UniCode Character and
            //represents 2 Chars, hence clearKey should be called twice
            mInputModeCallback.getInputConnection().deleteSurroundingText(1, 0);
        }
    }

    @Override
    public void commitInlineComposingText() {

    }

    @Override
    public void destroyInstance() {

    }
}
