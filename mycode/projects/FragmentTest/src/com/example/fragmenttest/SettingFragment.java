package com.example.fragmenttest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	final static String LOGTAG="SettingFragment_java";
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.v(LOGTAG, "onActivityCreated");
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.v(LOGTAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.v(LOGTAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onCreateView");
		View view = inflater.inflate(R.layout.setting_fragment, container, false);
		TextView tv1 = (TextView)view.findViewById(R.id.textView1);
		TextView tv2 = (TextView)view.findViewById(R.id.textView2);
		tv1.setFocusable(true);
		tv1.requestFocus();
		tv1.requestFocusFromTouch();
		tv1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(LOGTAG, "tvsetting11111111.setOnClickListener");
			}
		});
		tv2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(LOGTAG, "tvsetting22222222.setOnClickListener");				
			}
		});

		return view;
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onDetach");
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.v(LOGTAG, "onDestroyView");
		super.onDestroyView();
	}

}
