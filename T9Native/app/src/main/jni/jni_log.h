//
// Created by minhbq on 9/13/2018.
//

#ifndef T9NATIVE_JNI_LOG_H
#define T9NATIVE_JNI_LOG_H


#include <android/log.h>
#include <sys/types.h>
#include <unistd.h>
#include "jni_config.h"

// show thread id in log or not
#define LOG_THREAD_ID

// LOG_TAG
#define  LOG_TAG    "LOGJNI"


#define LOG_HEADER_COMPAT "%s/%s[%d] "
#define LOG_HEADER_VAL_COMPAT THIS_FILE,__FUNCTION__,__LINE__

#define LOG_HEADER_COMPAT_2 "%s[%d] "
#define LOG_HEADER_VAL_COMPAT_2 __FUNCTION__,__LINE__

#ifdef LOG_THREAD_ID
#define LOG_HEADER_ALL "%s/%s[%d][t%d] "
#define LOG_HEADER_VAL_ALL THIS_FILE,__FUNCTION__,__LINE__,gettid()
#else //!LOG_THREAD_ID
#define LOG_HEADER_ALL LOG_HEADER_COMPAT
#define LOG_HEADER_VAL_ALL LOG_HEADER_VAL_COMPAT
#endif //LOG_THREAD_ID



// debug level
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
// information level
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
// warning level
#define  LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
// error level
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

// log level values
#define LOG_LEVEL_H 2 // no log trace, user must use macro LOGI LOGD LOGE LOGW or LOGH directly
#define LOG_LEVEL_M 1 // enable LOG_TRACE macro only
#define LOG_LEVEL_L 0 // enabl LOG_TRACE, LOG_TRACE_FUNC_*

// select corresponding log level basing on macro define in build command
// to specify log level in build command, use -D<log level>
// support: LOG_LEVEL_HIG, LOG_LEVEL_MID, LOG_LEVEL_LOW

#if defined(LOG_LEVEL_HIG)
#define LOG_LEVEL LOG_LEVEL_H
#elif defined(LOG_LEVEL_MID)
#define LOG_LEVEL LOG_LEVEL_M
#elif defined(LOG_LEVEL_LOW)
#define LOG_LEVEL LOG_LEVEL_L
#else
#define LOG_LEVEL LOG_LEVEL_H
#endif //LOG_LEVEL

#define LOGH(arg,...)				LOGI("%s[%d] " arg, __FUNCTION__, __LINE__,##__VA_ARGS__)
#define LOGMMDS(arg,...)				LOG_TRACE(LOG_HEADER_ALL "" arg, LOG_HEADER_VAL_ALL, ##__VA_ARGS__)


#if (LOG_LEVEL <= LOG_LEVEL_L) // all log

#define LOG_TRACE(arg,...)				LOGI(LOG_HEADER_ALL "" arg, LOG_HEADER_VAL_ALL, ##__VA_ARGS__)
#define LOG_TRACE_FUNC 					LOGI(LOG_HEADER_ALL "IN" , LOG_HEADER_VAL_ALL)
#define LOG_TRACE_FUNC_BEGIN			LOGI(LOG_HEADER_ALL ">> START\n" , LOG_HEADER_VAL_ALL)
#define LOG_TRACE_FUNC_END 				LOGI(LOG_HEADER_ALL "<< END\n" , LOG_HEADER_VAL_ALL)
#define LOG_TRACE_FUNC_END_RET(ret) 	LOGI(LOG_HEADER_ALL "ret: %d << END\n" , LOG_HEADER_VAL_ALL,ret)
#undef LOGE
#define LOGE(arg,...)					LOGI(LOG_HEADER_ALL "ERR/" arg , LOG_HEADER_VAL_ALL, ##__VA_ARGS__)

#define LOG_TRACE_CRYPTO(arg,...)		LOGI(LOG_HEADER_ALL "CRYPTO/" arg, LOG_HEADER_VAL_ALL, ##__VA_ARGS__)
#define LOG_AUDIO(arg,...)		        LOGI(LOG_HEADER_ALL "" arg, LOG_HEADER_VAL_ALL, ##__VA_ARGS__)
#define LOG_AUDIO_NETSTAT(arg,...)		        LOGI(LOG_HEADER_ALL "" arg, LOG_HEADER_VAL_ALL, ##__VA_ARGS__)

#define DUMP_BUFFER(data, len)          dumpBuff2Hex((unsigned char*)data, (int)len)
#define DUMP_BUFFER_SUB(sub, data, len) dumpBuff2Hex(sub, (unsigned char*)data, (int)len)

