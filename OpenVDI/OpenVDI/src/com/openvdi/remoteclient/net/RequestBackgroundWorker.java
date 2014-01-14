package com.openvdi.remoteclient.net;

import android.util.Log;

public class RequestBackgroundWorker extends Thread {
	public static final String TAG = "RequestBackgroundWorker";

	private static PriorityRequestQueue queueRequest = null;
	private volatile static RequestBackgroundWorker worker = null;

	private volatile boolean running = true;

	public static void startWaitingForRequest()
	{
		if(worker == null)
		{
			synchronized (RequestBackgroundWorker.class) {
				if (worker == null) {
					worker = new RequestBackgroundWorker();
				}
			}
		}
	}

	private RequestBackgroundWorker()
	{
		super("RequestBackgroundWorker");
		queueRequest = new PriorityRequestQueue();
		start();
	}

	public static void queueRequest(Request request)
	{
		if (worker != null)
		{
			synchronized (worker)
			{
				queueRequest.addRequest(request);
				worker.notify();
			}
		}

	}

	public static void queueRequestFirst(Request request)
	{
		if (worker != null)
		{
			synchronized (worker) {
				queueRequest.addRequestFirst(request);
				worker.notify();
			}
		}
	}

	@Override
	public void run()
	{
		Log.w(TAG, "Start RequestBackgroundWorker");

		while (running)
		{
			try
			{
				synchronized (this)
				{
					if (queueRequest.isEmpty())
					{
						Log.w(TAG, "Waiting for new requests...");
						try
						{
							wait();
						}
						catch (Exception e) {}
					}
				}

				if (!running) {
					break;
				}

				Request request = queueRequest.getFirst();
				if (request != null) {
					Log.w(TAG, "Process request " + request.url);

					//If retry, waiting 2s
//					Thread.sleep(request.getRetryCount() * 2000);
					request.runRequest();
					
					request.setRetryCount(request.getRetryCount() - 1);
				}

				try
				{
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Log.w(TAG, "Stop RequestBackgroundWorker");
		worker = null;
	}

	public static void stopThread()
	{
		if (worker != null) {
			Log.w(TAG, "Try to stop RequestBackgroundWorker");
			synchronized (worker) {
				queueRequest.clear();
				worker.running = false;
				worker.notify();
			}

			if (worker != null) {
				worker.interrupt();
			}
		}
	}

	public static void clearOldRequests()
	{
		if (worker != null) {
			Log.w(TAG, "Clear old requests in RequestBackgroundWorker");
			synchronized (worker) {
				queueRequest.clear();
				worker.notify();
			}
		}
	}
}
