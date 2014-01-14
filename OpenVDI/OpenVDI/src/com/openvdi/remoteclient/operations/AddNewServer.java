package com.openvdi.remoteclient.operations;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author Tony Hoang </br> Date : 9/8/2013
 */
public class AddNewServer {
	
	public static final String KEY_TOPIC_DEFAULT = "topic-default";
	public static final String KEY_TOPIC_DEFAULT_NAME = "topic-default-name";
	public static boolean saveServer(String server, Context context) {
		Editor editor = context.getSharedPreferences(KEY_TOPIC_DEFAULT, Context.MODE_PRIVATE).edit();
		String re = restoreServerName(context);
		if(re.length() > 0){
			if(re.indexOf(server) == -1)
				server = re + "," + server;
			else {
				return false;
			}
		}
		editor.putString(KEY_TOPIC_DEFAULT_NAME, server);
		return editor.commit();
	}

	public static String restoreServerName(Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(KEY_TOPIC_DEFAULT, Context.MODE_PRIVATE);
		return (savedSession.getString(KEY_TOPIC_DEFAULT_NAME, ""));
	}
}
