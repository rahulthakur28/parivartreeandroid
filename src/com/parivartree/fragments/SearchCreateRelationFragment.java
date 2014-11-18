package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.em;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.CustomNotificationAdapter;
import com.parivartree.adapters.CustomSearchReationAdapter;
import com.parivartree.fragments.CreateEventFragment.CreateEventTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.SearchRecords;

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

	Activity activity;
	Button refineSearchbtn, createNewBtn;
	TextView textResultTitle;
	private ArrayList<SearchRecords> searchRecordsArrayList;
	private ArrayList<HashMap<String, String>> relationRecordsArrayList;
	String recommendNodeId, myRelationId, firstName, lastName, email, locality, userId, recommendedUserName;
	String relationships[] = new String[] {"relationship", "Father", "Mother", "Wife", "Brother", "Sister", "Son", "Daughter", "Husband"};
	String relationship_type;
	int sizeSearchList;

	CustomSearchReationAdapter SearchReationAdapter;
	ListView searchListView;
	private ProgressDialog pDialog;
	private SharedPreferences sharedPreferences;
	private Editor sharedPreferencesEditor;
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
		recommendedUserName = sharedPreferences.getString("node_first_name", " ") + " " + sharedPreferences.getString("node_last_name", " ");

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
		relationship_type = relationships[Integer.parseInt(myRelationId)];
		textResultTitle = (TextView) rootView.findViewById(R.id.textresulttitle);
		refineSearchbtn = (Button) rootView.findViewById(R.id.btnrefinesearch);
		createNewBtn = (Button) rootView.findViewById(R.id.btncreatenewrelation);
		searchListView = (ListView) rootView.findViewById(R.id.searchlistview);

		refineSearchbtn.setOnClickListener(this);
		createNewBtn.setOnClickListener(this);

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
		Dialog refineDialog = new Dialog(activity);
		refineDialog.setContentView(R.layout.refine_search_popup);
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
			return HttpConnectionUtils.getCreateNewUserResponse(
					params[0],
					params[1],
					params[2],
					params[3],
					params[4],
					params[5],
					params[6],
					activity.getResources().getString(R.string.hostname)
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
				int status = loginResponseObject .getInt("AuthenticationStatus");
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success") && (status == 1)) {
					Log.i("Get new user Response ", "user added");
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					
					if(recommendNodeId.equals(userId)){
						Crouton.makeText(activity, "You have successfully added " + (firstName + " " + lastName) + " to your family tree.", Style.INFO).show();
					}else{
						Crouton.makeText(activity, "You have successfully added " + (firstName + " " + lastName) + " to " + recommendedUserName + " for " + relationship_type + " relation.", Style.INFO).show();
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

}
