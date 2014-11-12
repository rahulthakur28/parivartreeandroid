package com.parivartree.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.Event;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("ResourceAsColor")
public class AllEventsFragment extends Fragment implements OnClickListener {
	private String TAG = "AllEventsFragment";
	private String userId = null, sessionname;
	private String inviteeStatusbd, eventIdbd, eventAuthorId, eventNamebd, eventDatebd, eventDescritionbd, locationbd,
			time, timeHourbd, timeMinbd, yourNamebd, reachListbd, eventListbd, dialogName;
	private SharedPreferences sharedPreferences;
	private TextView txtEevntName, txtLocation, txtDateTime, txtYourName, txtEevntDescription;
	private ImageView imageviewothz;
	private Button btnJoinEvent, btnMayEvent, btnDeclineEvent, btnDialogjoinok, btnDialogjoinCancel;;
	private EditText editDialogName;
	int eventNamePos, eventReachPos;
	LocationManager lm;
	GoogleMap googleMap;
	LinearLayout linear;
	Activity activity;
	private ArrayList<Event> joineesArrayList;

	public AllEventsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);

		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		sessionname = sharedPreferences.getString("sessionname", null);

		imageviewothz = (ImageView) rootView.findViewById(R.id.imageViewothz);
		txtEevntDescription = (TextView) rootView.findViewById(R.id.textothzeventdescription);
		txtEevntName = (TextView) rootView.findViewById(R.id.textothzeventname);
		txtLocation = (TextView) rootView.findViewById(R.id.textothzlocation);
		txtDateTime = (TextView) rootView.findViewById(R.id.textothzdatetime);
		txtYourName = (TextView) rootView.findViewById(R.id.textothzname);
		btnJoinEvent = (Button) rootView.findViewById(R.id.btnjoin);
		btnMayEvent = (Button) rootView.findViewById(R.id.btnmaybe);
		btnDeclineEvent = (Button) rootView.findViewById(R.id.btnnotavailable);
		linear = (LinearLayout) rootView.findViewById(R.id.joininglayout2);
		btnJoinEvent.setOnClickListener(this);
		btnMayEvent.setOnClickListener(this);
		btnDeclineEvent.setOnClickListener(this);

		// get data send from community fragment
		Bundle bndle = getArguments();
		eventIdbd = bndle.getString("eventid");
		eventNamebd = bndle.getString("eventname");
		eventAuthorId = bndle.getString("authorid");
		eventNamePos = bndle.getInt("event", 0);
		timeHourbd = bndle.getString("hour");
		timeMinbd = bndle.getString("min");
		yourNamebd = bndle.getString("name");
		locationbd = bndle.getString("location");
		eventDatebd = bndle.getString("eventdate");
		time = bndle.getString("time");
		inviteeStatusbd = bndle.getString("inviteestatus");
		eventDescritionbd = bndle.getString("eventdescription");

		UrlImageViewHelper.setUrlDrawable(imageviewothz, "https://www.parivartree.com/profileimages/thumbs/"
				+ eventAuthorId + "PROFILE.jpeg", getResources().getDrawable(R.drawable.dummyphoto1), 60000);
		txtEevntName.setText(eventNamebd);
		txtLocation.setText(locationbd);
		txtDateTime.setText(eventDatebd + "  " + time);
		txtYourName.setText(yourNamebd);
		txtEevntDescription.setText(eventDescritionbd);

		if (inviteeStatusbd.trim().equals("1")) {
			btnJoinEvent.setBackgroundResource(R.drawable.round_corners_while);
			btnJoinEvent.setTextColor(R.color.ll_black);
			btnMayEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnDeclineEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
		} else if (inviteeStatusbd.trim().equals("2")) {
			btnJoinEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnMayEvent.setBackgroundResource(R.drawable.round_corners_while);
			btnMayEvent.setTextColor(R.color.ll_black);
			btnDeclineEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
		} else if (inviteeStatusbd.trim().equals("3")) {
			btnJoinEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnMayEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnDeclineEvent.setBackgroundResource(R.drawable.round_corners_while);
			btnDeclineEvent.setTextColor(R.color.ll_black);
		} else {
			btnJoinEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnMayEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
			btnDeclineEvent.setBackgroundResource(R.drawable.rounded_corners_blue);
		}

		joineesArrayList = new ArrayList<Event>();
		Log.d(TAG, "eventid : " + eventIdbd);

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			final GetJoineesTask getJoineesTask = new GetJoineesTask();
			getJoineesTask.execute(eventIdbd);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getJoineesTask.getStatus() == AsyncTask.Status.RUNNING){
						getJoineesTask.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
		
		if (googleMap == null) {
			Log.d(TAG, "map not found");
			// removeMap();
			boolean connected = new ConDetect(getActivity()).isOnline();
			if (connected) {
				googleMap = getGoogleMap();
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
			// googleMap.addMarker(new MarkerOptions().position(new LatLng(0,
			// 0)));
		} else {
			Log.d(TAG, "Map was not called");
		}
		Geocoder gc = new Geocoder(getActivity());
		try {
			List<Address> li = gc.getFromLocationName(locationbd, 5);
			Address ad = li.get(0);
			Log.d(TAG, "lat.." + ad.getLatitude() + " longitude..." + ad.getLongitude());
			Double lat = ad.getLatitude();
			Double lon = ad.getLongitude();
			LatLng gizmeon = new LatLng(lat, lon);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gizmeon, 15));
			googleMap.addMarker(new MarkerOptions().title(locationbd).snippet(locationbd).position(gizmeon));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return rootView;
	}
