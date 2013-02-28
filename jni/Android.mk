LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := yuv420rgb
LOCAL_SRC_FILES := main.cpp yuv420rgb.h
#LOCAL_CFLAGS    := -g
LOCAL_LDLIBS +=  -llog -ldl
#LOCAL_ARM_NEON := true

include $(BUILD_SHARED_LIBRARY)