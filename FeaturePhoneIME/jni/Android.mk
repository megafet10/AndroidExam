LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_SRC_FILES := keypad_R9.a
LOCAL_BUILT_MODULE_STEM := keypad_R9.a
LOCAL_EXPORT_C_INCLUDE_DIRS := $(LOCAL_PATH)
LOCAL_MODULE_SUFFIX := .a
LOCAL_MODULE := keypad_R9
LOCAL_MODULE_CLASS := STATIC_LIBRARIES
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := \
        com_borqs_ime_input.cpp \
        alpha_native.cpp

LOCAL_STATIC_LIBRARIES := keypad_R9

ifeq ($(HOST_OS),cygwin)
        LOCAL_CFLAGS += -DNDK_BUILD
LOCAL_LDLIBS := -ldl -llog
else
        ifdef NDK_ROOT
        LOCAL_CFLAGS += -DNDK_BUILD
        LOCAL_LDLIBS := -ldl -llog
        else
        LOCAL_SHARED_LIBRARIES := \
                libcutils
endif
endif

LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_CFLAGS += -Wfatal-errors

LOCAL_C_INCLUDES := \
        $(JNI_H_INCLUDE) \
        $(LOCAL_PATH) \
        $(LOCAL_PATH/include) \

LOCAL_MODULE := libjni_basict9input

include $(BUILD_SHARED_LIBRARY)
include $(call all-makefiles-under,$(LOCAL_PATH))

