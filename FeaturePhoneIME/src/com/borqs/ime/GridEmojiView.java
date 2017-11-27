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
import android.inputmethodservice.InputMethodService;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GridEmojiView extends GridView {
    String[] mTempArray = null;
    private static final int BASE_HEX = 16;
    private static final String TAG = "IME_GridEmojiView";

    public GridEmojiView(Context context) {
        super(context);
    }

    public GridEmojiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridEmojiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setArray(String[] array){
        mTempArray = new String[array.length];
        mTempArray = array;
        init();
    }

    public void init() {
        ArrayAdapter<String> emojiAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.simple_list,mTempArray);
        this.setAdapter(emojiAdapter);
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
                String selectedEmoji = ((TextView)arg1).getText().toString();
                InputMethodService ims = (InputMethodService)getContext();
                InputConnection ic = ims.getCurrentInputConnection();
                ic.commitText(selectedEmoji, 1);
                int tag = (Integer)getTag();
                if(tag == IME.SYMBOLS_SCREEN){
                    ((IME)getContext()).hideWindow();
                    return;
                }
            }
        });
        this.setScrollbarFadingEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int pos = 0;
        int columns = 0;
        int nextPos = 0;
        int rows = 0;
        // TODO Auto-generated method stub
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                pos = this.getSelectedItemPosition();
                columns = this.getNumColumns();
                nextPos = pos - columns;
                if(nextPos >= 0) {
                    this.setSelection(nextPos);
                    return true;
                } else {
                    int code = (Integer) getTag();
                    if (code == 0) {
                        ((IME) getContext()).getmSymbolsTab().setFocusable(true);
                        ((IME) getContext()).getmSymbolsTab().requestFocus();
                    }
                    else if (code == 1) {
                        ((IME) getContext()).getmEmojiPeopleTab()
                                .setFocusable(true);
                        ((IME) getContext()).getmEmojiPeopleTab().requestFocus();
                    } else if (code == 2) {
                        ((IME) getContext()).getmEmojiObjectsTab().setFocusable(
                                true);
                        ((IME) getContext()).getmEmojiObjectsTab().requestFocus();
                    } else if (code == 3) {
                        ((IME) getContext()).getmEmojiNatureTab()
                                .setFocusable(true);
                        ((IME) getContext()).getmEmojiNatureTab().requestFocus();
                    } else if (code == 4) {
                        ((IME) getContext()).getmEmojiCarsTab().setFocusable(true);
                        ((IME) getContext()).getmEmojiCarsTab().requestFocus();
                    } else if (code == 5) {
                        ((IME) getContext()).getmEmojiPunctionsTab().setFocusable(
                                true);
                        ((IME) getContext()).getmEmojiPunctionsTab().requestFocus();
                    } else if (code == 6) {
                        ((IME) getContext()).getmEmoticonsTab().setFocusable(true);
                        ((IME) getContext()).getmEmoticonsTab().requestFocus();
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                pos = this.getSelectedItemPosition();
                columns = this.getNumColumns();
                nextPos = pos + columns;
                rows = this.getCount();
                if(nextPos >= rows) {
                    nextPos = nextPos % columns;
                }
                this.setSelection(nextPos);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                pos = this.getSelectedItemPosition();
                columns = this.getNumColumns();
                nextPos = pos + 1;
                rows = this.getCount();
                if(nextPos >= rows) {
                    nextPos = 0;
                }
                this.setSelection(nextPos);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                pos = this.getSelectedItemPosition();
                columns = this.getNumColumns();
                nextPos = pos - 1;
                rows = this.getCount();
                if(nextPos <= 0) {
                    nextPos = 0;
                }
                this.setSelection(nextPos);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch(keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                View v = getSelectedView();
                if (v == null || !(v instanceof TextView)){
                    return false;
                    }
                String selectedEmoji = ((TextView)getSelectedView()).getText().toString();
                if(TextUtils.isEmpty(selectedEmoji)) {
                    return false;
                }
                InputConnection ic = ((IME)getContext()).getCurrentInputConnection();
                ic.commitText(selectedEmoji, 1);
                int tag = (Integer)getTag();
                if(tag == IME.SYMBOLS_SCREEN){
                    ((IME)getContext()).hideWindow();
                    return true;
                }
                return true;
        }
        return false;
    }


    // function to check whether the character is a emoji unicode or not
    public static boolean checkAscii(char c) {
        return c >= 32 && c < 225;
    }
}
