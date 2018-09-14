/**
 * Copyright 2016 VTSmart - Viettel Group. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 * Brief: AES 256 crypto implementation, using openssl lib
 *
 * author: anhnh56
 * created:
 * history:
 */


#ifndef OTT_SIP_CRYPTO_AES_H
#define OTT_SIP_CRYPTO_AES_H


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////
#ifdef __cplusplus
extern "C" {
#endif

//#include "jni_std.h"

#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>
#include <openssl/ossl_typ.h>

#ifdef __cplusplus
}
#endif


#include "jni_crypto.h"
#include "jni_err.h"
#include "jni_log.h"
#include "jni_config.h"
///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// DATA TYPE DECLARATION //////////////////////////////////////////////////////
enum AES_MODE{
	AES_CBC_256 = 0,
	AES_CFB_8,
	AES_OFB
};

///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCION DECLARE ////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// CLASS DECLARATION //////////////////////////////////////////////////////////



class JniCryptoAES:public JniCrypto
{
/////////////////////// VARIABLE //////////////////////////////////////////////////////////////////
public:
    // TO BE IMPLEMENTED
protected:
    // TO BE IMPLEMENTED
private:
	EVP_CIPHER_CTX ctx; // context AES cipher
    AES_MODE mode;

/////////////////////// METHOD /////////////////////////////////////////////////////////////////////
public:
    JniCryptoAES();
	~JniCryptoAES();
	/**
	 * Heavy init members
	 */
    void construct();
	
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
	virtual int encrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize);	
	
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
	virtual int decrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize);


	virtual int encryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize);

	virtual int decryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize);

	/**
	 * Get size in byte for a datablock
	 * @return: >=0: size if success, < 0: error code
	 */
	virtual int getBlockSize();

	/**
	 * calculate estimated size in bytes for output encrypt/decypted ata
	 * @param isEncrypt: encrypt of decrypt data
	 * @param inputSize: size of data to be encrypted/decrypted
	 * @return: >=0: size, < 0: error code
	 */
	virtual int calcDataSize(bool isEncrypt, int inputSize);

	/**
	 * Free buffer allocated by calling API of this classed
	 */
	virtual void freeBuff(char* buff);

    void setMode(AES_MODE mode);
    const EVP_CIPHER* getCipher();

protected:
    // TO BE IMPLEMENTED
private:
    // TO BE IMPLEMENTED


};


#endif //OTT_SIP_CRYPTO_AES_H
