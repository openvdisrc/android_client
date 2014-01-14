package com.openvdi.remoteclient.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DownloadManager.Request;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.activities.GlobalData;
import com.openvdi.remoteclient.activities.LoginActivity;
import com.openvdi.remoteclient.models.Server;
import com.openvdi.remoteclient.net.ProgressDialogManager;
import com.openvdi.remoteclient.net.RequestBackgroundWorker;
import com.openvdi.remoteclient.net.RequestListener;
import com.openvdi.remoteclient.net.UrlHelper;
import com.openvdi.remoteclient.operations.AddNewServer;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class AddServerBodyFragment extends Fragment {
	private Button btnConnect;
	private Fragment fragment;
	private EditText etxtServername;

	ProgressDialogManager mProgressDialogManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// inflate fragment layout
		RelativeLayout theLayout = (RelativeLayout) inflater.inflate(R.layout.add_server_body, container, false);
		return theLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mProgressDialogManager = new ProgressDialogManager(getActivity());

		// get fragment
		fragment = (Fragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.add_server_body_fragment);

		// get editText ServerName
		etxtServername = (EditText) fragment.getView().findViewById(R.id.add_server_connection_name);
		if(GlobalData.DOMAIN != null && GlobalData.DOMAIN.length() > 0){
			etxtServername.setText(GlobalData.DOMAIN);
		}
		// get btnConnect
		btnConnect = (Button) fragment.getView().findViewById(R.id.btnConnect);

		// implement onClick event btnConnect
		btnConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String serverName = etxtServername.getText().toString().trim();
				if (serverName.length() == 0) {
					Toast toast = Toast.makeText(getActivity(), "Please enter Domain Name", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				if (!serverName.startsWith("http")) {
					Toast toast = Toast.makeText(getActivity(), "Please enter full domain with prefix HTTP", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				doConnectGetDomain(serverName);

			}
		});
	}

	void doConnectGetDomain(final String inputUrl) {
		String requestUrl = UrlHelper.getDomainUrl(inputUrl);
		Log.d("requestUrl", "" + requestUrl);
		com.openvdi.remoteclient.net.Request request = new com.openvdi.remoteclient.net.Request() {

			@Override
			public void runRequest() {
				send();
			}

			@Override
			public void requestFinished(JSONObject jsonObject) {
				// TODO Auto-generated method stub

				if (jsonObject != null && jsonObject.has("data")) {
					try {
						
						JSONArray array = jsonObject.getJSONArray("data");
						if(array != null && array.length() > 0) {
							GlobalData.clear();
						}
						for (int i = 0, n = array.length(); i < n; i++) {
							JSONObject mChild = array.getJSONObject(i);
							Server server = new Server();
							String id = mChild.has("id") ? mChild.getString("id") : "";
							String name = mChild.has("name") ? mChild.getString("name") : "";

							server.id = id;
							server.name = name;

							GlobalData.addServer(server);
						}
						GlobalData.DOMAIN = inputUrl;
						// Save ServerName to SharedPreferences
						if (!AddNewServer.saveServer(inputUrl,getActivity())) {
							// show error message
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									Toast toast = Toast.makeText(getActivity(), "Can not save Domain Name", Toast.LENGTH_SHORT);
									toast.show();
								}
							});
							
						}
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mProgressDialogManager.dismiss();
								// Open LoginActivity
								Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
								startActivity(intentLogin);
							}
						});

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void requestFailed(int errorCode, String message) {
				// TODO Auto-generated method stub
				mProgressDialogManager.dismiss();
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						new AlertDialog.Builder(getActivity()).setTitle("Error").setMessage("Can not connect to your domain, please try again!")
								.setPositiveButton("Close", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										// continue with delete
									}
								}).show();
					}
				});

			}

			@Override
			public void cancelRequest() {
				// TODO Auto-generated method stub

			}
		};
		request.url = requestUrl;
		RequestBackgroundWorker.queueRequest(request);
		mProgressDialogManager.showProgressDialog("Loading", "Getting data, please waiting a moment");
	}

}
