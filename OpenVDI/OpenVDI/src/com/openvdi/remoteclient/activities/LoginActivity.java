package com.openvdi.remoteclient.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.openvdi.remoteclient.R;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class LoginActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// inflate activity layout
		setContentView(R.layout.login);

		// get activity layout
		this.layout = (LinearLayout) findViewById(R.id.login_screen);
	}
}
