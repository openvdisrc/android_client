package com.openvdi.remoteclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @author Tony Hoang </br> Date : 8/8/2013
 */
public class BaseFragmentActivity extends FragmentActivity {
	protected int screen_width;
	protected int screen_height;
	protected LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove window title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * Resize activity when resume
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// get screen size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screen_width = displaymetrics.widthPixels;
		screen_height = displaymetrics.heightPixels;

		// set layout size
		layout.setLayoutParams(new FrameLayout.LayoutParams(
				screen_width * 4 / 5, screen_height * 4 / 5));
	}
}
