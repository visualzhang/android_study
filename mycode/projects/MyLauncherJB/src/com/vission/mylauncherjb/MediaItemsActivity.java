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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
//import android.os.SystemProperties;
import android.widget.Toast;

public class MediaItemsActivity extends Activity implements OnItemClickListener, OnItemSelectedListener {
	GridView mediaGridView;
	ImageAdapter mediaAdapter;
    //下面的图标是进入音视频后显示的，有QQMusic MoreTV DouBanDianTai
    private Integer[] media_item_focus = {
    		R.drawable.moretv_focus,R.drawable.qqmusic_focus,R.drawable.douban_focus
    };
    private Integer[] media_item_unfocus = {
    		R.drawable.moretv_unfocus,R.drawable.qqmusic_unfocus,R.drawable.douban_unfocus
    };
    private Integer[] media_text_id = {
    		R.string.media_moretv,R.string.media_qqmusic,R.string.media_douban
    };
    private final int MORETV_APP = 0;
    private final int QQMUSIC_APP = 1;
    private final int DOUBAN_APP = 2;
    
    private int  media_text_size = 24;
    private int  default_focus = 0;
    private int  first_time=1;
    
    private Timer timer=null;
    private TimerTask timerTask=null;
    private int   screentimeout=60*2*1000;
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
    			savescreen.setClass(MediaItemsActivity.this, ScreenSave.class);
    			startActivity(savescreen);
                // 结束Timer计时器
            } 
        } 
    }; 

    private void myStartActivity(Intent intent,String name){
		PackageManager pm= getPackageManager();
		ComponentName cn = intent.resolveActivity(pm);
		if(cn == null){
			Toast.makeText(getApplicationContext(), name+" 没有安装!", Toast.LENGTH_LONG).show();
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
  	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_items);
		
		mediaGridView = (GridView) findViewById(R.id.gride_media_item);
		
		mediaAdapter = new ImageAdapter(this,media_item_focus,media_item_unfocus,
				media_text_id,media_text_size);
		mediaAdapter.setDefaultFocus(default_focus);

		mediaGridView.setNumColumns(3);
		mediaGridView.setSelection(default_focus);
		mediaGridView.requestFocus();
		mediaGridView.setAdapter(mediaAdapter);
		
		mediaGridView.setOnItemClickListener(this);
		mediaGridView.setOnItemSelectedListener(this);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		stopScreenSaveTimer();
		startScreenSaveTimer();
		if(first_time == 1)
		{
			first_time = 0;
			try {
	            @SuppressWarnings("unchecked")
	            Class<GridView> c = (Class<GridView>) Class
	                    .forName("android.widget.GridView");
	            Method[] flds = c.getDeclaredMethods();
	            for (Method f : flds) {
	                if ("setSelectionInt".equals(f.getName())) {
	                    f.setAccessible(true);
	                    f.invoke(mediaGridView,
	                            new Object[] { Integer.valueOf(default_focus) });
	                }
	            }
	            mediaGridView.setSelection(default_focus);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		stopScreenSaveTimer();

		if(pos == MORETV_APP ){
			Intent intent = new Intent(); 
			ComponentName componentName = new ComponentName("com.moretv.tvapp",
					"com.moretv.tvapp.MoreTVWebActivity"); 
			 
			intent.setComponent(componentName); 
			myStartActivity(intent,"More TV");
		}
		if(pos == QQMUSIC_APP ){
			Intent intent = new Intent(); 
			ComponentName componentName = new ComponentName("com.tencent.qqmusicpad",
					"com.tencent.qqmusicpad.MainActivity"); 
			 
			intent.setComponent(componentName); 
			myStartActivity(intent,"QQ音乐");
		}
		if(pos == DOUBAN_APP ){
			Intent intent = new Intent(); 
			ComponentName componentName = new ComponentName("com.douban.radio",
					"com.douban.radio.Welcome"); 
			 
			intent.setComponent(componentName); 
			myStartActivity(intent,"豆瓣电台");
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
		mediaAdapter.notifyDataSetChanged(pos);
		stopScreenSaveTimer();
		startScreenSaveTimer();
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopScreenSaveTimer();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_MENU://处理掉 MENU,不让系统接受
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
        //return false;
    }
	/*
	public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                DisplayToast("弹起：返回键");
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                DisplayToast("按下：中间键");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                DisplayToast("按下：向上键");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                DisplayToast("按下：向下键");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                DisplayToast("按下：左方向键");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                DisplayToast("按下：右方向键");
                break;
            case KeyEvent.KEYCODE_MENU:
                DisplayToast("按下：MENU键");
                return true;
            default:
                break;
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }*/
  
   /* public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
             
            case KeyEvent.KEYCODE_DPAD_CENTER:
                DisplayToast("弹起：中间键");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                DisplayToast("弹起：向下键");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                DisplayToast("弹起：向左键");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                DisplayToast("弹起：向右键");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                DisplayToast("弹起：向上键");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }*/
  
	/*
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event)
    {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }
  
    public boolean onTouchEvent(MotionEvent event)
    {
        int iAction = event.getAction();
        if (iAction == MotionEvent.ACTION_CANCEL || iAction == MotionEvent.ACTION_DOWN
                || iAction == MotionEvent.ACTION_MOVE)
        {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        DisplayToast("触笔点击坐标:(" + Integer.toString(x) + "," + Integer.toString(y) + ")");
        return super.onTouchEvent(event);
    }
    */
    public void DisplayToast(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