@Override
public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	activity= getActivity();
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (v.getId() == R.id.btnjoin) {

			if (bool) {
				// showsJoinDialog("join",eventIdbd,eventAuthorId);
				JoinEventsTask joinEventTask = new JoinEventsTask();
				joinEventTask.execute("join", eventIdbd, eventAuthorId, userId, sessionname);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.btnmaybe) {
			if (bool) {
				// showsJoinDialog("maybe",eventIdbd,eventAuthorId);
				JoinEventsTask joinEventTask1 = new JoinEventsTask();
				joinEventTask1.execute("maybe", eventIdbd, eventAuthorId, userId, sessionname);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.btnnotavailable) {
			if (bool) {
				// showsJoinDialog("decline",eventIdbd,eventAuthorId);
				final JoinEventsTask joinEventTask2 = new JoinEventsTask();
				joinEventTask2.execute("decline", eventIdbd, eventAuthorId, userId, sessionname);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (joinEventTask2.getStatus() == AsyncTask.Status.RUNNING){
							joinEventTask2.cancel(true);
						}
					}
				}, 10000);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
		}

	}

	class JoinEventsTask extends AsyncTask<String, Void, String> {

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
			if (params[0].equals("join")) {
				httpResponse = HttpConnectionUtils.getJoinResponse(
						params[1],
						params[2],
						params[3],
						params[4],
						getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_join_event));
			} else if (params[0].equals("maybe")) {
				httpResponse = HttpConnectionUtils.getJoinResponse(
						params[1],
						params[2],
						params[3],
						params[4],
						getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_maybe_event));
			} else if (params[0].equals("decline")) {
				httpResponse = HttpConnectionUtils.getJoinResponse(
						params[1],
						params[2],
						params[3],
						params[4],
						getResources().getString(R.string.hostname)
								+ getActivity().getResources().getString(R.string.url_decline_event));
			}
			return httpResponse;
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Join maybe decline Response ", response);

			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {

					Log.d(TAG, "process success");
					Fragment fragment = new CommunityFragment();
					if (fragment != null) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null)
								.commit();
					}
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class GetJoineesTask extends AsyncTask<String, Void, String> {

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

			return HttpConnectionUtils.getJoineesResponse(params[0], getResources().getString(R.string.hostname)
					+ getActivity().getResources().getString(R.string.url_find_joinees));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("joinees list Response ", response);
			
			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("success")) {
					Log.d(TAG, "onpostexecute : got my joinees");
					JSONArray dataArray = eventListResponseObject.getJSONArray("data");
					for (int i = 0; i < dataArray.length(); i++) {
						JSONObject c = dataArray.getJSONObject(i);
						int inviteestatus = c.getInt("inviteestatus");
						if (inviteestatus == 1) {	
							Event eventObject = new Event();
							String firstname = c.getString("firstname");
							String lastname = c.getString("lastname");
							
							eventObject.setAuthorId(""+c.getInt("inviteeid"));
							eventObject.setName(firstname + " " + lastname);
							joineesArrayList.add(eventObject);
						}
					}
					addJoinees();
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content joinees!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	@SuppressLint("InflateParams")
	private void addJoinees() {
		// LayoutInflater inflater = (LayoutInflater)
		// getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// LinearLayout parent = (LinearLayout)
		// inflater.inflate(R.layout.fragment_delete_event, null);
		// LinearLayout linear = (LinearLayout)
		// parent.findViewById(R.id.joininglayout);

		if (joineesArrayList.size() > 0) {
			Log.d(TAG, "joineesHash : " + joineesArrayList);
			// ArrayList<String> joineesimage=(ArrayList<String>)
			// joineesHash.values();
			for (Event name : joineesArrayList) {
				Log.d(TAG, "joineesHash");

				LinearLayout LL = new LinearLayout(getActivity());
				Log.d(TAG, "joineesHash1");
				LayoutParams LLParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 280);
				Log.d(TAG, "joineesHash2");
				LLParams.setMargins(5, 5, 5, 5);
				LL.setLayoutParams(LLParams);
				LL.setPadding(2, 2, 2, 2);
				LL.setBackgroundColor(Color.WHITE);
				LL.setBackgroundResource(R.drawable.layout_border_file);
				LL.setOrientation(LinearLayout.VERTICAL);

				ImageView imv = new ImageView(getActivity());
				LayoutParams imvParams = new LayoutParams(150, 150);
				imvParams.gravity = Gravity.CENTER_HORIZONTAL;
				imv.setLayoutParams(imvParams);
				UrlImageViewHelper.setUrlDrawable(imv,
						"https://www.parivartree.com/profileimages/thumbs/" + name.getAuthorId()
								+ "PROFILE.jpeg", getResources().getDrawable(R.drawable.dummyphoto1), 0);

				TextView tv = new TextView(getActivity());
				LayoutParams tvParams = new LayoutParams(150, LayoutParams.WRAP_CONTENT);
				tvParams.setMargins(5, 5, 5, 5);
				tv.setLayoutParams(tvParams);
				tv.setText(name.getName());

				LL.addView(imv);
				LL.addView(tv);
				linear.addView(LL);
				
			}
		}
	}
	
