#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <jni.h>
#include "Provider.h"

#define LOG_TAG "uplayer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO  , LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , LOG_TAG, __VA_ARGS__)

jclass MainActivityClazz;
JNIEnv* jniEnv;
jobject mainActivity;
jmethodID play_method;
jmethodID pause_method;
jmethodID stop_method;
jmethodID forward_method;
jmethodID backward_method;

jclass LogUtil;
jmethodID logv_method;

void initLogUtil() {
	if (LogUtil == NULL && jniEnv != NULL) {
		jclass tLogUtil = (*jniEnv)->FindClass(jniEnv,
				"com/taiji/uplayer/LogUtil");
		if (tLogUtil == NULL) {
			LOGE("tLogUtil is null");
			return;
		}
		LOGI("tLogUtil is ok");
		LogUtil = (*jniEnv)->NewGlobalRef(jniEnv, tLogUtil);
		if (logv_method == NULL) {
			logv_method = (*jniEnv)->GetStaticMethodID(jniEnv, LogUtil, "Logv",
					"(Ljava/lang/String;Ljava/lang/String;)V");
			if (logv_method == NULL) {
				(*jniEnv)->DeleteLocalRef(jniEnv, LogUtil);
				LOGE("logv_method is null");
				return;
			}
		}
		LOGI("logv_method is ok");
	}
}

void logv(jstring tag, jstring msg) {
	if (LogUtil != NULL && logv_method != NULL && jniEnv != NULL) {
		__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "call logv");
		jstring log_tag = (*jniEnv)->NewStringUTF(jniEnv, tag);
		jstring log_msg = (*jniEnv)->NewStringUTF(jniEnv, msg);
		(*jniEnv)->CallStaticVoidMethod(jniEnv, LogUtil, logv_method, log_tag, log_msg);
		(*jniEnv)->DeleteLocalRef(jniEnv, log_tag);
		(*jniEnv)->DeleteLocalRef(jniEnv, log_msg);
	}
}

void play() {
	if (play_method != NULL && mainActivity != NULL) {
		LOGI("CallVoidMethod play_method");
		(*jniEnv)->CallVoidMethod(jniEnv, mainActivity, play_method);
	} else {
		LOGE("play_method is null");
	}
}

void pause() {
	if (pause_method != NULL && mainActivity != NULL) {
		LOGI("CallVoidMethod pause_method");
		(*jniEnv)->CallVoidMethod(jniEnv, mainActivity, pause_method);
	} else {
		LOGE("pause_method is null");
	}
}

void stop() {
	if (stop_method != NULL && mainActivity != NULL) {
		LOGI("CallVoidMethod stop_method");
		(*jniEnv)->CallVoidMethod(jniEnv, mainActivity, stop_method);
	} else {
		LOGE("stop_method is null");
	}
}

void forward() {
	if (forward_method != NULL && mainActivity != NULL) {
		LOGI("CallVoidMethod forward_method");
		(*jniEnv)->CallVoidMethod(jniEnv, mainActivity, forward_method);
	} else {
		LOGE("forward_method is null");
	}
}

void backward() {
	if (backward_method != NULL && mainActivity != NULL) {
		LOGI("CallVoidMethod backward_method");
		(*jniEnv)->CallVoidMethod(jniEnv, mainActivity, backward_method);
	} else {
		LOGE("backward_method is null");
	}
}

/**
 *  Java 中 声明的native init 方法的实现
 */
JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_init(JNIEnv* env,
        jobject thiz, jobject activity) {

    if (jniEnv == NULL) {
        jniEnv = env;
    }
    LOGI("Java_com_taiji_uplayer_MainActivity_init");
if (activity != NULL)
    {
        mainActivity =  (*jniEnv)->NewGlobalRef(jniEnv,activity);
        init_methods();
    }
}

/**
 *  Java 中 声明的native init 方法的实现
 */
JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_task(JNIEnv* env,
        jobject thiz)
{
    if (jniEnv == NULL) {
        jniEnv = env;
    }
    LOGI("Java_com_taiji_uplayer_MainActivity_task"); int i=0;
while (1)
{
//    	logv();
	//LOGI("task: i = %d",i++);
	//play();
	//sleep(10);
	//pause();
	sleep(5);
	//forward();
//        play();
	//backward();
//        play();
}
}

