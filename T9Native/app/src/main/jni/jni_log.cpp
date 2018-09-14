//
// Created by minhbq on 9/13/2018.
//

#include "jni_log.h"
#include "jni_std.h"
#include "native-lib.h"
#define THIS_LOG_TAG "jni"
#define THIS_FILE	    "jni_log.c"

/**
 * callback to add log to logger....
 * */
void JniLog::addToLoggerHandler(void** data)
{
    LOG_TRACE_FUNC_BEGIN;

    if (data != NULL){
        if (*data != NULL) {
            string_t *log = (string_t *) (*data);

            callToJavaAddLogger(THIS_LOG_TAG, strlen(THIS_LOG_TAG), log->ptr, log->len);

            // free resource
            RELEASE_STRING_T_PTR(log);
            *data = NULL;
        }
    }
    else
    {
        // nothing to do
    }

    LOG_TRACE_FUNC_END;
}