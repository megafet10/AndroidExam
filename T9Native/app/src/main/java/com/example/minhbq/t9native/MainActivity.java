package com.example.minhbq.t9native;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static MainActivity mInstance;
    private TextView textView;

    public static MainActivity getInstance () {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;

        textView= findViewById(R.id.textId);

        NativeUtil nativeUtil = NativeUtil.getInstance(this);

        nativeUtil.initLibJNIUtil();
        updateTextOnScreen(nativeUtil.getTextFromJNI());
        try {
           // for (int i =0; i <5; i++) {
                Thread.sleep(5000);
                nativeUtil.RunAESJni();
           // }
        } catch (Exception e) {

        }
    }

    public void updateTextOnScreen(String content) {
        textView.setText(content);
    }
}
