package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.em;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.CustomDropDownAdapter;
import com.parivartree.adapters.CustomSearchReationAdapter;
import com.parivartree.fragments.EditProfileFragment.CommunityTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.SearchRecords;
import com.parivartree.models.SpinnerItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link SearchCreateRelationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the
 * {@link SearchCreateRelationFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class SearchCreateRelationFragment extends Fragment implements OnClickListener {

	SpinnerItem tempItem;
	Activity activity;
	Button refineSearchbtn, createNewBtn;
	TextView textResultTitle;
	private ArrayList<SearchRecords> searchRecordsArrayList;
	private ArrayList<HashMap<String, String>> relationRecordsArrayList;
	String recommendNodeId, myRelationId, firstName, lastName, email, locality, userId, recommendedUserName;
	String relationships[] = new String[] { "relationship", "Father", "Mother", "Wife", "Brother", "Sister", "Son",
			"Daughter", "Husband" };
	String relationship_type;
	int sizeSearchList, counter;
	Spinner spinnerCommunity;
	CustomSearchReationAdapter SearchReationAdapter;
	ListView searchListView;
	private ProgressDialog pDialog;
	private SharedPreferences sharedPreferences;
	private Editor sharedPreferencesEditor;
	ArrayList<SpinnerItem> communityList;

	public SearchCreateRelationFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_search_create_relation, container, false);
		activity = getActivity();
		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		userId = sharedPreferences.getString("user_id", "NA");
		recommendedUserName = sharedPreferences.getString("node_first_name", " ") + " "
				+ sharedPreferences.getString("node_last_name", " ");

		Bundle bndle = getArguments();
		if (bndle != null) {
			if (bndle.containsKey("searchrelationList")) {
				searchRecordsArrayList = bndle.getParcelableArrayList("searchrelationList");
				sizeSearchList = searchRecordsArrayList.size();
			}
			if (bndle.containsKey("firstname")) {
				firstName = bndle.getString("firstname");
			}
			if (bndle.containsKey("lastname")) {
				lastName = bndle.getString("lastname");
			}
			if (bndle.containsKey("email")) {
				email = bndle.getString("email");
			}
			if (bndle.containsKey("locality")) {
				locality = bndle.getString("locality");
			}
			if (bndle.containsKey("recommendnodeid")) {
				recommendNodeId = bndle.getString("recommendnodeid");
			}
			if (bndle.containsKey("myrelationid")) {
				myRelationId = bndle.getString("myrelationid");
			}
		}
		if (searchRecordsArrayList == null) {
			searchRecordsArrayList = new ArrayList<SearchRecords>();
		}
		relationship_type = relationships[Integer.parseInt(myRelationId)];
		textResultTitle = (TextView) rootView.findViewById(R.id.textresulttitle);
		refineSearchbtn = (Button) rootView.findViewById(R.id.btnrefinesearch);
		createNewBtn = (Button) rootView.findViewById(R.id.btncreatenewrelation);
		searchListView = (ListView) rootView.findViewById(R.id.searchlistview);

		refineSearchbtn.setOnClickListener(this);
		createNewBtn.setOnClickListener(this);

		communityList = new ArrayList<SpinnerItem>();

		textResultTitle.setText("Parivartree has found " + sizeSearchList + " Results for "
				+ (firstName + " " + lastName));
		SearchReationAdapter = new CustomSearchReationAdapter(activity, searchRecordsArrayList,
				(firstName + " " + lastName), recommendNodeId, myRelationId, userId);
		searchListView.setAdapter(SearchReationAdapter);
		SearchReationAdapter.notifyDataSetChanged();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnrefinesearch) {
			showRefineSearchDialog();
		} else if (v.getId() == R.id.btncreatenewrelation) {
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				final CreateNewUserTask createNewUserTask = new CreateNewUserTask();
				createNewUserTask.execute(recommendNodeId, myRelationId, email, firstName, lastName, locality, userId);
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (createNewUserTask.getStatus() == AsyncTask.Status.RUNNING) {
							createNewUserTask.cancel(true);
						}
					}
				}, 10000);
			} else {
				Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}

		}
	}

	private void showRefineSearchDialog() {
		final Dialog refineDialog = new Dialog(activity);
		refineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		refineDialog.setContentView(R.layout.refine_search_popup);
		refineDialog.setCancelable(true);

		final EditText editMobileSearch = (EditText) refineDialog.findViewById(R.id.edit_mobile_search);
		spinnerCommunity = (Spinner) refineDialog.findViewById(R.id.spinner_community_search);

		final Button refineDialogSearchBtn = (Button) refineDialog.findViewById(R.id.searchsubmintbtn);
		refineDialogSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean bool = new ConDetect(getActivity()).isOnline();
				if (bool) {
					// Create object of AsycTask and execute
					final RefineSearchTask refineSearchTask = new RefineSearchTask();
					if ((communityList.size() == 0) && (communityList.get(spinnerCommunity.getSelectedItemPosition()).getValue()
							.equals("Choose a Community"))) {
						refineSearchTask.execute(recommendNodeId, myRelationId, email, firstName, lastName, locality,
								(editMobileSearch.getText().toString().trim()), "");
					} else {
						refineSearchTask.execute(recommendNodeId, myRelationId, email, firstName, lastName, locality,
								(editMobileSearch.getText().toString().trim()),
								(communityList.get(spinnerCommunity.getSelectedItemPosition()).getValue()));
					}
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (refineSearchTask.getStatus() == AsyncTask.Status.RUNNING) {
								refineSearchTask.cancel(true);
							}
						}
					}, 10000);
				} else {
					Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}
				refineDialog.dismiss();
			}
		});

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final CommunityTask communityTask = new CommunityTask();
			communityTask.execute();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (communityTask.getStatus() == AsyncTask.Status.RUNNING) {
						communityTask.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}

		refineDialog.show();
	}

	public class CreateNewUserTask extends AsyncTask<String, String, String> {
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
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getCreateNewUserResponse(params[0], params[1], params[2], params[3], params[4],
					params[5], params[6], activity.getResources().getString(R.string.hostname)
							+ activity.getResources().getString(R.string.url_create_user));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Log.i("New user Response Fetch ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				int status = loginResponseObject.getInt("AuthenticationStatus");
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success") && (status == 1)) {
					Log.i("Get new user Response ", "user added");
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();

					if (recommendNodeId.equals(userId)) {
						Crouton.makeText(
								activity,
								"You have successfully added " + (firstName + " " + lastName) + " to your family tree.",
								Style.INFO).show();
					} else {
						Crouton.makeText(
								activity,
								"You have successfully added " + (firstName + " " + lastName) + " to "
										+ recommendedUserName + " for " + relationship_type + " relation.", Style.INFO)
								.show();
					}
					((MainActivity) activity).changeFragment("HomeFragment");
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class CommunityTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// pDialog = new ProgressDialog(activity);
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getAllCommunityResponse(getResources().getString(R.string.hostname)
					+ getResources().getString(R.string.url_all_community));

		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing())
				pDialog.dismiss();
			pDialog = null;

			Log.i("Community Fetch Response ", "" + response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				int authenticationStatus = loginResponseObject.getInt("AuthenticationStatus");
				// if(authenticationStatus == 1) {
				String responseResult = loginResponseObject.getString("status");
				if ((responseResult.equals("Success")) && (authenticationStatus == 1)) {
					Log.d("debuging", "before");
					// TODO store the login response and
					JSONArray communities = loginResponseObject.getJSONArray("communities");
					JSONObject tempObject;
					// communityPos = 0;
					communityList.clear();
					tempItem = new SpinnerItem();
					tempItem.setValue("Choose a Community");
					communityList.add(tempItem);
					for (int i = 0; i < communities.length(); i++) {
						tempItem = new SpinnerItem();
						tempObject = communities.getJSONObject(i);
						tempItem.setValue(tempObject.getString("name"));
						communityList.add(tempItem);
					}
					if (communities.length() == 0) {
						SpinnerItem tempItem = new SpinnerItem();
						Log.d("debuging", "inside");
						tempItem.setId(0);
						tempItem.setValue("Others");
						communityList.add(tempItem);
					}
					Log.d("debuging", "after");

					CustomDropDownAdapter communityAdapter = new CustomDropDownAdapter(activity, communityList);
					spinnerCommunity.setAdapter(communityAdapter);
					communityAdapter.notifyDataSetChanged();
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing())
				pDialog.dismiss();
			pDialog = null;
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class RefineSearchTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			// pDialog = new ProgressDialog(activity);
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getRefineSearchResponse(params[0], params[1], params[2], params[3], params[4],
					params[5], params[6], params[7], getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_refine_search));
			// return null;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing())
				pDialog.dismiss();
			pDialog = null;

			Log.i("refinesearch Fetch Response ", "" + response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);

				int authenticationStatus = loginResponseObject.getInt("Authenticationstatus");

				String responseResult = loginResponseObject.getString("status");
				if ((responseResult.equals("success")) && (authenticationStatus == 1)) {
					// TODO store the login response and
					if (loginResponseObject.has("records")) {
						relationRecordsArrayList = new ArrayList<HashMap<String, String>>();
						searchRecordsArrayList.clear();
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
							if (item.has("relation")) {
								relationRecordsArrayList.clear();
								JSONArray relation = item.getJSONArray("relation");
								for (int j = 0; j < relation.length(); j++) {
									JSONObject itemrelation = relation.getJSONObject(j);
									HashMap<String, String> relationhash = new HashMap<String, String>();
									relationhash.put("relationname", "" + itemrelation.getString("relationname"));
									relationhash.put("name", "" + itemrelation.getString("name"));
									relationhash.put("id", "" + itemrelation.getInt("id"));
									relationhash.put("imageexists", "" + itemrelation.getInt("imageexists"));
									relationRecordsArrayList.add(relationhash);
								}
								searchRecordObject.setRelationRecords(relationRecordsArrayList);
							}

							searchRecordsArrayList.add(searchRecordObject);
						}
					}
					if (loginResponseObject.has("counter")) {
						counter = loginResponseObject.getInt("counter");
						textResultTitle.setText("Parivartree has found " + counter + " Results for "
								+ (firstName + " " + lastName));
					}
					if (loginResponseObject.has("nodeid")) {
						recommendNodeId = loginResponseObject.getString("nodeid");
					}
					if (loginResponseObject.has("relationid")) {
						myRelationId = loginResponseObject.getString("relationid");
					}
					SearchReationAdapter.notifyDataSetChanged();

				} else if ((responseResult.equals("Failure")) && (authenticationStatus == 2)) {
					Crouton.makeText(activity, "Please select atleast one field to get results.", Style.ALERT).show();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing())
				pDialog.dismiss();
			pDialog = null;
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}
}
