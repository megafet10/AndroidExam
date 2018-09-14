//
// Created by minhbq on 9/14/2018.
//

#include "jni_msg_ctrl.h"

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

    char* iv;
    int ivLen;
    char* key;
    int keyLen;
    char* odata;
    int maxSize;

    JniCryptoAES* cryptoAES = new JniCryptoAES();

    cryptoAES->encrypt( iv, ivLen, key, keyLen, content, contentLen, odata, maxSize);

}




