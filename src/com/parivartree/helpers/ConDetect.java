package com.parivartree.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConDetect {
	private static Context context;

	public ConDetect(Context context) {
		this.context = context;
	}

	public boolean isOnline() {
		try {

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				Log.d("ConDetect", "boolean value : " + netInfo.isConnectedOrConnecting());
				return true;
			}
		} catch (Exception e) {
			for (StackTraceElement tempStack : e.getStackTrace()) {
				// Log.d("Exception thrown: Treeview Fetch", "" +
				// tempStack.getLineNumber());
				Log.d("Exception thrown: joinees",
						"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
								+ tempStack.getMethodName());
			}
		}
		return false;

	}
}
