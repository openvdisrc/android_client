package com.openvdi.remoteclient.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.openvdi.remoteclient.R;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class DomainActivity extends BaseFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// inflate activity layout
		setContentView(R.layout.domain);

		// get activity layout
		this.layout = (LinearLayout) findViewById(R.id.domain_screen);
	}

	/**
	 * Override base size
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// override set layout size
		this.layout.setLayoutParams(new FrameLayout.LayoutParams(
				screen_width * 3 / 5, screen_height * 4 / 5));
	}
}
