package com.openvdi.remoteclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.activities.AddServerActivity;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class ServerHeaderFragment extends Fragment {
	private Button btnClose;
	private Button btnAdd;
	private Fragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// inflate fragment layout
		return inflater.inflate(R.layout.server_header, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// get fragment
		fragment = (Fragment) getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.server_header_fragment);

		// get btnClose
		btnClose = (Button) fragment.getView().findViewById(R.id.btnClose);

		// implement onClick event btnClose
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Close Activity
				getActivity().finish();
			}
		});

		// get btnAdd
		btnAdd = (Button) fragment.getView().findViewById(R.id.btnAdd);
		
		// implement onClick event btnAdd
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// Open AddServerActivity
				Intent intentAddServer = new Intent(getActivity(),
						AddServerActivity.class);
				startActivity(intentAddServer);
			}
		});
	}
}
