package com.openvdi.remoteclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.net.RequestBackgroundWorker;
import com.openvdi.remoteclient.operations.AddNewServer;

/**
 * @author Tony Hoang </br> Date : 8/8/2013
 */
public class MainActivity extends Activity {

	private Button btnAddServer;
	private Button btnServerList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// remove window title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// inflate activity layout
		setContentView(R.layout.main);

		// add action open AddServerActivity
		addListenerOnButtonAddServer();

		// add action open ServerListActivity
		addListenerOnButtonServerList();
		
		RequestBackgroundWorker.startWaitingForRequest();
	}

	/**
	 * add action open AddServerActivity
	 */
	private void addListenerOnButtonAddServer() {
		// inflate button AddServer layout
		btnAddServer = (Button) findViewById(R.id.btnAddServer);

		// implement onClick event btnAddServer
		btnAddServer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// open AddServerActivity
				Intent intentAddServer = new Intent(getApplicationContext(),
						AddServerActivity.class);
				startActivity(intentAddServer);
			}
		});
	}

	/**
	 * add action open ServerListActivity
	 */
	private void addListenerOnButtonServerList() {
		// get button AddServer layout
		btnServerList = (Button) findViewById(R.id.btnServerList);

		// implement onClick event btnServerList
		btnServerList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// open ServerActivity
				Intent intentServer = new Intent(getApplicationContext(),
						ServerActivity.class);
				startActivity(intentServer);
			}
		});
	}
}
