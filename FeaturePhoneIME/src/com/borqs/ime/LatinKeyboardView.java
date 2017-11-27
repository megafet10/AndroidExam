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
package com.borqs.ime;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.borqs.ime.inputmodes.IInputModeCallback;
import com.borqs.ime.inputmodes.InputMode;
import com.borqs.ime.inputmodes.InputStateABC;
import com.borqs.ime.inputmodes.InputStateAbc;
import com.borqs.ime.inputmodes.InputStateT9ABC;
import com.borqs.ime.inputmodes.InputStateT9Abc;
import com.borqs.ime.inputmodes.InputStateT9abc;
import com.borqs.ime.inputmodes.InputStateabc;
import com.borqs.ime.inputmodes.NumericInputMode;


public class LatinKeyboardView extends View {

    private static final String TAG = "LatinKeyboardView";
    protected SpannableStringBuilder mInlineWord = new SpannableStringBuilder();
    private InputMode mInputMode;
    private boolean mPredictionOn;
    private IInputModeCallback mInputModeCallback;
    private final String INPUT_LANG_KEY = "inputLang";

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public InputMode getCurrentInputMode() {
        return mInputMode;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_POUND: {
                if(!mInputMode.onKeyUp(keyCode, event)) {
                    handleInputStateChangeKey();
                }
                return true;
            }
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
            case KeyEvent.KEYCODE_STAR:
                if (mInputMode != null) {
                    return mInputMode.onKeyUp(keyCode, event);
                } else {
                    return false;
                }
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
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
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_STAR:
                if (mInputMode != null) {
                    return mInputMode.onKeyDown(keyCode, event);
                } else {
                    return false;
                }

            default:
                return false;
        }
    }

    private void handleInputStateChangeKey() {
         mInputMode.commitInlineComposingText();
         setCurrentInputMode(mInputMode.getNextInputStateEnum());
    }

    public void setT9AbcInputMode(IInputModeCallback cb) {
        boolean shift = false;
        InputMode.InputStates stateToSet = InputMode.InputStates.INPUT_STATE_T9abc;
        if(getCursorCapsMode(cb.getInputConnection())) {
            stateToSet = InputMode.InputStates.INPUT_STATE_T9Abc;
            shift = true;
        }
        if(mInputMode!= null) {
            InputMode.InputStates currentstate = mInputMode.getCurrentInputStateEnum();
            if (currentstate == stateToSet) {
                return;
            }
        }
        mPredictionOn = true;
        mInputModeCallback = cb;
        mInputMode = shift ? new InputStateT9Abc(cb) : new InputStateT9abc(cb);

    }

    public void setAbcInputMode(IInputModeCallback cb) {
        boolean shift = false;
        InputMode.InputStates stateToSet = InputMode.InputStates.INPUT_STATE_abc;
        if(getCursorCapsMode(cb.getInputConnection())) {
            stateToSet = InputMode.InputStates.INPUT_STATE_Abc;
            shift = true;
        }
        if(mInputMode!= null) {
            InputMode.InputStates currentstate = mInputMode.getCurrentInputStateEnum();
            if (currentstate == stateToSet) {
                return;
            }
        }
        mPredictionOn = false;
        mInputModeCallback = cb;
        mInputMode = shift ? new InputStateAbc(cb) : new InputStateabc(cb);

    }
    private void setCurrentInputMode(InputMode.InputStates currentInputStateEnum) {
        if(mInputMode.getCurrentInputStateEnum() == currentInputStateEnum){
            return;
        }
        if(currentInputStateEnum == InputMode.InputStates.INPUT_STATE_Abc) {
            mInputMode = new InputStateAbc(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_ABC) {
            mInputMode = new InputStateABC(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_abc) {
            mInputMode = new InputStateabc(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_123) {
            mInputMode = new NumericInputMode(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_T9Abc){
            mInputMode = new InputStateT9Abc(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_T9ABC){
            mInputMode = new InputStateT9ABC(mInputModeCallback);
        } else if (currentInputStateEnum == InputMode.InputStates.INPUT_STATE_T9abc){
            mInputMode = new InputStateT9abc(mInputModeCallback);
        }
    }

    public void setNumericInputMode(IInputModeCallback cb, int inputType) {
        if(mInputMode!= null &&
                mInputMode.getCurrentInputStateEnum() == InputMode.InputStates.INPUT_STATE_123){
            return;
        }
        mPredictionOn = false;
        mInputModeCallback = cb;
        mInputMode = new NumericInputMode(cb,inputType);
    }

    public void setInputLanguage(int languageIndex) {
        NativeAlphaInput.xt9input_setLanguage(languageIndex);
    }

    public void setHalfWord(boolean enable) {
        NativeAlphaInput.xt9input_setHalfWord(enable);
    }

    public void toggleCapsMode(boolean disable) {
        if(disable)
            handleInputStateChangeKey();
        else if (mInputMode.getCurrentInputStateEnum()
                == InputMode.InputStates.INPUT_STATE_T9abc) {
            setT9AbcInputMode(mInputModeCallback);
        } else if (mInputMode.getCurrentInputStateEnum()
                == InputMode.InputStates.INPUT_STATE_abc) {
            setAbcInputMode(mInputModeCallback);
        }

    }

    public void saveInputLangToPref(Context context, String inputLang){
        SharedPreferences preferences = PreferenceManager.
                getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putString(INPUT_LANG_KEY, inputLang);
        prefsEditor.commit();
    }

    public String getInputLangFromPref(Context context) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String inputLangFromPref = null;
        if (preferences != null) {
            inputLangFromPref = preferences.getString(INPUT_LANG_KEY, null);
        }
        return inputLangFromPref;
    }

    public void destroyInstance() {
        if(mInputMode != null) {
            mInputMode.destroyInstance();
            mInputMode = null;
        }
    }

    //if Caps is enabled, it returns true, else false
    public static boolean getCursorCapsMode(InputConnection ic){
        int lastChar = ic.getCursorCapsMode(TextUtils.CAP_MODE_SENTENCES | TextUtils.CAP_MODE_WORDS);
        return ((lastChar & (TextUtils.CAP_MODE_SENTENCES | TextUtils.CAP_MODE_WORDS )) != 0);
    }
}
