package com.openvdi.remoteclient.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.openvdi.remoteclient.R;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class ConnectActivity extends BaseFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// inflate activity layout
		setContentView(R.layout.connect);

		// get activity layout
		this.layout = (LinearLayout) findViewById(R.id.connect_screen);
	}
}
