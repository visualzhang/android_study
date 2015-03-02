#include "Provider.h"
#include <android/log.h>

extern JNIEnv* jniEnv;

jclass TestProvider;
jclass UsbData;

jobject mTestProvider;
jobject mUsbData;
jmethodID getTime;
jmethodID sayHello;
jmethodID getData;
jmethodID transUsbData;


#define  LOG_TAG    "UPlayer"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

int GetProviderInstance(jclass obj_class);

/**
 * 初始化 类、对象、方法
 */
void UnInitLocalRef(){
	if(TestProvider != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, TestProvider);
	if(mTestProvider != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, mTestProvider);
	if(getTime != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, getTime);
	if(sayHello != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, sayHello);
	if(getData != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, getData);
	if(UsbData != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, UsbData);
	if(mUsbData != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, mUsbData);
	if(transUsbData != NULL)
		(*jniEnv)->DeleteLocalRef(jniEnv, transUsbData);
}
int InitProvider() {

	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  1" );

	if(jniEnv == NULL) {
		return 0;
	}

	if(TestProvider == NULL) {
		jclass tTestProvider = (*jniEnv)->FindClass(jniEnv,"com/taiji/uplayer/TestProvider");
		if(tTestProvider == NULL){
			return -1;
		}
		TestProvider =  (*jniEnv)->NewGlobalRef(jniEnv,tTestProvider);
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  2 ok" );
	}

	if (mTestProvider == NULL) {
		if (GetProviderInstance(TestProvider) != 1) {
			(*jniEnv)->DeleteLocalRef(jniEnv, TestProvider);
			return -1;
		}
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  3 ok" );
	}
/*
	if(UsbData == NULL) {
			jclass tUsbData = (*jniEnv)->FindClass(jniEnv,"com/taiji/uplayer/TestProvider$UsbData");
			if(tUsbData == NULL){
				return -1;
			}
			UsbData =  (*jniEnv)->NewGlobalRef(jniEnv,tUsbData);
			__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "UsbData Begin  ok" );
		}

	if (mUsbData == NULL) {
		if (GetUsbDataInstance(UsbData) != 1) {
			UnInitLocalRef();
			return -1;
		}
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "mUsbData Begin   ok" );
	}
*/
	if (getTime == NULL) {
		getTime = (*jniEnv)->GetStaticMethodID(jniEnv, TestProvider, "getTime","()Ljava/lang/String;");
		if (getTime == NULL) {
			(*jniEnv)->DeleteLocalRef(jniEnv, TestProvider);
			(*jniEnv)->DeleteLocalRef(jniEnv, mTestProvider);
			return -2;
		}
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  4 ok" );
	}

	if (sayHello == NULL) {
		sayHello = (*jniEnv)->GetMethodID(jniEnv, TestProvider, "sayHello","(Ljava/lang/String;)V");
		if (sayHello == NULL) {
			(*jniEnv)->DeleteLocalRef(jniEnv, TestProvider);
			(*jniEnv)->DeleteLocalRef(jniEnv, mTestProvider);
			(*jniEnv)->DeleteLocalRef(jniEnv, getTime);
			return -3;
		}
		__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  5 ok" );
	}

	if (getData == NULL) {
		getData = (*jniEnv)->GetMethodID(jniEnv, TestProvider, "getData","([B[I)I");
			if (getData == NULL) {
				(*jniEnv)->DeleteLocalRef(jniEnv, TestProvider);
				(*jniEnv)->DeleteLocalRef(jniEnv, mTestProvider);
				(*jniEnv)->DeleteLocalRef(jniEnv, getTime);
				(*jniEnv)->DeleteLocalRef(jniEnv, sayHello);
				return -4;
			}
			__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  6 ok" );
	}
/*
	if (transUsbData == NULL) {
			getData = (*jniEnv)->GetMethodID(jniEnv, TestProvider, "transUsbData",
					"(Lcom/taiji/uplayer/TestProvider$UsbData;)I");
				if (getData == NULL) {
					UnInitLocalRef();
					return -5;
				}
				__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  7 ok" );
	}
*/
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "InitProvider Begin  7" );
	return 1;

}

int GetUsbDataInstance(jclass obj_class) {

	if(obj_class == NULL) {
		return 0;
	}

	jmethodID construction_id = (*jniEnv)->GetMethodID(jniEnv, obj_class,
			"<init>", "()V");

	if (construction_id == 0) {
		return -1;
	}

	jobject tUsbData = (*jniEnv)->NewObject(jniEnv, obj_class,
			construction_id);

	if (tUsbData == NULL) {
		return -2;
	}
	mUsbData = (*jniEnv)->NewGlobalRef(jniEnv,tUsbData);

	return 1;
}

