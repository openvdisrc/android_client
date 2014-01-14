package com.openvdi.remoteclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.activities.AddServerActivity;
import com.openvdi.remoteclient.activities.GlobalData;
import com.openvdi.remoteclient.activities.LoginActivity;
import com.openvdi.remoteclient.operations.AddNewServer;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class ServerListFragment extends Fragment {
	ListView listView;
	String serverList = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// inflate fragment layout
		LinearLayout theLayout = (LinearLayout) inflater.inflate(
				R.layout.server_list, container, false);
		listView = (ListView) theLayout.findViewById(R.id.server_listview);
		
		serverList = AddNewServer.restoreServerName(getActivity());
		Log.d("ServerListFragment", ">>>> "+serverList);
		
		return theLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// TODO: request server get Server list
		String[] values = null;
		if(serverList != null && serverList.length() > 0){
			values = serverList.split(",");
		} else {
			values = new String[] {serverList};
		}
		
		

		// set layout and values to adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.server_item, values);

		// set listview adapter
		listView.setAdapter(adapter);

		// remove divider on listview
		// listView.setDivider(null);
		
		// set listener onItemClick
		listView.setOnItemClickListener(itemListener);
	}

	/**
	 * implement onItemClick event
	 */
	private OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			 String item = (String) listView.getAdapter().getItem(position);
			// open LoginActivity
			GlobalData.DOMAIN = item;
			Intent intentLogin = new Intent(getActivity(), AddServerActivity.class);
			startActivity(intentLogin);
		}
	};
}
