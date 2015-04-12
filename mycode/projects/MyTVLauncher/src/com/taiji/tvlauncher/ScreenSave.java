package com.taiji.tvlauncher;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ScreenSave extends Activity implements OnClickListener {
	private ImageView ShowImage = null;  
    private int index = 0;
    private int [] Images = {R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5};  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题 

        //关闭系统屏保：
        final Window win = this.getWindow();
        final WindowManager.LayoutParams params = win.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
        		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;//FLAG_SHOW_WHEN_LOCKED;
        
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.screen_save);  
        this.ShowImage = (ImageView) this.findViewById(R.id.imageSaveScreen);  
        this.ShowImage.setBackgroundResource(R.drawable.p1);  
        ShowImage.setOnClickListener(this);
        autoShowImage();
    }  
  
    public void autoShowImage(){//自动播放图片
        new Thread(new Runnable() {  
            @Override  
            public void run() {
                while (true) {
                    Message msg =  new Message();  
                    msg.obj = index;  
                    handler.sendMessage(msg);  
                    index ++;  
                    if(index >= Images.length){  
                        index = 0;  
                    }  
                    try {  
                        Thread.sleep(10000);//具体应用时可以通过传参数的方法
                        //Mail.SendEmail("test");
                    } catch (InterruptedException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }).start();  
    }  
  
    @SuppressLint("HandlerLeak")
	public Handler handler = new Handler(){  
        @SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {  
            //这里之所以没有用switch 来判断传回的msg。what 来控制 是应为这里的handler 只处理更新图片，传回来的信息就是图片的编号，所以不需要  
        	ShowImage.setBackgroundResource(Images[(Integer) msg.obj]);
        };  
    };

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		ScreenSave.this.finish();
	}  
}
