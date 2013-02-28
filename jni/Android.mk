LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := yuv420rgb
LOCAL_SRC_FILES := main.cpp 
#LOCAL_CFLAGS    := -g
LOCAL_LDLIBS +=  -llog -ldl
#LOCAL_ARM_NEON := true

include $(BUILD_SHARED_LIBRARY)