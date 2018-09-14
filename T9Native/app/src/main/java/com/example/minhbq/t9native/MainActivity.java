package com.example.minhbq.t9native;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    static public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView= findViewById(R.id.textId);

        NativeUtil nativeUtil = NativeUtil.getInstance(this);

        nativeUtil.initLibJNIUtil();
        updateTextOnScreen(nativeUtil.getTextFromJNI());
        try {
           // for (int i =0; i <5; i++) {
                Thread.sleep(2000);
                nativeUtil.RunAESJni();
           // }
        } catch (Exception e) {

        }
    }

    static public void updateTextOnScreen(String content) {
        textView.setText(content);
    }
}
