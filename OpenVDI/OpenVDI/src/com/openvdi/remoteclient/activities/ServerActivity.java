package com.openvdi.remoteclient.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.operations.AddNewServer;

/**
 * @author Tony Hoang </br> Date : 8/8/2013
 */
public class ServerActivity extends BaseFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// inflate activity layout
		setContentView(R.layout.server);
		
		// get activity layout
		this.layout = (LinearLayout) findViewById(R.id.server_screen);
	}

}
