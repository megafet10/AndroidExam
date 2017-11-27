/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2017 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 *
 */

package com.borqs.ime;

/**
 * NativeAlphaInput declares the Java JNI APIs for XT9 native APIs which are implemented
 * in "libjni_xt9input.so".
 *
 */
final public class NativeAlphaInput {

    public enum LanguageIndex
    {
        Hindi,
        Assamese,
        Bengali,
        Gujarati,
        Marathi,
        Telugu,
        Tamil,
        Malayalam,
        Punjabi,
        Odia,
        Kannada,
        Urdu,
        Kashmiri,
        English,
        Nepali,
        Konkani,
        Maithili,
        Dogri,
        Sindhi,
        Sanskrit,
        Manipuri,
        Bodo,
        Santali
    }

    static public final int INPUT_MODE_abc = 0;
    static public final int INPUT_MODE_Abc = 1;
    static public final int INPUT_MODE_ABC = 2;

    static public native void xt9input_setLanguage(int langIndex);

    static public native void xt9input_setHalfWord(boolean enable);

    static public native void xt9input_setInputMode(int mode);

    static public native char xt9input_getMultitapKeyChar(int keyCode,
            int tapCount, char preChar);

    static public native int xt9input_getwords(int[] keyCodeSeq, int keyCodeLen, char[] predictedWords);
    static public native boolean xt9input_addCustomWord(char[] customWord, int wordLen);
    static public native boolean xt9input_deleteCustomWord(char[] customWord,  int wordLen);
}
