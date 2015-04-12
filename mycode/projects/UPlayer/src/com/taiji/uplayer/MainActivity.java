package com.taiji.uplayer;

import java.io.IOException;
import java.sql.Time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SurfaceHolder.Callback, OnCompletionListener, OnErrorListener,
        OnInfoListener, OnPreparedListener, OnVideoSizeChangedListener, Handler.Callback
{
    private enum MediaPlayerState {
        STOPPED, PLAYING, PAUSED
    }

    final int MSG_PLAY = 1;
    final int MSG_PAUSE = 2;
    final int MSG_STOP = 3;
    final int MSG_FORWARD = 4;
    final int MSG_BACKWARD = 5;
    //    private String mediaUrl = "http://119.147.17.153/videos/v0/20140613/0e/a8/35/abe2ba203aa02220ee21cc42af25a6a2.f4v?key=8d9c02183f3b940d&src=iqiyi.com&ran=194&qyid=7eaf7e5aaaf17bf73b220192c862a931&qypid=175370_11&ran=195&uuid=da119da8-549cf7a6-b&start=1024&end=8465299&ran=227";
    //    private String mediaUrl = "http://1234.tingge123.com/123/2014/11/匆匆那年-王菲.mp3";
    private String mediaUrl = "/mnt/sdcard/bajiukuanghuan.mp3";
    //    private String mediaUrl = "http://yinyueshiting.baidu.com/data2/music/123156403/264717162000128.mp3?xcode";
    final static String TAG = "MainActivity";
    private static final String libSoName = "uplayer";
    static private final Marker MARKER = MarkerFactory.getMarker("NOTIFY_ADMIN");
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    public static Context mContext = null;
    private Button btnClickStatic = null;
    private Button btnClick = null;
    private Button btnPlay = null;
    private Button btnPause = null;
    private Button btnStop = null;
    private ProgressBar progressBar = null;
    private TextView playTime = null;
    private MediaPlayerState mediaPlayerState = MediaPlayerState.STOPPED;

    private HandlerInvocation handlerInvocation = new HandlerInvocation();
    private Handler timerHandler = new Handler();
    private Handler msgHandler = new Handler(this);
    private final int updateInterval = 500;

    final private String currentFilePath = "/sdcard/android/kongfu.mp4";
    private String nullString=null;
    public native void init(Activity activity);

    public native void task();

    public native void getTime();

    public native void sayHello();

    public native void getData();

    public native void testUsbData();

    public native String testDirect();

    Logger log = LoggerFactory.getLogger(MainActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        mContext = this;
        //初始化控件
        initPlayer();
        init(this);
        initViews();
        HelloWorldRenderScript();
        final IntentFilter filter1 = new IntentFilter(ConstUtil.HOST);
        registerReceiver(icastReceiver, filter1);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ICAST_HOST);
        registerReceiver(resetBroadcastReceiver, filter);
        for (int i = 0; i < 4; i++)
        {
            new Thread(new Runnable()
            {
                
                @Override
                public void run()
                {
                    int i=0;
                    while(i++<4)
                    {
                        LogUtil.Logd(TAG, "xxx");
                        SystemClock.sleep(1000);
                    }
                    
                }
            }, "thread_test").start();
        }
    }
    
    private void HelloWorldRenderScript()
    {
        RenderScript rs = RenderScript.create(this);
        ScriptC_helloworld helloworldScript = new ScriptC_helloworld(rs,getResources(),R.raw.helloworld);
        helloworldScript.invoke_hello_world();
    }
    public static final String CMD="cmd";
    public static final String SENDER="sender";
    public static final String PARAM="param";
    public static final String ICAST="icast";
    public static final String CLIENT="com.smit.livevideo.icast.client";
    public static final String ICAST_HOST="com.smit.livevideo.icast.host";
    final static String ICAST_DONGLE_IS_RESETTING = "ICAST_DONGLE_IS_RESETTING";
    public static final String ICAST_DONGLE_HAS_RESET="ICAST_DONGLE_HAS_RESET";
    private BroadcastReceiver resetBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (ICAST_HOST.equals(action))
            {
              //解析出sender cmd 和 param参数,以String格式保存起来
//                String sender = intent.getStringExtra(SENDER);//为icast
                String cmd = intent.getStringExtra(CMD);
//                String param = intent.getStringExtra(PARAM);//为空
                if(ICAST_DONGLE_IS_RESETTING.equals(cmd))
                {
                    Log.d(TAG, "USB DONGLE 正在复位...");
                    Toast.makeText(MainActivity.this, "USB DONGLE 正在复位...", Toast.LENGTH_LONG).show();
                }
                if(ICAST_DONGLE_HAS_RESET.equals(cmd))
                {
                    Log.d(TAG, "USB DONGLE 已经复位完成");
                    Toast.makeText(MainActivity.this, "USB DONGLE 已经复位完成", Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    /**
     * 初始化控件
     */
    private void initViews()
    {
        surfaceView = (SurfaceView) findViewById(R.id.sf_main_activity_surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        progressBar = (ProgressBar) findViewById(R.id.pb_main_activity_progress);
        playTime = (TextView) findViewById(R.id.tv_main_activity_playtime);

        btnPlay = (Button) this.findViewById(R.id.btn_main_activity_play);
        btnPlay.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                play();
            }
        });

        btnPause = (Button) this.findViewById(R.id.btn_main_activity_pause);
        btnPause.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                pause();
            }
        });

        btnStop = (Button) this.findViewById(R.id.btn_main_activity_stop);
        btnStop.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                stop();
            }
        });

        btnClick = (Button) this.findViewById(R.id.btn_click);
        btnClick.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                sayHello();
                String str = testDirect();
                if (str != null)
                {
                    btnClickStatic.setText(str);
                }
            }
        });

        btnClickStatic = (Button) this.findViewById(R.id.btn_click_static);
        btnClickStatic.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                getTime();
                getData();
                //sayHello();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
               getSmartCardId();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                getDongleType();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                isSmartCardIn();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private BroadcastReceiver icastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String sender = intent.getStringExtra(SENDER);
            String cmd = intent.getStringExtra(CMD);
            if (ConstUtil.HOST.equals(sender))
            {
               if (ConstUtil.GET_DONGLE_TYPE.equals(cmd))
                {
                    String dongleType = intent.getStringExtra(PARAM);
                    LogUtil.Logd(TAG, "dongleType = "+dongleType);
                }
               if (ConstUtil.GET_SMART_CARD_ID.equals(cmd))
               {
                   String smartCardId = intent.getStringExtra(PARAM);
                   LogUtil.Logd(TAG, "smartCardId = "+smartCardId);
               }
               if (ConstUtil.IS_SMART_CARD_IN.equals(cmd))
               {
                   Boolean smartCardIn = intent.getBooleanExtra(PARAM, false);
                   LogUtil.Logd(TAG, "smartCardIn = "+smartCardIn);
               }
            }
        }
    };
    public static final String GET_DONGLE_TYPE = "GET_DONGLE_TYPE";
    public static final String GET_SMART_CARD_ID = "GET_SMART_CARD_ID";
    public static final String IS_SMART_CARD_IN = "IS_SMART_CARD_IN";
    void getDongleType()
    {
        Intent  getDongleTypeIntent = new Intent(CLIENT);
        getDongleTypeIntent.putExtra(ConstUtil.SENDER,CLIENT);
        getDongleTypeIntent.putExtra(ConstUtil.CMD,GET_DONGLE_TYPE);
        this.sendBroadcast(getDongleTypeIntent);
    }
    
    void getSmartCardId()
    {
        Intent getSmartCardIdIntent = new Intent(CLIENT); 
        getSmartCardIdIntent.putExtra(SENDER,ConstUtil.CLIENT); 
        getSmartCardIdIntent.putExtra(CMD,GET_SMART_CARD_ID); 
        this.sendBroadcast(getSmartCardIdIntent);
    }
    
    void isSmartCardIn()
    {
        Intent isSmartCardInIntent = new Intent(CLIENT); 
        isSmartCardInIntent.putExtra(SENDER,CLIENT); 
        isSmartCardInIntent.putExtra(CMD,IS_SMART_CARD_IN); 
        this.sendBroadcast(isSmartCardInIntent);
    }
    
    @Override
    public boolean handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case MSG_PLAY:
                play();
                break;
            case MSG_PAUSE:
                pause();
                break;
            case MSG_STOP:
                stop();
                break;
            case MSG_FORWARD:
                forward();
                break;
            case MSG_BACKWARD:
                backward();
                break;
            default:
                break;
        }
        return false;
    }

    private void initPlayer()
    {
        log.debug("initPlayer~~~~~");
        if (mediaPlayer == null)
        {
            log.debug("initPlayer~~~~~new player");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
        }
    }

    private void releasePlayer()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    void preparePlay()
    {
        try
        {
            mediaPlayer.setDataSource(mediaUrl);
            mediaPlayer.prepareAsync();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private class HandlerInvocation implements Runnable
    {
        public void run()
        {
            displayProgress();
            timerHandler.postDelayed(handlerInvocation, updateInterval);
        }
    };
boolean send = false;
    private void displayProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
        playTime.setVisibility(View.VISIBLE);
        Time progress = new Time(mediaPlayer.getCurrentPosition());
        Time total = new Time(mediaPlayer.getDuration());
        playTime.setText(progress.getMinutes() + ":" + progress.getSeconds() + " / " + total.getMinutes() + ":"
                + total.getSeconds());

        int progressValue = 0;
        if (mediaPlayer.getDuration() > 0)
        {
            progressValue = progressBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        }
        progressBar.setProgress(progressValue);
        log.debug("progressValue "+progressValue);
    }

    public void pause()
    {
        mediaPlayer.pause();
        mediaPlayerState = MediaPlayerState.PAUSED;
        timerHandler.removeCallbacks(handlerInvocation);
        displayProgress();
    }

    public void forward()
    {
        if (mediaPlayerState != MediaPlayerState.STOPPED)
        {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 500);
            mediaPlayer.start();
            timerHandler.postDelayed(handlerInvocation, updateInterval);
        }
        else
        {
            timerHandler.removeCallbacks(handlerInvocation);
            displayProgress();
        }
    }

    public void backward()
    {
        if (mediaPlayerState != MediaPlayerState.STOPPED)
        {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 500);
            mediaPlayer.start();
            timerHandler.postDelayed(handlerInvocation, updateInterval);
        }
        else
        {
            timerHandler.removeCallbacks(handlerInvocation);
            displayProgress();
        }
    }

    public void stop()
    {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayerState = MediaPlayerState.STOPPED;
        timerHandler.removeCallbacks(handlerInvocation);
        displayProgress();
        progressBar.setVisibility(View.GONE);
        playTime.setVisibility(View.GONE);
    }

    public void play()
    {
        if (mediaPlayerState == MediaPlayerState.STOPPED)
        {
            preparePlay();
        }
        else
        {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            mediaPlayerState = MediaPlayerState.PLAYING;
            timerHandler.postDelayed(handlerInvocation, updateInterval);
        }
    }

    /****************************************************
     * SurfaceHolder.Callback的接口实现
     ****************************************************/
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        log.debug("surfaceChanged Called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        log.debug("surfaceCreated Called");
        if (mediaPlayer != null)
        {
            mediaPlayer.setDisplay(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        log.debug("surfaceDestroyed Called");
    }

    /****************************************************
     * MediaPlayer Callback的接口实现
     ****************************************************/
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        log.debug("onCompletion Called");
        mediaPlayerState = MediaPlayerState.STOPPED;
        mediaPlayer.reset();
    }

    public boolean onError(MediaPlayer mediaPlayer, int whatError, int extra)
    {
        log.debug("onError Called");
        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
        {
            log.error("Media Error, Server Died " + extra);
        }
        else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN)
        {
            log.error("Media Error, Error Unknown " + extra);
        }
        else
        {
            log.error("Media Error, Error whatError:" + whatError + "extra:" + extra);
        }
        return false;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int whatInfo, int extra)
    {
        if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING)
        {
            log.debug("Media Info, Media Info Bad Interleaving " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE)
        {
            log.debug("Media Info, Media Info Not Seekable " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN)
        {
            log.debug("Media Info, Media Info Unknown " + extra);
        }
        else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING)
        {
            log.debug("MediaInfo, Media Info Video Track Lagging " + extra);
            /*
             * Android Version 2.0 and Higher } else if (whatInfo ==
             * MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
             * Log.v(LOGTAG,"MediaInfo, Media Info Metadata Update " + extra);
             */
        }
        return false;
    }

    public void onPrepared(MediaPlayer mediaPlayer)
    {
        log.debug("onPrepared Called");
        mediaPlayer.start();
        displayProgress();
        mediaPlayerState = MediaPlayerState.PLAYING;
        timerHandler.postDelayed(handlerInvocation, updateInterval);
    }

    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height)
    {
        log.debug("onVideoSizeChanged Called");
    }

    public void doPlay()
    {
        msgHandler.sendEmptyMessage(MSG_PLAY);
        return;
    }

    public void doPause()
    {
        msgHandler.sendEmptyMessage(MSG_PAUSE);
        return;
    }

    public void doStop()
    {
        msgHandler.sendEmptyMessage(MSG_STOP);
        return;
    }

    public void doForward()
    {
        msgHandler.sendEmptyMessage(MSG_FORWARD);
        return;
    }

    public void doBackward()
    {
        msgHandler.sendEmptyMessage(MSG_BACKWARD);
        return;
    }

    /**
     * 载入JNI生成的so库文件
     */
    static
    {
        System.loadLibrary(libSoName);
    }

}