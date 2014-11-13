package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.AutoCompleteRelationArrayAdapter;
import com.parivartree.adapters.LocationHintAdapter;
import com.parivartree.customviews.CustomAutoCompleteTextView;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.MyObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CreateRelationFragment extends Fragment implements
		OnClickListener, ValidationListener {

	private String TAG = "CreateRelationFragment";
	ArrayAdapter<MyObject> myAdapter;
	ArrayList<MyObject> ObjectItemData = new ArrayList<MyObject>();
	// MyObject[] ObjectItemData;
	String relationId, nodeId, userId, othersUserId, sessionname, relationName,
			toWhomName, userName;
	Activity activity;
	Context context;
	Button create;
	String autoEmail, gender, finalgender;
	HashMap<String, String> idstorage;
	TextView textViewTitle;
	CustomAutoCompleteTextView searchUserAutoComplete;
	@Required(order = 1)
	EditText firstNameEditText;
	@Required(order = 2)
	EditText lastNameEditText;
	@Required(order = 3)
	EditText emailEditText;
	@Required(order = 4)
	AutoCompleteTextView editLocation;
	CheckBox checkEmail;
	SearchUserTask searchUserTask = null;
	List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
	private LocationHintAdapter locationHintAdpter;
	private ArrayList<String> locationHints;
	SearchPlacesTask searchPlacesTask;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	int request_type = 1;
	// Keys used in Hashmap
	String[] from = { "txtname" };

	// Ids of views in listview_layout
	int[] to = { R.id.txtname };

	// setting this flag for avoid call searchuser AsyncTask atlast item is
	// selecting
	int flag = 0;
	// Saripaar validator
	Validator validator;

	public CreateRelationFragment() {
		
	}

	public CreateRelationFragment(String relationId, String nodeId) {
		this.relationId = relationId;
		this.nodeId = nodeId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = getActivity();
		context = getActivity().getApplicationContext();

		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName()
						+ getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		userId = sharedPreferences.getString("user_id", "0");
		gender = sharedPreferences.getString("selectgender", "1");
		toWhomName = (sharedPreferences.getString("node_first_name", "NA")
				+ " " + sharedPreferences.getString("node_last_name", "NA"));
		sessionname = sharedPreferences.getString("sessionname",
				"Not Available");
		validator = new Validator(this);
		validator.setValidationListener(this);

		View rootView = inflater.inflate(R.layout.fragment_create_relation,
				container, false);

		if (savedInstanceState == null) {
			// selectedVideoNumber = playlistItemId;
		} else {
			relationId = savedInstanceState.getString("relationId");
			nodeId = savedInstanceState.getString("nodeId");
		}

		if (relationId.equals("1")) {
			finalgender = "1";
			relationName = "Father";
		} else if (relationId.equals("2")) {
			finalgender = "2";
			relationName = "Mother";
		} else if (relationId.equals("4")) {
			finalgender = "1";
			relationName = "Brother";
		} else if (relationId.equals("5")) {
			finalgender = "2";
			relationName = "Sister";
		} else if (relationId.equals("6")) {
			finalgender = "1";
			relationName = "Son";
		} else if (relationId.equals("7")) {
			finalgender = "2";
			relationName = "Daughter";
		} else if (relationId.equals("3")) {
			if (gender.equalsIgnoreCase("1")) {
				finalgender = "2";
				relationId = "3";
				relationName = "Wife";
			} else {
				finalgender = "1";
				relationId = "8";
				relationName = "Husband";
			}
		}

		// Instantiating an adapter to store each items
		// R.layout.listview_layout defines the layout of each item
		textViewTitle = (TextView) rootView.findViewById(R.id.relationtitle2);
		searchUserAutoComplete = (CustomAutoCompleteTextView) rootView
				.findViewById(R.id.autoCompleteUser);
		searchUserAutoComplete.clearFocus();
		firstNameEditText = (EditText) rootView.findViewById(R.id.firstName);
		firstNameEditText.clearFocus();
		lastNameEditText = (EditText) rootView.findViewById(R.id.lastName);
		lastNameEditText.clearFocus();
		emailEditText = (EditText) rootView.findViewById(R.id.email);
		emailEditText.clearFocus();
		checkEmail = (CheckBox) rootView.findViewById(R.id.checkboxemail);
		editLocation = (AutoCompleteTextView) rootView
				.findViewById(R.id.autocompleterelationlocation);
		create = (Button) rootView.findViewById(R.id.create);

		textViewTitle.setText("Adding " + relationName + " of " + toWhomName);
		// firstNameEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		lastNameEditText.setText(sharedPreferences.getString("node_last_name",
				"NA"));
		// myAdapter = new AutoCompleteRelationArrayAdapter(getActivity(),
		// R.layout.list_view_row, ObjectItemData);
		// myAdapter.setNotifyOnChange(true);
		// searchUserAutoComplete.setAdapter(myAdapter);
		searchUserAutoComplete
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int spos, long dpos) {
						// TODO Auto-generated method stub
						flag = 1;

						searchUserAutoComplete.setText("");
						flag = 0;

					}
				});
		searchUserAutoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

				boolean bool = new ConDetect(activity).isOnline();
				if (bool) {
					if (searchUserTask != null) {
						searchUserTask.cancel(true);
					}

					Log.d(TAG,
							"-------------------------------------------------------------------------------");
					Log.d("Search user", "AsyncTask calling");
					if (flag == 0) {
						searchUserTask = new SearchUserTask();
						searchUserTask.execute(s.toString(), userId);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (searchUserTask.getStatus() == AsyncTask.Status.RUNNING){
									searchUserTask.cancel(true);
								}
							}
						}, 10000);
					}
				} else {
					Toast.makeText(activity,
							"!No Internet Connection,Try again",
							Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		// provide hinting for the location fields from Google Places API
		locationHints = new ArrayList<String>();
		locationHintAdpter = new LocationHintAdapter(activity,
				R.layout.item_location, locationHints);
		editLocation.setAdapter(locationHintAdpter);
		editLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				Log.d("Search User ", "s=" + s + " ,start=" + start
						+ " ,count=" + count + " ,after=" + after);
				boolean bool = new ConDetect(activity).isOnline();
				if (bool) {
					if (searchPlacesTask != null) {
						searchPlacesTask.cancel(true);
					}
					Log.d("Search user", "AsyncTask calling");
					searchPlacesTask = new SearchPlacesTask();
					searchPlacesTask.execute(s.toString().trim(),
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
					Toast.makeText(activity,
							"!No Internet Connection,Try again",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		create.setOnClickListener(this);
		checkEmail.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (buttonView.isChecked()) {
					boolean bool = new ConDetect(activity).isOnline();
					if (bool) {
						final AutoGenerateEmailTask autoGenerateEmailTask = new AutoGenerateEmailTask();
						autoGenerateEmailTask.execute();
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (autoGenerateEmailTask.getStatus() == AsyncTask.Status.RUNNING){
									autoGenerateEmailTask.cancel(true);
								}
							}
						}, 10000);
					} else {
						Toast.makeText(activity,
								"!No Internet Connection,Try again",
								Toast.LENGTH_LONG).show();
					}

				} else {
					emailEditText.setText("");
					autoEmail = null;
				}
			}
		});
		// ssearchUserAutoComplete.setKeyListener(this);
		return rootView;
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("relationId", this.relationId);
		savedInstanceState.putString("nodeId", this.nodeId);
		Log.d(TAG, "instance state saved");
	}

	public class AutoGenerateEmailTask extends
			AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils
					.autoGenerateEmailResponse(getResources().getString(
							R.string.hostname)
							+ getResources().getString(
									R.string.url_autogenerate_email));

		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Profile Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String result = loginResponseObject.getString("Result");
				if (result.equals("Success")) {
					autoEmail = loginResponseObject.getString("email");
					emailEditText.setText("Autogenerated");
				} else {
					emailEditText.setText("Try Again");
					autoEmail = "";
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(context,
						"Invalid Server Content - " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "" + "Invalid Server content !!");

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

	public class CreateRelationTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String httpResponse = null;
			if (params[0].equals("new")) {
				httpResponse = HttpConnectionUtils.createNewRelationResponse(
						params[1],
						params[2],
						params[3],
						params[4],
						params[5],
						params[6],
						params[7],
						params[8],
						getResources().getString(R.string.hostname)
								+ getResources().getString(
										R.string.url_add_relation));
			}
			return httpResponse;

		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Relation Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				int responseResult = loginResponseObject
						.getInt("AuthenticationStatus");
				if (responseResult == 1) {
					Log.d(TAG, " success...........!!");
					sharedPreferencesEditor.putString("node_id",
							sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					String nodeName = firstNameEditText.getText().toString()
							+ " " + lastNameEditText.getText().toString();
					Crouton.makeText(activity,
							"You have successfully invited " + nodeName,
							Style.INFO).show();
					// Toast.makeText(getActivity(), "Relation  added",
					// Toast.LENGTH_LONG).show();
					((MainActivity) activity).changeFragment("HomeFragment");
				} else if (responseResult == 2) {
					Crouton.makeText(activity, "invalid email-id syntax...",
							Style.INFO).show();
				} else if (responseResult == 5) {
					String invite = loginResponseObject.getString("invite");
					String negate = loginResponseObject.getString("negate");
					JSONArray account = loginResponseObject
							.getJSONArray("accounts");
					JSONObject details = (JSONObject) account.get(0);
					String fname = details.getString("firstname");
					String lname = details.getString("lastname");
					String fullname = fname + " " + lname;
					String otheruserid = details.getString("userid");
					if (invite.equals("1") && negate.equals("0")) {
						if (nodeId.equals(userId)) {
							userDialog(fullname, otheruserid, "invite");
						} else {
							userDialog(fullname, otheruserid, "recommend");
						}

					} else if (invite.equals("0") && negate.equals("1")) {

						userDialog(fullname, otheruserid, "unhide");

					} else if (invite.equals("0") && negate.equals("0")) {

						Toast.makeText(context,
								fullname + " is already connected",
								Toast.LENGTH_LONG).show();
					}
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(context,
						"Invalid Server Content - " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}
		}		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class InviteRelationTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String httpResponse = null;
			if (params[0].equals("invite")) {
				request_type = 1;
				httpResponse = HttpConnectionUtils.createExistRelationResponse(
						params[1],
						params[2],
						params[3],
						params[4],
						params[5],
						activity.getResources().getString(R.string.hostname)
								+ activity.getResources().getString(
										R.string.url_invite_user));
			}
			if (params[0].equals("recommend")) {
				request_type = 2;
				httpResponse = HttpConnectionUtils
						.createOthersRelationResponse(
								params[1],
								params[2],
								params[3],
								params[4],
								params[5],
								params[6],
								activity.getResources().getString(
										R.string.hostname)
										+ activity.getResources().getString(
												R.string.url_invite_user));
			}

			return httpResponse;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Relation Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				int responseResult = loginResponseObject
						.getInt("AuthenticationStatus");
				if (responseResult == 1) {
					// TODO store the login response and
					// JSONArray data =
					// loginResponseObject.getJSONArray("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					Log.d(TAG, " success...........!!");
					sharedPreferencesEditor.putString("node_id",
							sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					// String nodeName = firstNameEditText.getText().toString()
					// + " " + lastNameEditText.getText().toString();
					// Crouton.makeText(activity,
					// "You have successfully invited " + nodeName ,
					// Style.INFO).show();
					// Toast.makeText(getActivity(),
					// "Invite Successful",Toast.LENGTH_LONG).show();
					String nodeName = userName;
					if (request_type == 1) {
						// AutoCompleteRelationArrayAdapter.this.userName;
						Crouton.makeText(
								activity,
								"You have successfully invited " + nodeName
										+ " to your family tree.", Style.INFO)
								.show();
					} else if (request_type == 2) {
						Crouton.makeText(
								activity,
								"You have successfully recommended " + nodeName
										+ " to " + toWhomName
										+ "'s family tree.", Style.INFO).show();
					}
					((MainActivity) activity).changeFragment("HomeFragment");
				} else if (responseResult == 2) {
					Crouton.makeText(activity,
							"email id is invalid. Please try again... ",
							Style.INFO).show();
					// Toast.makeText(getActivity(),
					// "Email ID is wrong Please Try again",
					// Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(context,
						"Invalid Server Content - " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}
		}		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.create) {
			validator.validate();
		}
	}

	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		boolean bool = new ConDetect(activity).isOnline();
		if (bool) {
			if (emailEditText.getText().toString().equals("Autogenerated")
					|| emailEditText.getText().toString().equals("Try Again")) {

			} else {
				autoEmail = emailEditText.getText().toString();
			}
				final CreateRelationTask cRT2 = new CreateRelationTask();
			// String sessionname = sharedPreferences.getString("sessionname",
			// "Not Available");
			cRT2.execute("new",userId,nodeId, relationId, firstNameEditText.getText().toString(),
					lastNameEditText.getText().toString(), autoEmail, finalgender, sessionname);

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (cRT2.getStatus() == AsyncTask.Status.RUNNING){
							cRT2.cancel(true);
						}
					}
				}, 10000);
		} else {
			Toast.makeText(activity, "!No Internet Connection,Try again",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else if (failedView instanceof AutoCompleteTextView) {
			failedView.requestFocus();
			((AutoCompleteTextView) failedView).setError(message);
		} else {
			Log.d("Signup settings ", message);
		}
	}

	//
	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(),
	// "inside key listener "+keyCode+" event "+event,
	// Toast.LENGTH_LONG).show();
	// SearchUserTask searchUserTask= new SearchUserTask();
	// searchUserTask.execute(searchUserAutoComplete.getText().toString(),userId);
	// return true;
	// }

	public class SearchUserTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			if (searchUserAutoComplete.getText().toString().trim().equals("")) {
				searchUserAutoComplete.dismissDropDown();
				ObjectItemData.clear();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			// ---------change method name
			if (isCancelled()) {
				// Log.d(TAG, "async task is cancelled");
				return (null); // don't forget to terminate this method
			}
			return HttpConnectionUtils.getSearchUserResponse(
					params[0],
					params[1],
					getResources().getString(R.string.hostname)
							+ activity.getResources().getString(
									R.string.url_search_users));

		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			Log.i("event list Response ", response);

			try {
				JSONArray eventListResponseArray = new JSONArray(response);
				Log.d(TAG, "onpostexecute : searching users");
				// ObjectItemData = new
				// MyObject[eventListResponseArray.length()];
				ObjectItemData.clear();
				for (int i = 0; i < eventListResponseArray.length(); i++) {
					JSONObject c = eventListResponseArray.getJSONObject(i);
					if (c.has("result")) {
						String result = c.getString("result");
						if (result.equals("Success")) {
							String inviteUserId = c.getString("id");
							String name = c.getString("firstname") + " "
									+ c.getString("lastname");
							String status = c.getString("parameter");
							if (status.trim().equalsIgnoreCase("Unhide")) {

							} else {

								ObjectItemData.add(new MyObject(name,
										inviteUserId, status, relationId,
										nodeId));
							}
						}
					} else {
						ObjectItemData.add(new MyObject("No results found",
								null, "NA", relationId, nodeId));
					}

				}

				Log.i(TAG, "ObjectItemData size: - " + ObjectItemData.size());
				if (ObjectItemData.size() > 20) {
					ObjectItemData.subList(20, ObjectItemData.size() - 1)
							.clear();
					;
				}
				Log.d(TAG, "dataSetChanged");
				myAdapter = new AutoCompleteRelationArrayAdapter(activity,
						R.layout.list_view_row, ObjectItemData);
				searchUserAutoComplete.setAdapter(myAdapter);
				myAdapter.notifyDataSetChanged();
				// myAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(context,
						"Invalid Server Content - " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}

		}		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
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
				JSONArray predictionsArray = createEventObject
						.getJSONArray("predictions");
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
				locationHintAdpter = new LocationHintAdapter(activity,
						R.layout.item_location, locationHints);
				editLocation.setAdapter(locationHintAdpter);
				locationHintAdpter.notifyDataSetChanged();
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				if (activity.getApplicationContext() != null) {
					Toast.makeText(activity,
							"Invalid Server Content - " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				
				Log.d(TAG, "Invalid Server content!!");
			}
		}		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	private void userDialog(final String name, final String otherid,
			final String type) {
		String msg;
		String btnmsg;
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		userName = name;
		if (type.equals("invite")) {
			msg = "The User "
					+ name
					+ " is already available in another Family Tree would you like to invite.";
			btnmsg = "Invite";
		} else if (type.equals("recommend")) {
			msg = "The User "
					+ name
					+ " is already available in another Family Tree would you like to recommend.";
			btnmsg = "recommend";
		} else {
			msg = "The User " + name + " is hidden by you";
			btnmsg = "Unhide";
		}
		// Setting Dialog Title
		alertDialog.setTitle("Parivartree");
		// Setting Dialog Message
		alertDialog.setMessage(msg);
		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.signoutconfirm);
		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton(btnmsg,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
		if (type.equals("invite")) {
					final InviteRelationTask inviteRelationTask = new InviteRelationTask();
							inviteRelationTask.execute("invite", otherid,userId, relationId,sessionname,userId);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (inviteRelationTask.getStatus() == AsyncTask.Status.RUNNING){
								inviteRelationTask.cancel(true);
							}
						}
					}, 10000);
			} else if (type.equals("recommend")) {
				final InviteRelationTask inviteRelationTask1 = new InviteRelationTask();
				inviteRelationTask1.execute("recommend",otherid,nodeId, relationId,sessionname,name,userId);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (inviteRelationTask1.getStatus() == AsyncTask.Status.RUNNING){
							inviteRelationTask1.cancel(true);
						}
					}
				}, 10000);
			} else {
				final UnhideUserTask unhideTask = new UnhideUserTask();
							unhideTask.execute(otherid, userId);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (unhideTask.getStatus() == AsyncTask.Status.RUNNING){
							unhideTask.cancel(true);
						}
					}
				}, 10000);
						}
					}
				});

		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to invoke NO event
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	public class UnhideUserTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getHideUserResponse(
					params[0],
					params[1],
					activity.getResources().getString(R.string.hostname)
							+ activity.getResources().getString(
									R.string.url_unhide_user));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			Log.i("delete user Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					Log.i("unhide user Fetch Response ", "user unhide");
					String nodeName = firstNameEditText.getText().toString()
							+ " " + lastNameEditText.getText().toString();
					Crouton.makeText(activity,
							"You have successfully un-hidden " + nodeName,
							Style.INFO).show();
					((MainActivity) activity).changeFragment("HomeFragment");

				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(activity,
						"Invalid Server Content - " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}
}
