//
// Created by Philippe Fouquet on 19/09/2017.
//

#ifndef STATIONMETEO_I2C_NATIVE_H
#define STATIONMETEO_I2C_NATIVE_H

#include <android/log.h>
#include <stdio.h>
#include <android/log.h>
#include <fcntl.h>
#include <linux/i2c.h>
#include <memory.h>
#include <malloc.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "I2C", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"I2C",__VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "I2C", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "I2C", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "I2C", __VA_ARGS__)

extern "C" {
    JNIEXPORT jint JNICALL Java_com_philippefouquet_stationmeteo_Jni_i2c_init(JNIEnv *, jobject, jstring);
    JNIEXPORT jint JNICALL Java_com_philippefouquet_stationmeteo_Jni_i2c_close(JNIEnv *, jobject, jint);
    JNIEXPORT jint JNICALL Java_com_philippefouquet_stationmeteo_Jni_i2c_open(JNIEnv *, jobject, jint, jint);
    JNIEXPORT jint JNICALL Java_com_philippefouquet_stationmeteo_Jni_i2c_write(JNIEnv *, jobject, jint, jintArray, jint);
    JNIEXPORT jint JNICALL Java_com_philippefouquet_stationmeteo_Jni_i2c_read(JNIEnv *, jobject, jint, jintArray, jint);
};

#endif //STATIONMETEO_I2C_NATIVE_H
