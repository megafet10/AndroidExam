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

import android.view.KeyEvent;

public abstract class InputMode {

    public enum InputStates{
        INPUT_STATE_Abc,
        INPUT_STATE_abc,
        INPUT_STATE_ABC,
        INPUT_STATE_123,
        INPUT_STATE_T9Abc,
        INPUT_STATE_T9abc,
        INPUT_STATE_T9ABC,
    }

    public abstract InputStates getCurrentInputStateEnum();
    public abstract InputStates getNextInputStateEnum();

    public abstract boolean onKeyDown(int keyCode, KeyEvent event);

    public abstract boolean onKeyUp(int keyCode, KeyEvent event);

    public abstract void commitInlineComposingText();
    public abstract void destroyInstance();
}
