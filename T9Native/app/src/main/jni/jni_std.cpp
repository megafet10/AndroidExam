/**
 *
 * Brief: common function
 */

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////

#include "jni_std.h"
#include "ctype.h"
#include <stdlib.h>
///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////
#define THIS_FILE	    "jni_std.c"



///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// METHOD DEFINITION //////////////////////////////////////////////////////////

/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * -1: str2 > str1
 */
int stringicmp(const char *s1, const char *s2)
{

    while ((*s1==*s2) || (tolower(*s1)==tolower(*s2))) {
	if (!*s1++)
	    return 0;
	++s2;
    }
    return (tolower(*s1) < tolower(*s2)) ? -1 : 1;
}

/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * -1: str2 > str1
 */
int stringnicmp(const char *s1, const char *s2, int len)
{
	int ret = 0;
    if (len > 0)
	{

		ret = 1;
	    while ((*s1==*s2) || (tolower(*s1)==tolower(*s2))) 
		{
			if (!*s1++ || --len <= 0)
			{
				ret = 0;
				break;
			}
			++s2;
	    }
		if (ret != 0)
			ret = (tolower(*s1) < tolower(*s2)) ? -1 : 1;
    }
	else
	{
		ret = 0;
#ifdef DEBUG_LOG_STD_LIB
		LOG_TRACE("zero length");
#endif //DEBUG_LOG_STD_LIB
	}
#ifdef DEBUG_LOG_STD_LIB
	LOG_TRACE("cmp result %d", ret);
#endif //DEBUG_LOG_STD_LIB
    return ret;
}

/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * -1: str2 > str1
 */
int stringnicmp(const char *s1, int len1, const char *s2, int len2)
{
#ifdef DEBUG_LOG_STD_LIB
	LOG_TRACE("s1 %s", s1);
	LOG_TRACE("s1 len %d", len1);
	LOG_TRACE("s2 %s", s2);
	LOG_TRACE("s2 len %d", len2);
#endif //DEBUG_LOG_STD_LIB
	
    if (len1 > len2)
		return 1;
	if (len1 < len2)
		return -1;

    while ((*s1==*s2) || (tolower(*s1)==tolower(*s2))) {
	if (!*s1++ || --len1 <= 0)
	    return 0;
	++s2;
    }
    return (tolower(*s1) < tolower(*s2)) ? -1 : 1;
}


/**
 * compare 2 string
 * @return
 * 0: equal
 * 1: str1 > str2
 * 2: str2 > str1
 * -1: invalid
 */
int stringicmp(const string_t* str1, const string_t* str2)
{
	int ret = 0;
	int cmp_res = 0;

	if ((str1 != NULL) && (str2 != NULL))
	{
		if ((str1->ptr != NULL) && (str2->ptr != NULL))
		{
			if (str1->len == str2->len)
			{
				cmp_res = stringnicmp(str1->ptr, str2->ptr, str1->len);
				if (cmp_res == 0)
					ret = 0;
				else if (cmp_res < 0)
					ret = 2; // str2 > str1
				else
					ret = 1; // str2 < str1
			}
			else if (str1->len > str2->len)
				ret = 1;
			else
				ret = 2;

		}
		else
			ret = -1;

	}
	else
		ret = -1;
#ifdef DEBUG_LOG_STD_LIB
	LOG_TRACE("cmp result %d", ret);
#endif //DEBUG_LOG_STD_LIB
	return ret;
}

/**
 * convert server error code (SERR) to erro code (ERR)
 */
int mapSErr2Err(int serr_code)
{
	int ret = ERR_NONE;
	
	switch (serr_code)
	{
		case SERR_200_OK:
			ret = ERR_NONE;
			break;
		case SERR_408_TIMEOUT:
			ret = ERR_TIMEOUT;
			break;
		default:
			ret = ERR_FAILED;
			break;				
	};
#ifdef DEBUG_LOG_STD_LIB
	LOG_TRACE("serr %d --> err %d", serr_code, ret);
#endif //DEBUG_LOG_STD_LIB
	return ret;
}

/**
 * convert string to integer
 */
