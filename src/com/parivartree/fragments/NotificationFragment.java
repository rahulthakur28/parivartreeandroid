package com.parivartree.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parivartree.R;
import com.parivartree.adapters.CustomNotificationAdapter;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.NotificationModel;

public class NotificationFragment extends Fragment {
	private String TAG = "NotificationFragment";
	private String userId;
	ListView notificationList;
	// TextView viewAll;
	// String allNotification;
	ArrayList<NotificationModel> notificationModelList;
	CustomNotificationAdapter notificationCustomAdapter;
	private SharedPreferences sharedPreferences;
	NotificationModel notifiObject;

	BroadcastReceiver mBroadcastReceiver;

	public NotificationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);

		notificationList = (ListView) rootView.findViewById(R.id.notifiationlist);
		// viewAll = (TextView) rootView.findViewById(R.id.textviewall);
		Log.d("list before", "" + notificationModelList);
		notificationModelList = new ArrayList<NotificationModel>();
		Log.d("list after", "" + notificationModelList);
		notificationCustomAdapter = new CustomNotificationAdapter(getActivity(), this, notificationModelList, userId);
		notificationList.setAdapter(notificationCustomAdapter);
		// allNotification = "unread";
		// viewAll.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			GetNotificationTask getnotificationTask = new GetNotificationTask();
			getnotificationTask.execute(userId);

		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
	}

	public class GetNotificationTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
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

			return HttpConnectionUtils.getNotificationListResponse(
					params[0],
					getActivity().getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(R.string.url_notification));

		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
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

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content from Notification!!");
			}

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
			GetNotificationTask getnotificationTask = new GetNotificationTask();
			getnotificationTask.execute(userId);

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
}
