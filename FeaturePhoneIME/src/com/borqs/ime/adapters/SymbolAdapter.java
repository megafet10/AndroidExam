package com.borqs.ime.adapters;
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
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.borqs.ime.IME;
import com.borqs.ime.R;

public class SymbolAdapter extends ArrayAdapter<String> {
    String[] mSymbolarrays;
    public SymbolAdapter(Context context, int resource, String[] mSymbolarray) {
        super(context,resource,mSymbolarray);
        mSymbolarrays = mSymbolarray;
    }
    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null) {
                convertView = super.getView(position, convertView, parent);
                //EnterImage is added for the enter key in symbols grid.
                //In the new symbols array , position of 'enter' is shifted to second position
                //so the below code is checking for position 1 instead of position 0.
                if(position==1) {
                    convertView.setBackground(getContext().getResources().
                            getDrawable(R.drawable.enterbutton));
                    TextView textView = (TextView) convertView;
                    textView.setHeight(getContext().getResources()
                    .getDimensionPixelSize(R.dimen.emojis_symbols_column_height));
                }
            }
            return convertView;
        }
        public void onItemClickListener (AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String selectedEmoji = ((TextView)arg1).getText().toString();
            InputMethodService ims = (InputMethodService)getContext();
            InputConnection ic = ims.getCurrentInputConnection();
            ic.commitText(selectedEmoji, 1);
            ((IME)getContext()).hideWindow();
            return;
        }
    }
