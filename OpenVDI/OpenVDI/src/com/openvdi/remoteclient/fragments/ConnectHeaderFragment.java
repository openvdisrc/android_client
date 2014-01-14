package com.openvdi.remoteclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.openvdi.remoteclient.R;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class ConnectHeaderFragment extends Fragment{
	private Button btnClose;
	private Fragment fragment;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		// inflate fragment layout
        return inflater.inflate(R.layout.connect_header, container, false);        
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {     
        super.onActivityCreated(savedInstanceState);
      
        // get fragment
        fragment = (Fragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.connect_header_fragment);
		
        // get btnClose
        btnClose = (Button)fragment.getView().findViewById(R.id.btnConnectClose);
		
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
