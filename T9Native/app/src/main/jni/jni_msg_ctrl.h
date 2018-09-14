//
// Created by minhbq on 9/14/2018.
//

#ifndef T9NATIVE_JNI_MSG_CTRL_H
#define T9NATIVE_JNI_MSG_CTRL_H

#include "jni_crypto.h"
#include "jni_err.h"
#include "jni_log.h"
#include "jni_config.h"
#include "jni_crypto_aes.h"


class JniMsgCtrl {
////////////////////////////// VARIABLE //////////////////////////////////////
private:
    static JniMsgCtrl* mInstance;



    ////////////////////////////// METHOD ////////////////////////////////////////
private:
    //to init default value only, not create new object....
    JniMsgCtrl();
    ~JniMsgCtrl();

protected:
    /**
     * construct objects. Note that if members are alocated in constructor funcion, it may cause memory leakage if something wrong
     * occurr in constructor. Put here to make sure that we have change to destroy members variable
     * @return: ERR_NONE on success
     */
    int construct();

public:

    static JniMsgCtrl* getInstance();

    void testAES(char* content, int contentLen);
    void testSHA(char* content, int contentLen);

};


#endif //T9NATIVE_JNI_MSG_CTRL_H
