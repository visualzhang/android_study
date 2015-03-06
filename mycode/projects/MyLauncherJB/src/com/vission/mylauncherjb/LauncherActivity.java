package com.vission.mylauncherjb;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
//import android.os.SystemProperties;

public class LauncherActivity extends Activity  implements
										GridView.OnItemClickListener,
										GridView.OnItemSelectedListener, OnFocusChangeListener {
	GridView mainGridView;
	ImageAdapter menuImageAdapter;
	private Integer[] main_item_focus = {
	        R.drawable.local_focus,R.drawable.media_focus,R.drawable.live_focus,
	        R.drawable.guard_focus,R.drawable.settings_focus
	    };
    private Integer[] main_item_unfocus = {
	        R.drawable.local_unfocus,R.drawable.media_unfocus,R.drawable.live_unfocus,
	        R.drawable.guard_unfocus,R.drawable.settings_unfocus
	    };
    private Integer[] main_item_text = {
    		R.string.item_local,R.string.item_media,R.string.item_live,
    		R.string.item_guard,R.string.item_settings
    };
    private int main_text_size=30;
   
    private final int LOCAL_APP = 0;
    private final int MEDIA_APP = 1;
    private final int LIVE_APP = 2;
    private final int GUARD_APP = 3;
    private final int SETTINGS_APP = 4;
    
    private int default_focus=LIVE_APP;
    private int first_time=1;
    
    private Timer timer=null;
    private TimerTask timerTask=null;
    private int   screentimeout=60*2*1000;
    //private final String HIDE_STATUSBAR = "mbx.hideStatusBar.enable";
    //private final String HAS_ACCELEROMETER = "ubootenv.var.has.accelerometer";
    // 定义Handler  
    Handler timerhandler = new Handler() { 
 
        @Override 
        public void handleMessage(Message msg) { 
            super.handleMessage(msg); 
 
            Log.d("debug", "handleMessage in task：" 
                    + Thread.currentThread().getName()); 
 
            // Handler处理消息  
            if (msg.what == 1) {
    			//timerTask.cancel();
    			Intent savescreen = new Intent();
    			savescreen.setClass(LauncherActivity.this, ScreenSave.class);
    			startActivity(savescreen);
                // 结束Timer计时器
            } 
        } 
    }; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		//SystemProperties.set(HIDE_STATUSBAR,"true");
		//SystemProperties.set(HAS_ACCELEROMETER,"false");

		mainGridView = (GridView) findViewById(R.id.gvMenu);
		
		menuImageAdapter = new ImageAdapter(this,main_item_focus,main_item_unfocus,
				main_item_text,main_text_size);
		menuImageAdapter.setDefaultFocus(default_focus);
		
		mainGridView.setNumColumns(5);
		mainGridView.setAdapter(menuImageAdapter);

		mainGridView.setOnItemClickListener(this);
		mainGridView.setOnItemSelectedListener(this);
		mainGridView.setOnFocusChangeListener(this);
		
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		//WindowManager.LayoutParams attrs = getWindow().getAttributes();
		//attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		//getWindow().setAttributes(attrs);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.launcher, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		menuImageAdapter.notifyDataSetChanged(pos);
		stopScreenSaveTimer();
		startScreenSaveTimer();
		
	}
	//Timer for screen save
	public void startScreenSaveTimer() {
		if(timer == null){
			timer = new Timer();			
		}
		if(timerTask == null){
			timerTask = new TimerTask() { 
	            @Override 
	            public void run() { 
	                Log.d("debug", "run in task" 
	                        + Thread.currentThread().getName()); 
	                // 定义一个消息传过去  
	                Message msg = new Message(); 
	                msg.what = 1; 
	                timerhandler.sendMessage(msg); 
	            } 
			};
		}
		timer.schedule(timerTask, screentimeout);
	}
	
	public void stopScreenSaveTimer() {
		if(timer != null){
			timer.cancel();
			timer=null;
		}
		if(timerTask != null){
			timerTask.cancel();
			timerTask=null;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		/* 设置 GridView 的默认选中*/
		stopScreenSaveTimer();
		startScreenSaveTimer();
		
		if(first_time == 1)
		{
			first_time = 0;
			mainGridView.requestFocusFromTouch();
			try {
	            @SuppressWarnings("unchecked")
	            Class<GridView> c = (Class<GridView>) Class
	                    .forName("android.widget.GridView");
	            Method[] flds = c.getDeclaredMethods();
	            for (Method f : flds) {
	                if ("setSelectionInt".equals(f.getName())) {
	                    f.setAccessible(true);
	                    f.invoke(mainGridView,
	                            new Object[] { Integer.valueOf(2) });
	                }
	            }
	            mainGridView.setSelection(default_focus);	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	};
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
    public void onFocusChange(View v, boolean hasFocus) {
		stopScreenSaveTimer();
		startScreenSaveTimer();
    }
	
	private void myStartActivity(Intent intent,String name){
		PackageManager pm= getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		if(cn == null){
			Toast.makeText(this, name+" 没有安装!", Toast.LENGTH_LONG).show();
		}
		else{
			try{
				startActivity(intent);
			}
			catch (ActivityNotFoundException  e) {
				Toast.makeText(getApplicationContext(), name+" 没有安装!", Toast.LENGTH_LONG).show();
			}
			catch (Exception e) {
				Toast.makeText(getApplicationContext(), name+" 没有安装!", Toast.LENGTH_LONG).show();
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		menuImageAdapter.notifyDataSetChanged(pos);
		Intent intent;
		ComponentName componentName;
		stopScreenSaveTimer();

		switch (pos) {
		case LOCAL_APP:
			intent = new Intent(); 
			componentName = new ComponentName("com.fb.FileBrower",
					"com.fb.FileBrower.FileBrower"); 
			//ComponentName componentName = new ComponentName("com.hello","com.hello.App1_B");//这个报错 
			intent.setComponent(componentName); 
			startActivity(intent);
		break;
		case MEDIA_APP:
//			intent = new Intent();
//			intent.setClass(LauncherActivity.this, MediaItemsActivity.class);
//			startActivity(intent);
			intent = new Intent(); 
          componentName = new ComponentName("com.tencent.qqmusicpad",
                  "com.tencent.qqmusicpad.MainActivity"); 
          intent.setComponent(componentName); 
          myStartActivity(intent,"我的音乐");
		break;
		case LIVE_APP:
			intent = new Intent(); 
//			componentName = new ComponentName("com.moons.onlinetv",
//					"com.moons.onlinetv.OnlineTV"); 
//			intent.setComponent(componentName); 
//			myStartActivity(intent,"电视大师");
			componentName = new ComponentName("net.myvst.v2",
                    "com.vst.itv52.v1.LancherActivity"); 
            intent.setComponent(componentName); 
            myStartActivity(intent,"网络电视");
		break;
		case GUARD_APP:
			intent = new Intent();
//			componentName = new ComponentName("com.qihoo360.mobilesafe_tv",
//					"com.qihoo360.mobilesafe.ui.index.AppEnterActivity"); 
//			intent.setComponent(componentName); 
//			myStartActivity(intent,"360电视卫士");
			componentName = new ComponentName("com.smit.livevideo",
                    "com.smit.livevideo.activity.LauncherActivity"); 
            intent.setComponent(componentName); 
            myStartActivity(intent,"融合电视");
		break;
		case SETTINGS_APP:
			intent = new Intent(); 
			componentName = new ComponentName("com.android.settings",
					"com.android.settings.Settings"); 
			//ComponentName componentName = new ComponentName("com.hello","com.hello.App1_B");//这个报错 
			intent.setComponent(componentName); 
			startActivity(intent);
		break;

		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopScreenSaveTimer();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_MENU://处理掉 MENU,不让系统接受
                return true;
            case KeyEvent.KEYCODE_BACK://处理掉 MENU,不让系统接受
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
        //return false;
    }
  

}
