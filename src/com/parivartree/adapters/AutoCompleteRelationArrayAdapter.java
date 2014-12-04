package com.parivartree.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.SignUpDetailsActivity;
import com.parivartree.fragments.SearchCreateRelationFragment;
import com.parivartree.helpers.CroutonMessage;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.RectangularImageView;
import com.parivartree.models.MyObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AutoCompleteRelationArrayAdapter extends ArrayAdapter<MyObject> implements OnClickListener{
	private static final String TAG = "AutoCompleteRelationArrayAdapter";
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	Context mContext;
	Activity activity;
	int layoutResourceId;
	ArrayList<MyObject> data = new ArrayList<MyObject>();
	String userId, sessionname;
	int request_type = 1;
	String relationship_type;
	String relationships[] = new String[] { "relationship", "Father", "Mother", "Wife", "Brother", "Sister", "Son",
			"Daughter", "Husband" };
	int position = 0;
	String userName = "", toWhomName;
	private ProgressDialog pDialog;

	public AutoCompleteRelationArrayAdapter(Activity context, int layoutResourceId, ArrayList<MyObject> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = context;
		this.activity = context;
		this.data = data;
		sharedPreferences = this.mContext.getApplicationContext().getSharedPreferences(
				mContext.getPackageName() + mContext.getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		toWhomName = (sharedPreferences.getString("node_first_name", "NA") + " " + sharedPreferences.getString(
				"node_last_name", "NA"));
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
			RectangularImageView invitesearchimageview = (RectangularImageView) convertView
					.findViewById(R.id.invitesearchimageview);
			if ((objectItem.deceased == 1) && (objectItem.gender == 1)) {

				invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_gold));
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.male), 10000);

			} else if ((objectItem.deceased == 1) && (objectItem.gender == 2)) {

				invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_gold));
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.female), 10000);

			} else if (objectItem.gender == 1) {
				invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_blue));
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.male), 10000);

			} else if (objectItem.gender == 2) {
				invitesearchimageview.setBorderColor(Color.MAGENTA);
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.female), 10000);

			}
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.female), 10000);

			} else if (objectItem.gender == 1) {
				invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_blue));
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.male), 10000);

			} else if (objectItem.gender == 2) {
				invitesearchimageview.setBorderColor(Color.MAGENTA);
				UrlImageViewHelper.setUrlDrawable(invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg",
						mContext.getResources().getDrawable(R.drawable.female), 10000);

			}
			// get the TextView and then set the text (item name) and tag (item
			// ID) values
			final TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);

			Button buttonItem = (Button) convertView.findViewById(R.id.buttonItem);
			// in case you want to add some style, you can do something like:
			if (objectItem.objectStatus.equalsIgnoreCase("Already Connected")
					|| objectItem.objectStatus.equalsIgnoreCase("Already invited")) {
				buttonItem.setVisibility(View.GONE);
				textViewItem.setText(objectItem.objectName + "  (" + objectItem.objectStatus + ")");
				invitesearchimageview.setVisibility(View.VISIBLE);
			} else if (objectItem.objectStatus.equalsIgnoreCase("invite")) {
				buttonItem.setVisibility(View.VISIBLE);
				textViewItem.setText(objectItem.objectName);
				buttonItem.setText(objectItem.objectStatus);
				invitesearchimageview.setVisibility(View.VISIBLE);
			} else if (objectItem.objectName.equalsIgnoreCase("No results found")) {
				buttonItem.setVisibility(View.GONE);
				textViewItem.setText(objectItem.objectName);
				invitesearchimageview.setVisibility(View.INVISIBLE);
			} else if (objectItem.objectStatus.equalsIgnoreCase("NA")
					|| (objectItem.objectStatus.equalsIgnoreCase("Unhide"))) {
				buttonItem.setVisibility(View.GONE);
				textViewItem.setText(objectItem.objectName);
				invitesearchimageview.setVisibility(View.VISIBLE);
			}

			buttonItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					userName = objectItem.objectName;
					if (objectItem.nodeid.equals(userId)) {
						Log.d(TAG, "You  invited :" + objectItem.objectName);
						final CreateRelationTask cRT1 = new CreateRelationTask();
						String sessionname = sharedPreferences.getString("sessionname", "Not Available");
						Log.d("CreateRelation1", "Values : " + objectItem.objectId + ", " + userId + ", "
								+ objectItem.relationid + ", " + objectItem.objectName);
						TextView userName = (TextView) v.findViewById(R.id.textViewItem);
						// AutoCompleteRelationArrayAdapter.this.userName =
						// userName.getText().toString() ;

						cRT1.execute("exist", objectItem.objectId, userId, objectItem.relationid, sessionname, userId);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (cRT1.getStatus() == AsyncTask.Status.RUNNING) {
									cRT1.cancel(true);
								}
							}
						}, 10000);

					} else {
						final CreateRelationTask cRT3 = new CreateRelationTask();
						String sessionname = sharedPreferences.getString("sessionname", "Not Available");
						Log.d("CreateRelation2", "Values" + objectItem.objectId + ", " + objectItem.nodeid + ", "
								+ objectItem.relationid + ", " + objectItem.objectName + "," + userId + ","
								+ sessionname);
						TextView userName = (TextView) v.findViewById(R.id.textViewItem);
						// AutoCompleteRelationArrayAdapter.this.userName =
						// userName.getText().toString() ;
						cRT3.execute("others", objectItem.objectId, objectItem.nodeid, objectItem.relationid,
								sessionname, objectItem.objectName, userId);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (cRT3.getStatus() == AsyncTask.Status.RUNNING) {
									cRT3.cancel(true);
								}
							}
						}, 10000);
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

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(mContext);
			pDialog.setMessage("Loading...");
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
				relationship_type = relationships[Integer.parseInt(params[3])];
				httpResponse = HttpConnectionUtils.createExistRelationResponse(params[1], params[2], params[3],
						params[4], params[5], mContext.getResources().getString(R.string.hostname)
								+ mContext.getResources().getString(R.string.url_invite_user));
			}
			if (params[0].equals("others")) {
				request_type = 2;
				relationship_type = relationships[Integer.parseInt(params[3])];
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
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Log.i("Relation Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				int responseResult = loginResponseObject.getInt("AuthenticationStatus");
				String result = loginResponseObject.getString("Status");
				int Userstatus = 1;
				if(loginResponseObject.has("Userstatus")){ 
					Userstatus = loginResponseObject.getInt("Userstatus");
				}
				if ((result.equals("Success")) || (responseResult == 1)) {
					// TODO store the login response and
					// JSONArray data =
					// loginResponseObject.getJSONArray("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					Log.d(TAG, " success...........!!");
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					String nodeName = userName;
					Log.e(TAG, "request_type - " + request_type);
					if (request_type == 1) {
						// AutoCompleteRelationArrayAdapter.this.userName;
						String croutonmsg = "You have successfully invited " + nodeName + " to your family tree.You will have a complete access to further connections once " + nodeName + " accepts your invitation";
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
			
					} else if ((request_type == 2)  && (Userstatus == 0)) {
		
						String croutonmsg = "You have successfully invited "+ nodeName +" to join your family. You will have a complete access to further connections once "+ nodeName +" accepts your invitation";
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
		
					}else if (request_type == 2) {
						String recommendedUserName = sharedPreferences.getString("node_first_name", " ") + " "
								+ sharedPreferences.getString("node_last_name", " ");
					
						String croutonmsg = recommendedUserName +" has been notified to invite "+ nodeName +" as "+relationship_type;
						CroutonMessage.showCroutonInfo(activity, croutonmsg, 10000);
		
						Crouton crouton;
						crouton = Crouton.makeText(activity, croutonmsg, Style.INFO);
						crouton.setOnClickListener(AutoCompleteRelationArrayAdapter.this).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(10000).build()).show();			
					
					}
					// Toast.makeText(mContext,
					// "You have successfully invited ",
					// Toast.LENGTH_SHORT).show();
					((MainActivity) mContext).changeFragment("HomeFragment");
				} else if (responseResult == 2) {
					Crouton.makeText(activity, "You already invited this person", Style.ALERT).show();
					// Toast.makeText(mContext,
					// "Email ID is wrong Please Try again",
					// Toast.LENGTH_LONG).show();
				} else if (responseResult == 5) {
					Crouton.makeText(activity, "Email ID is already a parivartree account, use search user to invite",
							Style.ALERT).show();
					// Toast.makeText(mContext,
					// "Email ID already parivartree account use search user",
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
				Toast.makeText(mContext, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
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
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
