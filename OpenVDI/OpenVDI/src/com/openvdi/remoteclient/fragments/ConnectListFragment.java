package com.openvdi.remoteclient.fragments;

import java.net.URLDecoder;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.freerdp.freerdpcore.application.GlobalApp;
import com.freerdp.freerdpcore.domain.BookmarkBase;
import com.freerdp.freerdpcore.domain.ConnectionReference;
import com.freerdp.freerdpcore.domain.ManualBookmark;
import com.freerdp.freerdpcore.presentation.SessionActivity;
import com.openvdi.remoteclient.R;
import com.openvdi.remoteclient.activities.GlobalData;
import com.openvdi.remoteclient.models.OpenVdiGateway;
import com.openvdi.remoteclient.models.Pool;
import com.openvdi.remoteclient.net.ProgressDialogManager;
import com.openvdi.remoteclient.net.Request;
import com.openvdi.remoteclient.net.RequestBackgroundWorker;
import com.openvdi.remoteclient.net.UrlHelper;

/**
 * @author Tony Hoang </br>Date : 8/8/2013
 */
public class ConnectListFragment extends Fragment {
	private static BookmarkBase bookmark = null;
	private static String TAG = "ConnectListFragment";
	ListView listView;

	ProgressDialogManager mProgressDialogManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mProgressDialogManager = new ProgressDialogManager(getActivity());
		// inflate fragment layout
		LinearLayout theLayout = (LinearLayout) inflater.inflate(R.layout.connect_list, container, false);
		listView = (ListView) theLayout.findViewById(R.id.connect_listview);
		return theLayout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// TODO: request server Nimdesk get Pool list
		String[] values = new String[] {};

		List<Pool> pool = GlobalData.POOL_LIST;
		if (pool != null) {
			values = new String[pool.size()];
			for (int i = 0, n = pool.size(); i < n; i++) {
				Log.d("ADD Show ", pool.get(i).name);
				values[i] = pool.get(i).name;
			}
		}

		// set layout and values to adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.connect_item, values);

		// set listview adapter
		listView.setAdapter(adapter);

		// set listener onItemClick
		listView.setOnItemClickListener(itemListener);
	}

	/**
	 * implement onItemClick event
	 */
	private final OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Pool item = GlobalData.POOL_LIST.get(position);

			Log.d("CLick", item.name);
			doGetRequestId(item);
		}
	};

	void doGetRequestId(Pool item) {
		String url = UrlHelper.getRequestIDUrl(GlobalData.DOMAIN, GlobalData.userId, GlobalData.joinDomain, item.id);
		Request request = new Request() {

			@Override
			public void runRequest() {
				send();
			}

			@Override
			public void requestFinished(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
			}

			@Override
			public void requestFailed(int errorCode, String message) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
			}

			@Override
			public void cancelRequest() {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
			}

			@Override
			protected void processDataNotJson(Object data) {
				// TODO Auto-generated method stub
				if (data != null) {
					Log.d("Data not json", "> " + data);
					String requestData = data.toString();

					if (requestData.indexOf("=") != -1) {
						String[] rs = requestData.split("=");
						if (rs.length > 1) {
							// String requestId =
							// URLEncoder.encode(rs[1].trim());

							String requestId = rs[1].trim();
							Log.d("Data not json", "> requestId = " + requestId);
							GlobalData.requestId = requestId;
							doGetGateWay(requestId);
						}
					}
				}
			}
		};
		Log.d("Request", "url > " + url);
		request.url = url;
		RequestBackgroundWorker.queueRequest(request);
		mProgressDialogManager.showProgressDialog("Loading", "Connecting...please wait");
	}

	String getData(String input) {
		if (input != null) {
			if (input.indexOf("=") != -1) {
				String[] rs = input.split("=");
				if (rs.length > 1) {
					return rs[1].trim();
				}
			}
		}
		return "";
	}

	void doGetGateWay(String requestId) {
		String url = UrlHelper.getGateWay(GlobalData.DOMAIN, requestId);
		Request request = new Request() {

			@Override
			public void runRequest() {
				// TODO Auto-generated method stub
				send();
			}

			@Override
			public void requestFinished(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
				Log.d("Requst Data Gateway", "> " + jsonObject);
			}

			@Override
			public void requestFailed(int errorCode, String message) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
			}

			@Override
			protected void processDataNotJson(Object data) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
				// error=No desktop is available. Please contact the
				// administrator.
				if (data != null) {
					String requestData = data.toString();
					if (requestData.indexOf("error") != -1) {
						if (getRetryCount() > 0) {
							setRetryCount(getRetryCount() - 1);
							RequestBackgroundWorker.queueRequest(this);
						} else {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									new AlertDialog.Builder(getActivity()).setTitle("Error").setMessage("No desktop is available. Please contact the administrator.")
											.setPositiveButton("Close", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// continue with delete
												}
											}).show();
								}
							});
						}
					} else {
						OpenVdiGateway gateway = new OpenVdiGateway();
						String[] rs = requestData.split("\n");
						for (int i = 0; i < rs.length; i++) {
							String string = rs[i].trim();
							if (string.startsWith("ip")) {
								gateway.ip = getData(string);
							} else if (string.startsWith("port")) {
								gateway.port = getData(string);
							} else if (string.startsWith("os")) {
								gateway.os = getData(string);
							} else if (string.startsWith("redirects")) {
								gateway.redirects = getData(string);
							} else if (string.startsWith("protocols")) {
								gateway.protocols = getData(string);
							}
						}
						doConnect(gateway);
					}
				}

			}

			@Override
			public void cancelRequest() {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mProgressDialogManager.dismiss();
					}
				});
			}
		};
		Log.d("Request", "url > " + url);
		request.url = url;
		RequestBackgroundWorker.queueRequest(request);
	}

	void doConnect(OpenVdiGateway gateway) {
		String refStr = ConnectionReference.PATH_MANUAL_BOOKMARK_ID + "2";
		bookmark = GlobalApp.getManualBookmarkGateway().findById(ConnectionReference.getManualBookmarkId(refStr));

		boolean isNewBookmark = false;
		if (bookmark == null) {
			bookmark = new ManualBookmark();
			isNewBookmark = true;
		}

		bookmark.<ManualBookmark> get().setLabel("Nimdesk");
		bookmark.<ManualBookmark> get().setHostname(gateway.ip);
		if (!gateway.port.isEmpty()) {
			int port = 0;
			try {
				port = Integer.parseInt(gateway.port);
			} catch (NumberFormatException e) {
				Log.e(TAG, e.getMessage());
			}
			if (port > 0) {
				bookmark.<ManualBookmark> get().setPort(port);
			}
		}
		bookmark.<ManualBookmark> get().setDomain(GlobalData.joinDomain);
		bookmark.<ManualBookmark> get().setUsername(GlobalData.userName);
		bookmark.<ManualBookmark> get().setPassword(URLDecoder.decode(GlobalData.password));

		Log.d(TAG, gateway.toSimpleString());

		if (isNewBookmark) {
			GlobalApp.getManualBookmarkGateway().insert(bookmark);
		} else {
			GlobalApp.getManualBookmarkGateway().update(bookmark);
		}

		Bundle bundle = new Bundle();
		bundle.putString(SessionActivity.PARAM_CONNECTION_REFERENCE, refStr);

		Intent sessionIntent = new Intent(getActivity(), SessionActivity.class);
		sessionIntent.putExtras(bundle);
		startActivity(sessionIntent);
	}
}