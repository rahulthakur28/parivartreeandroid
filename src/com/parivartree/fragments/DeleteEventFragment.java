package com.parivartree.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.ForgotPasswordActivity.ForgotTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.Event;

public class DeleteEventFragment extends Fragment implements OnClickListener {
	private String TAG = "DeleteEventFragment";
	private ImageView imageViewDelete;
	private TextView txtEevntName, txtLocation, txtDateTime, txtYourName, txtDialogDelete, txtEevntDescription;
	private Button btnDeleteEvent, btnEditEvent, btnDialogdelete, btnDialogCancel;
	private String eventIdbd, eventNamebd, eventDatebd, eventDescritionbd, locationbd, time, timeHourbd, timeMinbd,
			yourNamebd, reachListbd, eventListbd;
	int eventNamePos, eventReachPos;
	private String userId = null;
	// private HashMap<Integer, String> joineesHash;
	private ArrayList<Event> joineesArrayList;
	private SharedPreferences sharedPreferences;
	LinearLayout linear;
	Fragment fragment;

	public DeleteEventFragment() {

	}

	LocationManager lm;
	GoogleMap googleMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_delete_event, container, false);
		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);

		imageViewDelete = (ImageView) rootView.findViewById(R.id.imageViewdelete);
		txtEevntDescription = (TextView) rootView.findViewById(R.id.textdeleteeventdescription);
		txtEevntName = (TextView) rootView.findViewById(R.id.textdeleteeventname);
		txtLocation = (TextView) rootView.findViewById(R.id.textdeletelocation);
		txtDateTime = (TextView) rootView.findViewById(R.id.textdeletedatetime);
		txtYourName = (TextView) rootView.findViewById(R.id.textdeletename);
		btnDeleteEvent = (Button) rootView.findViewById(R.id.btndelete);
		btnEditEvent = (Button) rootView.findViewById(R.id.btnedit);

		linear = (LinearLayout) rootView.findViewById(R.id.joininglayout);

		btnDeleteEvent.setOnClickListener(this);
		btnEditEvent.setOnClickListener(this);

		Bundle bndle = getArguments();
		eventIdbd = bndle.getString("eventid");
		eventNamebd = bndle.getString("eventname");
		eventNamePos = bndle.getInt("event", 0);
		// timeHourbd=bndle.getString("hour");
		// timeMinbd=bndle.getString("min");
		yourNamebd = bndle.getString("name");
		locationbd = bndle.getString("location");
		eventDatebd = bndle.getString("eventdate");
		time = bndle.getString("time");
		eventReachPos = bndle.getInt("eventreach", 0);
		eventDescritionbd = bndle.getString("eventdescription");

		UrlImageViewHelper.setUrlDrawable(imageViewDelete, "https://www.parivartree.com/profileimages/thumbs/" + userId
				+ "PROFILE.jpeg", getResources().getDrawable(R.drawable.dummyphoto1), 60000);
		txtEevntName.setText(eventNamebd);
		txtLocation.setText(locationbd);
		txtDateTime.setText(eventDatebd + "  " + time);
		txtYourName.setText(yourNamebd);
		txtEevntDescription.setText(eventDescritionbd);

		// joineesHash = new LinkedHashMap<Integer, String>();
		joineesArrayList = new ArrayList<Event>();
		Log.d(TAG, "eventid : " + eventIdbd);

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			GetJoineesTask getJoineesTask = new GetJoineesTask();
			getJoineesTask.execute(eventIdbd);
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btndelete) {
			// showdeleteDialog(eventIdbd,eventNamebd);
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				DeleteEventsTask deleteEventTask = new DeleteEventsTask();
				deleteEventTask.execute(eventIdbd, userId);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.btnedit) {
			Bundle bundle2 = new Bundle();
			bundle2.putString("eventid", eventIdbd);
			bundle2.putString("eventname", eventNamebd);
			bundle2.putInt("event", eventNamePos);
			bundle2.putString("time", time);
			bundle2.putString("name", yourNamebd);
			bundle2.putString("location", locationbd);
			bundle2.putString("eventdate", eventDatebd);
			bundle2.putString("eventdescription", eventDescritionbd);
			bundle2.putInt("eventreach", eventReachPos);
			// fragment=new EditEventFragment();
			// fragment.setArguments(bundle2);
			((MainActivity) this.getActivity()).changeFragment("EditEventFragment", bundle2);

		}
		// change to the new fragment
		if (fragment != null) {
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

		} else {
			// error in creating fragment
			Log.e("MainContainerActivity", "Error in creating fragment");
		}
	}

	public class DeleteEventsTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Deleting...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			// ---------change method name
			return HttpConnectionUtils.deleteEventResponse(
					params[0],
					params[1],
					getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(R.string.url_delete_event));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("delete event Response ", response);

			try {
				JSONObject deleteEventResponseObject = new JSONObject(response);
				String responseResult = deleteEventResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute :event deleted");

					Fragment fragment = new CommunityFragment();

					if (fragment != null) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null)
								.commit();

						// update selected item and title, then close the drawer
					}

				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content in Delete Event!!");
			}

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
	}

	@SuppressLint("InflateParams")
	private void addJoinees() {

		if (joineesArrayList.size() > 0) {
			for (Event name : joineesArrayList) {

				LinearLayout LL = new LinearLayout(getActivity());
				LayoutParams LLParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 280);
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
				//imv.setImageResource(R.drawable.dummyphoto1);

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

//	private void showdeleteDialog(final String eventidpass, String eventNamepass) {
//
//		// Create Object of Dialog class
//		final Dialog delete = new Dialog(getActivity());
//		// Set GUI of login screen
//		delete.setContentView(R.layout.show_delete_dialog);
//		delete.setTitle("Do you want to Delete");
//
//		// Init button of login GUI
//		btnDialogdelete = (Button) delete.findViewById(R.id.btndialogdelete);
//		btnDialogCancel = (Button) delete.findViewById(R.id.btnCancel);
//		txtDialogDelete = (TextView) delete.findViewById(R.id.textViewDelete1);
//		txtDialogDelete.setText(eventNamepass);
//		// Attached listener for login GUI button
//		btnDialogdelete.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				boolean bool = new ConDetect(getActivity()).isOnline();
//				if (bool) {
//					DeleteEventsTask deleteEventTask = new DeleteEventsTask();
//					deleteEventTask.execute(eventidpass, userId);
//				} else {
//					Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
//				}
//				delete.dismiss();
//			}
//		});
//		btnDialogCancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				delete.dismiss();
//			}
//		});
//
//		// Make dialog box visible.
//		delete.show();
//	}

	@Override
	public void onResume() {
		super.onResume();
		if (googleMap == null) {
			Log.d(TAG, "map not found");
			// removeMap();
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
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
			Double lat = ad.getLatitude();
			Double lon = ad.getLongitude();
			LatLng gizmeon = new LatLng(lat, lon);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gizmeon, 15));
			googleMap.addMarker(new MarkerOptions().title(locationbd).snippet(locationbd).position(gizmeon));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		removeMap();
		Log.d(TAG, "onPause of fragment called");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView called");
		if (googleMap != null) {

			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.map1)).commit();
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
					R.id.map1);
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
					.remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.map1)).commit();
			googleMap = null;
			Log.d(TAG, "removeMap: googleMap removed");
		}
		Log.d(TAG, "removeMap: googleMap was null");
	}
}
