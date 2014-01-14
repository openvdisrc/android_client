
package com.openvdi.remoteclient.net;

import org.json.JSONObject;

public interface RequestListener {
	public void onRequestComplete(JSONObject data, Request request);
	public void onRequestFailed(int errorCode,Request request);
}
