package com.example.fragmenttest;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {

	final static String LOGTAG = "MainActivity_java";
	SearchFragment searchFragment;
	SettingFragment settingFragment;
	SearchFragment searchFragment2;
	SettingFragment settingFragment2;
	View settingFramView;
	View myFramView;
	Fragment mContent;
	FragmentManager fm ;
	FragmentTransaction ft;
	ArrayList<Fragment> fragmentList;
	Fragment from;
	Fragment to;
	static int  curIndex=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fm = getFragmentManager();
		ft = fm.beginTransaction();
		searchFragment = (SearchFragment) getFragmentManager().findFragmentById(R.id.search_fragment);
		settingFragment = (SettingFragment) getFragmentManager().findFragmentById(R.id.setting_fragment);
		settingFramView = findViewById(R.id.mysettingfragment_container);
		myFramView = findViewById(R.id.myfragment_container);
		searchFragment2 = new SearchFragment();
		settingFragment2 = new SettingFragment();
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(searchFragment);
		fragmentList.add(settingFragment);
		fragmentList.add(searchFragment2);
		fragmentList.add(settingFragment2);
		/*
		if (!settingFragment2.isAdded()) {    // 先判断是否被add过
            ft.add(R.id.myfragment_container, settingFragment2).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            ft.show(settingFragment2).commit(); // 隐藏当前的fragment，显示下一个
        }
        */
	}

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		Log.v(LOGTAG,"onKeyDown keyevent:"+event);
		return false;
	}
	
	 public void switchContent(Fragment from, Fragment to) {
	        if (mContent != to) {
	        	Log.v(LOGTAG, "switchContent");
	            mContent = to;
	            FragmentTransaction transaction = fm.beginTransaction().setCustomAnimations(
	            		android.R.animator.fade_in, android.R.animator.fade_out);
	            if (!to.isAdded()) {    // 先判断是否被add过
	                transaction.hide(from).add(R.id.myfragment_container, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
	            } else {
	                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
	            }
	        }
	    }
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		Log.v(LOGTAG,"onKeyUp keyevent:"+event);
		ft = fm.beginTransaction();
		ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			//switchContent(searchFragment2, settingFragment2);
			//Log.v(LOGTAG, "KeyEvent.KEYCODE_BACK : switchContent");
			/*
			if(searchFragment.isHidden()){
				Log.v(LOGTAG, "ft.show(searchFragment)");
				ft.show(searchFragment);
				ft.commit();
			} else {
				Log.v(LOGTAG, "ft.hide(searchFragment)");
				ft.hide(searchFragment);
				ft.commit();
			}*/
			/*
			if(settingFramView.getVisibility() == View.GONE){
				settingFramView.setVisibility(View.VISIBLE);
			} else {
				settingFramView.setVisibility(View.GONE);
			}*/
			return true;
		case KeyEvent.KEYCODE_MENU:
			Log.v(LOGTAG, "KeyEvent.KEYCODE_MENU : switchContent");
			/*
			 * 让多个Fragment 切换时不重新实例化，这个方法比较好，如果
			 * 前台只有一个fragment，那么它理所当然会获取到focus，如果
			 * 我们自己控制不好就会出现focus掉到其他的控件上，虽然这个控件
			 * 我们看不到，其实是被上一个覆盖了！！这个很麻烦。
			 */
			if(curIndex == 0 || curIndex == 1 || curIndex == 2){
				from = fragmentList.get(curIndex);
				to = fragmentList.get(curIndex+1);
				curIndex++;
			} else if(curIndex == 3){
				from = fragmentList.get(curIndex);
				to = fragmentList.get(0);
				curIndex = 0;
			}
			switchContent(from, to);
			/*
			 * 这里是通过使用ft来显示和隐藏fragment，显示出来
			 * 也是获取不到focus，观察发现，foucs由系统来控制
			 * 系统初始化玩一后，focus在最后显示且要求focus的控件
			 * 如果上一个有focus的控件隐藏了，系统会切换到之前一个
			 * 还在显示的控件，如果都没有，应该就还给父窗口了，当
			 * 又有子窗口显示的时候，foucs会给子窗口
			 */
			/*
			if(searchFragment.isHidden()){
				Log.v(LOGTAG, "ft.show(searchFragment)");
				ft.show(searchFragment);
				ft.commit();
			} else {
				Log.v(LOGTAG, "ft.hide(searchFragment)");
				ft.hide(searchFragment);
				ft.commit();
			}
			*/
			/*
			 * 这里的myFramView是动态加载fragment的，显示隐藏是通过
			 * 设置FramLayout的属性来的，显示fragment后fragment
			 * 本身无法获取到focus
			 */
			/*
			if(myFramView.getVisibility() == View.GONE){
				myFramView.setVisibility(View.VISIBLE);
			} else {
				myFramView.setVisibility(View.GONE);
			}
			*/
			return true;
		default:
			break;
		}
		return false;
	}

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {

		}
	};

}
