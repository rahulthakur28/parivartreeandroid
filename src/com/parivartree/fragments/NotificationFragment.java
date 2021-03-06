package com.parivartree.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartree.R;
import com.parivartree.adapters.CustomNotificationAdapter;
import com.parivartree.fragments.HomeFragment.GetNewNotificationCountTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.CroutonMessage;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.NotificationModel;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class NotificationFragment extends Fragment implements OnClickListener{
	private String TAG = "NotificationFragment";
	private String userId;
	ListView notificationList;
	 TextView viewAll;
	 String allNotification ="unread";
	ArrayList<NotificationModel> notificationModelList;
	CustomNotificationAdapter notificationCustomAdapter;
	private SharedPreferences sharedPreferences;
	NotificationModel notifiObject;
	Activity activity;
	private ProgressDialog pDialog;
	TextView textNotificationCount;

	public NotificationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
		activity= getActivity();
		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		ActionBar actionBar = activity.getActionBar();
		textNotificationCount = (TextView) actionBar.getCustomView().findViewById(R.id.textnoofnotification);
		
		notificationList = (ListView) rootView.findViewById(R.id.notifiationlist);
		 viewAll = (TextView) rootView.findViewById(R.id.textviewall);
		Log.d("list before", "" + notificationModelList);
		notificationModelList = new ArrayList<NotificationModel>();
		Log.d("list after", "" + notificationModelList);
		notificationCustomAdapter = new CustomNotificationAdapter(activity, this, notificationModelList, userId);
		notificationList.setAdapter(notificationCustomAdapter);
		// allNotification = "unread";
		 viewAll.setOnClickListener(this);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			allNotification ="unread";
			final GetNotificationTask getnotificationTask = new GetNotificationTask();
			getnotificationTask.execute(userId);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getnotificationTask.getStatus() == AsyncTask.Status.RUNNING){
						getnotificationTask.cancel(true);
					}
				}
			}, 10000);
			
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
		boolean bools = new ConDetect(getActivity()).isOnline();
		if (bools) {
			// Create object of AsycTask and execute
			final GetNewNotificationCountTask getNewNotificationCountTask = new GetNewNotificationCountTask();
			getNewNotificationCountTask.execute(sharedPreferences.getString("user_id", "NA"));
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getNewNotificationCountTask.getStatus() == AsyncTask.Status.RUNNING){
						getNewNotificationCountTask.cancel(true);
					}
				}
			}, 10000);
			
		} else {
			CroutonMessage.showCroutonAlert(activity, "!No Internet Connection,Try again", 6000);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if ((pDialog != null) && pDialog.isShowing())
			pDialog.dismiss();
		pDialog = null;
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			allNotification ="setread";
			final GetNotificationSetReadStatusTask getNotificationSetReadStatusTask = new GetNotificationSetReadStatusTask();
			getNotificationSetReadStatusTask.execute(userId);
			
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
//		boolean bools = new ConDetect(getActivity()).isOnline();
//		if (bools) {
//			// Create object of AsycTask and execute
//			final GetNewNotificationCountTask getNewNotificationCountTask = new GetNewNotificationCountTask();
//			getNewNotificationCountTask.execute(sharedPreferences.getString("user_id", "NA"));
//			Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					if (getNewNotificationCountTask.getStatus() == AsyncTask.Status.RUNNING){
//						getNewNotificationCountTask.cancel(true);
//					}
//				}
//			}, 10000);
//			
//		} else {
//			CroutonMessage.showCroutonAlert(activity, "!No Internet Connection,Try again", 6000);
//		}
	}
	public class GetNewNotificationCountTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			pDialog = new ProgressDialog(getActivity());
