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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
//import com.gorillalogic.monkeytalk.server.JsonServer;







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
import com.parivartree.helpers.CroutonMessage;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.MyObject;
import com.parivartree.models.SearchRecordRelation;
import com.parivartree.models.SearchRecords;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CreateRelationFragment extends Fragment implements OnClickListener, ValidationListener {
	long startTime,elapsedTime ;
	private String TAG = "CreateRelationFragment";
	ArrayAdapter<MyObject> myAdapter;
	ArrayList<MyObject> ObjectItemData = new ArrayList<MyObject>();
	// MyObject[] ObjectItemData;
	String relationId, nodeId, userId, othersUserId, sessionname, relationName, toWhomName, userName;
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
	private ArrayList<SearchRecords> searchRecordsList;
	private ArrayList<SearchRecordRelation> relationRecordsList;
	SearchPlacesTask searchPlacesTask;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	int request_type = 1;
	// Keys used in Hashmap
	String[] from = { "txtname" };
	
	private ProgressDialog pDialog;
	boolean startLocationFlag = true;
	ScrollView parentscrollview;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Log.d(TAG, "relationId - " + relationId + ", nodeId - " + nodeId);
		activity = getActivity();
		context = getActivity().getApplicationContext();
		
		if(savedInstanceState != null && savedInstanceState.containsKey("relationId") && savedInstanceState.containsKey("nodeId")) {
			relationId = savedInstanceState.getString("relationId");
			nodeId = savedInstanceState.getString("nodeId");
		}

		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		userId = sharedPreferences.getString("user_id", "0");
		gender = sharedPreferences.getString("selectgender", "1");
		toWhomName = (sharedPreferences.getString("node_first_name", "NA") + " " + sharedPreferences.getString(
				"node_last_name", "NA"));
		sessionname = sharedPreferences.getString("sessionname", "Not Available");
		validator = new Validator(this);
		validator.setValidationListener(this);

		View rootView = inflater.inflate(R.layout.fragment_create_relation, container, false);
		searchRecordsList = new ArrayList<SearchRecords>();
		relationRecordsList = new ArrayList<SearchRecordRelation>();
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
		
		//parent for keybord issues
		parentscrollview = (ScrollView) rootView.findViewById(R.id.parentscrollview);
		
		textViewTitle = (TextView) rootView.findViewById(R.id.relationtitle2);
		searchUserAutoComplete = (CustomAutoCompleteTextView) rootView.findViewById(R.id.autoCompleteUser);
		searchUserAutoComplete.clearFocus();
		firstNameEditText = (EditText) rootView.findViewById(R.id.firstName);
		firstNameEditText.clearFocus();
		lastNameEditText = (EditText) rootView.findViewById(R.id.lastName);
		lastNameEditText.clearFocus();
		emailEditText = (EditText) rootView.findViewById(R.id.email);
		emailEditText.clearFocus();
		checkEmail = (CheckBox) rootView.findViewById(R.id.checkboxemail);
		editLocation = (AutoCompleteTextView) rootView.findViewById(R.id.autocompleterelationlocation);
		create = (Button) rootView.findViewById(R.id.create);

		textViewTitle.setText("Adding " + relationName + " of " + toWhomName);
		// firstNameEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		lastNameEditText.setText(sharedPreferences.getString("node_last_name", "NA"));
		// myAdapter = new AutoCompleteRelationArrayAdapter(getActivity(),
		// R.layout.list_view_row, ObjectItemData);
		// myAdapter.setNotifyOnChange(true);
		// searchUserAutoComplete.setAdapter(myAdapter);
		
		parentscrollview.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//hideKeyboard(v);
				return false;
			}
		});
		
		
		searchUserAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				searchUserAutoComplete.setText("");
				}
		});
		editLocation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				startLocationFlag = false;
			}
		});
