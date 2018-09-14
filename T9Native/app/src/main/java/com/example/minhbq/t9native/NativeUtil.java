package com.example.minhbq.t9native;

import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NativeUtil {
    public static NativeUtil instance = null;

    private JniReqHandleThread threadProcess = null;
    private JniReqHandleThread threadJniRequest = null;

    private boolean isInitJniUtil = false;


    public NativeUtil() {

        threadProcess = new JniReqHandleThread();
        threadJniRequest = new JniReqHandleThread();

    }

    public static NativeUtil getInstance() {
        if (instance == null) {
            instance = new NativeUtil();
        }
        return instance;
    }


    public String getTextFromJNI() {
        return stringFromJNI();
    }

    public void initLibJNIUtil () {
        threadProcess.addItem(new JniReq(null, new JniReqHandle() {
            @Override
            public boolean handle(Object data) {
                initLibJNI();
                isInitJniUtil = true;
                return true;
            }

            @Override
            public void onStop() {

            }
        }, "initJniUtil"));

        if(threadProcess.getState() == Thread.State.NEW ){
            threadProcess.start();
        }

        if(threadJniRequest.getState() == Thread.State.NEW ){
            threadJniRequest.start();
        }
    }


    //Load prebuild lib from JNI
    static {
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("crypto-util");

//        System.loadLibrary("openssl_static");
//        System.loadLibrary("opencrypto_static");
    }

    //--------Declare Native function----------
    public native String stringFromJNI () ;
    public native int testAESJNI();
    public native int testSHAJNI();
    public native void initLibJNI ();



    //JNI callback function

    public void onAddLogToJava (final String tag, final String log) {
        threadJniRequest.addItem(new JniReq(null, new JniReqHandle() {
            @Override
            public boolean handle(Object data) {
//                DebugConfig.LOGH(TAG, tag + ":" + log , true);
                return true;
            }

            @Override
            public void onStop() {

            }
        }, "onAddLogToJava"));
    }




    private class JniReq{
        private Object data;
        private JniReqHandle handle;
        private String tag;
        public JniReq(Object data, JniReqHandle handle, String tag)
        {
            this.data = data;
            this.handle = handle;
            this.tag = tag;
        }

        public Object getData(){
            return data;
        }
        public JniReqHandle getHandle(){
            return handle;
        }
        public String getTag(){return tag;}
    }



    interface JniReqHandle{
        boolean handle(Object data);
        void onStop();
    }

    private class JniReqHandleThread extends Thread {
        private final String TAG = "JniReqHandleThread";
        private BlockingQueue<JniReq> queue;
        private boolean isStop = false;
        private Timer watchdog;
        private JniReq lastItem = null;
        public JniReqHandleThread()
        {
            queue = new LinkedBlockingDeque<JniReq>();
            isStop = false;
            watchdog = null;
        }

        @Override
        public void run() {
            boolean shouldStop = false;
            while(!isStop)
            {
//                DebugConfig.LOG("thread JniUtil is running.....");


                try {
//                    DebugConfig.LOG("queue.take....");
                    JniReq item = queue.take();
                    if ((!isStop) && (item != null) && (item.handle != null))
                    {
                        lastItem = item;
                        isStop = !item.getHandle().handle(item.getData());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {

                }

            }

            //free all data hear:
            queue.clear();

        }

        public boolean addItem(JniReq req)
        {
            return queue.offer(req);
        }
        public void stopIt(){
//            boolean addres = false;
            isStop = true;
            while (!queue.offer(new JniReq(null, null, ""))) //dummy request
            {
                queue.remove();
            }

        }



    }


}