//			pDialog.setMessage("Getting Notification...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(true);
//			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground : " + params[0]);
			// ---------change method name

		
				return HttpConnectionUtils.getNotificationListResponse(
						params[0],
						"https://www.parivartree.com/app.php/mobilenotificationcount");
		
			
		}
		
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			
//			if ((pDialog != null) && pDialog.isShowing()) { 
//				pDialog.dismiss();
//			}
			
			Log.i("Notification count Response ", response);
			
			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				int status = eventListResponseObject.getInt("AuthenticationStatus");
				String responseResult = eventListResponseObject.getString("Status");
				String notificationCount = eventListResponseObject.getString("count");
				if ((responseResult.equals("Success")) && (status == 1)) {
					if(!notificationCount.equals("0")){
						textNotificationCount.setText(notificationCount);
						textNotificationCount.setVisibility(View.VISIBLE);
					}else{
						textNotificationCount.setVisibility(View.GONE);
					}
				}

			}catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - ", Toast.LENGTH_LONG).show();
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
//			if ((pDialog != null) && pDialog.isShowing()) { 
//				pDialog.dismiss();
//			}
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}
	public class GetNotificationTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null))
				pDialog.dismiss();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Getting Notification...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground : " + params[0]);
			// ---------change method name
			String response = "";
			if(allNotification.equalsIgnoreCase("read")){
				response = HttpConnectionUtils.getNotificationListResponse(
						params[0],
						getActivity().getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_all_notification));
			}else if(allNotification.equalsIgnoreCase("unread")){
				response =HttpConnectionUtils.getNotificationListResponse(
						params[0],
						getActivity().getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_unread_notification));
			}
			return response;
			
		}
		
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			
			Log.i("Notification list Response ", response);
			
			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute : got my notification");
					JSONArray dataArray = eventListResponseObject.getJSONArray("notification");
					notificationModelList.clear();
					// Toast.makeText(getActivity(),
					// "Size : "+eventArrayList.size(),
					// Toast.LENGTH_LONG).show();
					for (int i = 0; i < dataArray.length(); i++) {
						JSONObject c = dataArray.getJSONObject(i);
						JSONObject created = c.getJSONObject("created");

						notifiObject = new NotificationModel();

						notifiObject.setNotifid(c.getInt("id"));
						notifiObject.setNotificationtype(c.getInt("notificationtype"));
						notifiObject.setNotificationstatus(c.getString("notificationstatus"));
						Log.d(TAG, "entityid");
						notifiObject.setEntityid(c.getString("entityid"));
						Log.d(TAG, "entityid");
						notifiObject.setReadstatus(c.getInt("readstatus"));
						notifiObject.setImageexists(c.getInt("imageexists"));
						notifiObject.setEntityname(c.getString("entityname"));
						notifiObject.setEvent(c.getString("event"));
						notifiObject.setPost(c.getString("post"));
						notifiObject.setRelationname(c.getString("relationname"));
						notifiObject.setAddedby(c.getString("addedby"));
						notifiObject.setWeddingdate(c.getString("weddingdate"));

						String[] userdatetime = created.getString("date").split(" ");

						String _24HourTime = userdatetime[1];
						SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
						SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
						Date _24HourDt = _24HourSDF.parse(_24HourTime);
						System.out.println(_24HourDt);
						String finaltime = _12HourSDF.format(_24HourDt);

						String[] datett = userdatetime[0].split("\\-");
						String finaldate = datett[2] + "-" + datett[1] + "-" + (datett[0].substring(2)) + "  "
								+ finaltime;
						notifiObject.setDate(finaldate);
						notifiObject.setOwnerid(c.getString("ownerid"));

						setNotificationList(c.getString("event"), c.getString("entityname"),
								c.getInt("notificationtype"), c.getString("relationname"), c.getString("addedby"));
						// notifiObject.setExactNotificationText(notificationText);
						// notificationModelList.add(notifiObject);

					}
					if (notificationModelList.size() < 1) {
						notifiObject = new NotificationModel();
						notifiObject.setExactNotificationText("You do not have any New notifications");
						notificationModelList.add(notifiObject);
					}

					notificationCustomAdapter.notifyDataSetChanged();
					// Toast.makeText(getActivity(), "Listing Notification ",
					// Toast.LENGTH_SHORT).show();
				}

			}catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content from Notification!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}
	public class GetNotificationSetReadStatusTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			Log.d(TAG, "doInBackground : " + params[0]);
			// ---------change method name
		HttpConnectionUtils.getNotificationListResponse(
						params[0],
						"https://www.parivartree.com/app.php/mobilesetreadstatus");
		return null;		
		}		
	}
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// if (v.getId() == R.id.textviewall) {
	// allNotification = "all";
	// GetNotificationTask getnotificationTask1 = new GetNotificationTask();
	// getnotificationTask1.execute(userId);
	// }
	// }

	private void setNotificationList(String eventname, String entityname, int notificationtype, String relationname,
			String addedby) {
		String notification = " ";
		switch (notificationtype) {
		case 1:
			notification = entityname + " has invited you for an " + eventname;
			break;
		case 2:
			notification = entityname + " has posted on your wall";
			break;
		case 3:
			notification = entityname + " was added to your tree by " + addedby;
			break;
		case 4:
			notification = entityname + " has invited you to join the family as " + relationname;
			break;
		case 5:
			notification = entityname + " has accepted the invitation to join the event  '" + eventname + "'";
			break;
		case 6:
			notification = entityname + " has accepted the invitation to join your family";
			break;
		case 7:
			notification = entityname + " has been added by " + relationname + " to your family";
			break;
		case 8:
			notification = entityname + " has made " + relationname + " deceased";
			break;
		case 9:

			break;
		case 10:
			notification = entityname + " has recommended you to add " + addedby + " as your " + relationname;
			break;
		case 11:
			notification = entityname + " has recommended you to invite " + addedby + " as your " + relationname;
			break;
		case 12:

			break;
		case 13:

			break;
		case 14:

			break;
		case 15:

			break;
		case 16:
			notification = entityname + " has modified '" + eventname + "' event";
			break;
		case 19:
			notification = entityname +" has invited "+addedby+" as "+relationname;
			break;
		case 20:
			notification = entityname + " has invited you as "+relationname+" of "+addedby;
			break;
		default:
			 
			break;
		}
		if (notification.trim().length() > 5) {
			notifiObject.setExactNotificationText(notification);
			notificationModelList.add(notifiObject);
		}
	}

	public void refreshNotification() {
		Log.d(TAG, "adapterNotifi : remove object");
		// Log.d("passlist", "" + listPass);
		Log.d("list", "" + notificationModelList);
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final GetNotificationTask getnotificationTask = new GetNotificationTask();
			getnotificationTask.execute(userId);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getnotificationTask.getStatus() == AsyncTask.Status.RUNNING){
						getnotificationTask.cancel(true);
					}
				}
			}, 10000);

		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}	
		// notificationModelList.remove(itemPosition);
		// notificationCustomAdapter.notifyDataSetChanged();
		// Fragment fragment = new NotificationFragment();
		//
		// if (fragment != null) {
		// FragmentManager fragmentManager = getFragmentManager();
		// fragmentManager.beginTransaction().replace(R.id.frame_container,
		// fragment).commit();
		// }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.textviewall){
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				// Create object of AsycTask and execute
				allNotification ="read";
				final GetNotificationTask getnotificationTask = new GetNotificationTask();
				getnotificationTask.execute(userId);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (getnotificationTask.getStatus() == AsyncTask.Status.RUNNING){
							getnotificationTask.cancel(true);
						}
					}
				}, 10000);
				
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
			boolean bools = new ConDetect(getActivity()).isOnline();
			if (bools) {
				// Create object of AsycTask and execute
				final GetNewNotificationCountTask getNewNotificationCountTask = new GetNewNotificationCountTask();
				getNewNotificationCountTask.execute(sharedPreferences.getString("user_id", "NA"));
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (getNewNotificationCountTask.getStatus() == AsyncTask.Status.RUNNING){
							getNewNotificationCountTask.cancel(true);
						}
					}
				}, 10000);
				
			} else {
				CroutonMessage.showCroutonAlert(activity, "!No Internet Connection,Try again", 6000);
			}
		}
	}
}
