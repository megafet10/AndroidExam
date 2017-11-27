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

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import com.borqs.ime.NativeAlphaInput;

import java.util.ArrayList;
import java.util.List;

public abstract class PredictiveInputMode extends InputMode{

    private ForegroundColorSpan mFGMultiptappingCharSpan =
            new ForegroundColorSpan(Color.parseColor("#ffffff"));
    private BackgroundColorSpan mBKMultiptappingCharSpan =
            new BackgroundColorSpan(Color.parseColor("#000000"));
    protected SpannableStringBuilder mInlineWord = new SpannableStringBuilder();
    private List<Integer> mTypedKeycodes = new ArrayList<Integer>();
    private final String TAG = "PredictiveInputMode";
    protected IInputModeCallback mInputModeCallback;
    private char[] mPredictedWordsBuffer = new char[900];
    private String[] mPredictedWords;
    int mCurrentIndex = 0;
    private int mNumberofWords;
    // vars declaration ends...

    public PredictiveInputMode(IInputModeCallback cb) {
        mInputModeCallback = cb;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_STAR:
                handleBackSpace();
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mPredictedWords != null && mPredictedWords.length > 1) {
                    mCurrentIndex = (mCurrentIndex + 1) % mNumberofWords;
                    mInlineWord.clearSpans();
                    mInlineWord.clear();
                    mInlineWord.append(mPredictedWords[mCurrentIndex]);
                    initInputSpanned();
                    mInputModeCallback.getInputConnection().
                            setComposingText(mInlineWord, 1);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mPredictedWords != null && mPredictedWords.length > 1) {
                    mCurrentIndex = ((mCurrentIndex - 1) == -1) ?
                            (mNumberofWords - 1): (mCurrentIndex - 1);

                    mInlineWord.clearSpans();
                    mInlineWord.clear();
                    mInlineWord.append(mPredictedWords[mCurrentIndex]);
                    initInputSpanned();
                    mInputModeCallback.getInputConnection().
                            setComposingText(mInlineWord, 1);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                handleSpaceKey();
                return true;
            case KeyEvent.KEYCODE_POUND:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_0:
                if (!event.isTracking() && event.getRepeatCount() == 0) {
                    event.startTracking();
                }
                return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                handleSpaceKey();
                return true;
            case KeyEvent.KEYCODE_1:
                handleSymbolKeyPress();
                return true;
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                handleKeyPress(keyCode);
                return true;
        }
        return false;
    }

    private void handleBackSpace() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        final int length = mInlineWord.length();
        if (mTypedKeycodes.size() > 0) {
            mTypedKeycodes.remove(mTypedKeycodes.size()-1);
            mPredictedWords = null;
        }
        if (length > 0) {
            mInlineWord.delete(length - 1, length);
            ic.setComposingText(mInlineWord, 1);
            if (mInlineWord.length() == 0) {
                toggleCapsMode(false);
            }
        } else if (length == 0) {
            CharSequence cSeqBefore = ic.getTextBeforeCursor(1, 0);
            if (cSeqBefore != null) {
                checkAndDeleteEmojiChar(cSeqBefore);
                ic.deleteSurroundingText(1, 0);
                toggleCapsMode(false);
            }
        }
    }

    private void checkAndDeleteEmojiChar(CharSequence cSeqBefore) {
        if (cSeqBefore.length() > 0 && Character.UnicodeBlock.of(cSeqBefore.charAt(0))
                == Character.UnicodeBlock.LOW_SURROGATES) {
            //This is 32-bit UniCode Character and
            //represents 2 Chars, hence clearKey should be called twice
            mInputModeCallback.getInputConnection().deleteSurroundingText(1, 0);
        }
    }

    protected abstract void toggleCapsMode(boolean disable);

    private void handleSymbolKeyPress(){
        InputConnection ic = mInputModeCallback.getInputConnection();
        mInlineWord.clearSpans();
        mInlineWord.append(((char) '.'));
        if (ic != null) {
            ic.commitText(mInlineWord, 1);
        }
        mInlineWord.clear();
        mTypedKeycodes.clear();
        mPredictedWords = null;
    }
    private void handleKeyPress(int keyCode) {
        mPredictedWords = null;
        mTypedKeycodes.add(keyCode);

        int[] typedkeycodes = new int[mTypedKeycodes.size()];
        int i = 0;
        for(int temp: mTypedKeycodes) {
            typedkeycodes[i++]= temp;
        }
        mNumberofWords = NativeAlphaInput.xt9input_getwords(
                typedkeycodes, mTypedKeycodes.size(), mPredictedWordsBuffer);
        typedkeycodes = null;
        String words = new String(mPredictedWordsBuffer);
        mPredictedWords = words.split("\0");
        if (mNumberofWords <= 0) {
            ///((IME)mInputModeCallback).showSpellDialogActivity();
            mNumberofWords = mPredictedWords.length;
            mTypedKeycodes.remove(mTypedKeycodes.size()-1);
        }
        mInlineWord.clearSpans();
        mInlineWord.clear();
        mCurrentIndex = 0;
        if (mPredictedWords.length > 0) {
            mInlineWord.append(mPredictedWords[mCurrentIndex]);
            initInputSpanned();
            mInputModeCallback.getInputConnection().setComposingText(mInlineWord, 1);
        } else if (mTypedKeycodes.size() > 0) {
            mTypedKeycodes.remove(mTypedKeycodes.size() - 1);
        }
    }
    private void initInputSpanned() {
        if (mInlineWord.length() > 0) {
            mInlineWord.setSpan(this.mBKMultiptappingCharSpan, 0,
                    mInlineWord.length(), Spanned.SPAN_COMPOSING
                            | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mInlineWord.setSpan(this.mFGMultiptappingCharSpan,0,
                    mInlineWord.length(), Spanned.SPAN_COMPOSING
                            | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    protected void handleSpaceKey() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        boolean capsModeOff = true;
        if (mInlineWord.length() == 0) {
            //Case where the user is pressing only space
            capsModeOff = false;
        }
        mInlineWord.clearSpans();
        mInlineWord.append(((char) ' '));
        if (ic != null) {
            ic.commitText(mInlineWord, 1);
        }
        mInlineWord.clear();
        mTypedKeycodes.clear();
        mPredictedWords = null;
        toggleCapsMode(capsModeOff);
    }

    @Override
    public void destroyInstance() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        mInlineWord.clearSpans();
        if (ic != null) {
            ic.commitText(mInlineWord, 1);
        }
        mInlineWord.clear();
        mTypedKeycodes.clear();
        mPredictedWords = null;
        mInlineWord.clear();
        mPredictedWords = null;
        mPredictedWordsBuffer = null;
        mTypedKeycodes.clear();
        mTypedKeycodes = null;
    }
    @Override
    public void commitInlineComposingText(){
        InputConnection ic = mInputModeCallback.getInputConnection();
        if (ic != null) {
            ic.commitText(mInlineWord, 1);
        }
        mInlineWord.clear();
        mTypedKeycodes.clear();
        mPredictedWords = null;
    }
}