//	private void showsJoinDialog(final String arg, final String id, final String authid) {
//		// Create Object of Dialog class
//		final Dialog join = new Dialog(getActivity());
//		// Set GUI of login screen
//		join.setContentView(R.layout.dialog_join);
//		join.setTitle("Fill");
//
//		// Init button of login GUI
//		btnDialogjoinok = (Button) join.findViewById(R.id.btndialogjoin);
//		btnDialogjoinCancel = (Button) join.findViewById(R.id.btnjoinCancel);
//		editDialogName = (EditText) join.findViewById(R.id.txtdialogname);
//
//		// Attached listener for login GUI button
//		btnDialogjoinok.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				dialogName = editDialogName.getText().toString();
//				boolean bool = new ConDetect(getActivity()).isOnline();
//				if (bool) {
//					if (dialogName.trim().length() > 0 && arg.equals("join")) {
//						JoinEventsTask joinEventTask = new JoinEventsTask();
//						joinEventTask.execute("join", id, authid, userId, dialogName);
//					}
//					if (dialogName.trim().length() > 0 && arg.equals("maybe")) {
//						JoinEventsTask joinEventTask = new JoinEventsTask();
//						joinEventTask.execute("maybe", id, authid, userId, dialogName);
//					}
//
//					if (dialogName.trim().length() > 0 && arg.equals("decline")) {
//						JoinEventsTask joinEventTask = new JoinEventsTask();
//						joinEventTask.execute("decline", id, authid, userId, dialogName);
//					}
//
//				} else {
//					Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
//				}
//				join.dismiss();
//			}
//		});
//		btnDialogjoinCancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				join.dismiss();
//			}
//		});
//		
//		// Make dialog box visible.
//		join.show();
//	}

	@Override
	public void onResume() {
		super.onResume();
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//removeMap();
		Log.d(TAG, "onPause of fragment called");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView called");
		if (googleMap != null) {

			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.map2)).commit();
			googleMap = null;
			Log.d(TAG, "googleMap removed");
		} else {
			Log.d(TAG, "googleMap was null");
		}
		//
		Log.d(TAG, "onPause called");
	}

	private GoogleMap getGoogleMap() {
		if (googleMap == null && getActivity() != null && getActivity().getSupportFragmentManager() != null) {
			SupportMapFragment smf = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(
					R.id.map2);
			if (smf != null) {
				Log.d(TAG, "New Map instance returned");
				googleMap = smf.getMap();
			}
		}
		return googleMap;
	}

	public void removeMap() {
		if (googleMap != null) {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.map2)).commit();
			googleMap = null;
			Log.d(TAG, "removeMap: googleMap removed");
		}
		Log.d(TAG, "removeMap: googleMap was null");
	}
}
