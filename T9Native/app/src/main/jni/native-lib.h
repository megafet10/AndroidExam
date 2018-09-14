//
// Created by minhbq on 9/13/2018.
//

#ifndef T9NATIVE_NATIVE_LIB_H
#define T9NATIVE_NATIVE_LIB_H

#include <jni.h>
#include <openssl/aes.h>
#include <openssl/sha.h>
#include "jni_config.h"
#include "jni_log.h"
#include "jni_err.h"
#include "jni_msg_ctrl.h"

#ifdef __cplusplus
extern "C" {
#endif

//Java function call to C----------------------------------
JNIEXPORT void JNICALL Java_com_example_minhbq_t9native_NativeUtil_initLibJNI(
        JNIEnv *env,
        jobject instance);

JNIEXPORT jstring JNICALL Java_com_example_minhbq_t9native_NativeUtil_stringFromJNI(
        JNIEnv *env,
        jobject obj);

JNIEXPORT jint JNICALL Java_com_example_minhbq_t9native_NativeUtil_testAESJNI(
        JNIEnv *env,
        jobject obj);

JNIEXPORT jint JNICALL Java_com_example_minhbq_t9native_NativeUtil_testSHAJNI(
        JNIEnv *env,
        jobject obj);

///Callback function from C to Java----------
static void initGlobalJavaEnvironment(JNIEnv* env, jobject thiz);

void callToJavaAddLogger(const char* tag, int tag_len, const char* log, int len);

///Variable----------------------------------
static jmethodID s_method_addlogger_id; // method id of java function - verify data

#ifdef __cplusplus
}
#endif

#endif //T9NATIVE_NATIVE_LIB_H
