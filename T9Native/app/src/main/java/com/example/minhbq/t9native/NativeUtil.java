package com.example.minhbq.t9native;

import android.content.Context;
import android.text.TextUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NativeUtil {
    public static NativeUtil instance = null;
    private Context mContext;

    private JniReqHandleThread threadProcess = null;
    private JniReqHandleThread threadJniRequest = null;

    private boolean initJniUtil = false;

    public static void setInstance(NativeUtil instance) {
        NativeUtil.instance = instance;
    }
    public static NativeUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NativeUtil(context);
        }
        return instance;
    }

    public boolean isInitJniUtil() {
        return initJniUtil;
    }

    public void setInitJniUtil(boolean init) {
        initJniUtil = init;
    }

    public NativeUtil(Context context) {

        mContext = context;
        threadProcess = new JniReqHandleThread();
        threadJniRequest = new JniReqHandleThread();

    }


//Get the value from JNI immediately
    public String getTextFromJNI() { return stringFromJNI();}

    //Send command to JNI
    public int TestAESJni () {
        threadProcess.addItem(new JniReq(null, new JniReqHandle() {
            @Override
            public boolean handle(Object data) {
                testAESJNI();
                return true;
            }

            @Override
            public void onStop() {

            }
        }, "Test AES"));

        return 0;
    }

    public int TestSHAJni () {
        threadProcess.addItem(new JniReq(null, new JniReqHandle() {
            @Override
            public boolean handle(Object data) {
                testSHAJNI();
                return true;
            }

            @Override
            public void onStop() {

            }
        }, "Test SHA"));

        return 0;
    }

    public void initLibJNIUtil () {
        threadProcess.addItem(new JniReq(null, new JniReqHandle() {
            @Override
            public boolean handle(Object data) {
                initLibJNI();
                initJniUtil = true;
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
                DebugConfig.LOGH(tag, tag + ":" + log , true);
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
                DebugConfig.LOG("thread JniUtil is running.....");


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
