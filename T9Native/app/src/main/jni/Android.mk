LOCAL_PATH := $(call my-dir)

#location of jni lib and header file
export JNI_HEADER_DIR := $(LOCAL_PATH)/include
JNI_HEADER_DIR := -I$(JNI_HEADER_DIR)
export JNI_CFLAGS := $(JNI_HEADER_DIR) -DPJ_IS_BIG_ENDIAN=0 -DPJ_IS_LITTLE_ENDIAN=1


# copy openssl lib to outdir
include $(CLEAR_VARS)
LOCAL_MODULE    := openssl
LOCAL_SRC_FILES := $(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/openssl/libssl.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := opencrypto
LOCAL_SRC_FILES := $(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/openssl/libcrypto.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := openssl_static
LOCAL_SRC_FILES := $(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/openssl/libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := opencrypto_static
LOCAL_SRC_FILES := $(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/openssl/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

# Android build settings for JNI source
include $(CLEAR_VARS)

LOCAL_MODULE    := crypto-util
#LOCAL_SHARED_LIBRARIES := openssl opencrypto
LOCAL_STATIC_LIBRARIES:= openssl_static opencrypto_static
LOCAL_CFLAGS    := -Werror  -frtti -Wno-write-strings $(JNI_CFLAGS) -DLOG_LEVEL_LOW
LOCAL_LDLIBS    := -llog

LOCAL_SRC_FILES := native-lib.cpp
LOCAL_SRC_FILES += jni_crypto.cpp
LOCAL_SRC_FILES += jni_crypto_aes.cpp
LOCAL_SRC_FILES += jni_log.cpp
LOCAL_SRC_FILES += jni_std.cpp
LOCAL_SRC_FILES += jni_msg_ctrl.cpp

include $(BUILD_SHARED_LIBRARY)