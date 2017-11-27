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

#ifndef __LOG_H__
#define __LOG_H__

#include <android/log.h> // pulling Android API

#ifdef __cplusplus
extern "C" {
#endif
//
// Wrapping native Android logging API to provide macro for logging.
// LOGD and LOGV will can be turn on and off.  LOGE, LOGW and LOGI will
// always log.  The log messages can be viewed by DDMS tool.
//

#ifndef LOG_TAG
#define LOG_TAG "xt9input"
#endif

// turn on or off debug messages
#define DEBUG_ON

#ifndef DEBUG_ON
#define LOGD(...) ((void)0)
#define LOGV(...) ((void)0)
#else
#define LOGD(...) (LOG(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGV(...) (LOG(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
#endif

#define LOGI(...) (LOG(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGE(...) (LOG(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#define LOGW(...) (LOG(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))

#define LOG(priority, tag, ...) log_print(priority, tag, __VA_ARGS__)
#define log_print(priority, tag, fmt...) __android_log_print(priority, tag, fmt)

#ifdef __cplusplus
}
#endif

#endif
