package com.parivartree.helpers;

import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientSingleTon extends DefaultHttpClient {

	public static HttpClientSingleTon httpclient = null;
	private static HttpParams httpParams = null;
	
	private HttpClientSingleTon() {
		super();
	}

	public static synchronized HttpClientSingleTon getInstance() {
		if (httpclient == null) {
			httpclient = new HttpClientSingleTon();
		}
		List<Cookie> mCookies = null;
		
		mCookies = HttpClientSingleTon.httpclient.getCookieStore().getCookies();
		int cookieLength = mCookies.size();
		
		return httpclient;
	}

}
