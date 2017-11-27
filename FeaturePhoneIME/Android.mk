
LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_JNI_SHARED_LIBRARIES := libjni_basict9input

LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := BasicT9IME

LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/FtrPhone/system/app

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
