package com.taiji.uplayer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;


public class LogUtil
{
    public static final String KERMIT_TAG = "kermit";
    public static final String ERR_TAG = "error";
    static boolean flag = false;
    private static Toast toast;
    public static boolean DEBUG = true;//BuildConfig.DEBUG;
    final static int DEBUG_LEVEL = 3;
    public final static boolean STRICT_MODE = false;
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);// 设置日期格式
    public static Map<String, Integer> mapLogDebugCtrl = new HashMap<String, Integer>();
    static
    {
        //LogUtil.mapLogDebugCtrl.put(PFUtil.class.getSimpleName(), 2);
    }

    public static void Logv(final String tag, String msg)
    {

        if (DEBUG == false || tag == null || msg == null)
        {
            return;
        }
        if (LogUtil.mapLogDebugCtrl.get(tag) != null)
        {
            if (LogUtil.mapLogDebugCtrl.get(tag) < DEBUG_LEVEL)
                return;
        }
        Logger log = LoggerFactory.getLogger(tag);
        log.info(msg);
        Log.v(tag, msg);
    }

    public static void Logd(final String tag, String msg)
    {
        if (DEBUG == false || tag == null || msg == null)
        {
            return;
        }
        if (LogUtil.mapLogDebugCtrl.get(tag) != null)
        {
            if (LogUtil.mapLogDebugCtrl.get(tag) < DEBUG_LEVEL)
                return;
        }
        Log.d(tag, msg);
    }

    public static void Loge(final String tag, String msg)
    {
        if (tag == null || msg == null)
        {
            return;
        }
        Log.e(tag, msg);
    }

    public static Toast toast(Context context, String text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(text);
        }
        toast.show();
        return toast;
    }

    public static Toast toast(Context context, int text)
    {
        if (toast == null)
        {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(text);
        }
        toast.show();
        return toast;
    }

    public static void cancelToast()
    {
        if (toast != null)
        {
            toast.cancel();
            toast = null;
        }
    }

    public static void printCurTime(String tag)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        Date curDate = new Date(System.currentTimeMillis());
        String timeString = formatter.format(curDate);
        Log.d("iCast-Timer", tag + ":" + timeString);
    }

    public static void startStrictMode()
    {
        if (STRICT_MODE)
        {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 8)
            {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                        .detectNetwork().penaltyLog().build());
            }
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog()
                    .penaltyDeath().build());
        }
    }

    public static void turnON()
    {
        LogUtil.DEBUG = true;
    }

    public static void turnOFF()
    {
        LogUtil.DEBUG = false;
    }
    /**
     * 对文件进行压缩成.tgz格式
     */
    public static void zipFile(File zipFile, File resFile) throws FileNotFoundException, IOException
    {

        byte[] buf = new byte[1024];
        //压缩文件名
        ZipOutputStream zos = null;
        try
        {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        ZipEntry ze = null;
        //创建一个ZipEntry，并设置Name和其它的一些属性
        ze = new ZipEntry(resFile.getName());
        ze.setSize(resFile.length());
        ze.setTime(resFile.lastModified());
        //将ZipEntry加到zos中，再写入实际的文件内容
        try
        {
            zos.putNextEntry(ze);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        InputStream is = new BufferedInputStream(new FileInputStream(resFile));

        int readLen = -1;
        while ((readLen = is.read(buf, 0, 1024)) != -1)
        {
            zos.write(buf, 0, readLen);
        }

        is.close();
        zos.close();
    }

    /**
     * 获取Logcat打印信息
     */
    private static String getLogcatMessage(StringBuilder sb)
    {
        Process process = null;
        String text;
        try
        {
            String[] cmdLine = new String[]
            { "adb", "logcat", "-d", "-t", "1000", "-v", "time" };

            process = Runtime.getRuntime().exec(cmdLine);
            text = readFromStream(process.getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            text = e.toString();
        }
        finally
        {
            if (process != null)
                process.destroy();
        }
        sb.append("===========================[融合电视LogCat信息]======================================\r\n");
        sb.append(text);
        return text;
    }

    private static PackageInfo getPackageInfo(Context context) throws Exception
    {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    }

    private static String readFromStream(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8096);
        String line;

        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null)
        {
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }
}