#define LOG_ERROR(arg,...)              LOGE(arg, ##__VA_ARGS__)
#define LOG_HIGH(arg, ...)              LOGI(LOG_HEADER_ALL "H/" arg , LOG_HEADER_VAL_ALL, ##__VA_ARGS__)
#elif (LOG_LEVEL <= LOG_LEVEL_M)

#define LOG_TRACE(arg,...)				LOGI(LOG_HEADER_COMPAT "" arg, LOG_HEADER_VAL_COMPAT,##__VA_ARGS__)
#define LOG_TRACE_FUNC 					void()
#define LOG_TRACE_FUNC_BEGIN			void()
#define LOG_TRACE_FUNC_END 				void()
#define LOG_TRACE_FUNC_END_RET(ret) 	void()

#define LOG_TRACE_CRYPTO(arg,...) 		void()
#define LOG_AUDIO(arg,...) 		void()
#define LOG_AUDIO_NETSTAT(arg,...) 		void()

#define DUMP_BUFFER(data, len)          void()
#define DUMP_BUFFER_SUB(sub, data, len) void()

#define LOG_ERROR(arg,...)				LOGE(LOG_HEADER_COMPAT_2 "E/" arg, LOG_HEADER_VAL_COMPAT_2,##__VA_ARGS__)
#define LOG_HIGH(arg, ...)              LOGI(LOG_HEADER_COMPAT_2 "H/" arg , LOG_HEADER_VAL_COMPAT_2, ##__VA_ARGS__)

#elif (LOG_LEVEL <= LOG_LEVEL_H)

#define LOG_TRACE(arg,...)				void()
#define LOG_TRACE_FUNC 					void()
#define LOG_TRACE_FUNC_BEGIN			void()
#define LOG_TRACE_FUNC_END 				void()
#define LOG_TRACE_FUNC_END_RET(ret) 	void()

#define LOG_TRACE_CRYPTO(arg,...) 		void()
#define LOG_AUDIO(arg,...) 		void()
#define LOG_AUDIO_NETSTAT(arg,...) 		void()

#define DUMP_BUFFER(data, len)          void()
#define DUMP_BUFFER_SUB(sub, data, len) void()

#define LOG_ERROR(arg,...)				LOGE(LOG_HEADER_COMPAT_2 "E/" arg, LOG_HEADER_VAL_COMPAT_2,##__VA_ARGS__)
#define LOG_HIGH(arg, ...)              LOGI(LOG_HEADER_COMPAT_2 "H/" arg , LOG_HEADER_VAL_COMPAT_2, ##__VA_ARGS__)


#endif //


#ifndef DEBUG_LOG_CRYPTO
#ifdef LOG_TRACE_CRYPTO
#undef LOG_TRACE_CRYPTO
#define LOG_TRACE_CRYPTO(arg,...)		void()
#endif //LOG_TRACE_CRYPTO

#endif //DEBUG_LOG_CRYPTO



#ifndef DEBUG_LOG_AUDIO
#ifdef LOG_AUDIO
#undef LOG_AUDIO
#define LOG_AUDIO(arg,...)		void()
#endif //LOG_AUDIO
#endif //DEBUG_LOG_AUDIO

#ifndef DEBUG_LOG_AUDIO_NET_STAT
#ifdef LOG_AUDIO_NETSTAT
#undef LOG_AUDIO_NETSTAT
#define LOG_AUDIO_NETSTAT(arg,...)		void()
#endif //LOG_AUDIO_NETSTAT
#endif //DEBUG_LOG_AUDIO_NET_STAT




/**
 * Class to handle request
 * */
class JniLog
{
/////////////////////// VARIABLE //////////////////////////////////////////////////////////////////
public:
    // TO BE IMPLEMENTED
protected:
    // TO BE IMPLEMENTED
private:
/////////////////////// METHOD /////////////////////////////////////////////////////////////////////
public:

    /**
     * add log to logger
     * call to java layer for adding log to logger
     * len is limite to LOGGER_LIMIT_LEN
     * */
//    static int addLogLocked(const char* log, int len);
//    static int addLogLocked(const char* log);
//    static int addLogLockedFormat(const char *format, ...);


protected:
    // TO BE IMPLEMENTED
private:
    /**
     * callback functiont to send log to logger in java layer
     * */
    static void addToLoggerHandler(void** data);
//    static int addLogLocked(const char *format, va_list marker);
};


#endif //T9NATIVE_JNI_LOG_H
