package com.visual.sendmail;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

public class MailActivity extends Activity implements Handler.Callback
{
    Handler msgHandler;
    final int MSG_SEND_MAIL_START = 1;
    final int MSG_SEND_MAIL_SENDING = 2;
    final int MSG_SEND_MAIL_FINISH = 3;
    final int MSG_SEND_MAIL_FAIL = 4;
    TextView tvMailMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msgHandler = new Handler(this);
        tvMailMsg = (TextView) findViewById(R.id.tv_mail_msg);
        tvMailMsg.setText("点击ENTER开始发送邮件");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        sendMyMail(true);
                        // sendMail(); // Mail类这个是国外一个人写的，没有做改动
                    }
                }).start();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    void sendMyMail(boolean bNeedAuth)
    {
        msgHandler.sendEmptyMessage(MSG_SEND_MAIL_SENDING);
        List<String> fileList = new ArrayList<String>();
        fileList.add("/mnt/sdcard/test.txt");
        MyMail myMail = new MyMail("smtp.163.com", 25, "xxyy@163.com", "ssdd");
        boolean ret =false;
        if(bNeedAuth)
        {
            ret = myMail.sendEmail2("测试邮件", "icast.smit@gmail.com", "xxyy@163.com", "可以发送附件的邮件发送模块", fileList);
        }
        else {
            ret = myMail.sendEmail( "测试邮件","icast.smit@gmail.com","xxyy@163.com","可以发送附件的邮件发送模块",fileList);
        }
        if (ret)
        {
            msgHandler.sendEmptyMessage(MSG_SEND_MAIL_FINISH);
        }
        else
        {
            msgHandler.sendEmptyMessage(MSG_SEND_MAIL_FAIL);
        }

    }

    void sendMail()
    {
        msgHandler.sendEmptyMessage(MSG_SEND_MAIL_SENDING);
        Mail m = new Mail("yyzz@gmail.com", "aass");
        String[] toArr =
        { "rrtt@gmail.com" };
        m.setTo(toArr);
        m.setFrom("yyzz@gmail.com");
        m.setSubject("邮件发送测试");
        m.setBody("用中文来看看");
        try
        {
            m.addAttachment("/mnt/sdcard/test.txt");
            if (m.send())
            {
                msgHandler.sendEmptyMessage(MSG_SEND_MAIL_FINISH);
            }
            else
            {
                msgHandler.sendEmptyMessage(MSG_SEND_MAIL_FAIL);
            }
        }
        catch (Exception e)
        {
            msgHandler.sendEmptyMessage(MSG_SEND_MAIL_FAIL);
        }
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case MSG_SEND_MAIL_START:
                tvMailMsg.setText("准备发送邮件");
                break;
            case MSG_SEND_MAIL_SENDING:
                tvMailMsg.setText("正在发送邮件...");
                break;
            case MSG_SEND_MAIL_FINISH:
                tvMailMsg.setText("发送邮件完成");
                break;
            case MSG_SEND_MAIL_FAIL:
                tvMailMsg.setText("发送邮件失败");
                break;

            default:
                break;
        }
        return false;
    }
}
