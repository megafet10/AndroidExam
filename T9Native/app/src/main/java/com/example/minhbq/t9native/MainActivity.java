package com.example.minhbq.t9native;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textId);
        NativeUtil nativeUtil = NativeUtil.getInstance(this);

        nativeUtil.initLibJNIUtil();
        textView.setText(nativeUtil.getTextFromJNI());
    }
}