int init_methods() {

LOGI("init_methods Begin  1");
if (jniEnv == NULL) {
	return 0;
}
initLogUtil();
logv("xxxxxx", "msg from native");
if (MainActivityClazz == NULL) {        //com.taiji.uplayer.MainActivity
	jclass tMainActivityClazz = (*jniEnv)->FindClass(jniEnv,
			"com/taiji/uplayer/MainActivity");
	if (tMainActivityClazz == NULL) {
		LOGE("init_methods fail in FindClass MainActivity");
		return -1;
	}
	MainActivityClazz = (*jniEnv)->NewGlobalRef(jniEnv, tMainActivityClazz);
}

if (play_method == NULL) {
	play_method = (*jniEnv)->GetMethodID(jniEnv, MainActivityClazz, "doPlay",
			"()V");
	if (play_method == NULL) {
		LOGE("init_methods fail in play");
		return -2;
	}
	LOGI("init_methods pause_method in play");
}

if (pause_method == NULL) {
	pause_method = (*jniEnv)->GetMethodID(jniEnv, MainActivityClazz, "doPause",
			"()V");
	if (pause_method == NULL) {
		LOGE("init_methods fail in pause");
		return -3;
	}
	LOGI("init_methods pause_method in pause");
}

if (stop_method == NULL) {
	stop_method = (*jniEnv)->GetMethodID(jniEnv, MainActivityClazz, "doStop",
			"()V");
	if (stop_method == NULL) {
		LOGE("init_methods fail in stop");
		return -4;
	}
	LOGI("init_methods success in stop");
}

if (forward_method == NULL) {
	forward_method = (*jniEnv)->GetMethodID(jniEnv, MainActivityClazz,
			"doForward", "()V");
	if (forward_method == NULL) {
		LOGE("init_methods fail in forward");
		return -4;
	}
	LOGI("init_methods success in forward");
}

if (backward_method == NULL) {
	backward_method = (*jniEnv)->GetMethodID(jniEnv, MainActivityClazz,
			"doBackward", "()V");
	if (backward_method == NULL) {
		LOGE("init_methods fail in backward");
		return -4;
	}
	LOGI("init_methods success in backward");
}
return 1;
}

/**
 *  Java 中 声明的native getTime 方法的实现
 */
JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_getTime(JNIEnv* env,
	jobject thiz) {

if (jniEnv == NULL) {
	jniEnv = env;
}

GetTime();
}

/**
 *  Java 中 声明的native sayHello 方法的实现
 */
JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_sayHello(JNIEnv* env,
	jobject thiz) {
if (jniEnv == NULL) {
	jniEnv = env;
}

SayHello(env);
}
unsigned char buffer[10000];
int len[2];
JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_getData(JNIEnv* env,
        jobject thiz) {
    if (jniEnv == NULL) {
        jniEnv = env;
    }
    LOGI("GetData Java_com_taiji_uplayer_MainActivity_getData");int i;

    //if(buffer == NULL){
    //	buffer = (unsigned char*)calloc(100,1);
    //	len = (int*)calloc(2,4);
    //}
    GetData(buffer, len);

    for (i=0; i<10; i++) {
        LOGI(
	"getData: buffer[%d]=%d", i, buffer[i]);
}
        //GetData(NULL,NULL);

}

JNIEXPORT void JNICALL Java_com_taiji_uplayer_MainActivity_testUsbData(JNIEnv* env,
jobject thiz) {
if (jniEnv == NULL) {
jniEnv = env;
}
transUsbData();
}

JNIEXPORT jstring JNICALL Java_com_taiji_uplayer_MainActivity_testDirect(
JNIEnv* env, jobject thiz) {
if (jniEnv == NULL) {
jniEnv = env;
}
char* error = (char*) calloc(256, 1);
const jlong BUF_SIZE = 100;
void* buf = malloc(BUF_SIZE);
jobject jbuf = (*jniEnv)->NewDirectByteBuffer(jniEnv, buf, BUF_SIZE);
void* addr = (*jniEnv)->GetDirectBufferAddress(jniEnv, jbuf);
jlong size = (*jniEnv)->GetDirectBufferCapacity(jniEnv, jbuf);
jstring jstr;
if (jbuf) {
if (addr != buf) {
sprintf(error, "invalid buffer address: expected %p but was %p\n", buf, addr);
}
if (size != BUF_SIZE) {
sprintf(error + strlen(error),
		"invalid buffer capacity: expected %d but was %d\n", BUF_SIZE, size);
}
} else {
        // access to direct buffers not supported
if (addr != NULL | size != -1) {
sprintf(error,
		"inconsistent NIO support:\n,\
                    NewDirectByteBuffer() returned NULL;\n,\
                    GetDirectBufferAddress() returned %p\n,\
                    GetDirectBufferCapacity() returned %d\n",
		addr, size);
} else {
sprintf(error, "no NIO support\n");
}
}
LOGI("testDirect 000007");
jstr = strlen(error) ? (*jniEnv)->NewStringUTF(jniEnv, error) : NULL;
        //const char *key = (*jniEnv)->GetStringUTFChars(jniEnv, jstr, NULL);
		//LOGI("testDirect", key,0,0);
free(buf);
free(error);

return jstr;
}

