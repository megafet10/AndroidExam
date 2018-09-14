# Build both ARMv5TE and ARMv7-A machine code.
#APP_ABI := armeabi armeabi-v7a x86
APP_ABI := armeabi-v7a
#NDK_TOOLCHAIN_VERSION := 4.9
APP_STL:=c++_shared
APP_PLATFORM := android-19
APP_OPTIM := debug