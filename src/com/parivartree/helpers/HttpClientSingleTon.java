package com.parivartree.helpers;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientSingleTon extends DefaultHttpClient {

	public static HttpClientSingleTon httpclient = null;

	private HttpClientSingleTon() {
	}

	public static synchronized HttpClientSingleTon getInstance() {
		if (httpclient == null) {
			httpclient = new HttpClientSingleTon();
		}
		List<Cookie> mCookies = null;
		// client.addHeader("Cookie", "PHPSESSID=rcd7s8h59o632mfie1qd4hjpt6");
		mCookies = HttpClientSingleTon.httpclient.getCookieStore().getCookies();
		int cookieLength = mCookies.size();

		// if (mCookies.isEmpty()) {
		// Log.d("test_runner", "Cookies: None");
		// } else {
		// for (int cookieCount = 0;cookieCount < cookieLength; cookieCount++) {
		// Log.i("SingleTonn", "Cookies: [" + cookieCount + "]" +
		// mCookies.get(cookieCount ).toString());
		// }
		// }
		// Log.d("test_runner", "Cookies: [" +1+ "]" +
		// mCookies.get(2).toString());

		return httpclient;
	}

}
