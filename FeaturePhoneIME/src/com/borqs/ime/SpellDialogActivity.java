/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2016-17 All rights reserved.
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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SpellDialogActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "SpellDialogActivity";
    EditText mEditText;

        @Override
        protected void onCreate(Bundle b) {
            super.onCreate(b);
            setContentView(R.layout.spell_dialog);
            mEditText = (EditText)findViewById(R.id.word_addition_editText);
            mEditText.setSelection(mEditText.getText().length());
            Button saveButton = (Button)findViewById(R.id.ok_button);
            Button cancelButton = (Button) findViewById(R.id.cancel_button);
            saveButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);

        }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_button:
                finish();
                break;
            case R.id.ok_button:
                char[] temp = mEditText.getText().toString().toCharArray();
                boolean status = NativeAlphaInput.xt9input_addCustomWord(temp, temp.length);
                finish();
                break;
        }

    }
}