int GetProviderInstance(jclass obj_class) {

	if(obj_class == NULL) {
		return 0;
	}

	jmethodID construction_id = (*jniEnv)->GetMethodID(jniEnv, obj_class,
			"<init>", "()V");

	if (construction_id == 0) {
		return -1;
	}

	jobject tTestProvider = (*jniEnv)->NewObject(jniEnv, obj_class,
			construction_id);

	if (tTestProvider == NULL) {
		return -2;
	}
	mTestProvider = (*jniEnv)->NewGlobalRef(jniEnv,tTestProvider);

	return 1;
}

void TransUsbData(){

	//jfieldID byteData = (*jniEnv)->GetFieldID(UsbData,"data","[B");
	//jbyteArray pDataIn = (jbyteArray) (*jniEnv)->GetObjectField(mUsbData, byteData);
	//jsize theArrayLeng = (*jniEnv)->GetArrayLength(pDataIn);
	//(*jniEnv)->CallIntMethod(jniEnv, mTestProvider,transUsbData,mUsbData);
}
/**
 * 获取时间 ---- 调用 Java 方法
 */
void GetTime() {
	if(TestProvider == NULL || getTime == NULL) {
		int result = InitProvider();
		if (result != 1) {
			return;
		}
	}

	jstring jstr = NULL;
	char* cstr = NULL;
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "GetTime Begin" );
	jstr = (*jniEnv)->CallStaticObjectMethod(jniEnv, TestProvider, getTime);
	cstr = (char*) (*jniEnv)->GetStringUTFChars(jniEnv,jstr, 0);
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "Success Get Time from Java , Value = %s",cstr );
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "GetTime End" );

	(*jniEnv)->ReleaseStringUTFChars(jniEnv, jstr, cstr);
	(*jniEnv)->DeleteLocalRef(jniEnv, jstr);
}

/**
 * SayHello ---- 调用 Java 方法
 */
void SayHello(JNIEnv *env) {
	jniEnv = env;
	//if(TestProvider == NULL || mTestProvider == NULL || sayHello == NULL) {
		int result = InitProvider() ;
		if(result != 1) {
			return;
		}
	//}
	//jniEnv = env;
	jstring jstrMSG = NULL;
	jstrMSG =(*jniEnv)->NewStringUTF(jniEnv, "Hi,I'm From C");
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "SayHello Begin" );
	(*jniEnv)->CallVoidMethod(jniEnv, mTestProvider, sayHello,jstrMSG);
	__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "SayHello End" );

	(*jniEnv)->DeleteLocalRef(jniEnv, jstrMSG);
}

/**
 * GetData -- 调用Java 方法
 */

int GetData(unsigned char buffer[], int len[]){

	if(TestProvider == NULL || mTestProvider == NULL || getData == NULL){
		int ret = InitProvider();
		if(ret != 1){
			return 0;
		}
	}
	int i;
	jboolean b;
	LOGI("GetData Begin");
	//jobject jbuf = (*jniEnv)->NewDirectByteBuffer(jniEnv, buffer, 10000);
	//jbyteArray jbuffer =  (jbyteArray)jbuf;
	jbyteArray jbuffer = (*jniEnv)->NewByteArray(jniEnv, 10000);
	LOGI("GetData 000001" );
	jintArray jlen = (*jniEnv)->NewIntArray(jniEnv,8);
	LOGI("GetData 000002" );
	LOGI("GetData 000003" );
	//if(mTestProvider != NULL && getData != NULL)
		(*jniEnv)->CallIntMethod(jniEnv, mTestProvider,getData,jbuffer,jlen);
	LOGI("GetData 000004" );
	jbyte* elems = (*jniEnv)->GetByteArrayElements(jniEnv,jbuffer, &b);
	LOGI("GetData 000005" );
	memcpy(buffer,elems,10000);
	//for(i=0; i<10; i++){
	//		__android_log_print(ANDROID_LOG_INFO, "getData", "getData: buffer[%d]=%d",i,buffer[i]);
	//}
	len[0] = 10;
	LOGI("GetData End" );
	(*jniEnv)->DeleteLocalRef(jniEnv, jbuffer);
	(*jniEnv)->DeleteLocalRef(jniEnv, jlen);
	//(*jniEnv)->DeleteLocalRef(jniEnv, elems);

	return 0;
}










