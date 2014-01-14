package com.openvdi.remoteclient.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;


public class ProgressDialogManager implements IOnDestroy {

	private int count;
	private ProgressDialog mProgressDialog;
	private Context mContext;

	public ProgressDialogManager(Context context) {
		mContext = context;
	}

	public synchronized void showProgressDialog(String title, String message) {
		++count;
		if (mProgressDialog == null) {
			mProgressDialog = ProgressDialog.show(mContext, title, message);
		} else {
			mProgressDialog.setTitle(title);
			mProgressDialog.setMessage(message);
		}
	}

	public synchronized void showProgressDialog() {
		++count;
		if (mProgressDialog == null && !((Activity) mContext).isFinishing()) {
			mProgressDialog = ProgressDialog.show(mContext, "", "Processing...");
		}
	}

	public synchronized void dismiss() {
		--count;
		if (count == 0 && mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void onDestroy() {
		count = 0;
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
}