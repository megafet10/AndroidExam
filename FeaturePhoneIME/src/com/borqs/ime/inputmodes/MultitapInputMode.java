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
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;

import com.borqs.ime.IME;
import com.borqs.ime.NativeAlphaInput;
import com.borqs.ime.R;

public abstract class MultitapInputMode extends InputMode {
    protected SpannableStringBuilder mInlineWord = new SpannableStringBuilder();
    private ForegroundColorSpan mFGMultiptappingCharSpan =
            new ForegroundColorSpan(Color.parseColor("#ffffff"));
    private BackgroundColorSpan mBKMultiptappingCharSpan =
            new BackgroundColorSpan(Color.parseColor("#000000"));
    public static final int MSG_MULTITAP_TIMEOUT = 106;
    public static final int MULTITAP_INTERVAL = 750;
    private int multiTapCount = 0;
    private int lastTappedKeyCode = -1;
    private char mLastInputChar = '\0';
    private final String TAG = "MultitapInputMode";
    protected IInputModeCallback mInputModeCallback;
    private final String ENGLISH_LANG_TAG = "English";
    // vars declaration ends...

    public MultitapInputMode(IInputModeCallback cb) {
        mInputModeCallback = cb;
        boolean pendingMultitapTimeoutMesg = mHandler.hasMessages(MSG_MULTITAP_TIMEOUT);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                handleSpaceKey();
                return true;


            case KeyEvent.KEYCODE_1:
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_STAR:
                handleBackSpaceToDelete();
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

    public void  handleKeyPress(int keyCode) {
        // to set background and forground spans of composing text
        InputConnection ic = mInputModeCallback.getInputConnection();
        boolean pendingMultitapTimeoutMesg = mHandler.hasMessages(MSG_MULTITAP_TIMEOUT);

        // remove older timeOut-msg from handler, if it is present
        // otherwise handler's MSG_MULTITAP_TIMEOUT will also get called parallely
        if (pendingMultitapTimeoutMesg) {
            mHandler.removeMessages(MSG_MULTITAP_TIMEOUT);
        }
        if (lastTappedKeyCode == -1) {
            lastTappedKeyCode = keyCode;
            multiTapCount++;
            char temp = getCharFromEngine(keyCode, multiTapCount);
            mInlineWord.clear();
            mInlineWord.append(temp);
            initInputSpanned();
            if (ic != null) {
                ic.setComposingText(mInlineWord, 1);
            }

        } else if (lastTappedKeyCode != -1) {
            // i.e. if pressing same key again, then we have to
            // delete older chars and compose new char in EditText
            if (lastTappedKeyCode == keyCode) {
                multiTapCount++;
                char temp = getCharFromEngine(keyCode, multiTapCount);
                mInlineWord.clear();
                mInlineWord.append(temp);
                initInputSpanned();

                if (ic != null) {
                    ic.setComposingText(mInlineWord, 1);
                }
            }
            // i.e. if pressing diff key before timeout,
            // then commit earlier composing word/char into EditText
            // and start new composing word as new mInlineWord
            else if (lastTappedKeyCode != keyCode) {
                multiTapCount = 1;
                lastTappedKeyCode = keyCode;
                commitPreviousChar();
                if (getCurrentInputStateEnum() == InputStates.INPUT_STATE_Abc) {
                    //TODO : we will change inputmode generically
                    NativeAlphaInput.xt9input_setInputMode(0);
                    mInputModeCallback.updateStatusBarIcon(R.drawable.ic_stat_notify_abc_unshifted);
                }
                char temp = getCharFromEngine(keyCode, multiTapCount);
                mInlineWord.append(temp);
                initInputSpanned();

                if (ic != null) {
                    ic.setComposingText(mInlineWord, 1);
                }
            }
        }

        mHandler.sendMessageDelayed(mHandler.obtainMessage(
                MSG_MULTITAP_TIMEOUT), MULTITAP_INTERVAL);
    }

    private void initInputSpanned() {
        if (mInlineWord.length() > 0) {
            mInlineWord.setSpan(this.mBKMultiptappingCharSpan, mInlineWord.length() - 1,
                    mInlineWord.length(), Spanned.SPAN_COMPOSING
                            | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mInlineWord.setSpan(this.mFGMultiptappingCharSpan, mInlineWord.length() - 1,
                    mInlineWord.length(), Spanned.SPAN_COMPOSING
                            | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    protected void commitPreviousChar() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        mInlineWord.clearSpans();
        mInlineWord.removeSpan(mBKMultiptappingCharSpan);
        mInlineWord.removeSpan(mFGMultiptappingCharSpan);
        if(mInlineWord.length() > 0) {
            mLastInputChar = mInlineWord.charAt(mInlineWord.length() - 1);
        }else {
            mLastInputChar = '\0';
        }
        if (ic != null) {
            if(mInlineWord.length() > 0) {
                ic.commitText(mInlineWord, 1);
            }
        }
        // now clear the composing word(i.e. mInlineWord)
        mInlineWord.clear();

    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MULTITAP_TIMEOUT:
                    // If timeout happens before user presses again same key
                    // or diff key, then control will come here
                    if (mInlineWord.length() > 0) {
                        multiTapCount = 0;
                        commitPreviousChar();
                        toggleCapsMode(true);
                    }
                    break;
            }
        }
    };

    protected abstract void toggleCapsMode(boolean disable);

    public void commitInlineComposingText() {
        boolean pendingMultitapTimeoutMesg = mHandler.hasMessages(MSG_MULTITAP_TIMEOUT);
        // remove older timeOut-msg from handler, if it is present
        // otherwise handler's MSG_MULTITAP_TIMEOUT will also get called parallely
        if (pendingMultitapTimeoutMesg) {
            mHandler.removeMessages(MSG_MULTITAP_TIMEOUT);
        }
        InputConnection ic = mInputModeCallback.getInputConnection();
        if (ic != null) {
            lastTappedKeyCode = -1;
            multiTapCount = 0;
            commitPreviousChar();
            // now clear the composing word(i.e. mInlineWord)
            mInlineWord.clear();
        }
    }

    private void handleSpaceKey() {

        mInlineWord.append(((char) ' '));

        //Move out of Camel case only when Caps is not enabled
        commitPreviousChar();

        mInlineWord.clear();

        lastTappedKeyCode = -1;
        multiTapCount = 0;
        if (getCurrentInputStateEnum() == InputStates.INPUT_STATE_Abc) {
            toggleCapsMode(true);
        } else if (getCurrentInputStateEnum() == InputStates.INPUT_STATE_abc){
            toggleCapsMode(false);
        }
    }

    private void handleBackSpaceToDelete() {
        InputConnection ic = mInputModeCallback.getInputConnection();
        final int length = mInlineWord.length();
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
        mLastInputChar = '\0';

    }
    @Override
    public void destroyInstance() {
        commitInlineComposingText();
        mInlineWord.clear();
    }

    private char getCharFromEngine(int keyCode, int multiTapCount) {
        char charPassToEngine = '\0';
        char charReturnedFromEngine = '\0';
        if (!ENGLISH_LANG_TAG.equalsIgnoreCase(((IME) mInputModeCallback).
                getCurrentInputLang())) {
            charPassToEngine = mLastInputChar;
        }

        charReturnedFromEngine = NativeAlphaInput.xt9input_getMultitapKeyChar(
                keyCode, multiTapCount, charPassToEngine);
        return charReturnedFromEngine;
    }

    private void checkAndDeleteEmojiChar(CharSequence cSeqBefore) {
        if (cSeqBefore.length() > 0 && Character.UnicodeBlock.of(cSeqBefore.charAt(0))
                == Character.UnicodeBlock.LOW_SURROGATES) {
            //This is 32-bit UniCode Character and
            //represents 2 Chars, hence delete should be called twice
            mInputModeCallback.getInputConnection().deleteSurroundingText(1, 0);
        }
    }
}
