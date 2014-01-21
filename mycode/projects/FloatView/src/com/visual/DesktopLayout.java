package com.visual;

import com.DeskTip.R;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DesktopLayout extends LinearLayout {

	public DesktopLayout(Context context) {
		super(context);
		setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams mLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(mLayoutParams);

		// 显示的ICON
		ImageView mImageView = new ImageView(context);
		mImageView.setImageResource(R.drawable.icon);
		addView(mImageView, mLayoutParams);

		// 显示的文字
		TextView mTextView = new TextView(context);
		mTextView.setText("Hello");
		mTextView.setTextSize(30);
		addView(mTextView, mLayoutParams);
	}
}
