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
		if (!settingFragment2.isAdded()) {    // ���ж��Ƿ�add��
            ft.add(R.id.myfragment_container, settingFragment2).commit(); // ���ص�ǰ��fragment��add��һ����Activity��
        } else {
            ft.show(settingFragment2).commit(); // ���ص�ǰ��fragment����ʾ��һ��
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
	            if (!to.isAdded()) {    // ���ж��Ƿ�add��
	                transaction.hide(from).add(R.id.myfragment_container, to).commit(); // ���ص�ǰ��fragment��add��һ����Activity��
	            } else {
	                transaction.hide(from).show(to).commit(); // ���ص�ǰ��fragment����ʾ��һ��
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
			 * �ö��Fragment �л�ʱ������ʵ��������������ȽϺã����
			 * ǰֻ̨��һ��fragment����ô��������Ȼ���ȡ��focus�����
			 * �����Լ����Ʋ��þͻ����focus���������Ŀؼ��ϣ���Ȼ����ؼ�
			 * ���ǿ���������ʵ�Ǳ���һ�������ˣ���������鷳��
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
			 * ������ͨ��ʹ��ft����ʾ������fragment����ʾ����
			 * Ҳ�ǻ�ȡ����focus���۲췢�֣�foucs��ϵͳ������
			 * ϵͳ��ʼ����һ��focus�������ʾ��Ҫ��focus�Ŀؼ�
			 * �����һ����focus�Ŀؼ������ˣ�ϵͳ���л���֮ǰһ��
			 * ������ʾ�Ŀؼ��������û�У�Ӧ�þͻ����������ˣ���
			 * �����Ӵ�����ʾ��ʱ��foucs����Ӵ���
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
			 * �����myFramView�Ƕ�̬����fragment�ģ���ʾ������ͨ��
			 * ����FramLayout���������ģ���ʾfragment��fragment
			 * �����޷���ȡ��focus
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
