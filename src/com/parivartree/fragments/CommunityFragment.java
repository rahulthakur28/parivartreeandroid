package com.parivartree.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.CustomAdapter;
import com.parivartree.fragments.AllEventsFragment.GetJoineesTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.Event;

public class CommunityFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private String TAG = "CommunityFragment";
	private Fragment fragment;
	private String userId, authorId;
	private Button btnCreateEvent;
	private TextView txtMyevent, txtUpcommingevent, txtRecentevent;
	EditText editDialogName;
	private SharedPreferences sharedPreferences;
	Bundle bundle;
	ListView eventList;
	ArrayList<Event> eventArrayList;
	CustomAdapter listAdapter;
	String whichEvent;
	RelativeLayout txtmyevntLayout, txtupcmgevntLayout, txtrecentevntLayout;

	public CommunityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_community, container, false);

		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);

		txtMyevent = (TextView) rootView.findViewById(R.id.txtmyevnt);
		txtUpcommingevent = (TextView) rootView.findViewById(R.id.txtupcmgevnt);
		txtRecentevent = (TextView) rootView.findViewById(R.id.txtrecentevnt);
		btnCreateEvent = (Button) rootView.findViewById(R.id.btncreateevtlst1);
		eventList = (ListView) rootView.findViewById(R.id.eventlist);

		txtmyevntLayout = (RelativeLayout) rootView.findViewById(R.id.txtmyevntLayout);
		txtupcmgevntLayout = (RelativeLayout) rootView.findViewById(R.id.txtupcmgevntLayout);
		txtrecentevntLayout = (RelativeLayout) rootView.findViewById(R.id.txtrecentevntLayout);
		txtmyevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_orange));

		eventArrayList = new ArrayList<Event>();
		eventArrayList.clear();
		listAdapter = new CustomAdapter(getActivity(), eventArrayList);
		eventList.setAdapter(listAdapter);

		btnCreateEvent.setOnClickListener(this);
		txtMyevent.setOnClickListener(this);
		txtUpcommingevent.setOnClickListener(this);
		txtRecentevent.setOnClickListener(this);
		eventList.setOnItemClickListener(this);
		// display my events at page load time
		getMyEvent();
		return rootView;
	}

	private void getMyEvent() {
		whichEvent = "myevents";
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			GetMyEventsTask myEventsTask1 = new GetMyEventsTask();
			myEventsTask1.execute("myevents", userId);
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int spos, long dpos) {
		// TODO Auto-generated method stub

		if (eventArrayList.get(spos).getAuthorId().equals(userId)) {

			bundle = new Bundle();
			bundle.putString("eventid", eventArrayList.get(spos).getEventId());
			bundle.putString("eventname", eventArrayList.get(spos).getEventName());
			bundle.putString("authorid", eventArrayList.get(spos).getAuthorId());
			bundle.putInt("event", eventArrayList.get(spos).getEvent());
			bundle.putString("time", eventArrayList.get(spos).getTime());
			// bundle.putString("hour",eventArrayList.get(spos).getEventId());
			// bundle.putString("min",eventArrayList.get(spos).getEventId());
			bundle.putString("name", eventArrayList.get(spos).getName());
			bundle.putString("location", eventArrayList.get(spos).getLocation());
			bundle.putString("eventdate", eventArrayList.get(spos).getDate());
			bundle.putString("eventdescription", eventArrayList.get(spos).getEventDescription());
			bundle.putInt("eventreach", eventArrayList.get(spos).getReach());
			// fragment=new DeleteEventFragment();
			// fragment.setArguments(bundle);
			((MainActivity) this.getActivity()).changeFragment("DeleteEventFragment", bundle);
		} else {
			bundle = new Bundle();
			bundle.putString("eventid", eventArrayList.get(spos).getEventId());
			bundle.putString("eventname", eventArrayList.get(spos).getEventName());
			bundle.putString("authorid", eventArrayList.get(spos).getAuthorId());
			bundle.putInt("event", eventArrayList.get(spos).getEvent());
			bundle.putString("time", eventArrayList.get(spos).getTime());
			// bundle.putString("hour",eventArrayList.get(spos).getEventId());
			// bundle.putString("min",eventArrayList.get(spos).getEventId());
			bundle.putString("name", eventArrayList.get(spos).getName());
			bundle.putString("location", eventArrayList.get(spos).getLocation());
			bundle.putString("eventdate", eventArrayList.get(spos).getDate());
			bundle.putString("eventdescription", eventArrayList.get(spos).getEventDescription());
			bundle.putInt("eventreach", eventArrayList.get(spos).getReach());
			bundle.putString("inviteestatus", eventArrayList.get(spos).getInviteestatus());

			((MainActivity) this.getActivity()).changeFragment("AllEventsFragment", bundle);
			// fragment=new AllEventsFragment();
			// fragment.setArguments(bundle);
		}
		// change to the new fragment
		// if (fragment != null) {
		// FragmentManager fragmentManager =
		// getActivity().getSupportFragmentManager();
		// fragmentManager.beginTransaction().replace(R.id.frame_container,
		// fragment).commit();
		//
		// } else {
		// // error in creating fragment
		// Log.e("MainContainerActivity", "Error in creating fragment");
		// }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btncreateevtlst1:
			// got create event fragment page
			((MainActivity) this.getActivity()).changeFragment("CreateEventFragment");
			break;
		case R.id.txtmyevnt:
			txtmyevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_orange));
			txtupcmgevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			txtrecentevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			getMyEvent();
			break;
		case R.id.txtupcmgevnt:
			txtmyevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			txtupcmgevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_orange));
			txtrecentevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			whichEvent = "upcomingevents";
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				GetMyEventsTask myEventsTask2 = new GetMyEventsTask();
				myEventsTask2.execute("upcomingevents", userId);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.txtrecentevnt:
			txtmyevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			txtupcmgevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_blue));
			txtrecentevntLayout.setBackgroundColor(this.getResources().getColor(R.color.pt_orange));
			whichEvent = "recentevents";

			boolean bool1 = new ConDetect(getActivity()).isOnline();
			if (bool1) {
				GetMyEventsTask myEventsTask3 = new GetMyEventsTask();
				myEventsTask3.execute("upcomingevents", userId);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}

	}

	public class GetMyEventsTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			// ---------change method name
			String httpResponse = null;
			if (params[0].equals("myevents")) {
				httpResponse = HttpConnectionUtils.getEventListResponse(
						params[1],
						getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_my_event));
			} else if (params[0].equals("upcomingevents")) {
				httpResponse = HttpConnectionUtils.getEventListResponse(
						params[1],
						getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_others_event));
			}
			return httpResponse;
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("event list Response ", response);

			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("result");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("success")) {
					Log.d(TAG, "onpostexecute : got my events");
					JSONArray dataArray = eventListResponseObject.getJSONArray("data");
					eventArrayList.clear();
					for (int i = 0; i < dataArray.length(); i++) {
						JSONObject c = dataArray.getJSONObject(i);

						final int event = Integer.parseInt(c.getString("event"));
						final String eventName = c.getString("eventname");
						authorId = c.getString("author");
						final String eventid = c.getString("id");
						String eventdescription = c.getString("eventdescription");
						final int reach = Integer.parseInt(c.getString("eventrule"));
						String firstName = c.getString("firstname");
						String lastName = c.getString("lastname");
						final String name = firstName + " " + lastName;

						String _24HourTime = c.getString("time");
						SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
						SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
						Date _24HourDt = _24HourSDF.parse(_24HourTime);
						System.out.println(_24HourDt);
						final String time = _12HourSDF.format(_24HourDt);

						final String hour = time.substring(0, 2);
						final String min = time.substring(3);
						final String location = c.getString("location");
						JSONObject eventdate = c.getJSONObject("eventdate");

						final String dummydate = eventdate.getString("date").substring(0, 10);
						String[] datett = dummydate.split("\\-");
						final String date = datett[2] + "-" + datett[1] + "-" + (datett[0]);

						Event eventobj = new Event();
						if (whichEvent.equals("myevents")) {

							eventobj.setEventName(eventName);
							eventobj.setEventId(eventid);
							eventobj.setAuthorId(authorId);
							eventobj.setDate(date);
							eventobj.setEvent(event);
							eventobj.setEventDescription(eventdescription);
							eventobj.setLocation(location);
							eventobj.setName(name);
							eventobj.setTime(time);
							eventobj.setReach(reach);

							eventArrayList.add(eventobj);
						} else if (whichEvent.equals("upcomingevents")) {
							String inviteeStatus = c.getString("inviteestatus");
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
							Calendar calendar1 = Calendar.getInstance();
							Calendar calendar2 = Calendar.getInstance();
							String currentdate = calendar2.get(Calendar.DAY_OF_MONTH) + "-"
									+ (1 + calendar2.get(Calendar.MONTH)) + "-" + calendar2.get(Calendar.YEAR);

							Date date1;
							Date date2;
							try {
								date1 = dateFormat.parse(currentdate);
								date2 = dateFormat.parse(date);
								calendar1.setTime(date1);
								calendar2.setTime(date2);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (calendar2.compareTo(calendar1) == 0 || calendar2.compareTo(calendar1) == 1) {
								eventobj.setEventName(eventName);
								eventobj.setEventId(eventid);
								eventobj.setAuthorId(authorId);
								eventobj.setDate(date);
								eventobj.setEvent(event);
								eventobj.setEventDescription(eventdescription);
								eventobj.setLocation(location);
								eventobj.setName(name);
								eventobj.setTime(time);
								eventobj.setReach(reach);
								eventobj.setInviteestatus(inviteeStatus);
								Log.d(TAG, "upcomming" + eventName);
								eventArrayList.add(eventobj);
							}

						} else if (whichEvent.equals("recentevents")) {
							String inviteeStatus = c.getString("inviteestatus");
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
							Calendar calendar1 = Calendar.getInstance();
							Calendar calendar2 = Calendar.getInstance();
							String currentdate = calendar2.get(Calendar.DAY_OF_MONTH) + "-"
									+ (1 + calendar2.get(Calendar.MONTH)) + "-" + calendar2.get(Calendar.YEAR);

							Date date1;
							Date date2;
							try {
								date1 = dateFormat.parse(currentdate);
								date2 = dateFormat.parse(date);
								calendar1.setTime(date1);
								calendar2.setTime(date2);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (calendar2.compareTo(calendar1) == -1) {
								eventobj.setEventName(eventName);
								eventobj.setEventId(eventid);
								eventobj.setAuthorId(authorId);
								eventobj.setDate(date);
								eventobj.setEvent(event);
								eventobj.setEventDescription(eventdescription);
								eventobj.setLocation(location);
								eventobj.setName(name);
								eventobj.setTime(time);
								eventobj.setReach(reach);
								eventobj.setInviteestatus(inviteeStatus);
								Log.d(TAG, "recent" + eventName);
								eventArrayList.add(eventobj);
							}

						}

					}
					listAdapter.notifyDataSetChanged();
					Log.d(TAG, "geting events ");
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}

		}
	}
}
