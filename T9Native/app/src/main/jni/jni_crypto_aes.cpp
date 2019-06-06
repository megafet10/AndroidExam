/**
 *
 * Brief: AES 256 crypto implementation, using openssl lib
 *
 */

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// HEADER INCLUDE ////////////////////////////////////////////////

#include "jni_crypto_aes.h"

///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// DEFINITION ////////////////////////////////////////////////////
#define THIS_FILE	    "jni_crypto_aes.c"


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// DATA TYPE DECLARATION //////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////// GLOBAL VARIABLE ///////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////// FUNCION DECLARE ////////////////////////////////////////////////////


///////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////// METHOD DEFINITION //////////////////////////////////////////////////////////

JniCryptoAES::JniCryptoAES():JniCrypto()
{
	LOG_TRACE_FUNC;
    mode = AES_CBC_256;

}

JniCryptoAES::~JniCryptoAES()
{
	LOG_TRACE_FUNC;
	/* Clean up */
    ERR_free_strings();
    EVP_cleanup();


	/* Clean up */
	EVP_CIPHER_CTX_cleanup(&ctx);

}

/**
 * Heavy init members
 */
void JniCryptoAES::construct()
{
//	/* Initialise the library */
	ERR_load_crypto_strings();
}

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
int JniCryptoAES::encrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize)
{
	int ret = 0;
	int len;
	int ciphertext_len = 0;
	int cipherBlkSize = 0;
	int expSize = 0;
    LOG_TRACE_FUNC_BEGIN;
	LOG_TRACE_CRYPTO("encrypt mode %d with key leng %d", mode, keyLen);
	LOG_TRACE_CRYPTO("EVP_CIPHER_CTX_init");
	EVP_CIPHER_CTX_init(&ctx);

	LOG_TRACE_CRYPTO("EVP_EncryptInit_ex");
#ifdef DEBUG_LOG_CRYPTO
	if (key != NULL) {
		LOG_TRACE_CRYPTO("AES key size %d", keyLen);

		dumpBuff2Hex("AES key ", (unsigned char *) key, keyLen);
	}

	if (iv != NULL) {
		LOG_TRACE_CRYPTO("AES key sv, size %d", ivLen);

		dumpBuff2Hex("AES key sv ", (unsigned char *) iv, ivLen);
	}


	if (data != NULL) {
		LOG_TRACE_CRYPTO("AES data, size %d", size);

		dumpBuff2Hex("AES data ", (unsigned char *) data, size);
	}
#endif //DEBUG_LOG_CRYPTO

	/* Initialise the encryption operation. IMPORTANT - ensure you use a key
	 * and IV size appropriate for your cipher
	 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
	 * IV size for *most* modes is the same as the block size. For AES this
	 * is 128 bits */
	if(1 != EVP_EncryptInit_ex(&ctx, getCipher(), NULL, (unsigned char*)key, (unsigned char*)iv))
	{
		LOGE("EVP_EncryptInit_ex failed");
		ret = ERR_FAILED;
		goto ERROR;
	}

	cipherBlkSize = EVP_CIPHER_CTX_block_size(&ctx);
	LOG_TRACE_CRYPTO("cipherBlkSize %d", cipherBlkSize);
	expSize = ((keyLen/cipherBlkSize) * cipherBlkSize + (keyLen%cipherBlkSize == 0)) ?0:cipherBlkSize;

	LOG_TRACE_CRYPTO("expSize %d, max out size %d", expSize, maxSize);

	if ((odata != NULL) && (maxSize >= expSize))
	{

		LOG_TRACE_CRYPTO("EVP_EncryptUpdate, mode %d", mode);
		/* Provide the message to be encrypted, and obtain the encrypted output.
		 * EVP_EncryptUpdate can be called multiple times if necessary
		 */
		if (mode == AES_OFB)
		{
			unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
			int loop = (int)(((float)maxSize/(float)size) + 0.5f);
			LOG_TRACE_CRYPTO("encrypt with OFB, loop time %d data size %d", loop, size);


			for(int i = 0; (i < loop) && (((int)p - (int)odata) < maxSize - 16); i++) {
                if(!EVP_EncryptUpdate(&ctx, p, &outlen, (unsigned char *)data, size))
                {
                    LOGE("EVP_EncryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

			LOG_TRACE_CRYPTO("final it");
            if(!EVP_EncryptFinal(&ctx, p, &outlen)){
                  LOGE("EVP_EncryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }
            p += outlen;

            ciphertext_len = (int)p - (int)odata;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
			dumpBuff2Hex("encrypt data ", (unsigned char*)odata, ciphertext_len/2);
#endif //DEBUG_LOG_CRYPTO
		}
        else if (mode == AES_CFB_8) {
            unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
            for(int i = 0; i < size; i++) {

                if(!EVP_EncryptUpdate(&ctx, p, &outlen, &((unsigned char *)data)[i], 1))
                {
                    LOGE("EVP_EncryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

            if(!EVP_EncryptFinal(&ctx, p, &outlen)){
                  LOGE("EVP_EncryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }

            p += outlen;

            ciphertext_len = (int)p - (int)odata;
        }
        else {
            LOG_TRACE_CRYPTO("start encrypt");
            if (1 != EVP_EncryptUpdate(&ctx, (unsigned char *) odata, &len, (unsigned char *) data,
                                       size)) {
                  LOGE("EVP_EncryptUpdate failed");
                ret = ERR_FAILED;
                goto ERROR;
            }


            ciphertext_len = len;


		LOG_TRACE("EVP_EncryptFinal_ex");
            /* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
            if (1 != EVP_EncryptFinal_ex(&ctx, (unsigned char *) (odata + len), &len)) {
                  LOGE("EVP_EncryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }


            ciphertext_len += len;

        }
        ret = ciphertext_len;
#ifdef DEBUG_LOG_CRYPTO
		LOG_TRACE("Encrypt ok, len %d", ciphertext_len);
        if (odata != NULL) {
            LOG_TRACE_CRYPTO("AES out data, size %d", size);

            DUMP_BUFFER_SUB("AES out data ", (unsigned char *) odata, 16);
        }
#endif // DEBUG_LOG_CRYPTO

		goto EXIT;
	}
	else
	{
		LOGE("maxSize %d expSize %d", maxSize, expSize);
		LOGE("invalid param");
		ret = ERR_INVALID_PARAM;
	}

ERROR:

	ciphertext_len = 0;
EXIT:
	EVP_CIPHER_CTX_cleanup(&ctx);

    LOG_TRACE_FUNC_END;
	return ciphertext_len;
}


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
int JniCryptoAES::decrypt(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize)
{
	int ret = 0;
	int len;
	int ciphertext_len =0;
	int cipherBlkSize = 0;
	int expSize = 0;

	EVP_CIPHER_CTX_init(&ctx);

	LOG_TRACE_CRYPTO("EVP_DecryptInit_ex");
	/* Initialise the encryption operation. IMPORTANT - ensure you use a key
	 * and IV size appropriate for your cipher
	 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
	 * IV size for *most* modes is the same as the block size. For AES this
	 * is 128 bits */
	if(1 != EVP_DecryptInit_ex(&ctx, getCipher(), NULL, (unsigned char*)key, (unsigned char*)iv))
	{
		LOGE("EVP_DecryptInit_ex failed");
		ret = ERR_FAILED;
		goto ERROR;
	}


	cipherBlkSize = EVP_CIPHER_CTX_block_size(&ctx);
	LOG_TRACE("cipherBlkSize %d", cipherBlkSize);
	expSize = ((keyLen/cipherBlkSize) * cipherBlkSize + (keyLen%cipherBlkSize == 0)) ?0:cipherBlkSize;

	LOG_TRACE_CRYPTO("expSize %d, max out size %d", expSize, maxSize);

#ifdef DEBUG_LOG_CRYPTO
    if (key != NULL) {
        LOG_TRACE_CRYPTO("AES key size %d", keyLen);

        dumpBuff2Hex("AES key ", (unsigned char *) key, keyLen);
    }

    if (iv != NULL) {
        LOG_TRACE_CRYPTO("key sv, size %d", ivLen);

        dumpBuff2Hex("AES key sv ", (unsigned char *) iv, ivLen);
    }


    if (data != NULL) {
        LOG_TRACE_CRYPTO("AES data, size %d", size);

        dumpBuff2Hex("AES data ", (unsigned char *) data, size);
    }
#endif //DEBUG_LOG_CRYPTO

	if ((odata != NULL) && (maxSize >= expSize)) {

        LOG_TRACE_CRYPTO("EVP_DecryptUpdate");
        /* Provide the message to be encrypted, and obtain the encrypted output.
         * EVP_EncryptUpdate can be called multiple times if necessary
         */
		if (mode == AES_OFB)
		{
			unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
			int loop = (int)(((float)maxSize/(float)size) + 0.5f);
			LOG_TRACE_CRYPTO("decrypt with OFB, loop time %d data size %d", loop, size);


			for(int i = 0; (i < loop) && (((int)p - (int)odata) < maxSize - 16); i++) {
                if(!EVP_DecryptUpdate(&ctx, p, &outlen, (unsigned char *)data, size))
                {
                    LOGE("EVP_DecryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

			LOG_TRACE_CRYPTO("final it");
            if(!EVP_EncryptFinal(&ctx, p, &outlen)){
                  LOGE("EVP_DecryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }
            p += outlen;

            ciphertext_len = (int)p - (int)odata;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
			dumpBuff2Hex("encrypt data ", (unsigned char*)odata, ciphertext_len/2);
#endif //DEBUG_LOG_CRYPTO
		}
		else if (mode == AES_CFB_8) {
            unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
            for(int i = 0; i < size; i++) {

                if(!EVP_DecryptUpdate(&ctx, p, &outlen, &((unsigned char *)data)[i], 1))
                {
                    LOGE("EVP_DecryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

            LOG_TRACE_CRYPTO("EVP_DecryptFinal_ex");
            /* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
            if (1 != EVP_DecryptFinal_ex(&ctx, p, &outlen)) {
                  LOGE("EVP_DecryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }
            p += outlen;

            ciphertext_len = (int)p - (int)odata;

        }
        else {
            LOG_TRACE_CRYPTO("start decrypt");
            if (1 !=
                EVP_DecryptUpdate(&ctx, (unsigned char *) odata, &len, (unsigned char *) data,
                                  size)) {
                  LOGE("EVP_DecryptUpdate failed");
                ret = ERR_FAILED;
                goto ERROR;
            }


            ciphertext_len = len;


            LOG_TRACE_CRYPTO("EVP_DecryptFinal_ex");
            /* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
            if (1 != EVP_DecryptFinal_ex(&ctx, (unsigned char *) odata + len, &len)) {
                  LOGE("EVP_DecryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }


            ciphertext_len += len;
        }
		ret = ciphertext_len;

#ifdef DEBUG_LOG_CRYPTO
		LOG_TRACE_CRYPTO("decrypt ok, len %d", ciphertext_len);
        if (odata != NULL) {
            LOG_TRACE_CRYPTO("AES out data, size %d", size);

            DUMP_BUFFER_SUB("AES out data ", (unsigned char *) odata, 16);
        }
#endif //DEBUG_LOG_CRYPTO
		goto EXIT;
	}
	else
	{
		LOGE("invalid param");
		ret = ERR_INVALID_PARAM;
	}

ERROR:
	ciphertext_len = 0;
EXIT:
	EVP_CIPHER_CTX_cleanup(&ctx);

	return ciphertext_len;

}


int JniCryptoAES::encryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize)
{
	int ret = 0;
	int len;
	int ciphertext_len = 0;
	int cipherBlkSize = 0;
	int expSize = 0;
	int new_size_data = 0;
	char* new_data = NULL;
	//hash IV to use as padding data
	int padding_data_len = JniCrypto::sha256KeySize();
	char *padding_data = new char[padding_data_len + 1];
//    LOG_TRACE_FUNC_BEGIN;
	LOG_TRACE_CRYPTO("encrypt mode %d with key leng %d", mode, keyLen);
	LOG_TRACE_CRYPTO("EVP_CIPHER_CTX_init");
	EVP_CIPHER_CTX_init(&ctx);


	LOG_TRACE_CRYPTO("EVP_EncryptInit_ex");
#ifdef DEBUG_LOG_CRYPTO
	if (key != NULL) {
		LOG_TRACE_CRYPTO("AES  key size %d", keyLen);
		dumpBuff2Hex("AES KEY FOR THIS IM (SHA256(SECRET KEY)) ", (unsigned char *) key, keyLen);
	}

	if (iv != NULL) {
		LOG_TRACE_CRYPTO("AES  IM IV len, size %d", ivLen);

		dumpBuff2Hex("AES IM IV FOR THIS IM ", (unsigned char *) iv, ivLen);
	}


	if (data != NULL) {
		LOG_TRACE_CRYPTO("AES IM DATA size (bytes - include CR and LF) %d", size);

		dumpBuff2Hex("AES IM DATA ", (unsigned char *) data, size);
	}
#endif //DEBUG_LOG_CRYPTO

	/* Initialise the encryption operation. IMPORTANT - ensure you use a key
	 * and IV size appropriate for your cipher
	 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
	 * IV size for *most* modes is the same as the block size. For AES this
	 * is 128 bits */
	if(1 != EVP_EncryptInit_ex(&ctx, getCipher(), NULL, (unsigned char*)key, (unsigned char*)iv))
	{
		LOGE("EVP_EncryptInit_ex failed");
		ret = ERR_FAILED;
		goto ERROR;
	}
	if(1 != EVP_CIPHER_CTX_set_padding(&ctx, 0)) // no padding
	{
		LOGE("EVP_CIPHER_CTX_set_padding failed");
		ret = ERR_FAILED;
		goto ERROR;
	}

	cipherBlkSize = EVP_CIPHER_CTX_block_size(&ctx);
	LOG_TRACE_CRYPTO("cipherBlkSize %d", cipherBlkSize);

	expSize = ((keyLen/cipherBlkSize) * cipherBlkSize + (keyLen%cipherBlkSize == 0))?0:cipherBlkSize;


	LOG_TRACE_CRYPTO("expSize %d, max out size %d", expSize, maxSize);

	if ((odata != NULL) && (maxSize >= expSize))
	{

		LOG_TRACE_CRYPTO("EVP_EncryptUpdate, mode %d", mode);
		/* Provide the message to be encrypted, and obtain the encrypted output.
		 * EVP_EncryptUpdate can be called multiple times if necessary
		 */
		if (mode == AES_OFB)
		{
			unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
			int loop = (int)(((float)maxSize/(float)size) + 0.5f);
			LOG_TRACE_CRYPTO("encrypt with OFB, loop time %d data size %d", loop, size);


			for(int i = 0; (i < loop) && (((int)p - (int)odata) < maxSize - 16); i++) {
                if(!EVP_EncryptUpdate(&ctx, p, &outlen, (unsigned char *)data, size))
                {
                    LOGE("EVP_EncryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

			LOG_TRACE_CRYPTO("final it");
            if(!EVP_EncryptFinal(&ctx, p, &outlen)){
                  LOGE("EVP_EncryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }
            p += outlen;

            ciphertext_len = (int)p - (int)odata;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
			dumpBuff2Hex("encrypt data ", (unsigned char*)odata, ciphertext_len/2);
#endif //DEBUG_LOG_CRYPTO
		}
		else if (mode == AES_CFB_8) {
			unsigned char* p = (unsigned char *)odata;
			int outlen = 0;
			for(int i = 0; i < size; i++) {

				if(!EVP_EncryptUpdate(&ctx, p, &outlen, &((unsigned char *)data)[i], 1))
				{
					LOGE("EVP_EncryptUpdate failed");
					ret = ERR_FAILED;
					goto ERROR;
				}
				p += outlen;
				LOG_TRACE_CRYPTO("out len loop %d", ciphertext_len);
			}

			LOG_TRACE_CRYPTO("out len  %d", ciphertext_len);


			if(!EVP_EncryptFinal(&ctx, p, &outlen)){
				LOGE("EVP_EncryptFinal_ex failed");
				ret = ERR_FAILED;
				goto ERROR;
			}

			p += outlen;
			LOG_TRACE_CRYPTO("out len final %d - outlen %d ", (int)p , (int)odata);

			ciphertext_len = (int)p - (int)odata;
		}
		else {

			//mode cbc


			JniCrypto::sha256((char *)iv, ivLen , padding_data, padding_data_len);
			padding_data[padding_data_len] = 0;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("padding_data_len  %d", padding_data_len);
			dumpBuff2Hex("IM DATA USING FOR PADDING (SHA256(IV)) ", (unsigned char*)padding_data, padding_data_len);
#endif //DEBUG_LOG_CRYPTO

			//calculate bit to padding
			//expSize = (keyLen/cipherBlkSize) * cipherBlkSize + (keyLen%cipherBlkSize == 0)?0:cipherBlkSize;
			int bit_need_padding = 0;

			if(size%cipherBlkSize != 0){
				bit_need_padding = (cipherBlkSize - size%cipherBlkSize);
			}
			LOG_TRACE_CRYPTO("bit_need_padding %d ", bit_need_padding);

			if(bit_need_padding != 0){
				LOG_TRACE_CRYPTO("begin to padding");
				new_size_data = size + bit_need_padding;
				new_data = new char[new_size_data + 1];
				memcpy((unsigned char*)new_data,(unsigned char*)data,size);
//				dumpBuff2Hex("padding_data memcpy data", (unsigned char*)new_data, size);
				memcpy((unsigned char*)new_data + size, (unsigned char*)padding_data,bit_need_padding);
//				dumpBuff2Hex("IM DATA AFTER PADDING ", (unsigned char*)new_data, new_size_data);
				new_data[new_size_data]='\0';
				if(NULL != padding_data)
					delete []padding_data;

				LOG_TRACE_CRYPTO("data size %d ", size);

				if (1 != EVP_EncryptUpdate(&ctx, (unsigned char *)odata, &len,(unsigned char *)new_data,
										   new_size_data)) {
					LOGE("EVP_EncryptUpdate failed");
					ret = ERR_FAILED;
					goto ERROR;
				}

				LOG_TRACE_CRYPTO("EVP_EncryptUpdate len had manual padding  %d", len);

			} else { //no need to padding
				LOGE("IM NO NEED TO PADDING");


				LOG_TRACE_CRYPTO("data size %d ", size);

				if (1 != EVP_EncryptUpdate(&ctx, (unsigned char *) odata, &len, (unsigned char *)data,
										   size)) {
					LOGE("EVP_EncryptUpdate failed");
					ret = ERR_FAILED;
					goto ERROR;
				}

				LOG_TRACE_CRYPTO("EVP_EncryptUpdate len %d", len);

			}

			ciphertext_len = len;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
//			dumpBuff2Hex("AES OFB : encrypt data ", (unsigned char*)odata, ciphertext_len);
#endif //DEBUG_LOG_CRYPTO


//		LOG_TRACE("EVP_EncryptFinal_ex");
			/* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
			if (1 != EVP_EncryptFinal_ex(&ctx, (unsigned char *) (odata + len), &len)) {
				LOGE("EVP_EncryptFinal_ex failed");
				ret = ERR_FAILED;
				goto ERROR;
			}
			LOG_TRACE_CRYPTO("EVP_EncryptFinal_ex len %d", len);


			ciphertext_len += len;

			if(new_data)
				delete []new_data;

			if(NULL != padding_data)
				delete []padding_data;

#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
			dumpBuff2Hex("IM DATA ENCRYPTED ", (unsigned char*)odata, ciphertext_len);
#endif //DEBUG_LOG_CRYPTO
		}
		ret = ciphertext_len;
//		LOG_TRACE("Encrypt ok, len %d", ciphertext_len);
		goto EXIT;
	}
	else
	{
		LOGE("maxSize %d expSize %d", maxSize, expSize);
		LOGE("invalid param");
		ret = ERR_INVALID_PARAM;
	}

	ERROR:
	if(new_data)
		delete []new_data;

	if(NULL != padding_data)
		delete []padding_data;

	ciphertext_len = 0;
	EXIT:
	EVP_CIPHER_CTX_cleanup(&ctx);

//    LOG_TRACE_FUNC_END;
	return ciphertext_len;
}


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
int JniCryptoAES::decryptIM(char* iv, int ivLen, char* key, int keyLen, char* data, int size, char* odata, int maxSize)
{
	int ret = 0;
	int len;
	int ciphertext_len;
	int cipherBlkSize = 0;
	int expSize = 0;

	EVP_CIPHER_CTX_init(&ctx);


	LOG_TRACE_CRYPTO("EVP_DecryptInit_ex");
	/* Initialise the encryption operation. IMPORTANT - ensure you use a key
	 * and IV size appropriate for your cipher
	 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
	 * IV size for *most* modes is the same as the block size. For AES this
	 * is 128 bits */
	if(1 != EVP_DecryptInit_ex(&ctx, getCipher(), NULL, (unsigned char*)key, (unsigned char*)iv))
	{
		LOGE("EVP_DecryptInit_ex failed");
		ret = ERR_FAILED;
		goto ERROR;
	}

	if(1 != EVP_CIPHER_CTX_set_padding(&ctx, 0)) // no padding
	{
		LOGE("EVP_CIPHER_CTX_set_padding failed");
		ret = ERR_FAILED;
		goto ERROR;
	}


	cipherBlkSize = EVP_CIPHER_CTX_block_size(&ctx);
	LOG_TRACE("cipherBlkSize %d", cipherBlkSize);
	expSize = ((keyLen/cipherBlkSize) * cipherBlkSize + (keyLen%cipherBlkSize == 0))?0:cipherBlkSize;

	LOG_TRACE_CRYPTO("expSize %d, max out size %d", expSize, maxSize);

	if ((odata != NULL) && (maxSize >= expSize)) {

		LOG_TRACE_CRYPTO("EVP_DecryptUpdate");
		/* Provide the message to be encrypted, and obtain the encrypted output.
         * EVP_EncryptUpdate can be called multiple times if necessary
         */
		if (mode == AES_OFB)
		{
			unsigned char* p = (unsigned char *)odata;
            int outlen = 0;
			int loop = (int)(((float)maxSize/(float)size) + 0.5f);
			LOG_TRACE_CRYPTO("decrypt with OFB, loop time %d data size %d", loop, size);


			for(int i = 0; (i < loop) && (((int)p - (int)odata) < maxSize - 16); i++) {
                if(!EVP_DecryptUpdate(&ctx, p, &outlen, (unsigned char *)data, size))
                {
                    LOGE("EVP_DecryptUpdate failed");
                    ret = ERR_FAILED;
                    goto ERROR;
                }
                p += outlen;
            }

			LOG_TRACE_CRYPTO("final it");
            if(!EVP_EncryptFinal(&ctx, p, &outlen)){
                  LOGE("EVP_DecryptFinal_ex failed");
                ret = ERR_FAILED;
                goto ERROR;
            }
            p += outlen;

            ciphertext_len = (int)p - (int)odata;
#ifdef DEBUG_LOG_CRYPTO
			LOG_TRACE_CRYPTO("out len %d", ciphertext_len);
			dumpBuff2Hex("encrypt data ", (unsigned char*)odata, ciphertext_len/2);
#endif //DEBUG_LOG_CRYPTO
		}
		else if (mode == AES_CFB_8) {
			unsigned char* p = (unsigned char *)odata;
			int outlen = 0;
			for(int i = 0; i < size; i++) {

				if(!EVP_DecryptUpdate(&ctx, p, &outlen, &((unsigned char *)data)[i], 1))
				{
					LOGE("EVP_DecryptUpdate failed");
					ret = ERR_FAILED;
					goto ERROR;
				}
				p += outlen;
			}

			LOG_TRACE_CRYPTO("EVP_DecryptFinal_ex");
			/* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
			if (1 != EVP_DecryptFinal_ex(&ctx, p, &outlen)) {
				LOGE("EVP_DecryptFinal_ex failed");
				ret = ERR_FAILED;
				goto ERROR;
			}
			p += outlen;

			ciphertext_len = (int)p - (int)odata;

		}
		else {

			if (1 !=
				EVP_DecryptUpdate(&ctx, (unsigned char *) odata, &len, (unsigned char *) data,
								  size)) {
				LOGE("EVP_DecryptUpdate failed");
				ret = ERR_FAILED;
				goto ERROR;
			}


			ciphertext_len = len;


			LOG_TRACE_CRYPTO("EVP_DecryptFinal_ex");
			/* Finalise the encryption. Further ciphertext bytes may be written at
             * this stage.
             */
			if (1 != EVP_DecryptFinal_ex(&ctx, (unsigned char *) odata + len, &len)) {
				LOGE("EVP_DecryptFinal_ex failed");
				ret = ERR_FAILED;
				goto ERROR;
			}


			ciphertext_len += len;
		}
		ret = ciphertext_len;

		LOG_TRACE_CRYPTO("decrypt ok, len %d", ciphertext_len);
		goto EXIT;
	}
	else
	{
		LOGE("invalid param");
		ret = ERR_INVALID_PARAM;
	}

	ERROR:
	ciphertext_len = 0;
	EXIT:
	EVP_CIPHER_CTX_cleanup(&ctx);

	return ciphertext_len;

}

/**
 * Get size in byte for a datablock
 * @return: >=0: size if success, < 0: error code
 */
int JniCryptoAES::getBlockSize()
{
	int ret = 0;
    LOG_TRACE_FUNC_BEGIN;

	EVP_CIPHER_CTX_init(&ctx);
//	LOG_TRACE("EVP_EncryptInit_ex");
	/* Initialise the encryption operation. IMPORTANT - ensure you use a key
	 * and IV size appropriate for your cipher
	 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
	 * IV size for *most* modes is the same as the block size. For AES this
	 * is 128 bits */
	if(1 != EVP_EncryptInit_ex(&ctx, getCipher(), NULL, NULL, 0))
	{
		LOGE("EVP_EncryptInit_ex failed");
		ret = ERR_FAILED;
		goto ERROR;
	}
	ret = EVP_CIPHER_CTX_block_size(&ctx);

	LOG_TRACE_CRYPTO("block size %d", ret);

ERROR:
	EVP_CIPHER_CTX_cleanup(&ctx);

    LOG_TRACE_FUNC_END;
	return ret;
}	

/**
 * calculate estimated size in bytes for output encrypt/decypted ata
 * @param isEncrypt: encrypt of decrypt data
 * @param inputSize: size of data to be encrypted/decrypted
 * @return: >=0: size, < 0: error code
 */
int JniCryptoAES::calcDataSize(bool isEncrypt, int inputSize)
{
	int ret = 0;
	int blkSize = 0;

//    LOG_TRACE_FUNC_BEGIN;
	EVP_CIPHER_CTX_init(&ctx);
	if (isEncrypt)
	{
		LOG_TRACE("EVP_EncryptInit_ex");
		/* Initialise the encryption operation. IMPORTANT - ensure you use a key
		 * and IV size appropriate for your cipher
		 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
		 * IV size for *most* modes is the same as the block size. For AES this
		 * is 128 bits */
		if(1 != EVP_EncryptInit_ex(&ctx, getCipher(), NULL, NULL, NULL))
		{
			LOGE("EVP_EncryptInit_ex failed");
			ret = ERR_FAILED;
			goto EXIT;
		}

	}
	else
	{
//		LOG_TRACE("EVP_DecryptInit_ex");
		/* Initialise the encryption operation. IMPORTANT - ensure you use a key
		 * and IV size appropriate for your cipher
		 * In this example we are using 256 bit AES (i.e. a 256 bit key). The
		 * IV size for *most* modes is the same as the block size. For AES this
		 * is 128 bits */
		if(1 != EVP_DecryptInit_ex(&ctx, getCipher(), NULL, NULL, NULL))
		{
			LOGE("EVP_DecryptInit_ex failed");
			ret = ERR_FAILED;
			goto EXIT;
		}


	}
	blkSize = EVP_CIPHER_CTX_block_size(&ctx);
	// TODO: just for dummy, not real correct
	ret = inputSize + blkSize;

//	LOG_TRACE("input %d blksize %d --> %d", inputSize, blkSize, ret);
EXIT:

	EVP_CIPHER_CTX_cleanup(&ctx);
    LOG_TRACE_FUNC_END;
	return ret;
}	


/**
 * Free buffer allocated by calling API of this classed
 */
void JniCryptoAES::freeBuff(char* buff)
{
    LOG_TRACE_FUNC_BEGIN;

	if (NULL != buff)
		delete[] buff;

//    LOG_TRACE_FUNC_END;

}

void JniCryptoAES::setMode(AES_MODE mode){
    LOG_TRACE_CRYPTO("mode %d", mode);
	this->mode = mode;

}


const EVP_CIPHER* JniCryptoAES::getCipher()
{
    const EVP_CIPHER* cipher;
	LOG_TRACE_CRYPTO("get cipher mode %d", mode);
    switch (mode)
    {
        case AES_CFB_8:
            cipher = EVP_aes_256_cfb8();
            break;
		case AES_OFB:
            cipher = EVP_aes_256_ofb();
            break;
        case AES_CBC_256:
        default:
            cipher = EVP_aes_256_cbc();
    }
    return cipher;
}