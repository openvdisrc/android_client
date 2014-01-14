package com.openvdi.remoteclient.net;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class UrlHelper {

	public static final String live = "Insert a domain";
	public static final String base = live;

	public static final String API_KEY = "074dfba7efe5fde2f0e8037b60c507e4";
	public static final String SECRET_KEY = "f5c15582da0f70723246729b97768bdc";

	public static final String START_SESSION_URL = base + "/api/upload/startSession?";
	public static final String FINISH_SESSION_URL = base + "/api/upload/finishSession?";
	public static final String DO_UPLOAD_METALDATA_URL = base + "/api/upload/doUploadMeta?";
	public static final String DO_UPLOAD_FILE = base + "/api/upload/doUploadFile?";

	public static final String UPDATE_SESSION_URL = base + "/api/upload/updateSession?";
	public static final String DO_UPDATE_METALDATA_URL = base + "/api/upload/doUpdateUploadMeta?";
	public static final String DO_DELETE_RECORD = base + "/api/upload/doDeleteRecord?";

	private static List<String> list = new LinkedList<String>();
	
	public static String getDomainUrl(String domain){
		String url = domain;
		if(domain.endsWith("/")){
			url = domain.subSequence(0, domain.length() - 1).toString();
		} else {
			url = domain + "/dp/rpc/dc/getdomains?fmt=json";
		}
		
		return url;
	}
//	http://rdp.nimdesk.com/dp/rpc/dc/connect?request=f7c801a0-3e07-11e3-8e2f-005056b04546
	public static String getGateWay(String domain, String requestId){
		StringBuffer url = new StringBuffer(domain);
		url.append("/dp/rpc/dc/connect?request=");
		url.append(requestId);
		
		return url.toString();
	}
	
//	http://rdp.nimdesk.com/dp/rpc/dc/request?user=e746c840-2348-11e3-86e7-005056b03af8&domain=nimspace.local&pool=29365e10-23f7-11e3-9db6-005056b03af8

	public static String getRequestIDUrl(String domain, String userId, String domianConect, String poolId){
		StringBuffer url = new StringBuffer(domain);
		url.append("/dp/rpc/dc/request?user=");
		url.append(userId);
		url.append("&domain=").append(domianConect);
		url.append("&pool=").append(poolId);
		
		return url.toString();
	}
	
	public static String getLoginUrl(String domain, String user, String pass, String lDap){
//http://rdp.nimdesk.com/dp/rpc/dc/login?fmt=json&user=jyang&ldap=f9db6740-223a-11e3-bfe7-005056b04546&pass=ca%24hc0w%21%21%21
		StringBuffer url = new StringBuffer(domain);
		url.append("/dp/rpc/dc/login?fmt=json&user=");
		url.append(user);
		url.append("&ldap=");
		url.append(lDap);
		url.append("&pass=");
		url.append(pass);
		
		return url.toString();
	}

	public static List<NameValuePair> getUrlOfParams(String[] params, String[] values) {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.length);
		for(int i = 0 , n = params.length ; i < n ;i++){
			Log.i("", params[i] + "-" +values[i]);
			nameValuePairs.add(new BasicNameValuePair(params[i],values[i]));
		}
		nameValuePairs.add(new BasicNameValuePair("api_key",API_KEY));
		nameValuePairs.add(new BasicNameValuePair("sig",getSig(params, values)));
	    return nameValuePairs;
	}

	static String getSig(String[] params, String[] values){
		//build sig
		List<String> paramsList = new LinkedList<String>();
		int len = params.length;
		for (int i = 0; i < len; ++i) {
			paramsList.add(params[i] + "=" + values[i]);
		}
		Collections.sort(paramsList);
		paramsList.add(SECRET_KEY);

		StringBuilder builder = new StringBuilder();
		for (String str : paramsList) {
			builder.append(str);
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String getUrlOfMethod(String root, String methodName, String[] params, String[] values) {
		try {
			//build sig
			List<String> paramsList = new LinkedList<String>();
			int len = params.length;
			for (int i = 0; i < len; ++i) {
				paramsList.add(params[i] + "=" + values[i]);
			}
			Collections.sort(paramsList);
			paramsList.add(SECRET_KEY);

			StringBuilder builder = new StringBuilder();
			for (String str : paramsList) {
				builder.append(str);
			}

			//build url
			StringBuffer urlBuild = new StringBuffer();
			urlBuild.append(root);
			urlBuild.append("api_key=" + API_KEY);
			for (int i = 0; i < len; ++i) {
				urlBuild.append("&" + params[i] + "=" + URLEncoder.encode(values[i]));
			}
			return urlBuild.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}

	public static List<NameValuePair> getValueOfMethodPost(String root, String methodName, String[] params, String[] values) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		try {
			nameValuePairs.clear();

			nameValuePairs.add(new BasicNameValuePair("method", methodName));
			nameValuePairs.add(new BasicNameValuePair("api_key", API_KEY));

			int len = params.length;
			for (int i = 0; i < len; ++i) {
				nameValuePairs.add(new BasicNameValuePair(params[i], values[i]));
			}

			StringBuilder builder = new StringBuilder();
			String temp;

			list.clear();

			builder.append(root);
			temp = "method=" + methodName;
			list.add(temp);
			builder.append(temp);

			temp = "api_key=" + API_KEY;
			list.add(temp);
			if (!methodName.equals("")) {
				builder.append("&" + temp);
			} else {
				builder.append(temp);
			}

			for (int i = 0; i < len; ++i) {
				list.add(params[i] + "=" + values[i]);
				builder.append("&" + params[i] + "=" + values[i]);
			}

			// String url = builder.toString();

			Collections.sort(list);
			builder = new StringBuilder();
			for (String str : list) {
				builder.append(str);
			}
			builder.append(SECRET_KEY);

			nameValuePairs.add(new BasicNameValuePair("sig", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nameValuePairs;
	}

}
