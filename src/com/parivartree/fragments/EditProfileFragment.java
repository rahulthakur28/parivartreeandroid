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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartree.MainActivity;
import com.parivartree.OtpcodeActivity;
import com.parivartree.R;
import com.parivartree.adapters.CustomDropDownAdapter;
import com.parivartree.adapters.LocationHintAdapter;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.CroutonMessage;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.SpinnerItem;
import com.parivartree.models.UserProfile;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class EditProfileFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
	boolean set = false;
	private EditText editTextFirstName, editTextLastName, editTextGender, editTextPincode,
			editTextMobile, editTextWeddingDate, editTextHomeTown,
			editTextProfession, editTextDobdate, editTextAddGothra, editTextAddCommunity;
	TextView textViewName, textViewEmail;
	// editTextEmail
	private AutoCompleteTextView editTextLocality;
	private LocationHintAdapter locationHintAdpter;
	private ArrayList<String> locationHints;
	SearchPlacesTask searchPlacesTask;
	Button buttonSave;
	Spinner spinnerReligion, spinnerCommunity, spinnerGothra, spinnerRelationStatus;
	ProgressDialog pDialog;
	LinearLayout linearAddGothra, linearAddCommunity, linearGothra;
	String religion, community, gothra;
	int religionPos, communityPos, gothraPos;

	Activity activity;
	Context context;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	String userId, nodeId;
	UserProfile changedUserProfile;
	boolean startLocationFlag = true;
	ArrayList<SpinnerItem> religionList, communityList, gothraList;
	ArrayList<String> RelationStatusList;
	String mobileUid,mobileNumber, mobileCode, successHash,croutonmsg;
	private final String TAG = "EditProfileFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

		activity = getActivity();
		context = getActivity().getApplicationContext();
		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);

		textViewName = (TextView) rootView.findViewById(R.id.textViewname);
		textViewEmail = (TextView) rootView.findViewById(R.id.textViewemail);
		editTextFirstName = (EditText) rootView.findViewById(R.id.editTextFirstName);
		editTextLastName = (EditText) rootView.findViewById(R.id.editTextLastName);
		// editTextEmail = (EditText) rootView.findViewById(R.id.editTextEmail);
		editTextAddCommunity = (EditText) rootView.findViewById(R.id.editTextaddcommunity);
		editTextAddGothra = (EditText) rootView.findViewById(R.id.editTextaddgothra);
		editTextDobdate = (EditText) rootView.findViewById(R.id.textViewdob);
		editTextWeddingDate = (EditText) rootView.findViewById(R.id.textViewweddate);
		editTextGender = (EditText) rootView.findViewById(R.id.editTextGender);
		linearAddCommunity = (LinearLayout) rootView.findViewById(R.id.linearaddcommunity);
		linearAddGothra = (LinearLayout) rootView.findViewById(R.id.linearaddgothra);
		linearGothra = (LinearLayout) rootView.findViewById(R.id.lineargothra);

		linearAddCommunity.setVisibility(View.GONE);
		linearAddGothra.setVisibility(View.GONE);

		spinnerRelationStatus = (Spinner) rootView.findViewById(R.id.spinnerMaritalStatus);
		RelationStatusList = new ArrayList<String>();
		RelationStatusList.add("Choose Marital Status");
		RelationStatusList.add("Single");
		RelationStatusList.add("Married");
		RelationStatusList.add("Divorced");
		spinnerRelationStatus.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				RelationStatusList));

		editTextLocality = (AutoCompleteTextView) rootView.findViewById(R.id.editTextLocality);
		editTextHomeTown = (EditText) rootView.findViewById(R.id.editTextHometown);
		editTextPincode = (EditText) rootView.findViewById(R.id.editTextPincode);
		editTextMobile = (EditText) rootView.findViewById(R.id.editTextMobile);
		// editTextReligion = (EditText)
		// rootView.findViewById(R.id.editTextReligion);
		// editTextCommunity = (EditText)
		// rootView.findViewById(R.id.editTextCommunity);
		// editTextGothra = (EditText)
		// rootView.findViewById(R.id.editTextGothra);
		editTextProfession = (EditText) rootView.findViewById(R.id.editTextProfession);

		spinnerReligion = (Spinner) rootView.findViewById(R.id.spinnerReligion);
		spinnerCommunity = (Spinner) rootView.findViewById(R.id.spinnerCommunity);
		spinnerGothra = (Spinner) rootView.findViewById(R.id.spinnerGothra);

		buttonSave = (Button) rootView.findViewById(R.id.buttonSave);

		buttonSave.setOnClickListener(this);
		spinnerReligion.setOnItemSelectedListener(this);
		spinnerCommunity.setOnItemSelectedListener(this);
		spinnerGothra.setOnItemSelectedListener(this);
		editTextDobdate.setOnClickListener(this);
		editTextWeddingDate.setOnClickListener(this);
		religionList = new ArrayList<SpinnerItem>();
		// religionList.add("Select");
		communityList = new ArrayList<SpinnerItem>();
		// communityList
		gothraList = new ArrayList<SpinnerItem>();
		// gothraList

		userId = sharedPreferences.getString("user_id", "0");
		nodeId = sharedPreferences.getString("node_id", userId);
		Log.d("edit profile userid", userId);

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final ReligionTask rT = new ReligionTask();
			rT.execute();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (rT.getStatus() == AsyncTask.Status.RUNNING){
						rT.cancel(true);
					}
				}
			}, 10000);

			final GothraTask gT = new GothraTask();
			gT.execute();
			Handler handler1 = new Handler();
			handler1.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (gT.getStatus() == AsyncTask.Status.RUNNING){
						gT.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
		// provide hinting for the location fields from Google Places API
		editTextLocality.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				startLocationFlag = false;
			}
		});
		locationHints = new ArrayList<String>();
		locationHintAdpter = new LocationHintAdapter(getActivity(), R.layout.item_location, locationHints);
		editTextLocality.setAdapter(locationHintAdpter);
		editTextLocality.addTextChangedListener(new TextWatcher() {

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
					if(startLocationFlag){
						searchPlacesTask = new SearchPlacesTask();
						searchPlacesTask.execute(editTextLocality.getText().toString().trim(),
								getResources().getString(R.string.places_key));
					}else{
						searchPlacesTask.cancel(true);
						startLocationFlag = true;
					}
					
					
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
					Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
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
	 public void onPause() {
	  super.onPause();
	  
	  if ((pDialog != null))
	   pDialog.dismiss();
	  pDialog = null;
	     
	 }
	public class ProfileTask extends AsyncTask<String, String, String> {
	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
			// pDialog = new ProgressDialog(activity);
			if(pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getProfileViewResponse(params[0], params[1],
					getResources().getString(R.string.hostname) + getResources().getString(R.string.url_view_profile2));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			
			Log.i("Profile update Fetch Response ", "" + response);
			try {
				
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					JSONArray data = loginResponseObject.getJSONArray("data");
					JSONObject userProfileData = (JSONObject) data.get(0);
					
					UserProfile userProfile = new UserProfile();
					
					userProfile.setDob(userProfileData.getString("Dob"));
					userProfile.setEmail(userProfileData.getString("Email"));
					userProfile.setFirstName(userProfileData.getString("Firstname"));
					userProfile.setLastName(userProfileData.getString("Lastname"));
					userProfile.setGender(userProfileData.getString("Gender"));
					userProfile.setLocality(userProfileData.getString("locality"));
					userProfile.setPincode(userProfileData.getString("pincode"));
					userProfile.setHometown(userProfileData.getString("hometown"));
					userProfile.setMobile(userProfileData.getString("mobile"));
					if (userProfileData.has("Maritalstatus")) {
						userProfile.setMaritalStatus(userProfileData.getString("Maritalstatus"));
					} else {
						userProfile.setMaritalStatus("NA");
					}
					userProfile.setWeddingDate(userProfileData.getString("wedding_Date"));
					userProfile.setReligion(userProfileData.getString("religion"));
					userProfile.setCommunity(userProfileData.getString("community"));
					userProfile.setGothra(userProfileData.getString("gothra"));
					userProfile.setProfession(userProfileData.getString("profession"));

					textViewName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
					textViewEmail.setText(userProfile.getEmail());

					if (!userProfile.getDob().equals("NA")) {
						editTextDobdate.setText(userProfile.getDob());
					} else {
						editTextDobdate.setText("");
					}
					if (!userProfile.getWeddingDate().equals("NA")) {
						editTextWeddingDate.setText(userProfile.getWeddingDate());
					} else {
						editTextWeddingDate.setText("");
					}
					if (!userProfile.getFirstName().equals("NA")) {
						editTextFirstName.setText(userProfile.getFirstName());
					} else {
						editTextFirstName.setText("");
					}
					if (!userProfile.getLastName().equals("NA")) {
						editTextLastName.setText(userProfile.getLastName());
					} else {
						editTextLastName.setText("");
					}
					if (!userProfile.getGender().equals("NA")) {
						editTextGender.setText(userProfile.getGender());
					} else {
						editTextGender.setText("");
					}
					if (!userProfile.getLocality().equals("NA")) {
						editTextLocality.setText(userProfile.getLocality());
					} else {
						editTextLocality.setText("");
					}
					if (!userProfile.getPincode().equals("NA")) {
						editTextPincode.setText(userProfile.getPincode());
					} else {
						editTextPincode.setText("");
					}
					if (!userProfile.getHometown().equals("NA")) {
						editTextHomeTown.setText(userProfile.getHometown());
					} else {
						editTextHomeTown.setText("");
					}
					if (!userProfile.getMobile().equals("NA")) {
						editTextMobile.setText(userProfile.getMobile());
					} else {
						editTextMobile.setText("");
					}
					if (!userProfile.getProfession().equals("NA")) {
						editTextProfession.setText(userProfile.getProfession());
					} else {
						editTextProfession.setText("");
					}
					
					int spinPosition = 0;
					if (userProfile.getMaritalStatus().equals("NA")) {
						spinPosition = 0;
					} else if (userProfile.getMaritalStatus().equals("Single")) {
						spinPosition = 1;
					} else if (userProfile.getMaritalStatus().equals("Married")) {
						spinPosition = 2;
					} else if (userProfile.getMaritalStatus().equals("Divorced")) {
						spinPosition = 3;
					}
					
					spinnerRelationStatus.setSelection(spinPosition);
					for (int i = 0; i < religionList.size(); i++) {
						if ((religionList.get(i).getValue()).equals(userProfile.getReligion()) || (religionList.get(i).getValue()).equals("NA")) {
							Log.d("Religion ------", "userProfile.getReligion()" + "," + religionList.get(i).getValue());
							spinnerReligion.setSelection(i);
						}
					}
					for (int i = 0; i < communityList.size(); i++) {
						if ((communityList.get(i).getValue()).equals(userProfile.getCommunity()) || (communityList.get(i).getValue()).equals("NA")) {
							spinnerCommunity.setSelection(i);
						}
					}
					if (communityList.size() == 0) {

					}
					for (int i = 0; i < gothraList.size(); i++) {
						if ((gothraList.get(i).getValue()).equals(userProfile.getGothra()) || (gothraList.get(i).getValue()).equals("NA")) {
							spinnerGothra.setSelection(i);
						}
					}
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}

	public class ReligionTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
			// pDialog = new ProgressDialog(activity);
			if(pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getReligionResponse(getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_religion));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;

			Log.i("Religion Fetch Response ", "" + response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				// int authenticationStatus =
				// loginResponseObject.getInt("AuthenticationStatus");
				// if(authenticationStatus == 1) {
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					JSONArray data = loginResponseObject.getJSONArray("data");
					JSONObject tempObject;
					SpinnerItem tempItem = new SpinnerItem();
					tempItem.setValue("Select a Religion");
					religionList.add(tempItem);
					for (int i = 0; i < data.length(); i++) {
						tempItem = new SpinnerItem();
						tempObject = data.getJSONObject(i);
						tempItem.setId(tempObject.getInt("id"));
						tempItem.setValue(tempObject.getString("name"));
						religionList.add(tempItem);
					}
					CustomDropDownAdapter religionAdapter = new CustomDropDownAdapter(context, religionList);
					Log.d(TAG, "religionAdapter Count religion : " + religionAdapter.getCount());
					spinnerReligion.setAdapter(religionAdapter);
					spinnerReligion.setSelection(religionPos);
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}

	public class CommunityTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
			// pDialog = new ProgressDialog(activity);
			if(pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getCommunityResponse(params[0], getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_community));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;

			Log.i("Community Fetch Response ", "" + response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				// int authenticationStatus =
				// loginResponseObject.getInt("AuthenticationStatus");
				// if(authenticationStatus == 1) {
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					JSONArray data = loginResponseObject.getJSONArray("data");
					JSONObject tempObject;
					communityPos = 0;
					communityList.clear();
					SpinnerItem tempItem = new SpinnerItem();
					tempItem.setValue("Select a Community");
					communityList.add(tempItem);
					for (int i = 0; i < data.length(); i++) {
						tempItem = new SpinnerItem();
						tempObject = data.getJSONObject(i);
						tempItem.setId(tempObject.getInt("id"));
						tempItem.setValue(tempObject.getString("name"));
						communityList.add(tempItem);
					}
					if (data.length() == 0) {
						 tempItem = new SpinnerItem();
						tempItem.setId(0);
						tempItem.setValue("Others");
						communityList.add(tempItem);
					}

					CustomDropDownAdapter communityAdapter = new CustomDropDownAdapter(context, communityList);
					Log.d(TAG, "communityAdapter Count community : " + communityAdapter.getCount());

					spinnerCommunity.setAdapter(communityAdapter);
					Log.d("community position", "" + communityPos);
					spinnerCommunity.setSelection(communityPos);
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}

	public class GothraTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
			// pDialog = new ProgressDialog(activity);
			if(pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getGothraResponse(getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_gothra));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Log.i("Gothra Fetch Response ", "" + response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					JSONArray data = loginResponseObject.getJSONArray("data");
					JSONObject tempObject;
					SpinnerItem tempItem = new SpinnerItem();
					tempItem.setValue("Select a Gothra");
					gothraList.add(tempItem);
					for (int i = 0; i < data.length(); i++) {
						tempItem = new SpinnerItem();
						tempObject = data.getJSONObject(i);
						tempItem.setId(tempObject.getInt("id"));
						tempItem.setValue(tempObject.getString("name"));
						gothraList.add(tempItem);
					}
					CustomDropDownAdapter gothraAdapter = new CustomDropDownAdapter(context, gothraList);
					spinnerGothra.setAdapter(gothraAdapter);
					spinnerGothra.setSelection(gothraPos);

					boolean bool = new ConDetect(getActivity()).isOnline();
					if (bool) {
						// Create object of AsycTask and execute
						final ProfileTask pT = new ProfileTask();
						pT.execute(nodeId, userId);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (pT.getStatus() == AsyncTask.Status.RUNNING){
									pT.cancel(true);
								}
							}
						}, 10000);
					} else {
						Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.buttonSave) {

			changedUserProfile = new UserProfile();
			changedUserProfile.setUserid(nodeId);
			changedUserProfile.setUid(userId);
			Log.d("---fff---", "" + nodeId);
			changedUserProfile.setDob(editTextDobdate.getText().toString().trim());
			// changedUserProfile.setEmail(editTextEmail.getText().toString());
			changedUserProfile.setFirstName(editTextFirstName.getText().toString());
			changedUserProfile.setLastName(editTextLastName.getText().toString());
			changedUserProfile.setGender(editTextGender.getText().toString());
			changedUserProfile.setLocality(editTextLocality.getText().toString());
			changedUserProfile.setPincode(editTextPincode.getText().toString());
			changedUserProfile.setHometown(editTextHomeTown.getText().toString());
			changedUserProfile.setMobile(editTextMobile.getText().toString());
			Log.d("--------------------- ", "" + (spinnerRelationStatus.getSelectedItem().toString().trim()));
			if ((spinnerRelationStatus.getSelectedItem().toString().trim()).equals("Choose Marital Status")) {
				changedUserProfile.setMaritalStatus("NA");
			} else if ((spinnerRelationStatus.getSelectedItem().toString().trim()).equals("Single")) {
				changedUserProfile.setMaritalStatus("1");
			} else if ((spinnerRelationStatus.getSelectedItem().toString().trim()).equals("Married")) {
				changedUserProfile.setMaritalStatus("2");
			} else if ((spinnerRelationStatus.getSelectedItem().toString().trim()).equals("Divorced")) {
				changedUserProfile.setMaritalStatus("3");
			} else {
				changedUserProfile.setMaritalStatus("NA");
			}
			changedUserProfile.setWeddingDate(editTextWeddingDate.getText().toString().trim());
			if((religionList.get(spinnerReligion.getSelectedItemPosition()).getValue()).equalsIgnoreCase("Select a Religion")){
				changedUserProfile.setReligion("NA");
			}else{
				changedUserProfile.setReligion(religionList.get(spinnerReligion.getSelectedItemPosition()).getValue());
			}
			if((communityList.get(spinnerCommunity.getSelectedItemPosition()).getValue()).equalsIgnoreCase("Select a Community")){
				changedUserProfile.setCommunity("NA");
			}else{
				changedUserProfile.setCommunity((communityList.get(spinnerCommunity.getSelectedItemPosition()).getValue()));
			}
			if((gothraList.get(spinnerGothra.getSelectedItemPosition()).getValue()).equalsIgnoreCase("Select a Gothra")){
				changedUserProfile.setGothra("NA");
			}else{
				changedUserProfile.setGothra((gothraList.get(spinnerGothra.getSelectedItemPosition()).getValue()));
			}
			changedUserProfile.setProfession(editTextProfession.getText().toString());
			if ((editTextAddCommunity.getText().toString().trim().length() > 0)
					&& (linearAddCommunity.isShown() == true)) {
				changedUserProfile.setAddCommunity(editTextAddCommunity.getText().toString());
			} else {
			}
			if ((editTextAddGothra.getText().toString().trim().length() > 0) && (linearAddGothra.isShown() == true)) {
				changedUserProfile.setAddGothra(editTextAddGothra.getText().toString());
			} else {
			}

			Log.i("-------------adfjsdfskjsdg-------- ", religionList.get(spinnerReligion.getSelectedItemPosition())
					.getValue());

			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				// Create object of AsycTask and execute
				final SaveProfileTask sPT = new SaveProfileTask();
				sPT.execute(changedUserProfile);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (sPT.getStatus() == AsyncTask.Status.RUNNING){
							sPT.cancel(true);
						}
					}
				}, 10000);
			} else {
				Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}
		}
		if (v.getId() == R.id.textViewdob) {
			dateDialog(editTextDobdate);
		}
		if (v.getId() == R.id.textViewweddate) {
			dateDialog(editTextWeddingDate);
		}
	}

	public class SaveProfileTask extends AsyncTask<UserProfile, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
			
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("submitting data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(UserProfile... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getEditProfileResponse(params[0], getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_edit_profile1_url));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Log.i("Edit Profile Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				// String responseResult =
				// loginResponseObject.getString("Status");
				int authenticationStatus = loginResponseObject.getInt("AuthenticationStatus");
				if (loginResponseObject.has("msg")) {
					croutonmsg = loginResponseObject.getString("msg");
				}
				if (loginResponseObject.has("mobilecode")) {
					mobileCode = loginResponseObject.getString("mobilecode");
				}
				if (loginResponseObject.has("newhash")) {
					successHash = loginResponseObject.getString("newhash");
				}
				if (loginResponseObject.has("id")) {
					mobileUid = loginResponseObject.getString("id");
				}
				if (loginResponseObject.has("mobile no")) {
					mobileNumber = loginResponseObject.getString("mobile no");
				}			
				// if(responseResult.equals("Success")) {
				if (authenticationStatus == 1) {
					if(loginResponseObject.has("flag")){
						Intent intentMobile = new Intent(activity,OtpcodeActivity.class);
						intentMobile.putExtra("croutonmsg", croutonmsg);
						intentMobile.putExtra("mobileCode", mobileCode);
						intentMobile.putExtra("successHash", successHash);
						intentMobile.putExtra("mobileUid", mobileUid);
						intentMobile.putExtra("mobileNumber", mobileNumber);
						startActivity(intentMobile);
					}else{	
						savedSuccessfully();
					}
				} else if(authenticationStatus == 2){
					Crouton.makeText(activity, "The mobile number you entered is already registered with us. PLease enter a different mobile number", Style.ALERT).show();
				}else if(authenticationStatus == 4){
					Crouton.makeText(activity, croutonmsg, Style.ALERT).show();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			  if ((pDialog != null) && pDialog.isShowing())
				   pDialog.dismiss();
				  pDialog = null;
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}

	}

	public class SearchPlacesTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			if ((pDialog != null))
				pDialog.dismiss();
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
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
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
				editTextLocality.setAdapter(locationHintAdpter);
				locationHintAdpter.notifyDataSetChanged();
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			//Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
		}
	}

	private void savedSuccessfully() {
		Log.d(TAG, "Profile edited successfully!");
		CroutonMessage.showCroutonInfo(activity, "You have successfully updated the profile information!", 7000);
		((MainActivity) this.getActivity()).changeFragment("ProfileFragment");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int spos, long dpos) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.spinnerReligion:
			spinnerReligion.setSelection(spos);
			if (religionList.get(spos).getValue().equalsIgnoreCase("HINDU")) {
				linearGothra.setVisibility(View.VISIBLE);
			} else {
				linearGothra.setVisibility(View.GONE);
			}
			if (!religionList.get(spos).getValue().equalsIgnoreCase("Select a Religion")) {
				boolean bool = new ConDetect(getActivity()).isOnline();
				if (bool) {
					// Create object of AsycTask and execute
					final CommunityTask communityTask = new CommunityTask();
					communityTask.execute("" + religionList.get(spos).getId());
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (communityTask.getStatus() == AsyncTask.Status.RUNNING){
								communityTask.cancel(true);
							}
						}
					}, 10000);
				} else {
					CroutonMessage.showCroutonAlert(activity, "!No Internet Connection,Try again", 7000);
				}
			}else{
				SpinnerItem tempItem = new SpinnerItem();
				tempItem.setValue("Select a Community");
				communityList.add(tempItem);
				CustomDropDownAdapter communityAdapter = new CustomDropDownAdapter(context, communityList);

				spinnerCommunity.setAdapter(communityAdapter);
				spinnerCommunity.setSelection(communityPos);
			}
			

			break;
		case R.id.spinnerCommunity:
			spinnerCommunity.setSelection(spos);
			Log.d(TAG, "-------------------------community-------------------------" + spos);
			if (communityList.get(spos).getValue().equalsIgnoreCase("Others")) {
				linearAddCommunity.setVisibility(View.VISIBLE);
			} else {
				linearAddCommunity.setVisibility(View.GONE);
			}

			break;
		case R.id.spinnerGothra:
			spinnerGothra.setSelection(spos);
			if (gothraList.get(spos).getValue().equalsIgnoreCase("Others")) {
				linearAddGothra.setVisibility(View.VISIBLE);
			} else {
				linearAddGothra.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void dateDialog(final EditText txtview) {
		// TODO Auto-generated method stub
		int day, month, year;

		Log.d("profile", "date text click!!");
		Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		
		final DateSetListener _datePickerDialogCallback = new DateSetListener(txtview);
		final DatePickerDialog dpd = new DatePickerDialog(getActivity(),_datePickerDialogCallback, year, month, day);
		
		dpd.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					set = true;
					 DatePicker datePicker = dpd.getDatePicker();
					 _datePickerDialogCallback.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
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
	private class DateSetListener implements DatePickerDialog.OnDateSetListener {
		EditText txtview;
		public DateSetListener(EditText txtview)
		{
			DateSetListener.this.txtview = txtview;
		}
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			if (set) {
				String date = dayOfMonth + "-" + (1 + monthOfYear) + "-" + year;
				txtview.setText(date);
				
			}
		}
	}
}
