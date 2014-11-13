package com.parivartree.helpers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.parivartree.models.UserProfile;

public class HttpConnectionUtils {

	private static String TAG = "HttpConnecntionUtils";
	private static int TIMEOUT = 10000;

	public static String getProfileResponse(String userId, String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		// HttpPost httpPost = new HttpPost(url);
		// HttpGet httpGet = new HttpGet(url);

		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userid", userId));

		String paramsString = URLEncodedUtils.format(nameValuePair, "UTF-8");
		HttpGet httpGet = new HttpGet(url + "?" + paramsString);
		Log.d(TAG, "Profile - " + url + "?" + paramsString);

		HttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}

	public static String getCommunityResponse(String religionId, String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		// HttpPost httpPost = new HttpPost(url);
		// HttpGet httpGet = new HttpGet(url);

		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("religionid", religionId));

		String paramsString = URLEncodedUtils.format(nameValuePair, "UTF-8");
		HttpGet httpGet = new HttpGet(url + "?" + paramsString);
		Log.d(TAG, "Profile - " + url + "?" + paramsString);


		HttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}
	
	
	// above two are get requests

//		nameValuePair.add(new BasicNameValuePair("email", userName));
//		nameValuePair.add(new BasicNameValuePair("pass", password));
//
//		return processHTTPPostExecution(url, nameValuePair);
//	}
	public static String getLoginResponse(String userName, String password, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("email", userName));
		nameValuePair.add(new BasicNameValuePair("password", password));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getForgotPasswordResponse(String email, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("email", email));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getSignupResponse(String email, String firstname, String lastname, String gender, String locality, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair("email", email));
		nameValuePair.add(new BasicNameValuePair("firstname", firstname));
		nameValuePair.add(new BasicNameValuePair("lastname", lastname));
		nameValuePair.add(new BasicNameValuePair("gender", gender));
		nameValuePair.add(new BasicNameValuePair("locality", locality));

		return processHTTPPostExecution(url, nameValuePair);
	}

	// not calling processHTTP
	public static String getEditProfileResponse(UserProfile userProfile, String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		HttpPost httpPost = new HttpPost(url);
		
		BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    ((DefaultHttpClient) httpClient).setParams(httpParams);
		
	      
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userid", userProfile.getUserid()));
		nameValuePair.add(new BasicNameValuePair("uid", userProfile.getUid()));
		nameValuePair.add(new BasicNameValuePair("dob", userProfile.getDob()));
		nameValuePair.add(new BasicNameValuePair("firstname", userProfile.getFirstName()));
		nameValuePair.add(new BasicNameValuePair("lastname", userProfile.getLastName()));
		nameValuePair.add(new BasicNameValuePair("locality", userProfile.getLocality()));
		nameValuePair.add(new BasicNameValuePair("pin", userProfile.getPincode()));
		nameValuePair.add(new BasicNameValuePair("hometown", userProfile.getHometown()));
		nameValuePair.add(new BasicNameValuePair("mobile", userProfile.getMobile()));
		nameValuePair.add(new BasicNameValuePair("maritalstatus", userProfile.getMaritalStatus()));
		nameValuePair.add(new BasicNameValuePair("wedding_date", userProfile.getWeddingDate()));
		nameValuePair.add(new BasicNameValuePair("religion", userProfile.getReligion()));
		nameValuePair.add(new BasicNameValuePair("community", userProfile.getCommunity()));
		nameValuePair.add(new BasicNameValuePair("gothra", userProfile.getGothra()));
		nameValuePair.add(new BasicNameValuePair("profession", userProfile.getProfession()));
		if (userProfile.getAddCommunity() != null) {
			nameValuePair.add(new BasicNameValuePair("othercommunity", userProfile.getAddCommunity()));
		}
		if (userProfile.getAddGothra() != null) {
			nameValuePair.add(new BasicNameValuePair("othergothra", userProfile.getAddGothra()));
		}

		Log.d(TAG, "url -" + url);
		HttpResponse response = null;

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response = httpClient.execute(httpPost);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}

	// not calling processTTP
	public static String getReligionResponse(String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		HttpPost httpPost = new HttpPost(url);
		
		BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    ((DefaultHttpClient) httpClient).setParams(httpParams);
		
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

		Log.d(TAG, "url -" + url);
		HttpResponse response = null;

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response = httpClient.execute(httpPost);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}

	// not calling
	public static String getGothraResponse(String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		HttpPost httpPost = new HttpPost(url);
		
		BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    ((DefaultHttpClient) httpClient).setParams(httpParams);
	    
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

		Log.d(TAG, "url -" + url);
		HttpResponse response = null;

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response = httpClient.execute(httpPost);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}

	// not calling
	public static String getMyTreeResponse(String nodeid, String userid, String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		HttpPost httpPost = new HttpPost(url);
		
		BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    ((DefaultHttpClient) httpClient).setParams(httpParams);
	    
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("uid", userid));

		Log.d(TAG, "url -" + url);
		HttpResponse response = null;

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response = httpClient.execute(httpPost);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}
	
	public static String createNewRelationResponse(String uid, String nodeid, String relationId, String firstName,
			String lastName, String email, String gender, String sessionname, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(8);
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("relationid", relationId));
		nameValuePair.add(new BasicNameValuePair("firstname", firstName));
		nameValuePair.add(new BasicNameValuePair("lastname", lastName));
		nameValuePair.add(new BasicNameValuePair("email", email));
		nameValuePair.add(new BasicNameValuePair("gender", gender));
		nameValuePair.add(new BasicNameValuePair("sessionname", sessionname));


		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getCreateEventResponse(String uid, String eventdate, String event, String eventname,
			String eventdescription, String location, String reach, String timehour, String timemin, String yourname,
			String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(11);
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		nameValuePair.add(new BasicNameValuePair("eventdate", eventdate));
		nameValuePair.add(new BasicNameValuePair("event", event));
		nameValuePair.add(new BasicNameValuePair("eventname", eventname));
		nameValuePair.add(new BasicNameValuePair("eventdescription", eventdescription));
		nameValuePair.add(new BasicNameValuePair("location", location));
		nameValuePair.add(new BasicNameValuePair("reach", reach));
		nameValuePair.add(new BasicNameValuePair("timeone", timehour));
		nameValuePair.add(new BasicNameValuePair("timetwo", timemin));
		nameValuePair.add(new BasicNameValuePair("name", yourname));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getEditEventResponse(String eventid, String event, String eventname, String eventdate,
			String eventdescription, String location, String reach, String timehour, String timemin, String yourname,
			String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(11);
		nameValuePair.add(new BasicNameValuePair("id", eventid));
		nameValuePair.add(new BasicNameValuePair("event", event));
		nameValuePair.add(new BasicNameValuePair("eventname", eventname));
		nameValuePair.add(new BasicNameValuePair("eventdate", eventdate));
		nameValuePair.add(new BasicNameValuePair("eventdescription", eventdescription));
		nameValuePair.add(new BasicNameValuePair("location", location));
		nameValuePair.add(new BasicNameValuePair("reach", reach));
		nameValuePair.add(new BasicNameValuePair("timeone", timehour));
		nameValuePair.add(new BasicNameValuePair("timetwo", timemin));
		nameValuePair.add(new BasicNameValuePair("name", yourname));
		nameValuePair.add(new BasicNameValuePair("uid", uid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getEventListResponse(String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("uid", uid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String deleteEventResponse(String eventid, String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
		nameValuePair.add(new BasicNameValuePair("id", eventid));
		nameValuePair.add(new BasicNameValuePair("uid", uid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getJoinResponse(String eventid, String authorId, String userID, String name, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair("id", eventid));
		nameValuePair.add(new BasicNameValuePair("author_id", authorId));
		nameValuePair.add(new BasicNameValuePair("uid", userID));
		nameValuePair.add(new BasicNameValuePair("name", name));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String setPasswordResponse(String uid, String password, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		nameValuePair.add(new BasicNameValuePair("password", password));

		return processHTTPPostExecution(url, nameValuePair);
	}
	public static String changePasswordResponse(String sessionhash, String password, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		nameValuePair.add(new BasicNameValuePair("sessionhash", sessionhash));
		nameValuePair.add(new BasicNameValuePair("password", password));

		return processHTTPPostExecution(url, nameValuePair);
	}
	public static String getGeneralSettingresponse(String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getNotificationListResponse(String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("uid", uid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getSearchUserResponse(String key, String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("key", key));
		nameValuePair.add(new BasicNameValuePair("uid", uid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String createExistRelationResponse(String nodeid, String uid, String relationId, String sessionname,
			String sessionid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		nameValuePair.add(new BasicNameValuePair("relationid", relationId));
		nameValuePair.add(new BasicNameValuePair("sessionname", sessionname));
		nameValuePair.add(new BasicNameValuePair("sessionid", sessionid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String createOthersRelationResponse(String nodeid, String uid, String relationId, String name,
			String sessionname, String sessionid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		nameValuePair.add(new BasicNameValuePair("relationid", relationId));
		nameValuePair.add(new BasicNameValuePair("name", name));
		nameValuePair.add(new BasicNameValuePair("sessionname", sessionname));
		nameValuePair.add(new BasicNameValuePair("sessionid", sessionid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String InvitationAcceptResponse(String id, String notifid, String uid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
		nameValuePair.add(new BasicNameValuePair("id", id));
		nameValuePair.add(new BasicNameValuePair("notifid", notifid));
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		Log.d("HTTPPostExecution", "HTTPPostExecution  : " + nameValuePair);
		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String InvitationDeclineResponse(String id, String notifid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("id", id));
		nameValuePair.add(new BasicNameValuePair("notifid", notifid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getJoineesResponse(String eventid, String url) {
		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("eventid", eventid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getFacebookResponse(String email, String lastname, String gender, String firstname, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		nameValuePair.add(new BasicNameValuePair("email", email));
		nameValuePair.add(new BasicNameValuePair("lastname", lastname));
		nameValuePair.add(new BasicNameValuePair("gender", gender));
		nameValuePair.add(new BasicNameValuePair("firstname", firstname));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getDeleteUserResponse(String nodeid, String userid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("uid", userid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getDeceasedUserResponse(String userid, String nodeid, String date, String name, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(4);
		nameValuePair.add(new BasicNameValuePair("uid", userid));
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("date", date));
		nameValuePair.add(new BasicNameValuePair("name", name));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getHideUserResponse(String nodeid, String userid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("nodeid", nodeid));
		nameValuePair.add(new BasicNameValuePair("uid", userid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getMakeAliveResponse(String ownerid, String userid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userid", ownerid));
		nameValuePair.add(new BasicNameValuePair("uid", userid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getImageUploadResponse(String userid, String imageString, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("uid", userid));
		nameValuePair.add(new BasicNameValuePair("image_string", imageString));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getImmediateFamilyResponse(String url, String userid) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("uid", userid));

		return processHTTPPostExecution(url, nameValuePair);
	}

	public static String getFieldPrivacyResponse(String userid, String fieldname, String privacy, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("uid", userid));
		nameValuePair.add(new BasicNameValuePair("fieldname", fieldname));
		nameValuePair.add(new BasicNameValuePair("privacy", privacy));

		return processHTTPPostExecution(url, nameValuePair);
	}
	
	public static String getProfileViewResponse(String userid, String uid,String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userid", userid));
		nameValuePair.add(new BasicNameValuePair("uid", uid));
		return processHTTPPostExecution(url, nameValuePair);
	}public static String getMobileVerifyResponse(String userhash, String smscode,String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userhash", userhash));
		nameValuePair.add(new BasicNameValuePair("smscode", smscode));
		return processHTTPPostExecution(url, nameValuePair);
	}public static String getMobileVerifyProfileUpdateResponse(String userhash, String smscode,String mobileno, String userid, String url) {

		// Building post parameters key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		nameValuePair.add(new BasicNameValuePair("userhash", userhash));
		nameValuePair.add(new BasicNameValuePair("smscode", smscode));
		nameValuePair.add(new BasicNameValuePair("mobile", mobileno));
		nameValuePair.add(new BasicNameValuePair("userid", userid));
		return processHTTPPostExecution(url, nameValuePair);
	}
	
	// not calling
	public static String getPlacesResponse(String inputText, String key) {	 
		String responseBody = "";
		  HttpClient httpClient = HttpClientSingleTon.getInstance();
		  // HttpPost httpPost = new HttpPost(url);
		  // HttpGet httpGet = new HttpGet(url);
		  
		  CookieStore cookieStore = new BasicCookieStore();
		  HttpContext httpContext = new BasicHttpContext();
		  httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		  
		  String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
		  // Building post parameters key and value pair
		  List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
		  nameValuePair.add(new BasicNameValuePair("input", inputText));
		  nameValuePair.add(new BasicNameValuePair("key", key));
		  nameValuePair.add(new BasicNameValuePair("sensor", "false"));
		  
		  String paramsString = URLEncodedUtils.format(nameValuePair, "UTF-8");
		  Log.d(TAG, "getPlacesResponse url - " + url + "?" + paramsString);
		  HttpGet httpGet = new HttpGet(url + "?" + paramsString);

			HttpResponse response = null;

			try {
				response = httpClient.execute(httpGet);
			}catch (ConnectTimeoutException e) {
				// TODO Auto-generated catch block
				responseBody = "timeout";
				e.printStackTrace();
			}
			catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				responseBody = "timeout";
				e.printStackTrace();
			}
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		  
		  try {
		   if (response != null && response.getEntity() != null) {
		    responseBody = EntityUtils.toString(response.getEntity());
		   }
		  } catch (ParseException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  
		  return responseBody;
		 }
		 
	// common method for http execution
	private static String processHTTPPostExecution(String url, List<NameValuePair> nameValuePair) {
		String responseBody = "";
		Log.d(TAG, "url -" + url);
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		HttpPost httpPost = new HttpPost(url);

		BasicHttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
	    ((DefaultHttpClient) httpClient).setParams(httpParams);
		
//		HttpParams params = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(params, 10000);
//	    HttpConnectionParams.setSoTimeout(params, 10000); // 1 minute
//	    httpPost.setParams(params);
//	    Log.d("connection timeout", String.valueOf(HttpConnectionParams
//	    .getConnectionTimeout(params)));
//	    Log.d("socket timeout",
//	    String.valueOf(HttpConnectionParams.getSoTimeout(params)));
		BasicHttpParams httpParams = new BasicHttpParams();
	     HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
	     ((DefaultHttpClient) httpClient).setParams(httpParams);
	     
		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpResponse response = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			response = httpClient.execute(httpPost);
		} catch(SSLPeerUnverifiedException e) {
			responseBody = "timeout";
		} catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}

	
	// get call
	public static String autoGenerateEmailResponse(String url) {
		String responseBody = "";
		HttpClient httpClient = HttpClientSingleTon.getInstance();
		// HttpPost httpPost = new HttpPost(url);
		// HttpGet httpGet = new HttpGet(url);

		CookieStore cookieStore = new BasicCookieStore();
		HttpContext httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

		HttpGet httpGet = new HttpGet(url);
		Log.d(TAG, "relation auto email - " + url);

		HttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
		}catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			responseBody = "timeout";
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (response != null && response.getEntity() != null) {
				responseBody = EntityUtils.toString(response.getEntity());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseBody;
	}
}
