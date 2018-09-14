/**
 * Copyright 2016 VTSmart - Viettel Group. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 * Brief: crypto abstract class for jni
 *
 * author: anhnh56
 * created:
 * history:
 */


#ifndef OTT_SIP_CRYPTO_H
#define OTT_SIP_CRYPTO_H

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////
#include "jni_std.h"
#include "jni_config.h"
#include "jni_log.h"
#include "jni_err.h"
//
//#include <pjmedia/types.h>
//#include <pjlib-util/crc32.h>

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// DATA TYPE DECLARATION //////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCION DECLARE ////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// CLASS DECLARATION //////////////////////////////////////////////////////////

// TODO: create a JniCryptoFactory to create corresponding algorigthm object?


class JniCrypto
{
/////////////////////// VARIABLE //////////////////////////////////////////////////////////////////
public:
    // TO BE IMPLEMENTED
protected:
    // TO BE IMPLEMENTED
private:
    // TO BE IMPLEMENTED
/////////////////////// METHOD /////////////////////////////////////////////////////////////////////
public:
//    virtual JniCrypto();
    virtual ~JniCrypto()=0;
	/**
	 * Heavy init members
	 */
	virtual void construct() = 0;
	// TODO: padding?

	/**
	 * Encrypt data
	 * CALLER SHOULD call @calcDataSize() to calculate data size and allocate buffer for odata
	 * @param iv, ivLen: init vector
	 * @param key, keyLen: key to encrypt
	 * @param data, size: data to be encrypt
	 * @param odata (OUT): encrypted data. 
	 * @param maxSize: max size of odata.
	 * @return: >=0: real size of odata if success, < 0: error code
	 */
	virtual int encrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize) = 0;	

	/**
	 * Decrypt data
	 * CALLER SHOULD call @calcDataSize() to calculate data size and allocate buffer for odata
	 * @param iv, ivLen: init vector
	 * @param key, keyLen: key to decrypt
	 * @param data, size: data to be decrypt
	 * @param odata (OUT): decrypted data. 
	 * @param maxSize: max size of odata.
	 * @return: >=0: real size of odata if success, < 0: error code
	 */
	virtual int decrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize) = 0;

	virtual int encryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize) = 0;
	virtual int decryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize) = 0;

	/**
	 * Get size in byte for a datablock
	 * @return: >=0: size if success, < 0: error code
	 */
	virtual int getBlockSize() = 0;
	
	/**
	 * calculate estimated size in bytes for output encrypt/decypted ata
	 * @param isEncrypt: encrypt of decrypt data
	 * @param inputSize: size of data to be encrypted/decrypted
	 * @return: >=0: size, < 0: error code
	 */
	virtual int calcDataSize(bool isEncrypt, int inputSize) = 0;

	/**
	 * Free buffer allocated by calling API of this classed
	 */
	virtual void freeBuff(char* buff) = 0;

	// TODO: putting SHA has here just temporary, should move to its class (i.e. SHA class?)
	/**
	 * SHA256 hash
	 */
    static int sha256(char* ibuf, int length, char* outbuf, int outSize);
	
	/**
	 * SHA256 hash for set of data
	 */
    static int sha256(string_t* ibuffArr, int number, char* outbuf, int outSize);

	
	/**
	 * Size in bytes of hashed data
	 */
	static int sha256KeySize();


	/**
	 * convert a byte array to hex string, hex string will not include '0x'
	 * @param ibuff, length: data to be converted
	 * @param outbuf, outSize: output buffer. CALLER must prepare enought memory, mean outSize must equal or larger than length*2
	 */
	static int byte2hex(char* ibuf, int length, char* outbuf, int outSize);

	/**
	 * convert a hext string to byte array, hex string must not include '0x'
	 * @param ibuff, length: data to be converted
	 * @param outbuf, outSize: output buffer. CALLER must prepare enought memory, mean outSize must equal or larger than length/2
	 */
	static int hex2byte(char* ibuf, int length, char* outbuf, int outSize);


	/**
	 * calculate estimating size of data after decoding base64 buffer
	 */
	static int calcBase64DecodeLength(const char* b64input, int len);

	/**
	 * decode base64 data
	 * CALLER must FREE data returned by this function (i.e delete[] buffer)
	 * @param b64message, len: input base64 data
	 * @param buffer, olength: output data. Buffer will be allocated by this function, so CALLER must FREE it after using (i.e delete[] buffer)
	 * @return ERR_NONE when success
	 */
	static int base64Decode(char* b64message, int len, char** buffer, int* olength) ;

	/**
	 * encode data to base64 format
	 * CALLER must FREE data returned by this function (i.e delete[] buffer)
	 * @param buffer, length: input data to be encode base64 format
	 * @param b64text, olength: output data in base 64 format.
	 *                                       Buffer will be allocated by this function, so CALLER must FREE it after using (i.e delete[] buffer)
	 * @return ERR_NONE when success
	 */
	static int base64Encode(const char* buffer, int length, char** b64text, int* olength);


protected:
    // TO BE IMPLEMENTED
private:
    // TO BE IMPLEMENTED


};



#endif //OTT_SIP_CRYPTO_H
