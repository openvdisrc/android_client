package com.openvdi.remoteclient.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class Request {
	public static final String TAG = "Request";

	public static final int GET = 0;
	public static final int POST = 4;

	public static final int PRIORITY_CRITICAL = 10;
	public static final int PRIORITY_HIGH = 9;
	public static final int PRIORITY_NORMAL = 8;
	public static final int PRIORITY_LOW = 7;
	public static final int PRIORITY_REDUNDANT = 6;

	private int priority;
	private static int numOfRequests = 0;

	protected int errorCode;
	protected String errorMessage;

	public static final int ENTRY_NULL = 1;
	public static final int NETWORK_ERROR = 2;
	public static final int COMMON_ERROR = 3;
	public static final int NO_UPDATES = -9;

	public static final int ERROR_ITEM_NOT_FOUND = 110;
	public static final int ERROR_ITEM_ALREADY_EXISTS = 109;

	public String url;
	public int type = GET;
	public long id;
	public long requestId;
	private int retryCount = 5;

	public void setRequestId(long id) {
		this.requestId = id;
	}

	public long getRequestId() {
		return requestId;
	}

	@Override
	public boolean equals(Object o) {
		Request other = (Request) o;
		if (other != null && other.url != null)
			return url.equals(other.url);
		else
			return false;
	}

	public static String AGENT = "User-Agent";
	public static String FIREFOX = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)";
	public static int CONNECTION_TIMEOUT = 30000;
	public static int SOCKET_TIMEOUT = 30000;
	public static int NOTIFICATION_SOCKET_TIMEOUT = 30000;
	public static int SOCKET_BUFFER_SIZE = 8192;

	private static HttpParams httpParameters;

	static {
		httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
		HttpParams notificationHttpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(notificationHttpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(notificationHttpParameters, NOTIFICATION_SOCKET_TIMEOUT);
		HttpConnectionParams.setSocketBufferSize(httpParameters, SOCKET_BUFFER_SIZE);
		HttpConnectionParams.setTcpNoDelay(httpParameters, true);
	}

	public void send() {
		this.id = ++numOfRequests;

		switch (type) {
		case GET:
			get();
			break;

		case POST:
			post();
			break;
		}
	}
	
	public int getErrorCode(){
		return this.errorCode;
	}

	public void processDataReturn(Object data) throws JSONException {
		if (data instanceof JSONObject) {
			JSONObject json = (JSONObject) data;
			if (json.has("error_code")) {
				errorCode = json.getInt("error_code");
				//Log.d(TAG, "error code = " + errorCode);
				if (errorCode != 0) {
					processErrorCode(errorCode, null);
					return;
				} else {
					requestFinished(json);
				}
			} else {
				requestFinished(json);
			}
		} else {
			requestFinished(null);
		}

	}

	private void processErrorCode(int errorCode, String messageContent) {
		String message = null;
		if (messageContent != null) {
			message = messageContent;
		}
		switch (errorCode) {
		case ENTRY_NULL:
			if (message == null) {
				message = "Empty";
			}
			break;
		case NETWORK_ERROR:
			if (message == null) {
				message = "network error";
			}
			break;
		case COMMON_ERROR:
			if (message == null) {
				message = "unknow error";
			}
		default : message = "";
			break;
		}
		this.errorCode = errorCode;
		this.errorMessage = message.trim().equals("") ? "Empty" : message.trim();
		requestFailed(errorCode, message);
	}

	public List<NameValuePair> nameValuePairs;
	private void post() {
		//Log.i("post", "> do post");
		HttpClient httpClientPost = new DefaultHttpClient();
		synchronized (httpClientPost)
		{

			//Log.i("post", "> do post "+url);
			HttpPost httpPost = new HttpPost(url);

			HttpResponse response = null;
			HttpEntity entity = null;
			String jsonData = null;
			InputStream instream = null;

			try {
				//Log.i("post", "> setEntity");
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
				//Log.i("post", "> setEntity done - pre execute");
				response = httpClientPost.execute(httpPost);
				//Log.i("post", "> execute");
				entity = response.getEntity();
				//Log.i("post", "> executed......");
				if (entity != null) {
					//Log.i("post", "> entri 1 in");
					instream = entity.getContent();
					//Log.i("post", "> entri 2 in");
					jsonData = convertStreamToString(instream);
//Log.i("post", "> "+jsonData);
					JSONObject json = new JSONObject(jsonData);

					// process response data
					switch (type) {
					case POST:
					case GET:
						processDataReturn(json);
						break;
					}
				}
				else
				{
					//Log.i("post", "> entri null");
					processErrorCode(-1, "Unknow error");
				}
			} catch (JSONException e) {
				//Log.i("post", "> "+e.getMessage());
			} catch (ClientProtocolException e) {
				//Log.i("post", "> "+e.getMessage());
			} catch (UnknownHostException e) {
				//Log.i("post", "> "+e.getMessage());
			} catch (IOException e) {
				//Log.i("post", "> "+e.getMessage());
			} catch (Exception e) {
				//Log.i("post", "> "+e.getMessage());
			} finally {
				httpClientPost.getConnectionManager().shutdown();
			}
		}
	}

	public static final int MESSAGES_ID = 2;
	public static byte[] getByteInputStream(String url) {

		// HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpClient httpClient = new DefaultHttpClient();
		synchronized (httpClient) {

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
			HttpConnectionParams.setSocketBufferSize(httpParameters, SOCKET_BUFFER_SIZE);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);

			HttpGet httpGet = new HttpGet(url);
			httpGet.setParams(httpParameters);

			HttpResponse response = null;
			InputStream is = null;
			try {
				httpGet.setHeader("User-Agent", System.getProperties().getProperty("http.agent"));

				httpGet.setHeader("Accept-Encoding", "gzip, deflate");

				response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

				// this is storage overwritten on each iteration with bytes
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];

				// we need to know how may bytes were read to write them to the
				// byteBuffer
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					byteBuffer.write(buffer, 0, len);
				}

				// and then we can return your byte array.
				return byteBuffer.toByteArray();

			} catch (Exception e) {
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				httpClient.getConnectionManager().shutdown();
			}
		}
		return null;
	}
	
	protected void processDataNotJson(Object data) {
		
	}
	
	public boolean isJson(String data){
		try {
			JSONObject jsonObject = new JSONObject(data);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void get() {

		// HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpClient httpClient = new DefaultHttpClient();

		//Log.d(TAG, "Start request " + id);
		//Log.d(TAG, "URL = " + url);
		synchronized (httpClient) {

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
			HttpConnectionParams.setSocketBufferSize(httpParameters, SOCKET_BUFFER_SIZE);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);

			HttpGet httpGet = new HttpGet(url);
			httpGet.setParams(httpParameters);

			HttpResponse response = null;
			String jsonData = null;
			try {
				httpGet.setHeader("User-Agent", System.getProperties().getProperty("http.agent"));

				httpGet.setHeader("Accept-Encoding", "gzip, deflate");

				response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				if (response != null) {

					//Log.d(TAG, "DONE request");
					jsonData = convertStreamToString(is);
					if(!isJson(jsonData)){
						processDataNotJson(jsonData);
						return;
					}
					//Log.d(TAG, "DONE request > "+jsonData);
						try {
							JSONObject json = new JSONObject(jsonData);
							//Log.d(TAG, "Response JSON of request " + id + " = " + jsonData);
							// process response data
							switch (type) {
							case GET:
								processDataReturn(json);
								break;
							}
						} catch (JSONException ex) {

							JSONArray json = new JSONArray(jsonData);
							//Log.d(TAG, "Response JSON of request " + id + " = " + jsonData);

							// process response data
							switch (type) {
							case GET:
								processDataReturn(json);
								break;
							}
						}

					
				}
			} catch (JSONException e) {
				processErrorCode(NETWORK_ERROR, null);
				//Log.w(TAG, "Request " + id + " failed");
			} catch (ClientProtocolException e) {
				processErrorCode(NETWORK_ERROR, null);
				//Log.w(TAG, "Request " + id + " failed");
			} catch (IOException e) {
				processErrorCode(COMMON_ERROR, e.getMessage());
				//Log.w(TAG, "Request " + id + " failed");
			} catch (Exception e) {
				e.printStackTrace();
				processErrorCode(COMMON_ERROR, e.getMessage());
				//Log.w(TAG, "Request " + id + " failed");
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
		System.gc();
	}

	int tryAgainNumer = 0;

	public void decreaseTryAgainNumber() {
		tryAgainNumer += 1;
	}

	public abstract void cancelRequest();

	public abstract void runRequest();

	public abstract void requestFinished(JSONObject jsonObject);

	public abstract void requestFailed(int errorCode, String message);

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
}
