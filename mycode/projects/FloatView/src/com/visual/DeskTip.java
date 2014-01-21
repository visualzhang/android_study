package com.visual;

import com.DeskTip.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

public class DeskTip extends Activity {

	private WindowManager mWindowManager;

	private WindowManager.LayoutParams mLayoutParams;

	private DesktopLayout mDesktopLayout;

	private long starttime;

	/**
	 * 创建悬浮窗体
	 */
	private void createDesktopLayout() {
		mDesktopLayout = new DesktopLayout(this);
		mDesktopLayout.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				onActionMove(event);
				return true;
			}
		});
	}

	/**
	 * 设置WindowManager
	 */
	private void createWindowManager() {
		// 取得系统窗体
		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService("window");

		// 窗体的布局样式
		mLayoutParams = new WindowManager.LayoutParams();

		// 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
		mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		// 设置窗体焦点及触摸：
		// FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
		mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置显示的模式
		mLayoutParams.format = PixelFormat.RGBA_8888;

		// 设置对齐的方法
		mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		// 设置窗体宽度和高度
		mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		// 设置窗体显示的位置，否则在屏幕中心显示
		mLayoutParams.x = 50;
		mLayoutParams.y = 50;
	}

	private void onActionMove(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			long end = System.currentTimeMillis() - starttime;
			// 双击的间隔在 200ms 到 500ms 之间
			if (end > 200 && end < 500) {
				closeDesk();
				return;
			}
			starttime = System.currentTimeMillis();
		}

		mLayoutParams.x = (int) (event.getRawX() - (mDesktopLayout.getWidth()));
		mLayoutParams.y = (int) (event.getRawY() - (mDesktopLayout.getHeight()));

		mWindowManager.updateViewLayout(mDesktopLayout, mLayoutParams);
	}

	/**
	 * 显示DesktopLayout
	 */
	private void showDesk() {
		mWindowManager.addView(mDesktopLayout, mLayoutParams);
		finish();
	}

	/**
	 * 关闭DesktopLayout
	 */
	private void closeDesk() {
		mWindowManager.removeView(mDesktopLayout);
		finish();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			showDesk();
			Intent intent = new Intent(this,MyFloatViewActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
    	return super.onKeyDown(keyCode, event);
    }
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		createWindowManager();
		createDesktopLayout();

		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDesk();
			}
		});
	}
}