//		searchUserAutoComplete.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int spos, long dpos) {
//				// TODO Auto-generated method stub
//				
//				searchUserAutoComplete.setText("");
//			
//
//			}
//		});
		searchUserAutoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				if(s.toString().startsWith("com.parivartree.models")){
					
				}else if(s.toString().length() > 0){
					Log.d(TAG, "--length--"+s.toString().length());
					Log.d(TAG, "--log--"+s+","+start+","+count+","+after);
				boolean bool = new ConDetect(getActivity()).isOnline();
				if (bool) {
					if (searchUserTask != null) {
						searchUserTask.cancel(true);
					}
					Log.d("Search user", "AsyncTask calling");
						searchUserTask = new SearchUserTask();
						searchUserTask.execute(s.toString(), userId);
					
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (searchUserTask.getStatus() == AsyncTask.Status.RUNNING) {
									searchUserTask.cancel(true);
								}
							}
						}, 10000);
					
				} else {
					Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}
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
					if(startLocationFlag){
					searchPlacesTask = new SearchPlacesTask();
					searchPlacesTask.execute(s.toString().trim(), getResources().getString(R.string.places_key));
					
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (searchPlacesTask.getStatus() == AsyncTask.Status.RUNNING) {
								searchPlacesTask.cancel(true);
							}
						}
					}, 10000);
					}else{
						searchPlacesTask.cancel(true);
						startLocationFlag = true;
					}
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
		create.setOnClickListener(this);
		checkEmail.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (buttonView.isChecked()) {
					boolean bool = new ConDetect(getActivity()).isOnline();
					if (bool) {
						final AutoGenerateEmailTask autoGenerateEmailTask = new AutoGenerateEmailTask();
						autoGenerateEmailTask.execute();
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (autoGenerateEmailTask.getStatus() == AsyncTask.Status.RUNNING) {
									autoGenerateEmailTask.cancel(true);
								}
							}
						}, 10000);
					} else {
						Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
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
	
	@Override
	public void onPause() {
		super.onPause();
		
		if ((pDialog != null) && pDialog.isShowing())
			pDialog.dismiss();
		pDialog = null;
	    
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		if(!relationId.equals(null) && !nodeId.equals(null)) {
			outState.putString("relationId", relationId);
			outState.putString("nodeId", nodeId);
		}
	}
	
	public class AutoGenerateEmailTask extends AsyncTask<String, String, String> {
		
		//private ProgressDialog pDialog;
		
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
			return HttpConnectionUtils.autoGenerateEmailResponse(getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_autogenerate_email));
			
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
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
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "" + "Invalid Server content !!");

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
			checkEmail.setChecked(!checkEmail.isChecked());
		}

	}

	public class CreateRelationTask extends AsyncTask<String, String, String> {

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
						params[9],
						getResources().getString(R.string.hostname)
								+ getResources().getString(R.string.url_add_relation));
			}
			return httpResponse;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Log.i("Relation Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				int responseResult = loginResponseObject.getInt("AuthenticationStatus");
				if (responseResult == 1) {
					Log.d(TAG, " success...........!!");
					if (loginResponseObject.has("records")) {
						searchRecordsList.clear();
						JSONArray records = loginResponseObject.getJSONArray("records");
						for (int i = 0; i < records.length(); i++) {
							JSONObject item = records.getJSONObject(i);

							SearchRecords searchRecordObject = new SearchRecords();
							searchRecordObject.setUserid(item.getInt("userid"));
							searchRecordObject.setGender(item.getInt("gender"));
							searchRecordObject.setStatus(item.getInt("status"));
							searchRecordObject.setDeceased(item.getInt("deceased"));
							searchRecordObject.setConnected(item.getInt("connected"));
							searchRecordObject.setImageexists(item.getInt("imageexists"));
							searchRecordObject.setInvite(item.getInt("invite"));
							searchRecordObject.setCity(item.getString("city"));
							searchRecordObject.setState(item.getString("state"));
							searchRecordObject.setFirstname(item.getString("firstname"));
							searchRecordObject.setLastname(item.getString("lastname"));
							relationRecordsList = new ArrayList<SearchRecordRelation>();
							if (item.has("relation")) {
								relationRecordsList.clear();
								
								JSONArray relation = item.getJSONArray("relation");
								for (int j = 0; j < relation.length(); j++) {
									JSONObject itemrelation = relation.getJSONObject(j);
									SearchRecordRelation searchRecordRelation = new SearchRecordRelation();
									searchRecordRelation.setRelationname(itemrelation.getString("relationname"));
									searchRecordRelation.setName(itemrelation.getString("name"));
									searchRecordRelation.setId(itemrelation.getInt("id"));
									searchRecordRelation.setImageexists(itemrelation.getInt("imageexists"));
				
									relationRecordsList.add(searchRecordRelation);
								}
//								StringBuilder relationString = new StringBuilder();
//								for(HashMap<String, String> hash : relationRecordsList){
//									relation1 = hash.get("name")+" ("+hash.get("relationname")+") ";
//									relationString.append(relation1);
//								}
//								Log.d("  #####  ","relation List"+relationString);
								Log.d("ooooooListoooo ", " "+relationRecordsList);
								//searchRecordObject.setRelationRecords(relationRecordsList);
								//Log.d("ooooooList hashoooo ", " "+searchRecordObject.getRelationRecords());
							}
							searchRecordObject.setRelationRecords(relationRecordsList);
							Log.d("ooooooList hashoooo ", " "+searchRecordObject.getRelationRecords());
							searchRecordsList.add(searchRecordObject);
						}	
						for(int z=0;z<searchRecordsList.size();z++){
							
							//searchRecordsList.get(z).getRelationRecords();
							Log.d("oooooooooo", ""+searchRecordsList.get(z).getFirstname());
							Log.d("oooooooooo", ""+searchRecordsList.get(z).getRelationRecords());
						}
						Bundle bundle = new Bundle();
						bundle.putParcelableArrayList("searchrelationList", searchRecordsList);
						bundle.putString("firstname", ((firstNameEditText.getText().toString().trim())));		
						bundle.putString("lastname", ((lastNameEditText.getText().toString().trim())));
						bundle.putString("email", autoEmail);
						bundle.putString("locality", (editLocation.getText().toString().trim()));
						
						if (loginResponseObject.has("nodeid")) {
							bundle.putString("recommendnodeid", loginResponseObject.getString("nodeid"));
						}
						if (loginResponseObject.has("relationid")) {
							bundle.putString("myrelationid", loginResponseObject.getString("relationid"));
						}
						((MainActivity) activity).changeFragment("SearchCreateRelationFragment",bundle);
					} else {

						sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
						sharedPreferencesEditor.commit();
						String nodeName = firstNameEditText.getText().toString() + " "
								+ lastNameEditText.getText().toString();
					
						CroutonMessage.showCroutonInfo(activity, "You have successfully added " + nodeName, 10000);
						((MainActivity) activity).changeFragment("HomeFragment");

					}
				} else if (responseResult == 2) {
					Crouton.makeText(activity, "invalid email-id syntax...", Style.INFO).show();
				} else if (responseResult == 5) {
					String invite = loginResponseObject.getString("invite");
					String negate = loginResponseObject.getString("negate");
					JSONArray account = loginResponseObject.getJSONArray("accounts");
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
						CroutonMessage.showCroutonAlert(activity, fullname + " is already connected", 10000);
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
				Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
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

	public class InviteRelationTask extends AsyncTask<String, String, String> {
		
		//private ProgressDialog pDialog;
		
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
				httpResponse = HttpConnectionUtils.createExistRelationResponse(params[1], params[2], params[3],
						params[4], params[5], activity.getResources().getString(R.string.hostname)
								+ activity.getResources().getString(R.string.url_invite_user));
			}
			if (params[0].equals("recommend")) {
				request_type = 2;
				httpResponse = HttpConnectionUtils.createOthersRelationResponse(params[1], params[2], params[3],
						params[4], params[5], params[6], activity.getResources().getString(R.string.hostname)
								+ activity.getResources().getString(R.string.url_invite_user));
			}
			return httpResponse;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Relation Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				int responseResult = loginResponseObject.getInt("AuthenticationStatus");
				int Userstatus = 1;
				if(loginResponseObject.has("Userstatus")){
					Userstatus = loginResponseObject.getInt("Userstatus");
				}
				if (responseResult == 1) {
					// TODO store the login response and
					// JSONArray data =
					// loginResponseObject.getJSONArray("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					Log.d(TAG, " success...........!!");
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
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
						String croutonmsg = "You have successfully invited " + nodeName + " to your family tree.You will have a complete access to further connections once " + nodeName + " accepts your invitation";
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
						
					} else if ((request_type == 2)  && (Userstatus == 0)) {
		
						String croutonmsg = "You have successfully invited "+ nodeName +" to join your family. You will have a complete access to further connections once "+ nodeName +" accepts your invitation";
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
		
					} else if (request_type == 2) {
						String croutonmsg = toWhomName +" has been notified to invite "+ nodeName +" as "+relationName;
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
					}
					((MainActivity) activity).changeFragment("HomeFragment");
				} else if (responseResult == 2) {
					Crouton.makeText(activity, "You already invited this person", Style.INFO).show();
					// Toast.makeText(getActivity(),
					// "Email ID is wrong Please Try again",
					// Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
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
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			if (emailEditText.getText().toString().equals("Autogenerated")
					|| emailEditText.getText().toString().equals("Try Again")) {

			} else {
				autoEmail = emailEditText.getText().toString();
			}
			final CreateRelationTask cRT2 = new CreateRelationTask();
			// String sessionname = sharedPreferences.getString("sessionname",
			// "Not Available");
			Log.d("CreateRelation3", "Values" + userId + ", " + nodeId + ", " + relationId + ", "
					+ firstNameEditText.getText().toString() + ", " + lastNameEditText.getText().toString() + ", "
					+ autoEmail + ", " + finalgender + ", " + sessionname);
			cRT2.execute("new",userId, nodeId, relationId, (firstNameEditText.getText().toString().trim()), (lastNameEditText
					.getText().toString().trim()), autoEmail, finalgender, sessionname, (editLocation.getText().toString()
					.trim()));

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (cRT2.getStatus() == AsyncTask.Status.RUNNING) {
						cRT2.cancel(true);
					}
				}
			}, 10000);
		} else {
			CroutonMessage.showCroutonAlert(activity, "!No Internet Connection,Try again", 7000);
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

	int searchTaskProcessCalledCount = 0;
	
	public class SearchUserTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			if (searchUserAutoComplete.getText().toString().trim().equals("")) {
				searchUserAutoComplete.dismissDropDown();
				ObjectItemData.clear();
			}
			searchTaskProcessCalledCount++;
			Log.d(TAG, "searchTaskProcessCalledCount ++ : " + searchTaskProcessCalledCount);
			startTime = System.currentTimeMillis();
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
							+ getActivity().getResources().getString(R.string.url_search_users));
			
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			elapsedTime = System.currentTimeMillis() - startTime;
			Log.i("relation response time ", ""+((elapsedTime/1000)));
			Log.i("relation list Response ", response);
			searchTaskProcessCalledCount--;
			Log.d(TAG, "searchTaskProcessCalledCount -- : " + searchTaskProcessCalledCount);
			if(response.equals("timeout")) {
				if(searchTaskProcessCalledCount == 0) {
					
					Crouton.makeText(activity, "Network connection is slow", Style.ALERT).show();
				}
			} else { 
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
								int mgender=1,deceased=0;
								if (c.has("gender")) {
									mgender = c.getInt("gender");
								}
								if (c.has("deceased")) {
									deceased = c.getInt("deceased");
								}
								String inviteUserId = c.getString("id");
								String name = c.getString("firstname") + " " + c.getString("lastname");
								String status = c.getString("parameter");
								if (status.trim().equalsIgnoreCase("Unhide")) {
									
								} else {
									ObjectItemData.add(new MyObject(name, inviteUserId, status, relationId, nodeId, mgender, deceased));
								}
							}
						} else {
							ObjectItemData.add(new MyObject("No results found", null, "NA", relationId, nodeId, 0, 0));
						}
					}

					Log.i(TAG, "ObjectItemData size: - " + ObjectItemData.size());
					if (ObjectItemData.size() > 20) {
						ObjectItemData.subList(20, ObjectItemData.size() - 1).clear();
						;
					}
					Log.d(TAG, "dataSetChanged");
					myAdapter = new AutoCompleteRelationArrayAdapter(getActivity(), R.layout.list_view_row, ObjectItemData);
					searchUserAutoComplete.setAdapter(myAdapter);
					myAdapter.notifyDataSetChanged();
				// myAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					for (StackTraceElement tempStack : e.getStackTrace()) {
						// Log.d("Exception thrown: Treeview Fetch", "" +
						// tempStack.getLineNumber());
						Log.d("Exception thrown: ",
								"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
										+ tempStack.getMethodName());
					}
					Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
					// + e.getMessage()
					Log.d(TAG, "Invalid Server content!!");
				}
			}

		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			searchTaskProcessCalledCount--;
			Log.d(TAG, "searchTaskProcessCalledCount -- : " + searchTaskProcessCalledCount);
			if (searchTaskProcessCalledCount == 0) {
				//Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
			}
			
		}
	}

	
	int searchPlacesProcessCalledCount = 0;
	public class SearchPlacesTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			searchPlacesProcessCalledCount++;
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
			
			searchPlacesProcessCalledCount--;
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
				if(startLocationFlag){
				locationHintAdpter = new LocationHintAdapter(getActivity(), R.layout.item_location, locationHints);
				editLocation.setAdapter(locationHintAdpter);
				locationHintAdpter.notifyDataSetChanged();
				}else{
					Log.d(TAG, "==== Server content!!====");
					startLocationFlag = true;
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content!!");
			}
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			searchPlacesProcessCalledCount--;
			if(searchPlacesProcessCalledCount == 0) {
				//Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
			}
		}
	}

	private void userDialog(final String name, final String otherid, final String type) {
		String msg;
		String btnmsg;
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
		userName = name;
		if (type.equals("invite")) {
			msg = "The User " + name + " is already available in another Family Tree would you like to invite.";
			btnmsg = "Invite";
		} else if (type.equals("recommend")) {
			msg = "The User " + name + " is already available in another Family Tree would you like to invite.";
			btnmsg = "invite";
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
		alertDialog.setPositiveButton(btnmsg, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (type.equals("invite")) {
					final InviteRelationTask inviteRelationTask = new InviteRelationTask();
					inviteRelationTask.execute("invite", otherid, userId, relationId, sessionname, userId);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (inviteRelationTask.getStatus() == AsyncTask.Status.RUNNING) {
								inviteRelationTask.cancel(true);
							}
						}
					}, 10000);
				}else if (type.equals("recommend")) {
					final InviteRelationTask inviteRelationTask1 = new InviteRelationTask();
					inviteRelationTask1.execute("recommend", otherid, nodeId, relationId, sessionname, name, userId);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (inviteRelationTask1.getStatus() == AsyncTask.Status.RUNNING) {
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
							if (unhideTask.getStatus() == AsyncTask.Status.RUNNING) {
								unhideTask.cancel(true);
							}
						}
					}, 10000);
				}
			}
		});

		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
							+ activity.getResources().getString(R.string.url_unhide_user));
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
					String nodeName = firstNameEditText.getText().toString() + " "
							+ lastNameEditText.getText().toString();
					
					CroutonMessage.showCroutonInfo(activity, "You have successfully un-hidden " + nodeName+" from your family tree", 7000);
					((MainActivity) activity).changeFragment("HomeFragment");

				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(activity, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}
	protected void hideKeyboard(View view)
	{
	    InputMethodManager in = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	    in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
