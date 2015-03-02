package com.taiji.uplayer;

import android.content.Context;
import android.graphics.Paint;
import android.text.format.Time;

public class StrUtil
{

    static String TAG = "StrUtil";

    public static boolean isNullOrEmpty(String value)
    {
        return (value == null) || (value.length() == 0) || (value.equals("null"));
    }

    public static String[] concat(String[] a, String[] b)
    {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static String getHourMinStr(Time time)
    {
        if (time != null)
        {
            return String.format("%02d", time.hour) + ":" + String.format("%02d", time.minute);
        }
        else
        {
            return "00:00";
        }
    }

    public static String trimUrlTail(String url)
    {
        if (isNullOrEmpty(url))
        {
            return url;
        }
        int i = url.indexOf("?");
        if (i != -1)
        {
            return url.substring(0, i);
        }
        return url;
    }

    public static boolean equalAndNotNull(final String a, final String b)
    {
        if (isNullOrEmpty(a) || isNullOrEmpty(b))
        {
            return false;
        }
        if (a.equals(b))
        {
            return true;
        }
        return false;
    }
    
    public static int parseInt(final String string, final int radix)
    {
        try
        {
            return Integer.parseInt(string, radix);
        }
        catch (NumberFormatException e)
        {
            LogUtil.Loge(TAG, "parseInt: NumberFormatException string = " + string + " radix = " + radix);
            //LogUtil.handleUncaughtException(LiveVideoApplication.getInstance(), e);
            return 0;
        }
        catch (Exception e)
        {
            LogUtil.Loge(TAG, "parseInt: Exception string = " + string + " radix = " + radix);
            //LogUtil.handleUncaughtException(LiveVideoApplication.getInstance(), e);
            return 0;
        }
    }
}
