package com.openvdi.remoteclient.net;

public interface UploadListener {
	public void onDataProcessed(Object entity);
	public void onErrorData(String errorMessage);
	public void progress(long l, String des);

}
