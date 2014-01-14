package com.openvdi.remoteclient.fragments;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.openvdi.remoteclient.activities.ConnectActivity;
import com.openvdi.remoteclient.activities.DomainActivity;
import com.openvdi.remoteclient.activities.GlobalData;
import com.openvdi.remoteclient.models.Pool;
import com.openvdi.remoteclient.models.Server;
import com.openvdi.remoteclient.net.ProgressDialogManager;
import com.openvdi.remoteclient.net.RequestBackgroundWorker;
import com.openvdi.remoteclient.net.UrlHelper;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class LoginBodyFragment extends Fragment {
	private Button btnLogin;
	private Fragment fragment;
	private EditText etxtDomain;
	private EditText loginUsername;
	private EditText loginPassword;

	ProgressDialogManager mProgressDialogManager;
	private final Server server = new Server();
	public static final int CODE_CHOOSE_DOMAIN = 110;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// inflate fragment layout
		RelativeLayout theLayout = (RelativeLayout) inflater.inflate(R.layout.login_body, container, false);
		return theLayout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null && resultCode == CODE_CHOOSE_DOMAIN) {

			String id = data.getStringExtra("server_id");
			String name = data.getStringExtra("server_name");

			server.id = id;
			server.name = name;

			etxtDomain.setText(server.name);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mProgressDialogManager = new ProgressDialogManager(getActivity());
		// get fragment
		fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.login_body_fragment);
		Fragment fragmentHeader = (Fragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.login_header_fragment);

		// get edittext Domain
		etxtDomain = (EditText) fragment.getView().findViewById(R.id.login_domain);
		Button btnLoginRefresh = (Button)fragmentHeader.getView().findViewById(R.id.btnLoginRefresh);
		if(btnLoginRefresh != null) {
			Log.d("onActivityCreated > ","onActivityCreated Click");
			btnLoginRefresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					Log.d("onActivityCreated > ","onActivityCreated Click >>>");
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mProgressDialogManager.showProgressDialog("Loading", "Refresh...please waiting a moment");
						}
					});
					
					
					new Thread(new Runnable() {
						public void run() {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							getActivity().runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									mProgressDialogManager.dismiss();
								}
							});
							
						}
					}).start();
					
				}
			});
		}
		
		if(GlobalData.DOMAIN_LIST != null && !GlobalData.DOMAIN_LIST.isEmpty()) {
			Server s = GlobalData.DOMAIN_LIST.get(0);
			server.id = s.id;
			server.name = s.name;
			
			etxtDomain.setText(server.name);
		}
		
		loginUsername = (EditText) fragment.getView().findViewById(R.id.login_username);
		loginPassword = (EditText) fragment.getView().findViewById(R.id.login_password);

		loginUsername.setText("jyang");
		loginPassword.setText("ca$hc0w!!!");

		// implement onClick event edittext Domain
		etxtDomain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open DomainActivity
				Intent intentDomain = new Intent(getActivity(), DomainActivity.class);
				startActivityForResult(intentDomain, CODE_CHOOSE_DOMAIN);
			}
		});

		// get btnLogin
		btnLogin = (Button) fragment.getView().findViewById(R.id.btnLogin);

		// implement onClick event btnLogin
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String user = loginUsername.getText().toString().trim();
				String pass = loginPassword.getText().toString().trim();
				String domainExt = etxtDomain.getText().toString().trim();
				if (user.length() == 0) {
					Toast toast = Toast.makeText(getActivity(), "Please enter user name", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				if (pass.length() == 0) {
					Toast toast = Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				if (domainExt.length() == 0) {
					Toast toast = Toast.makeText(getActivity(), "Please choose domain", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				user = URLEncoder.encode(user);
				pass = URLEncoder.encode(pass);
				// Open ConnectActivity
				doLogin(user, pass, server.id, domainExt);
			}
		});
	}

	void doLogin(final String user, final String pass, final String pDal, final String domainExt) {
		String requestUrl = UrlHelper.getLoginUrl(GlobalData.DOMAIN, user, pass, pDal);
		Log.d("requestUrl", "" + requestUrl);
		com.openvdi.remoteclient.net.Request request = new com.openvdi.remoteclient.net.Request() {

			@Override
			public void runRequest() {
				send();
			}

			@Override
			public void requestFinished(JSONObject jsonObject) {
				Log.d("Request data", "" + jsonObject);

				GlobalData.joinDomain = domainExt;
				GlobalData.password = pass;
				GlobalData.pDal = pDal;
				GlobalData.userName = user;

				try {

					JSONArray pools = jsonObject.getJSONObject("data").optJSONArray("pools");
					if (pools != null) {
						GlobalData.userId =  jsonObject.getJSONObject("data").has("userid") ?  jsonObject.getJSONObject("data").getString("userid") : "";
						Log.d("Add POOL User Id", GlobalData.userId);
						for (int i = 0, n = pools.length(); i < n; i++) {
							JSONObject child = pools.getJSONObject(i);
							Pool pool = new Pool();
							pool.id = child.has("id") ? child.getString("id") : "";
							pool.ready = child.has("ready") ? child.getBoolean("ready") : false;
							pool.name = child.has("name") ? child.getString("name") : "";
							Log.d("Add POOL ", pool.name);
							GlobalData.addPool(pool);
						}
					}
					mProgressDialogManager.dismiss();

					if (jsonObject != null && jsonObject.has("data")) {
						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mProgressDialogManager.dismiss();
								Intent intentConnect = new Intent(getActivity(), ConnectActivity.class);
								startActivity(intentConnect);
							}
						});
					}
				} catch (JSONException e) {
					mProgressDialogManager.dismiss();
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(getActivity()).setTitle("Error").setMessage("Can not connect to your domain with your info, please try again!")
									.setPositiveButton("Close", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// continue with delete
										}
									}).show();
						}
					});

				}

			}

			@Override
			public void requestFailed(int errorCode, String message) {
				// TODO Auto-generated method stub
				mProgressDialogManager.dismiss();
				Log.i("", "errorCode > " + errorCode);
			}

			@Override
			public void cancelRequest() {
				// TODO Auto-generated method stub

			}
		};
		request.url = requestUrl;
		RequestBackgroundWorker.queueRequest(request);
		mProgressDialogManager.showProgressDialog("Loading", "Logging...please waiting a moment");
	}

}
