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

#include "keypad_R9.h"
#include "alpha_native.h"


using namespace xt9input;

// ----------------------------------------------------------------------------

//
// helper function to throw an exception
//
static void throwException(JNIEnv *env, const char* ex, const char* fmt, int data)
{
    if (jclass cls = env->FindClass(ex)) {
        char msg[1000];
        sprintf(msg, fmt, data);
        env->ThrowNew(cls, msg);
        env->DeleteLocalRef(cls);
    }
}


static int registerNatives(JNIEnv *env)
{
    return registerAlphaNative(env);
}

/*
 * Returns the JNI version on success, -1 on failure.
 */
extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        fprintf(stderr, "ERROR: GetEnv failed\n");
        return -1;
    }
    assert(env != NULL);

    if (registerNatives(env) != JNI_TRUE) {
        fprintf(stderr, "ERROR: xt9input native registration failed\n");
        return -1;
    }

    /* success -- return valid version number */
    return JNI_VERSION_1_6;
}
