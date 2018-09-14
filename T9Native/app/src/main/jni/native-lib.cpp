#include <native-lib.h>

// java VM
static JavaVM* s_javaVM = NULL;
static jobject s_activity_obj; // class obj of java function

JNIEXPORT jstring JNICALL Java_com_example_minhbq_t9native_NativeUtil_stringFromJNI(
        JNIEnv *env,
        jobject obj)
{
    return env->NewStringUTF("hello from MinhBQ");
}


JNIEXPORT jint JNICALL Java_com_example_minhbq_t9native_NativeUtil_testAESJNI(
        JNIEnv *env,
        jobject obj)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_com_example_minhbq_t9native_NativeUtil_testSHAJNI(
        JNIEnv *env,
        jobject obj)

{
    return 0;
}

JNIEXPORT void JNICALL Java_com_example_minhbq_t9native_NativeUtil_initLibJNI(
        JNIEnv *env,
        jobject instance)
{
    initGlobalJavaEnvironment(env, instance);

}

/**
*  init global java environment
*/
static void initGlobalJavaEnvironment(JNIEnv* env, jobject thiz)
{
    LOG_TRACE_FUNC_BEGIN;
    if(s_javaVM != NULL){
        return;
    }
    env->GetJavaVM(&s_javaVM);

    // object which has java method callback
    jclass cls = env->GetObjectClass(thiz);
    s_activity_obj = env->NewGlobalRef(thiz);

    s_method_addlogger_id  = env->GetMethodID(cls, "onAddLogToJava", "(Ljava/lang/String;Ljava/lang/String;) V" );


    LOG_TRACE_FUNC_END;

}
void callToJavaAddLogger(const char* tag, int tag_len, const char* log, int len)
{
    JNIEnv *env;
    bool isAttach = false;
    int ret = 0;
    jint status = JNI_OK;
    jstring logString = NULL;
    jstring logTagString = NULL;
    LOG_TRACE_FUNC_BEGIN;

    LOG_TRACE("add to logger %s", log);

    int getEnvStat = s_javaVM->GetEnv((void **)&env, JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        LOG_TRACE ("call attach thread");
        status = s_javaVM->AttachCurrentThread(&env, NULL);
        if (status != 0) {
            LOGE("Failed to atatcj thread: %d", status);
            ret = ERR_ATTACH_FAILED;
            goto FAILED;
        }
        isAttach = true;
    }

    if ((log != NULL) && (len > 0))
    {
        LOG_TRACE("log %s", log);
        logString = env->NewStringUTF(log);
    }

    if ((tag != NULL) && (tag_len > 0))
    {
        LOG_TRACE("tag %s", tag);
        logTagString = env->NewStringUTF(tag);
    }


    env->CallVoidMethod(s_activity_obj, s_method_addlogger_id,
                        logTagString,
                        logString);

    // release resource
    if(NULL != logString)
        env->DeleteLocalRef(logString);

    if(NULL != logTagString)
        env->DeleteLocalRef(logTagString);

    if (isAttach && s_javaVM->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_EDETACHED) {
        LOG_TRACE ("call detach thread");
        status = s_javaVM->DetachCurrentThread();
        if (status != JNI_OK)
        {
            LOGE("Failed to detach thread: %d", status);
            ret = ERR_DETACH_FAILED;
        }
    }

    FAILED:

    LOG_TRACE_FUNC_END;

}