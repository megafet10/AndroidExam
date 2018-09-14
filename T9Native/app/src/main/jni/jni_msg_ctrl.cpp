//
// Created by minhbq on 9/14/2018.
//

#include "jni_msg_ctrl.h"
#include "native-lib.h"

#define THIS_FILE	    "jni_msg_ctrl.c"
/////////////////////////////////// GLOBAL VARIABLE ///////////////////////////////////////////////


JniMsgCtrl* JniMsgCtrl::mInstance = NULL;

JniMsgCtrl::JniMsgCtrl() {

}

JniMsgCtrl::~JniMsgCtrl() {

}

JniMsgCtrl *JniMsgCtrl::getInstance() {
    if (mInstance == NULL) {
        mInstance = new JniMsgCtrl();
        mInstance->construct();
    }
    return mInstance;
}

int JniMsgCtrl::construct() {

    return 0;
}

void JniMsgCtrl::testAES(char *content, int contentLen) {

    char* fake_key = "123456";
    char* fake_iv  = "000000";
    int keyLen = 0;
    char* odata;
    int maxSize;
    int ret;

    JniCryptoAES* cryptoAES = new JniCryptoAES();
    cryptoAES->construct();
    cryptoAES->setMode(AES_CBC_256);

    keyLen = JniCrypto::sha256KeySize();
    char* key = new char[keyLen + 1];
    JniCrypto::sha256(fake_key, strlen(fake_key), key, keyLen);
    key[keyLen] = 0;

    maxSize = cryptoAES->calcDataSize(true,contentLen);
    odata = new char[maxSize+1];

    int iv_len = JniCrypto::sha256KeySize();
    char *iv = new char[iv_len + 1];
    JniCrypto::sha256(fake_iv, strlen(fake_iv), iv, iv_len);
    iv[iv_len] = 0;

    ret = cryptoAES->encrypt( iv, iv_len, key, keyLen, content, contentLen, odata, maxSize);

//    JniCrypto::base64Encode(odata, ret, &oBuff, &ret);

    int hexLen =  ret*2;
    char* oHex = new char [hexLen + 1];
    oHex[hexLen] = 0;

    JniCrypto::byte2hex(odata, ret, oHex, hexLen);

    char* tag = "AES: ";
    int tagLen = strlen(tag);

    callToJavaAddLogger(tag, tagLen, oHex, hexLen);

    delete [] odata;
    delete [] oHex;

}




