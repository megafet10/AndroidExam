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

#include "Log.h"

#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <fcntl.h>
#include <jni.h>
#include <ctype.h>

#include "keypad_R9.h"
#include "data.h"

namespace xt9input {

static void setLanguage(JNIEnv *env, jobject object, jint languageIndex)
{
    LOGV("Enter setLanguage()...");
    LanguageIndex langIndex = (LanguageIndex)languageIndex;
    int result = 0;
    reverieSetLanguage(langIndex, &result);
    LOGV("Exit setLanguage()...status = %d", result);
}

static void setHalfWord(JNIEnv *env, jobject object, jint enable)
{
    LOGV("Enter setHalfWord()...");
    char enableHalfWord = static_cast<char>(enable);
    reverieSetHalfWord(enableHalfWord);
    LOGV("Exit setHalfWord()...");
}

static void setInputMode(JNIEnv *env, jobject object, jint inputMode)
{
    LOGV("Enter setInputMode()... %d ", inputMode);
    bool ret = false;
    char val = static_cast<char>(inputMode);
    reverieSetMode(val);
    LOGV("Exit setInputMode()...%s", ret ? "success" : "failed");
}

static jchar getMultitapKeyChar(JNIEnv *env, jobject object,
        jint keyCode, jint tapCount, jchar prevUnichar)
{
    LOGV("Enter getMultitapKeyChar()...");
    jchar* word;
    int result = 0;
    unsigned short typedChar = getReverieKeyChar( keyCode, tapCount, prevUnichar, &result);
    return (jchar)typedChar;
    LOGV("Exit getMultitapKeyChar()...%d", result);

}

static jint getWords(JNIEnv *env, jobject object, jintArray keycodeSeq,  jint keycodeLen,
        jcharArray predictedWords)
{
    LOGV("Enter getWords()...");
    int status = 0;
    int numberOfWords = 0;
    jint*  keySeq = env->GetIntArrayElements(keycodeSeq, NULL);
    jchar* words = env->GetCharArrayElements(predictedWords, NULL);

    for(int a=0; keySeq[a] != 0; a++) {
        LOGV("getWords()...first key code is  %d ", keySeq[a]);
    }

    unsigned short wordsPredicted[30][30];
    reverieGetPredictedWords(keySeq, keycodeLen, wordsPredicted, &numberOfWords, &status);

    LOGV("Exit getWords()...number of words got is %d, status is = %d",
        numberOfWords, status);
    int k=0;
    for(int i=0; i<numberOfWords; i++) {
        for(int j=0; wordsPredicted[i][j] != '\0' ; j++){
            LOGV("%c", wordsPredicted[i][j]);
            words[k++] = (jchar) wordsPredicted[i][j];
        }
        words[k++] = '\0';
        LOGV("    New word");
    }

    env->ReleaseIntArrayElements(keycodeSeq, keySeq, 0);
    env->ReleaseCharArrayElements(predictedWords, words, 0);
    return numberOfWords;
}
static jboolean addCustomWord(JNIEnv *env, jobject object, jcharArray customWord, jint wordLen){
    LOGV("Enter addCustomWords()...");
    int status = 0;
    jchar* words = env->GetCharArrayElements(customWord, NULL);
    reverieAddCustomWord((unsigned short*) words, wordLen, &status);
     env->ReleaseCharArrayElements(customWord, words, 0);
    return status == 1 ? true : false;
}

static jboolean deleteCustomWord(JNIEnv *env, jobject object, jcharArray customWord, jint wordLen){
    LOGV("Enter deleteCustomWords()...");
    int status = 0;
    jchar* words = env->GetCharArrayElements(customWord, NULL);
    reverieDeleteCustomWord((unsigned short*) words, wordLen, &status);
    env->ReleaseCharArrayElements(customWord, words, 0);
    return status == 1 ? true : false;

}

static JNINativeMethod gAlphaMethods[] = {
    {"xt9input_setLanguage", "(I)V",  (void*)setLanguage},
    {"xt9input_setHalfWord", "(Z)V",  (void*)setHalfWord},
    {"xt9input_setInputMode", "(I)V",  (void*)setInputMode},
    {"xt9input_getMultitapKeyChar", "(IIC)C",  (void*)getMultitapKeyChar},
    {"xt9input_getwords", "([II[C)I",  (void*)getWords},
    {"xt9input_addCustomWord", "([CI)Z",  (void*)addCustomWord},
    {"xt9input_deleteCustomWord", "([CI)Z",  (void*)deleteCustomWord},
};

int registerAlphaNative(JNIEnv *env)
{
    const char* className = "com/borqs/ime/NativeAlphaInput";
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        fprintf(stderr,
            "Alpha Native registration unable to find class '%s'\n", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gAlphaMethods,
            sizeof(gAlphaMethods) / sizeof(gAlphaMethods[0])) < 0) {
        fprintf(stderr, "Alpha RegisterNatives failed for '%s'\n", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}
}

