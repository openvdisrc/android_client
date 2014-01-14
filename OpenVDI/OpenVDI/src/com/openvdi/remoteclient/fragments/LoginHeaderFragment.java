package com.openvdi.remoteclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.activities.GlobalData;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class LoginHeaderFragment extends Fragment {
	private Button btnClose;
	private Fragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// inflate fragment layout
		return inflater.inflate(R.layout.login_header, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// get fragment
		fragment = (Fragment) getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.login_header_fragment);
		TextView title = (TextView)fragment.getView().findViewById(R.id.login_title);
		if(title != null) {
			title.setText(GlobalData.DOMAIN);
		}
		
		// get btnClose
		btnClose = (Button) fragment.getView().findViewById(R.id.btnLoginClose);
		
		// implement onClick event btnClose
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Close Activity
				getActivity().finish();
			}
		});
	}
}