int str2ni(char* val, int len)
{	
	int ret = 0;
	bool minus = false;
	int i = 0;
	if ((val != NULL) && (len > 0))
	{
#ifdef DEBUG_LOG_STD_LIB
		LOG_TRACE("convert %s", val);
#endif //DEBUG_LOG_STD_LIB
		if (val[0] == '-')
		{
			minus = true;
			i = 1;
		}
		else if (val[0] == '+')
		{
			minus = false;
			i = 1;
		}
		else
		{
			minus = false;
			i = 0;
		}


		for (; i < len; i++)
		{
			if (IS_DIGIT_CHAR(val[i]))
			{
				ret *= 10;
				ret += CHAR_TO_DIGIT(val[i]);
			}
			else
			{
				break;
			}
		}
		if (true == minus)
			ret = -ret;
#ifdef DEBUG_LOG_STD_LIB
		LOG_TRACE("convert res %d", ret);
#endif //DEBUG_LOG_STD_LIB
	}
	return ret;
}

bool isStringEmpty(string_t str)
{
	if ((str.ptr == NULL) || (str.len <= 0))
		return true;

	return false;
}

bool isStringEmpty(string_t* str)
{
    if ((str == NULL) || (str->ptr == NULL) || (str->len <= 0))
        return true;

    return false;
}

int str2int(char* val, int len){
    int invVal = 0;
    if ((val != NULL) && (len > 0) && (len < MAX_INTEGER_LENGTH))
    {
        char tmp[MAX_INTEGER_LENGTH];
        int tmp_len = (len < MAX_INTEGER_LENGTH-1)? len:(MAX_INTEGER_LENGTH-1);
        memset (tmp, 0, MAX_INTEGER_LENGTH);
        memcpy(tmp, val, tmp_len);
        tmp[len] = 0;
        invVal = atoi(tmp);
    }
    else
    {
        invVal = 0;
    }
    return invVal;
}

int stringcpy(string_t* dest, string_t* src)
{
    if ((dest != NULL) && !IS_STRING_T_PTR_EMPTY(src))
    {
        dest->ptr = new char[src->len + 1];
        strncpy(dest->ptr, src->ptr, src->len);
        dest->ptr[src->len] = 0;
        dest->len = src->len;
        return dest->len;
    }
    return 0;
}

void int2buff(char* buff, int size, int32_t val)
{

    size = size > 4? 4 : size;
    for (int i = 0; i < size; i ++)
    {
        buff[i] = (char)(val & 0x000000FF);
        val = val >> 8;
    }
}
int32_t buff2Int(char* buff, int size)
{
    size = size > 4? 4 : size;
    int32_t  val = 0;
    int32_t mask = 0x000000FF;
    for (int i = 0; i < size; i ++)
    {
        val |= (((int32_t) buff[i] & 0x000000FF) << (i*8));
    }
    return val;
}


void dumpBuff2Hex(char* sub, unsigned char* data, int len)
{
    unsigned char *hex_buf = NULL;
    int i = 0;
    unsigned char val = 0;
    int length = len * 2;

    hex_buf = new unsigned char[length + 1];

    if (NULL != hex_buf)
    {
        memset(hex_buf, 0, length + 1);

        for (i = 0; i < len; i++)
        {
            val = 0;
            // 1st haft
            val = data[i] >> 4;
            if(val > 9)
                hex_buf[2*i] = val + 87;// a..z
            else
                hex_buf[2*i] = val + 48; // 0..9

            // 2nd haft
            val = data[i] & 0x0f;
            if(val > 9)
                hex_buf[2*i+1] = val + 87; // a..z
            else
                hex_buf[2*i+1] = val + 48; // 0..9
        }
        LOGMMDS("%s: hex %s", sub, hex_buf);
        LOGE("%s: hex %s", sub, hex_buf);
        delete[] hex_buf;

    }
    else // no memroy
    {
        LOG_TRACE("No memory");
    }


}
void dumpBuff2Hex(unsigned char* data, int len)
{
    unsigned char *hex_buf = NULL;
    int i = 0;
    unsigned char val = 0;
    int length = len * 2;

    hex_buf = new unsigned char[length + 1];

    if (NULL != hex_buf)
    {
        memset(hex_buf, 0, length + 1);

        for (i = 0; i < len; i++)
        {
            val = 0;
            // 1st haft
            val = data[i] >> 4;
            if(val > 9)
                hex_buf[2*i] = val + 87;// a..z
            else
                hex_buf[2*i] = val + 48; // 0..9

            // 2nd haft
            val = data[i] & 0x0f;
            if(val > 9)
                hex_buf[2*i+1] = val + 87; // a..z
            else
                hex_buf[2*i+1] = val + 48; // 0..9
        }
        LOGE("hex %s", hex_buf);
        LOGMMDS("hex %s", hex_buf);
        delete[] hex_buf;

    }
    else // no memroy
    {
        LOG_TRACE("No memory");
    }

}