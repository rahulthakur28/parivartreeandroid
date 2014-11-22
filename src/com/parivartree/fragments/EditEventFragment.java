package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.R;
import com.parivartree.adapters.LocationHintAdapter;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EditEventFragment extends Fragment implements OnClickListener, ValidationListener {

	boolean set = false;
	private String TAG = "EditEventFragment";
	@Required(order = 1)
	private EditText editEventName;
	@Required(order = 2)
	private EditText editEventDate;
	@Required(order = 3)
	private EditText editEventDescrition;
	@Required(order = 4)
	private AutoCompleteTextView editLocation;
	private LocationHintAdapter locationHintAdpter;
	private ArrayList<String> locationHints;
	SearchPlacesTask searchPlacesTask;
	private Button btnEditEvent;
	// private DatePicker datePicker;
	private Spinner spinnerEvntName, spinnerReach, spinnerEventEditHour, spinnerEventEditMin;
	private ArrayList<String> spinnerevntNameList, spinnerReachList, spinnerevntHourList, spinnerEventMinList;
	// Shared preferences
	private SharedPreferences sharedPreferences;
	private String eventIdbd, eventNamebd, eventDatebd, eventDescritionbd, locationbd, time, timeHourbd, timeMinbd,
			yourNamebd, reachListbd, eventListbd;
	int eventNamePos, eventReachPos;
	private String userId = null, sessionname;
	// Saripaar validator
	Validator validator;
Activity activity;
	private ProgressDialog pDialog;

	public EditEventFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_edit_event, container, false);

		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		sessionname = sharedPreferences.getString("sessionname", null);
		validator = new Validator(this);
		validator.setValidationListener(this);

		spinnerEvntName = (Spinner) rootView.findViewById(R.id.spinneredit1);
		editEventDate = (EditText) rootView.findViewById(R.id.editeventdateedit);
		// datePicker = (DatePicker) rootView.findViewById(R.id.datePickeredit);
		editEventName = (EditText) rootView.findViewById(R.id.editTextedit3);
		editEventDescrition = (EditText) rootView.findViewById(R.id.editTextedit4);
		editLocation = (AutoCompleteTextView) rootView.findViewById(R.id.editTextedit5);
		spinnerReach = (Spinner) rootView.findViewById(R.id.spinneredit2);
		spinnerEventEditHour = (Spinner) rootView.findViewById(R.id.spinneredithour);
		spinnerEventEditMin = (Spinner) rootView.findViewById(R.id.spinnereditmin);
		btnEditEvent = (Button) rootView.findViewById(R.id.btneditok);
		spinnerevntNameList = new ArrayList<String>();
		spinnerevntNameList.add("Birthday");
		spinnerevntNameList.add("Wedding");
		spinnerevntNameList.add("Family Meet");
		spinnerevntNameList.add("Community Meet");
		spinnerevntNameList.add("Bussiness Meet");
		ArrayAdapter<String> spinnerEvntAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, spinnerevntNameList);
		spinnerEvntName.setAdapter(spinnerEvntAdapter);
		spinnerReachList = new ArrayList<String>();
		spinnerReachList.add("Public");
		spinnerReachList.add("Family");
		spinnerReachList.add("Private");
		ArrayAdapter<String> spinnerReachAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, spinnerReachList);
		spinnerReach.setAdapter(spinnerReachAdapter);

		spinnerevntHourList = new ArrayList<String>();
		spinnerevntHourList.add("01");
		spinnerevntHourList.add("02");
		spinnerevntHourList.add("03");
		spinnerevntHourList.add("04");
		spinnerevntHourList.add("05");
		spinnerevntHourList.add("06");
		spinnerevntHourList.add("07");
		spinnerevntHourList.add("08");
		spinnerevntHourList.add("09");
		spinnerevntHourList.add("10");
		spinnerevntHourList.add("11");
		spinnerevntHourList.add("12");
		spinnerevntHourList.add("13");
		spinnerevntHourList.add("14");
		spinnerevntHourList.add("15");
		spinnerevntHourList.add("16");
		spinnerevntHourList.add("17");
		spinnerevntHourList.add("18");
		spinnerevntHourList.add("19");
		spinnerevntHourList.add("20");
		spinnerevntHourList.add("21");
		spinnerevntHourList.add("22");
		spinnerevntHourList.add("23");
		spinnerevntHourList.add("24");
		spinnerEventEditHour.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				spinnerevntHourList));
		spinnerEventMinList = new ArrayList<String>();
		spinnerEventMinList.add("00");
		spinnerEventMinList.add("15");
		spinnerEventMinList.add("30");
		spinnerEventMinList.add("45");
		spinnerEventEditMin.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				spinnerEventMinList));

		btnEditEvent.setOnClickListener(this);
		editEventDate.setOnClickListener(this);
		// provide hinting for the location fields from Google Places API
		locationHints = new ArrayList<String>();
		locationHintAdpter = new LocationHintAdapter(getActivity(), R.layout.item_location, locationHints);
		editLocation.setAdapter(locationHintAdpter);
		editLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				Log.d("Search User ", "s=" + s + " ,start=" + start + " ,count=" + count + " ,after=" + after);
				boolean bool = new ConDetect(getActivity()).isOnline();
				if (bool) {
					if (searchPlacesTask != null) {
						searchPlacesTask.cancel(true);
					}
					Log.d("Search user", "AsyncTask calling");
					searchPlacesTask = new SearchPlacesTask();
					searchPlacesTask.execute(editLocation.getText().toString().trim(),
							getResources().getString(R.string.places_key));

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (searchPlacesTask.getStatus() == AsyncTask.Status.RUNNING){
								searchPlacesTask.cancel(true);
							}
						}
					}, 10000);
				} else {
					Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		populateViews();
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if ((pDialog != null) && pDialog.isShowing())
			pDialog.dismiss();
		pDialog = null;
	    
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity= getActivity();
	}
	private void populateViews() {
		int day, month, year;
		Bundle bndle = getArguments();
		eventIdbd = bndle.getString("eventid");
		eventNamebd = bndle.getString("eventname");
		eventNamePos = bndle.getInt("event", 0);
		eventReachPos = bndle.getInt("eventreach", 0);
		eventDescritionbd = bndle.getString("eventdescription");
		yourNamebd = bndle.getString("name");
		locationbd = bndle.getString("location");
		eventDatebd = bndle.getString("eventdate");
		time = bndle.getString("time");
		timeHourbd = time.substring(0, 2);
		timeMinbd = time.substring(3);
		// year = Integer.parseInt(eventDatebd.trim().substring(6));
		// month = Integer.parseInt(eventDatebd.substring(3, 5));
		// day = Integer.parseInt(eventDatebd.trim().substring(0, 2));
		// datePicker.init(year, (month - 1), day, null);

		editEventDate.setText(eventDatebd);
		spinnerEvntName.setSelection((eventNamePos - 1));
		spinnerReach.setSelection((eventReachPos - 1));
		editEventName.setText(eventNamebd);
		editLocation.setText(locationbd);
		// editEventDate.setText(eventDatebd);
		editEventDescrition.setText(eventDescritionbd);
		// editTimeHour.setText(timeHourbd);
		// editTimeMin.setText(timeMinbd);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.editeventdateedit) {
			dateDialog(editEventDate);
		}
		if (v.getId() == R.id.btneditok) {
			Log.d(TAG, "button clicked");
			// int day = datePicker.getDayOfMonth();
			// int month = datePicker.getMonth() + 1;
			// int year = datePicker.getYear();
			// String date = day + "-" + month + "-" + year;

			validator.validate();
		}
	}

	public class EditEventTask extends AsyncTask<String, Void, String> {

		

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
			Log.d(TAG, "doInBackground event id : " + params[0]);

			return HttpConnectionUtils.getEditEventResponse(
					params[0],
					params[1],
					params[2],
					params[3],
					params[4],
					params[5],
					params[6],
					params[7],
					params[8],
					params[9],
					params[10],
					getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(R.string.url_edit_event));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("edit Event Response ", response);

			try {
				JSONObject createEventObject = new JSONObject(response);
				String responseResult = createEventObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute : Success");
					// mail send to user
					Log.d(TAG, "Event is edited");

					// After creating event go back to community fragment
					Fragment fragment = new CommunityFragment();
					if (fragment != null) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null)
								.commit();

					}
				}
				int status = createEventObject.getInt("AuthenticationStatus");
				// int status = 0;
				if (status == 1) {
					// TODO successful authentication
					Log.d(TAG, "success");

				} else if (status == 2) {
					// TODO Account blocked
					/**
					 * Your account has been blocked for crossing maximum
					 * authentication failure attempts! Please click on forgot
					 * password to re-generate your password
					 */
					Log.d(TAG, responseResult);
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

	public class SearchPlacesTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			// TODO Auto-generated method stub
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground uid" + params[0]);
			return HttpConnectionUtils.getPlacesResponse(params[0], params[1]);
		}

		protected void onPostExecute(String response) {

			super.onPostExecute(response);
			// pDialog.dismiss();
			Log.i("Ceate Event Response ", response);
			try {
				JSONObject createEventObject = new JSONObject(response);
				JSONArray predictionsArray = createEventObject.getJSONArray("predictions");
				/*
				 * String responseResult =
				 * createEventObject.getString("Status"); Log.d(TAG,
				 * "onpostexecute" + responseResult); if
				 * (responseResult.equals("Success")) { }
				 */
				locationHints.clear();
				for (int i = 0; i < predictionsArray.length() && i < 20; i++) {
					JSONObject tempItem = predictionsArray.getJSONObject(i);
					locationHints.add(tempItem.getString("description"));
				}
				locationHintAdpter = new LocationHintAdapter(getActivity(), R.layout.item_location, locationHints);
				editLocation.setAdapter(locationHintAdpter);
				locationHintAdpter.notifyDataSetChanged();
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
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}

	public void dateDialog(final EditText txtview) {
		// TODO Auto-generated method stub
		int day, month, year;

		Log.d("profile", "date text click!!");
		Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		final DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (set) {
					String date = dayOfMonth + "-" + (1 + monthOfYear) + "-" + year;
					txtview.setText(date);
				}
			}
		}, year, month, day);
		dpd.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					set = true;
				}
			}
		});

		dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_NEGATIVE) {
					set = false;
					dpd.hide();
				}
			}
		});
		dpd.show();
	}

	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		// /asynTAsk execute
		String eventnumber = "0";
		if (editEventName.getText().toString().trim().length() > 0 && editEventDescrition.getText().toString

		().trim().length() > 0 && editLocation.getText().toString().trim().length() > 0) {
			if (spinnerEvntName.getSelectedItem().toString().trim().equals("Birthday")) {
				eventnumber = "1";
			} else if (spinnerEvntName.getSelectedItem().toString().trim().equals("Wedding")) {
				eventnumber = "2";
			} else if (spinnerEvntName.getSelectedItem().toString().trim().equals("Family Meet")) {
				eventnumber = "3";
			} else if (spinnerEvntName.getSelectedItem().toString().trim().equals("Community Meet")) {
				eventnumber = "4";
			} else if (spinnerEvntName.getSelectedItem().toString().trim().equals("Bussiness Meet")) {
				eventnumber = "5";
			}

			String reachnumber = "3";
			if (spinnerReach.getSelectedItem().toString().trim().equals("Public")) {
				reachnumber = "1";
			} else if (spinnerReach.getSelectedItem().toString().trim().equals("Family")) {
				reachnumber = "2";
			}
			if (reachnumber.endsWith("0")) {
				Log.d(TAG, "You Set Reach as Private");
			}

			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				// Create object of AsycTask and execute
				final EditEventTask editEventTask = new EditEventTask();
				editEventTask.execute(eventIdbd, eventnumber, editEventName.getText().toString(), editEventDate
						.getText().toString(), editEventDescrition.getText().toString(), editLocation.getText()
						.toString(), reachnumber, spinnerEventEditHour.getSelectedItem().toString(),
						spinnerEventEditMin.getSelectedItem().toString(), sessionname, userId);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (editEventTask.getStatus() == AsyncTask.Status.RUNNING){
							editEventTask.cancel(true);
						}
					}
				}, 10000);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}

		} else {
			Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else {
			Log.d("Signup settings ", message);
		}
	}
}
