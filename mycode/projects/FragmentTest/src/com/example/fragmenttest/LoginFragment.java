package com.example.fragmenttest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login,container,false);
		Button btnCommit = (Button) view.findViewById(R.id.btnCommit);
		Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Commit Clicked!", Toast.LENGTH_SHORT).show();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Cancel Clicked!", Toast.LENGTH_SHORT).show();
			}
		});
		/*
		view.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});*/
		return view;
	}
/*
 * fragment 没有自己的onKeyDown函数，这个函数我们会在MainActivity的onKeyDown
 * 中进行调用，返回值
 * 0  : 表示这个消息我不管，返给activity就好了
 * 1  : 表示这个消息我处理了，不让activity再处理
 * 2  : 表示这个消息我不处理，也不让activity来处理，给系统来处理
 */
	int onKeyDown(int keyCode, KeyEvent event) {
		
		/*
		 * 这里是把返回键直接给activity来处理，如果activity的没有处理
		 * 而直接给系统，则整个app 会退出，一般会做处理，必然隐藏这个fragment
		 * 其实如果fragment是动态加载的话，在这里可以自动隐藏
		 */
		//if(keyCode == KeyEvent.KEYCODE_BACK)
		//	return 0;
		
		/*
		 * 添加下面的处理是在commit cancel按钮不再往下移动focus,测试OK
		 */
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
			View view = getView();
			Button btnCommit = (Button) view.findViewById(R.id.btnCommit);
			Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
			if(btnCancel.isFocused() || btnCommit.isFocused()){
				return 1;
			}
			return 2;
		}
		return 2;
	}
}
