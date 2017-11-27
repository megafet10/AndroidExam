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
package com.borqs.ime.adapters;

import java.util.ArrayList;
import java.util.LinkedList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShiftModeListAdapter extends BaseAdapter {
    private Context mContext;
    public LinkedList<View> mShiftModeView;
    private ArrayList<String> shiftModeList;

    public ShiftModeListAdapter(Context context, ArrayList<String> shiftModeList){
        mContext = context;
        this.shiftModeList = shiftModeList;
        build();
    }

    private void build() {
        mShiftModeView = new LinkedList<View>();

        for (String shiftMode : shiftModeList) {
                    TextView cv;
                    cv = (TextView)LayoutInflater.from(mContext).
                            inflate(android.R.layout.simple_list_item_1, null, false);
                    cv.setTag(shiftMode);
                    cv.setEnabled(true);
                    cv.setTextColor(Color.BLACK);
                    cv.setText(shiftMode);
                    mShiftModeView.add(cv);
        }
    }

    public int getCount() {
        return mShiftModeView.size();
    }

    public Object getItem(int position) {
        return mShiftModeView.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return mShiftModeView.get(position);
    }

}
