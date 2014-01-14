package com.openvdi.remoteclient.fragments;

import java.util.Vector;

import android.content.Intent;
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
import com.openvdi.remoteclient.activities.GlobalData;
import com.openvdi.remoteclient.models.Server;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class DomainListFragment extends Fragment {
	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// inflate fragment layout
		LinearLayout theLayout = (LinearLayout) inflater.inflate(
				R.layout.domain_list, container, false);
		listView = (ListView) theLayout.findViewById(R.id.domain_listview);
		return theLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// TODO: request server Nimdesk get Domain list
		String[] values = null;

		if(GlobalData.DOMAIN_LIST != null){
			values = new String[GlobalData.DOMAIN_LIST.size()];
			for (int i = 0; i < GlobalData.DOMAIN_LIST.size(); i++) {
				Server server = GlobalData.DOMAIN_LIST.get(i);
				
				values[i] = (server.name);
			}
		} else {
			
		}
		// set layout and values to adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.domain_item, values);

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
			if(GlobalData.DOMAIN_LIST != null){
				Server item = (Server) GlobalData.DOMAIN_LIST.get(position);
				Intent returnIntent = new Intent();
				 returnIntent.putExtra("server_id",item.id);
				 returnIntent.putExtra("server_name",item.name);
				 getActivity().setResult(LoginBodyFragment.CODE_CHOOSE_DOMAIN,returnIntent);     
				 getActivity().finish();
			}
			
		}
	};

}
