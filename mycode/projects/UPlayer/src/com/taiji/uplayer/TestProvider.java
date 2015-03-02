package com.taiji.uplayer;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.widget.Toast;

public class TestProvider
{
    public static Logger log = LoggerFactory.getLogger(MainActivity.class);

    public class UsbData
    {
        byte[] data;
        int len;
        int timeout;
    }

    public static String getTime()
    {
        log.info("Call From C Java Static Method");
        Toast.makeText(MainActivity.mContext, "Call From C Java Static Method", Toast.LENGTH_LONG).show();
        return String.valueOf(System.currentTimeMillis());
    }

    public void sayHello(String msg)
    {
        log.info("Call From C Java Not Static Method ：" + msg);
        Toast.makeText(MainActivity.mContext, "Call From C Java Not Static Method ：" + msg, Toast.LENGTH_LONG).show();
    }

    //public int getData(Object buffer1, Object len1) {
    public int getData(byte dataBuffer[], int dataLen[])
    {
        log.info("DataBuffer " + "start public int getData");
        Random rand = new Random();
        for (int i = 0; i < 10000; i++)
        {
            dataBuffer[i] = (byte) (((rand.nextInt() % 100) + 100) % 100);
            dataLen[0]++;
            //Log.e("DataBuffer", "dataBuffer["+i+"]="+dataBuffer[i]);
            //log.info("dataBuffer["+i+"]="+dataBuffer[i]);
        }
        log.info("DataBuffer " + "end public int getData");
        log.info("Call From C Java Not Static Method ：getData");
        //LogUtils.toastMessage(MainActivity.mContext,"Call From C Java Not Static Method ：getData");
        return 0;//dataLen[0];
    }

    public int transUsbData(UsbData myData)
    {

        return myData.len;
    }
}
