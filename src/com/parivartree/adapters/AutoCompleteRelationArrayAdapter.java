package com.parivartree.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.MyObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AutoCompleteRelationArrayAdapter extends ArrayAdapter<MyObject> {
	private static final String TAG = "AutoCompleteRelationArrayAdapter";
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	Context mContext;
	Activity activity;
	int layoutResourceId;
	ArrayList<MyObject> data = new ArrayList<MyObject>();
	String userId, sessionname;
	int request_type = 1;
	int position = 0;
	String userName = "";

	public AutoCompleteRelationArrayAdapter(Context context, int layoutResourceId, ArrayList<MyObject> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = context;
		this.activity = (Activity) context;
		this.data = data;
		sharedPreferences = this.mContext.getApplicationContext().getSharedPreferences(
				mContext.getPackageName() + mContext.getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		sessionname = sharedPreferences.getString("sessionname", "Not Available");
		sharedPreferencesEditor = sharedPreferences.edit();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try {

			/*
			 * The convertView argument is essentially a "ScrapView" as
			 * described is Lucas post
			 * http://lucasr.org/2012/04/05/performance-tips
			 * -for-androids-listview/ It will have a non-null value when
			 * ListView is asking you recycle the row layout. So, when
			 * convertView is not null, you should simply update its contents
			 * instead of inflating a new row layout.
			 */
			if (convertView == null) {
				// inflate the layout
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(layoutResourceId, parent, false);
			}

			// object item based on the position
			final MyObject objectItem = data.get(position);

			// get the TextView and then set the text (item name) and tag (item
			// ID) values
			final TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);

			Button buttonItem = (Button) convertView.findViewById(R.id.buttonItem);
			// in case you want to add some style, you can do something like:
			if (objectItem.objectStatus.equalsIgnoreCase("Already Connected")
					|| objectItem.objectStatus.equalsIgnoreCase("Already invited")) {
				buttonItem.setVisibility(View.GONE);
				textViewItem.setText(objectItem.objectName + "  (" + objectItem.objectStatus + ")");
			} else if (objectItem.objectStatus.equalsIgnoreCase("invite")) {
				buttonItem.setVisibility(View.VISIBLE);
				textViewItem.setText(objectItem.objectName);
				buttonItem.setText(objectItem.objectStatus);
			} else if (objectItem.objectStatus.equalsIgnoreCase("NA")
					|| (objectItem.objectStatus.equalsIgnoreCase("Unhide"))) {
				buttonItem.setVisibility(View.GONE);
				textViewItem.setText(objectItem.objectName);
			}

			buttonItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (objectItem.nodeid.equals(userId)) {
						Log.d(TAG, "You  invited :" + objectItem.objectName);
						CreateRelationTask cRT1 = new CreateRelationTask();
						String sessionname = sharedPreferences.getString("sessionname", "Not Available");
						Log.d("CreateRelation1", "Values : " + objectItem.objectId + ", " + userId + ", "
								+ objectItem.relationid + ", " + objectItem.objectName);
						TextView userName = (TextView) v.findViewById(R.id.textViewItem);
						AutoCompleteRelationArrayAdapter.this.userName = userName.getText().toString() ;
						cRT1.execute("exist", objectItem.objectId, userId, objectItem.relationid, sessionname, userId);
						
					} else {
						CreateRelationTask cRT3 = new CreateRelationTask();
						String sessionname = sharedPreferences.getString("sessionname", "Not Available");
						Log.d("CreateRelation2", "Values" + objectItem.objectId + ", " + objectItem.nodeid + ", "
								+ objectItem.relationid + ", " + objectItem.objectName + "," + userId + ","
								+ sessionname);
						TextView userName = (TextView) v.findViewById(R.id.textViewItem);
						AutoCompleteRelationArrayAdapter.this.userName = userName.getText().toString() ;
						cRT3.execute("others", objectItem.objectId, objectItem.nodeid, objectItem.relationid,
								sessionname, objectItem.objectName, userId);
					}
				}
			});

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;

	}

	public class CreateRelationTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Fetching profile details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String httpResponse = null;
			if (params[0].equals("exist")) {
				request_type = 1;
				httpResponse = HttpConnectionUtils.createExistRelationResponse(params[1], params[2], params[3],
						params[4], params[5], mContext.getResources().getString(R.string.hostname)
								+ mContext.getResources().getString(R.string.url_invite_user));
			}
			if (params[0].equals("others")) {
				request_type = 2;
				httpResponse = HttpConnectionUtils.createOthersRelationResponse(params[1], params[2], params[3],
						params[4], params[5], params[6], mContext.getResources().getString(R.string.hostname)
								+ mContext.getResources().getString(R.string.url_invite_user));
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
				int responseResult = loginResponseObject.getInt("AuthenticationStatus");
				if (responseResult == 1) {
					// TODO store the login response and
					// JSONArray data =
					// loginResponseObject.getJSONArray("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					Log.d(TAG, " success...........!!");
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					
					if(request_type == 1) {
						String nodeName = AutoCompleteRelationArrayAdapter.this.userName;
						Crouton.makeText(activity, "You have successfully invited " + nodeName + " to your family tree.", Style.INFO).show();
					} else if (request_type == 2) {
						
					}
					//Toast.makeText(mContext, "You have successfully invited ", Toast.LENGTH_SHORT).show();
					((MainActivity) mContext).changeFragment("HomeFragment");
				} else if (responseResult == 2) {
					Crouton.makeText(activity, "Email id is invalid. Please try again... ", Style.INFO).show();
					//Toast.makeText(mContext, "Email ID is wrong Please Try again", Toast.LENGTH_LONG).show();
				} else if (responseResult == 5) {
					Crouton.makeText(activity, "Email ID is already a parivartree account, use search user to invite", Style.INFO).show();
					//Toast.makeText(mContext, "Email ID already parivartree account use search user", Toast.LENGTH_LONG).show();
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(mContext, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}
		}
	}
}
