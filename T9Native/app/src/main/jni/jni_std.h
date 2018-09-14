/**
 *
 * Brief: common header

 */


#ifndef JNI_STD_H
#define JNI_STD_H

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////

#include <stddef.h>
#include <stdio.h>
#include "jni_log.h"
#include "jni_err.h"
#include "jni_config.h"

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////

// max length of server url
#define MAX_URL_LEN 128

// max length of user id
#define MAX_USER_ID_LEN 64
// mas leng of user pass
#define MAX_USER_PASS_LEN 512

#define MAX_PHONE_LENGTH	40

// max integer len
#define MAX_INTEGER_LENGTH 12

#define JUMP_EXIT_IF_ERR(val,expected) \
	do{ \
		if ((val) != (expected)) \
			goto EXIT;\
	} \
	while(0) 

#define JUMP_IF_ERR(val,expected, label) \
		do{ \
			if ((val) != (expected)) \
				goto label ;\
		} \
		while(0) 


#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define MAX(a, b) ((a) > (b) ? (a) : (b))



/**
 * @def UNUSED_ARG(arg)
 * @param arg   The argument name.
 * UNUSED_ARG prevents warning about unused argument in a function.
 */
#define UNUSED_ARG(arg)  (void)arg

#ifndef ASSERT
#define ASSERT(cond, fmt, args...) do{ \
		((cond)?(void)0:__android_log_assert("##cond", LOG_TAG"/ASSERT", fmt, ##args)); \
	}while(0)
#endif //ASSERT


#define INIT_STRING_T(val) do{ \
	val.ptr = NULL; \
	val.len = 0; \
	}while(0)

#define RELEASE_STRING_T(val) do{ \
	if (val.ptr != NULL) \
	{ \
		delete[] val.ptr; \
	}\
	val.ptr = NULL; \
	val.len = 0; \
	}while(0)

#define RELEASE_STRING_T_PTR(val) do{ \
	if (val != NULL) \
	{ \
        if (val->ptr != NULL) \
        { \
            delete[] val->ptr; \
        }\
        val->ptr = NULL; \
        val->len = 0; \
        delete val;\
	}\
	val = NULL; \
	}while(0)

#define SET_STRING_T(out, val, val_len) do{ \
	if (out.ptr != NULL) \
	{ \
		delete[] out.ptr; \
	}\
	out.ptr = new char[val_len + 1]; \
    memcpy(out.ptr, val, val_len); \
    out.ptr[val_len] = 0; \
    out.len = val_len; \
	}while(0)

#define SET_PJSTRING_TO_STRING_T(out, val) do{ \
    if (!IS_PJSTRING_T_EMPTY(val)) \
    { \
        if (out.ptr != NULL) \
        { \
            delete[] out.ptr; \
        }\
        out.ptr = new char[(int)val.slen + 1]; \
        memcpy(out.ptr, val.ptr, (int)val.slen); \
        out.ptr[(int)val.slen] = 0; \
        out.len = (int)val.slen; \
    }\
	}while(0)

#define IS_STRING_T_EMPTY(val) \
	((val.ptr == NULL) || (val.len == 0))

#define IS_STRING_T_PTR_EMPTY(val) \
	((val == NULL) || (val->ptr == NULL) || (val->len == 0))

#define INIT_PJSTRING_T(val) do{ \
	val.ptr = NULL; \
	val.slen = 0; \
	}while(0)

#define RELEASE_PJSTRING_T(val) do{ \
	if (val.ptr != NULL) \
	{ \
		delete[] val.ptr; \
	}\
	val.ptr = NULL; \
	val.slen = 0; \
	}while(0)

#define RELEASE_STDSTRING_T(val) do{ \
	val.clear(); \
	}while(0)


#define SET_PJSTRING_T(out, val, val_len) do{ \
	if (out.ptr != NULL) \
	{ \
		delete[] out.ptr; \
	}\
	out.ptr = new char[val_len + 1]; \
    memcpy(out.ptr, val, val_len); \
    out.ptr[val_len] = 0; \
    out.slen = val_len; \
	}while(0)


#define IS_PJSTRING_T_EMPTY(val) \
	((val.ptr == NULL) || (val.slen == 0))

#define IS_PJSTRING_T_PTR_EMPTY(val) \
	((val == NULL) || (val->ptr == NULL) || (val->slen == 0))

#define IS_STDSTRING_T_EMPTY(val) \
	((content.data() == NULL) || (content.size() == 0))


#define DECLARE_STRING_T(val) string_t ##val = {NULL, 0}

// check if it's digit char
#define IS_DIGIT_CHAR(c) (((c) >= '0') && ((c) <= '9'))

// convert char to digit
#define CHAR_TO_DIGIT(c) ((c) - '0')

#define TIME_TO_MS(tval) (tval.sec*1000 + tval.msec)

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// DATA TYPE DECLARATION //////////////////////////////////////////////////////

struct string_t
{
	char* ptr;
	int len;
};
///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCION DECLARE ////////////////////////////////////////////////////

/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * -1: str2 > str1
 */
int stringnicmp(const char *s1, int len1, const char *s2, int len2);


/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * 2: str2 > str1
 * -1: invalid
 */
int stringicmp(const string_t* str1, const string_t* str2);

/**
 * convert server error code (SERR) to erro code (ERR)
 */
int mapSErr2Err(int serr_code);

/**
 * convert string to integer
 */
int str2ni(char* val, int len);

bool isStringEmpty(string_t str);
bool isStringEmpty(string_t* str);
int str2int(char* val, int len);

/**
 * Copy string_t
 * CAUTION: caller must take care that dest memory is freed before call,
 * and also free dest after that
 * */
int stringcpy(string_t* dest, string_t* src);

void int2buff(char* buff, int size, int32_t val);
int32_t buff2Int(char* buff, int size);


// LOGGING marcro

void dumpBuff2Hex(unsigned char* data, int len);
void dumpBuff2Hex(char* sub, unsigned char* data, int len);
///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// CLASS DECLARATION //////////////////////////////////////////////////////////




#endif //JNI_STD_H
