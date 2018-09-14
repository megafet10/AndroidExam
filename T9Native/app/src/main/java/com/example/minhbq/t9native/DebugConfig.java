package com.example.minhbq.t9native;
import android.util.Log;

public class DebugConfig {

    /**
     * Enable/disable debug log to be shown
     */
    // TODO: show warning dialog if this macro is set to TRUE
    public static final boolean SHOW_DEBUG_LOG = true; // true for debug

    /** Print debug log with TAG
     * @param TAG
     * @param msg
     */
    public static void LOG(String TAG, String msg)
    {
        if (SHOW_DEBUG_LOG)
        {
                Log.d(TAG, msg);
        }
    }

    /** Print debug log with class.method name
     * @param msg
     */
    public static void LOG(String msg)
    {
        if (SHOW_DEBUG_LOG)
        {

            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            final String TAG;
            if (ste.length > 3)
                TAG =  ste[3].getClassName() + "." + ste[3].getMethodName();
            else
                TAG = "LOG.Unknown";
            LOG(TAG, msg);
        }
    }

    /** Print debug log with TAG
     * @param TAG
     * @param msg
     */
    public static void LOGFUNC(String TAG, String msg)
    {
        if (SHOW_DEBUG_LOG)
        {
                Log.d(TAG, ">>>> " + msg + " <<<<");
        }
    }

    /** Print debug log with TAG
     * @param TAG
     * @param msg
     */
    public static void LOGE(String TAG, String msg)
    {
        LOGE(TAG, msg, false);
    }

    /** Print debug log with TAG, add to logger if set
     * @param TAG
     * @param msg
     */
    public static void LOGE(String TAG, String msg, boolean addLogger)
    {
        Log.e(TAG, msg);

    }

    /** Print debug log with class.method name
     * @param msg
     */
    public static void LOGE(String msg)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        final String TAG;
        if (ste.length > 3)
            TAG =  ste[3].getClassName() + "." + ste[3].getMethodName();
        else
            TAG = "VOTT.Unknown";
        LOGE(TAG, msg);
    }



    /** Print debug log with TAG
     * @param TAG
     * @param msg
     */
    public static void LOGH(String TAG, String msg)
    {
        LOGH(TAG, msg, false);
    }

    /** Print debug log with TAG
     * @param TAG
     * @param msg
     */
    public static void LOGH(String TAG, String msg, boolean addLogger)
    {

        Log.i(TAG, msg);
    }

    /** Print debug log with class.method name
     * @param msg
     */
    public static void LOGH(String msg)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        final String TAG;
        if (ste.length > 3)
            TAG =  ste[3].getClassName() + "." + ste[3].getMethodName();
        else
            TAG = "VOTT.Unknown";
        LOG(TAG, msg);

    }

    /** Print debug log with TAG and format string
     * @param TAG
     * @param format
     * @param args
     */
    public static void LOGF(String TAG, String format, Object... args)
    {
        if (SHOW_DEBUG_LOG)
        {
            Log.i(TAG, String.format(format, args));
        }
    }


    /** Pritn debug log with class.method and format string
     * @param format
     * @param args
     */
    public static void LOGFM(String format, Object... args)
    {
        if (SHOW_DEBUG_LOG)
        {
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            final String TAG;
            if (ste.length > 3)
                TAG =  ste[3].getClassName() + "." + ste[3].getMethodName();
            else
                TAG = "LOG.Unknown";
            LOGF(TAG, format, args);

        }
    }

}
