/**
 *
 * Brief: crypto abstract class for jni
 *
 * author:
 * created:
 * history:
 */

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////

#include "jni_crypto.h"
#include <openssl/sha.h>
#include <openssl/bio.h>
#include <openssl/evp.h>
#include <openssl/buffer.h>


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////
#define THIS_FILE	    "jni_crypto.c"


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// DATA TYPE DECLARATION //////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// GLOBAL VARIABLE ///////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCION DECLARE ////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// METHOD DEFINITION //////////////////////////////////////////////////////////

    JniCrypto::~JniCrypto(){
        LOG_TRACE_FUNC;
    }
/**
 * SHA256 hash
 */
int JniCrypto::sha256(char* ibuf, int length, char* outbuf, int outSize)
{
	int ret = 0;
    LOG_TRACE_FUNC_BEGIN;
    SHA256_CTX context;
	// TODO: check this again
	if ((ibuf != NULL) && (length > 0) && (NULL != outbuf) && (outSize >= SHA256_DIGEST_LENGTH))
	{
		// init context
	    if(SHA256_Init(&context))
	    {
	    	// add data to be ashed
			if(SHA256_Update(&context, (unsigned char*)ibuf, length))
	        {
	        	// finish hashing, copy data to outbuf
	        	if(SHA256_Final((unsigned char*)outbuf, &context))
	       		{
	       			ret = ERR_NONE;
	        	}
				else
		        {
		        	ret = ERR_FAILED;
					LOGE("final sha256 failed");
				}
			}
			else
	        {
	        	// TODO: should call Final when update failed?
	        	ret = ERR_UPDATE_FAILED;
				LOGE("update sha256 failed");
			}
		}
		else
	    {
	     	ret = ERR_INIT_FAILED;
			LOGE("init sha256 failed");
		}
	}
	else
    {
     	ret = ERR_INVALID_PARAM;
		LOGE("invalid param");
	}
//    LOG_TRACE_FUNC_END;
	return ret;
}

/**
 * Size in bytes of hashed data
 */
int JniCrypto::sha256KeySize()
{
	return SHA256_DIGEST_LENGTH;
}

/**
 * SHA256 hash for set of data
 */
int JniCrypto::sha256(string_t* ibuffArr, int number, char* outbuf, int outSize)
{
	int ret = 0;
    LOG_TRACE_FUNC_BEGIN;
    SHA256_CTX context;
	if ((ibuffArr != NULL) && (number > 0) && (NULL != outbuf) && (outSize >= SHA256_DIGEST_LENGTH))
	{
	    if(SHA256_Init(&context))
	    {
	    	for (int i = 0; i < number; i++) // start hasing for each element in array
	    	{
	    		LOG_TRACE("has %d %s", i, ibuffArr[i].ptr);
				if(!SHA256_Update(&context, (unsigned char*)ibuffArr[i].ptr, ibuffArr[i].len))
				{
					ret = ERR_FAILED;
					LOGE("update sha256 failed at %d", i);
					break;

				}

			}
			if((ERR_NONE == ret) && SHA256_Final((unsigned char*)outbuf, &context))
			{
				ret = ERR_NONE;
			}
			else
			{
				// TODO: should call Final when update failed?
				ret = ERR_UPDATE_FAILED;
				LOGE("final sha256 failed");
			}


		}
		else
	    {
	     	ret = ERR_INIT_FAILED;
			LOGE("init sha256 failed");
		}
	}
	else
    {
     	ret = ERR_INVALID_PARAM;
		LOGE("invalid param");
	}
    LOG_TRACE_FUNC_END;
	return ret;

}

/**
 * convert a byte array to hex string, hex string will not include '0x'
 * @param ibuff, length: data to be converted
 * @param outbuf, outSize: output buffer. CALLER must prepare enought memory, mean outSize must equal or larger than length*2
 */
int JniCrypto::byte2hex(char* ibuf, int length, char* outbuf, int outSize)
{
	int len = length*2;
	char val = 0;
	LOG_TRACE_FUNC_BEGIN;

	if ((ibuf != NULL) && (length > 0) && (NULL != outbuf) && (outSize >= len))
	{
		for (int i = 0; i < length; i++)
		{
			val = 0;
			// 1st haft
			val = (ibuf[i] >> 4) & 0x0f ;
			if(val > 9)
				outbuf[2*i] = val + 87;// a..z
			else
				outbuf[2*i] = val + 48; // 0..9

			// 2nd haft
			val = ibuf[i] & 0x0f;
			if(val > 9)
				outbuf[2*i+1] = val + 87; // a..z
			else
				outbuf[2*i+1] = val + 48; // 0..9
		}
		outbuf[len] = 0;
	}
	;
	return 0;
}

/**
 * convert a hext string to byte array, hex string must not include '0x'
 * @param ibuff, length: data to be converted
 * @param outbuf, outSize: output buffer. CALLER must prepare enought memory, mean outSize must equal or larger than length/2
 */
