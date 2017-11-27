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

import com.borqs.ime.IME;
import com.borqs.ime.LatinKeyboardView;
import com.borqs.ime.NativeAlphaInput;
import com.borqs.ime.R;

public class InputStateabc extends MultitapInputMode{

    private static final InputMode.InputStates currentInputStateEnum =
            InputMode.InputStates.INPUT_STATE_abc;
    private static final InputMode.InputStates nextInputStateEnum =
            InputMode.InputStates.INPUT_STATE_ABC;
    private static final int mStatusBarIcon = R.drawable.ic_stat_notify_abc_unshifted;


    public InputStates getCurrentInputStateEnum(){
        return currentInputStateEnum;
    }

    public InputMode.InputStates getNextInputStateEnum() {
        if(mInputModeCallback.getLangugage() == IME.ENGLISH_LANGUAGE_INDEX)
            return InputStates.INPUT_STATE_ABC;
        else if (mInputModeCallback.isPredictionOn())
            return InputStates.INPUT_STATE_T9Abc;
        else
            return InputStates.INPUT_STATE_123;
    }

    public InputStateabc(IInputModeCallback cb) {
        super(cb);
        NativeAlphaInput.xt9input_setInputMode(0);
        cb.updateStatusBarIcon(mStatusBarIcon);
    }

    protected void toggleCapsMode(boolean disable){
        if(mInputModeCallback.getLangugage() != IME.ENGLISH_LANGUAGE_INDEX){
            return;
        }

        if(LatinKeyboardView.getCursorCapsMode(mInputModeCallback.getInputConnection())
                && !disable) {
            mInputModeCallback.toggleCapsMode(false);
        }
    }
}
