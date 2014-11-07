package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.R;
import com.parivartree.SignUpDetailsActivity;
import com.parivartree.ForgotPasswordActivity.ForgotTask;
import com.parivartree.adapters.LocationHintAdapter;
import com.parivartree.fragments.SettingsFragment.SetPasswordTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

public class CreateEventFragment extends Fragment implements OnClickListener, ValidationListener {
	boolean set = false;
	private String TAG = "CreateEventFragment";
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
	private Button btnCreateEvent;
	// private DatePicker datePicker;
	private Spinner spinnerEvntName, spinnerReach, spinnerEventHour, spinnerEventMin;
	private ArrayList<String> spinnerevntNameList, spinnerReachList, spinnerevntHourList, spinnerEventMinList;
	// Shared preferences
	private SharedPreferences sharedPreferences;
	String sessionname;
	// Saripaar validator
	Validator validator;

	public CreateEventFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);

		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		sessionname = sharedPreferences.getString("sessionname", null);
		validator = new Validator(this);
		validator.setValidationListener(this);

		spinnerEvntName = (Spinner) rootView.findViewById(R.id.spinner1);
		editEventDate = (EditText) rootView.findViewById(R.id.editeventdate);
		editEventName = (EditText) rootView.findViewById(R.id.editText3);

		// datePicker = (DatePicker)
		// rootView.findViewById(R.id.datePickercreate);
		editEventDescrition = (EditText) rootView.findViewById(R.id.editText4);
		editLocation = (AutoCompleteTextView) rootView.findViewById(R.id.editText5);
		spinnerReach = (Spinner) rootView.findViewById(R.id.spinner2);
		spinnerEventHour = (Spinner) rootView.findViewById(R.id.spinnercreatehour);
		spinnerEventMin = (Spinner) rootView.findViewById(R.id.spinnercreatemin);
		// editYourName = (EditText) rootView.findViewById(R.id.editText9);
		btnCreateEvent = (Button) rootView.findViewById(R.id.btncreate);
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
		spinnerEventHour.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				spinnerevntHourList));
		spinnerEventMinList = new ArrayList<String>();
		spinnerEventMinList.add("00");
		spinnerEventMinList.add("15");
		spinnerEventMinList.add("30");
		spinnerEventMinList.add("45");
		spinnerEventMin.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				spinnerEventMinList));

		btnCreateEvent.setOnClickListener(this);
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
					searchPlacesTask.execute(s.toString().trim(), getResources().getString(R.string.places_key));
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
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.editeventdate) {
			dateDialog(editEventDate);
		}
		if (v.getId() == R.id.btncreate) {
			// int day = datePicker.getDayOfMonth();
			// int month = datePicker.getMonth() + 1;
			// int year = datePicker.getYear();
			// String date = day + "-" + month + "-" + year;
			validator.validate();
			Log.d(TAG, "button clicked");

		}
	}

	public class CreateEventTask extends AsyncTask<String, Void, String> {

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
			Log.d(TAG, "doInBackground uid" + params[0]);

			return HttpConnectionUtils.getCreateEventResponse(params[0], params[1], params[2], params[3], params[4],
					params[5], params[6], params[7], params[8], params[9], getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(R.string.url_create_event));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Ceate Event Response ", response);

			try {
				JSONObject createEventObject = new JSONObject(response);
				String responseResult = createEventObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute : Success");
					// mail send to user
					Log.d(TAG, "Event is Created");

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
					Toast.makeText(getActivity(), " Event date cannot be left blank.", Toast.LENGTH_LONG).show();
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

	public class SearchPlacesTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			// TODO Auto-generated method stub
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground uid  " + params[0]);
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
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}
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
			Log.d(TAG, "hour" + spinnerEventHour.getSelectedItem().toString());
			// Log.d(TAG, "doInBackground uid" + date);

			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {

				CreateEventTask createEventTask = new CreateEventTask();
				createEventTask.execute(sharedPreferences.getString("user_id", null), editEventDate.getText()
						.toString(), eventnumber, editEventName.getText().toString(), editEventDescrition.getText()
						.toString(), editLocation.getText().toString(), reachnumber, spinnerEventHour.getSelectedItem()
						.toString().trim(), spinnerEventMin.getSelectedItem().toString().trim(), sessionname);
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