int JniCrypto::hex2byte(char* ibuf, int length, char* outbuf, int outSize)
{
	int len = length/2;
	char val = 0;
	int i=0,j=0;
//	LOG_TRACE_FUNC_BEGIN;
	LOG_TRACE("hex string %s, len %d", ibuf, length);

	if ((ibuf != NULL) && (length > 0) && (NULL != outbuf) && (outSize >= len))
	{
		for (i = 0, j = 0; i < (length - 1); i+=2, j++)
		{
			val = 0;
			// 1st half
			if (ibuf[i] > 90) // a..f
			{
				val = (ibuf[i] - 97) + 10;
			}
			else if (ibuf[i] > 57) // A..F
			{
				val = (ibuf[i] - 65) + 10;
			}
			else // 0..9
			{
				val = ibuf[i] - 48;				
			}
		
			outbuf[j] = (val << 4);
		
			// 2nd half
		
			if (ibuf[i+1] > 90) // a..f
			{
				val = ibuf[i+1] - 97 + 10;
			}
			else if (ibuf[i+1] > 57) // A..F
			{
				val = ibuf[i+1] - 65 + 10;
			}
			else // 0..9
			{
				val = ibuf[i+1] - 48; 			
			}
			
			outbuf[j] |= val & 0xFF;
		}
		outbuf[len] = 0;
//		LOG_TRACE("dec last element 0x%x", outbuf[len-1]);
	}
	LOG_TRACE("len %d", len);
//	LOG_TRACE_FUNC_END;
	return len;
}


/**
 * calculate estimating size of data after decoding base64 buffer
 */
int JniCrypto::calcBase64DecodeLength(const char* b64input, int len) { //Calculates the length of a decoded string
//	LOG_TRACE_FUNC_BEGIN;
//	size_t len = strlen(b64input);
	int padding = 0;

	if (b64input[len-1] == '=' && b64input[len-2] == '=') //last two chars are =
		padding = 2;
	else if (b64input[len-1] == '=') //last char is =
		padding = 1;

//	LOG_TRACE_FUNC_END;

	return (len*3)/4 - padding;
}

/**
 * decode base64 data
 * CALLER must FREE data returned by this function (i.e delete[] buffer)
 * @param b64message, len: input base64 data
 * @param buffer, olength: output data. Buffer will be allocated by this function, so CALLER must FREE it after using (i.e delete[] buffer)
 * @return ERR_NONE when success
 */
int JniCrypto::base64Decode(char* b64message, int len, char** buffer, int* olength)
{
	LOG_TRACE_FUNC_BEGIN;

	BIO *bio, *b64;

	LOG_TRACE_CRYPTO("msg to decode len %d", len);
	LOG_TRACE_CRYPTO("msg to decode %s", b64message);

	// TODO: handler error case

	int decodeLen = calcBase64DecodeLength(b64message, len);

	*buffer = new char[decodeLen + 1];
	(*buffer)[decodeLen] = '\0';

	bio = BIO_new_mem_buf(b64message, len);
	b64 = BIO_new(BIO_f_base64());
	bio = BIO_push(b64, bio);

	BIO_set_flags(bio, BIO_FLAGS_BASE64_NO_NL); //Do not use newlines to flush buffer
	*olength = BIO_read(bio, *buffer, strlen(b64message));
	LOG_TRACE("expected decodeLen %d, decodeLen %d", decodeLen, *olength);
	//ASSERT((*olength == decodeLen),"len not equal decode len"); //length should equal decodeLen, else something went horribly wrong
	//not throw exception here, it cause crash app
	if(*olength != decodeLen){
		BIO_free_all(bio);
		return ERR_FAILED;
	}
	BIO_free_all(bio);

	LOG_TRACE("decode len %d", *olength);

	LOG_TRACE_FUNC_END;

	return 0;
}

/**
 * encode data to base64 format
 * CALLER must FREE data returned by this function (i.e delete[] buffer)
 * @param buffer, length: input data to be encode base64 format
 * @param b64text, olength: output data in base 64 format.
 * Buffer will be allocated by this function, so CALLER must FREE it after using (i.e delete[] buffer)
 * @return ERR_NONE when success
 */
int JniCrypto::base64Encode(const char* buffer, int length, char** b64text, int* olength)
{ //Encodes a binary safe base 64 string
	LOG_TRACE_FUNC_BEGIN;

	BIO *bio, *b64;
	BUF_MEM *bufferPtr;
	LOG_TRACE("init memory");
	b64 = BIO_new(BIO_f_base64());
	bio = BIO_new(BIO_s_mem());
	bio = BIO_push(b64, bio);

	// TODO: handler error case

	LOG_TRACE("start writing with target len %d", length);

	BIO_set_flags(bio, BIO_FLAGS_BASE64_NO_NL); //Ignore newlines - write everything in one line
	BIO_write(bio, buffer, length);
	BIO_flush(bio);
	BIO_get_mem_ptr(bio, &bufferPtr);


	LOG_TRACE("create buff");

	*b64text = new char[(bufferPtr->length + 1) * sizeof(char)];

	LOG_TRACE("copy data, length %d", bufferPtr->length);

	memcpy(*b64text, bufferPtr->data, bufferPtr->length);


	(*b64text)[bufferPtr->length] = '\0';
	*olength = bufferPtr->length;

#ifdef DEBUG_LOG_CRYPTO
	LOG_TRACE("encode str %s len %d", *b64text, *olength);
#endif //DEBUG_LOG_CRYPTO

	LOG_TRACE("close all");
	BIO_set_close(bio, BIO_NOCLOSE);

	BIO_free_all(bio);

	LOG_TRACE_FUNC_END;

	return 0;
}
