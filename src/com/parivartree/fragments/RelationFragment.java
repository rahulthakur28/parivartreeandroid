package com.parivartree.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.helpers.HttpConnectionUtils;

public class RelationFragment extends Fragment implements OnClickListener {

	Context context;
	LinearLayout fatherTextView, motherTextView, brotherTextView, sisterTextView, sonTextView, daughterTextView,
			spouseTextView;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	String userid, nodeid,name,gender;
	TextView textViewtitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_relation, container, false);
		context = getActivity().getApplicationContext();

		sharedPreferences = context.getSharedPreferences(
				context.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		userid = sharedPreferences.getString("user_id", "0");
		nodeid = sharedPreferences.getString("node_id", userid);
		name = (sharedPreferences.getString("node_first_name", "NA")+" "+sharedPreferences.getString("node_last_name", "NA"));
		//gender = sharedPreferences.getString("selectgender", "0");
		
		textViewtitle = (TextView) rootView.findViewById(R.id.relationtitle);
		fatherTextView = (LinearLayout) rootView.findViewById(R.id.father);
		motherTextView = (LinearLayout) rootView.findViewById(R.id.mother);
		brotherTextView = (LinearLayout) rootView.findViewById(R.id.brother);
		sisterTextView = (LinearLayout) rootView.findViewById(R.id.sister);
		sonTextView = (LinearLayout) rootView.findViewById(R.id.son);
		daughterTextView = (LinearLayout) rootView.findViewById(R.id.daughter);
		spouseTextView = (LinearLayout) rootView.findViewById(R.id.spouse);
		
		textViewtitle.setText("Add new Relation to "+name);

		fatherTextView.setOnClickListener(this);
		motherTextView.setOnClickListener(this);
		brotherTextView.setOnClickListener(this);
		sisterTextView.setOnClickListener(this);
		daughterTextView.setOnClickListener(this);
		sonTextView.setOnClickListener(this);
		spouseTextView.setOnClickListener(this);

		context = this.getActivity().getApplicationContext();

		//ProfileTask profileTask = new ProfileTask();
		//profileTask.execute(nodeid);

		return rootView;

	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		if (v.getId() == R.id.father) {
			((MainActivity) this.getActivity()).createRelation("1", nodeid);
		} else if (v.getId() == R.id.mother) {
			((MainActivity) this.getActivity()).createRelation("2", nodeid);
		} else if (v.getId() == R.id.brother) {
			((MainActivity) this.getActivity()).createRelation("4", nodeid);
		} else if (v.getId() == R.id.sister) {
			((MainActivity) this.getActivity()).createRelation("5", nodeid);
		} else if (v.getId() == R.id.spouse) {
			((MainActivity) this.getActivity()).createRelation("3", nodeid);
		} else if (v.getId() == R.id.son) {
			((MainActivity) this.getActivity()).createRelation("6", nodeid);
		} else if (v.getId() == R.id.daughter) {
			((MainActivity) this.getActivity()).createRelation("7", nodeid);
		}

	}

//	public class ProfileTask extends AsyncTask<String, String, String> {
//
//		private ProgressDialog pDialog;
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			pDialog = new ProgressDialog(getActivity());
//			pDialog.setMessage("Loading...");
//			pDialog.setIndeterminate(false);
//			pDialog.setCancelable(true);
//			pDialog.show();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			return HttpConnectionUtils.getProfileResponse(
//					params[0],
//					getActivity().getResources().getString(R.string.hostname)
//							+ getResources().getString(R.string.url_view_profile));
//		}
//
//		@Override
//		protected void onPostExecute(String response) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(response);
//			pDialog.dismiss();
//			Log.i("Profile Fetch Response ", response);
//			try {
//				JSONObject loginResponseObject = new JSONObject(response);
//				String responseResult = loginResponseObject.getString("Status");
//				if (responseResult.equals("Success")) {
//					// TODO store the login response and
//					JSONArray data = loginResponseObject.getJSONArray("data");
//					JSONObject userProfileData = (JSONObject) data.get(0);
//					if (userProfileData.has("Gender")) {
//						String gender = userProfileData.getString("Gender");
//						sharedPreferencesEditor.putString("gender", gender);
//						sharedPreferencesEditor.commit();
//					}
//				}
//			} catch (Exception e) {
//				for (StackTraceElement tempStack : e.getStackTrace()) {
//					Log.d("Exception thrown: ", "" + tempStack.getLineNumber() + " " + tempStack.getMethodName());
//				}
//				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
//				Log.d("profile", "Invalid Server content from Profile!!");
//			}
//		}
//	}
}
