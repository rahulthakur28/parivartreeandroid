package com.parivartree.adapters;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.AutoCompleteRelationArrayAdapter.CreateRelationTask;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.RectangularImageView;
import com.parivartree.models.SearchRecords;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomSearchReationAdapter extends BaseAdapter{
	Activity activity;
	String searchName, recommendNodeId, myRelationId,userId;
	ArrayList<SearchRecords> searchRecordsArrayList;
	private ArrayList<HashMap<String, String>> relationRecordsArrayList;
	LayoutInflater inflater;
	int inviteType = 0;
	private SharedPreferences sharedPreferences;
	private Editor sharedPreferencesEditor;
	int request_type = 1;
	String relationship_type,userName="";
	String relationships[] = new String[] {"relationship", "Father", "Mother", "Wife", "Brother", "Sister", "Son", "Daughter", "Husband"};
	private ProgressDialog pDialog;
	
	public CustomSearchReationAdapter(Activity activity, ArrayList<SearchRecords> searchRecordsArrayList,
			String searchName, String recommendNodeId, String myRelationId, String userId) {
		this.activity = activity;
		this.searchRecordsArrayList = searchRecordsArrayList;
		this.searchName = searchName;
		this.recommendNodeId = recommendNodeId;
		this.myRelationId = myRelationId;
		this.userId = userId;
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sharedPreferences = this
				.activity
				.getApplicationContext()
				.getSharedPreferences(activity.getPackageName() + activity.getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		relationship_type = relationships[Integer.parseInt(myRelationId)];
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchRecordsArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchRecordsArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if (convertView == null) {
			// Inflate the view since it does not exist
			convertView = inflater.inflate(R.layout.item_search_relationlist, parent, false);

			// Create and save off the holder in the tag so we get quick
			// access to inner fields
			// This must be done for performance reasons
			holder = new Holder();
			holder.searchimageview = (RectangularImageView) convertView.findViewById(R.id.searchimageview);
			holder.searchimageview.setLayoutParams(new LayoutParams((int)(activity.getResources().getDisplayMetrics().density * 90), (int)(activity.getResources().getDisplayMetrics().density * 90)));
			holder.textSearchName = (TextView) convertView.findViewById(R.id.textsearchname);
			holder.textsearchlocation = (TextView) convertView.findViewById(R.id.textsearchlocation);
			holder.textSearchRelationTitle = (TextView) convertView.findViewById(R.id.textsearchrelationtitle);
			holder.textsearchrelation = (TextView) convertView.findViewById(R.id.textsearchrelation);
			holder.searchInviteBtn = (Button) convertView.findViewById(R.id.searchinvitebtn);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		String location = "NA";
		String relation = "NA";
		StringBuilder relationString = new StringBuilder();
		int id = searchRecordsArrayList.get(position).getUserid();
		int gender = searchRecordsArrayList.get(position).getGender();
		int deceased = searchRecordsArrayList.get(position).getDeceased();
		int connected = searchRecordsArrayList.get(position).getConnected();
		int invite = searchRecordsArrayList.get(position).getInvite();
		// get image
		if (deceased == 1) {
			holder.searchimageview.setBorderColor(activity.getResources().getColor(R.color.pt_gold));
			UrlImageViewHelper.setUrlDrawable(holder.searchimageview,
					"https://www.parivartree.com/profileimages/thumbs/" + id + "PROFILE.jpeg", activity.getResources()
							.getDrawable(R.drawable.male), 10000);
			holder.searchimageview.invalidate();

		} else if (gender == 1) {
			holder.searchimageview.setBorderColor(activity.getResources().getColor(R.color.pt_blue));
			UrlImageViewHelper.setUrlDrawable(holder.searchimageview,
					"https://www.parivartree.com/profileimages/thumbs/" + id + "PROFILE.jpeg", activity.getResources()
							.getDrawable(R.drawable.male), 10000);
			holder.searchimageview.invalidate();
			Log.v("CustomSearchRelation Adapter", "height - " + holder.searchimageview.getHeight());
		} else if (gender == 2) {
			holder.searchimageview.setBorderColor(Color.MAGENTA);
			UrlImageViewHelper.setUrlDrawable(holder.searchimageview,
					"https://www.parivartree.com/profileimages/thumbs/" + id + "PROFILE.jpeg", activity.getResources()
							.getDrawable(R.drawable.female), 10000);
			holder.searchimageview.invalidate();
			Log.v("CustomSearchRelation Adapter", "height - " + holder.searchimageview.getHeight());
		}
		holder.textSearchName
				.setText((searchRecordsArrayList.get(position).getFirstname() + " " + searchRecordsArrayList.get(
						position).getLastname()));
		if ((!(searchRecordsArrayList.get(position).getCity()).equals("NA"))
				&& (!(searchRecordsArrayList.get(position).getState()).equals("NA"))) {
			location = (searchRecordsArrayList.get(position).getCity()) + ","
					+ (searchRecordsArrayList.get(position).getState());
		} else if ((!(searchRecordsArrayList.get(position).getCity()).equals("NA"))
				&& ((searchRecordsArrayList.get(position).getState()).equals("NA"))) {
			location = (searchRecordsArrayList.get(position).getCity());
		} else if (((searchRecordsArrayList.get(position).getCity()).equals("NA"))
				&& (!(searchRecordsArrayList.get(position).getState()).equals("NA"))) {
			location = (searchRecordsArrayList.get(position).getState());
		} else {
			location = "NA";
		}
		holder.textsearchlocation.setText(location);
		
		relationRecordsArrayList = searchRecordsArrayList.get(position).getRelationRecords();
		if((relationRecordsArrayList !=null) && (relationRecordsArrayList.size() > 0)){
			holder.textSearchRelationTitle.setVisibility(View.VISIBLE);
			holder.textsearchrelation.setVisibility(View.VISIBLE);
			for(HashMap<String, String> hash : relationRecordsArrayList){
				relation = hash.get("name")+" ("+hash.get("relationname")+") ";
				relationString.append(relation);
			}
			
			holder.textsearchrelation.setText(relationString.toString());
			
		}else{
			holder.textSearchRelationTitle.setVisibility(View.INVISIBLE);
			holder.textsearchrelation.setVisibility(View.INVISIBLE);
		}

		if(connected == 1){
			holder.searchInviteBtn.setText("Already Connected");
			holder.searchInviteBtn.setTextColor(activity.getResources().getColor(R.color.ll_black));
			holder.searchInviteBtn.setBackgroundColor(activity.getResources().getColor(R.color.pt_grey));
			holder.searchInviteBtn.setClickable(false);
			
		}else if(invite == 1){
			holder.searchInviteBtn.setText("Already Invited");
			holder.searchInviteBtn.setTextColor(activity.getResources().getColor(R.color.ll_black));
			holder.searchInviteBtn.setBackgroundColor(activity.getResources().getColor(R.color.pt_grey));
			holder.searchInviteBtn.setClickable(false);
		}else if(recommendNodeId.equals(userId)){
			holder.searchInviteBtn.setText("Invite");
			holder.searchInviteBtn.setTextColor(activity.getResources().getColor(R.color.pt_white));
			holder.searchInviteBtn.setBackgroundResource((R.drawable.rounded_corners_blue));
			holder.searchInviteBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Crouton.makeText(activity, "You click  for invite", Style.ALERT).show();

					String sessionname = sharedPreferences.getString("sessionname", "Not Available");
					userName = (searchRecordsArrayList.get(position).getFirstname())+" "+(searchRecordsArrayList.get(position).getLastname());

					final CreateRelationTask cRT1 = new CreateRelationTask();
					cRT1.execute("exist", ""+(searchRecordsArrayList.get(position).getUserid()), userId, myRelationId,
							sessionname, userId);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (cRT1.getStatus() == AsyncTask.Status.RUNNING) {
								cRT1.cancel(true);
							}
						}
					}, 10000);
				}
			});
		}else if(!recommendNodeId.equals(userId)){
			holder.searchInviteBtn.setText("Recommend");	
			holder.searchInviteBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Crouton.makeText(activity, "You click for recommend", Style.ALERT).show();
					
					String sessionname = sharedPreferences.getString("sessionname", "Not Available");
					userName = (searchRecordsArrayList.get(position).getFirstname())+" "+(searchRecordsArrayList.get(position).getLastname());
					
					final CreateRelationTask cRT2 = new CreateRelationTask();
					cRT2.execute("others", ""+(searchRecordsArrayList.get(position).getUserid()), recommendNodeId, myRelationId,
							sessionname, ((searchRecordsArrayList.get(position).getFirstname())+" "+(searchRecordsArrayList.get(position).getLastname())), userId);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (cRT2.getStatus() == AsyncTask.Status.RUNNING){
								cRT2.cancel(true);
							}
						}
					}, 10000);
				}
			});
		}
		
		return convertView;
	}

	private static class Holder {
		public TextView textSearchName, textsearchlocation, textsearchrelation,textSearchRelationTitle;
		public Button searchInviteBtn;
		public RectangularImageView searchimageview;
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
			if (params[0].equals("exist")) {
				request_type = 1;
				//relationship_type = relationships[Integer.parseInt(params[3])];
				httpResponse = HttpConnectionUtils.createExistRelationResponse(params[1], params[2], params[3],
						params[4], params[5], activity.getResources().getString(R.string.hostname)
								+ activity.getResources().getString(R.string.url_invite_user));
			}
			if (params[0].equals("others")) {
				request_type = 2;
				//relationship_type = relationships[Integer.parseInt(params[3])];
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
				String result = loginResponseObject.getString("Status");
				if ((result.equals("Success")) || (responseResult == 1)) {
					// TODO store the login response and
					sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
					sharedPreferencesEditor.commit();
					
					String nodeName = userName;
					if(request_type == 1) {				
								//AutoCompleteRelationArrayAdapter.this.userName;
						Crouton.makeText(activity, "You have successfully invited " + nodeName + " to your family tree.", Style.INFO).show();
					} else if (request_type == 2) {
						String recommendedUserName = sharedPreferences.getString("node_first_name", " ") + " " + sharedPreferences.getString("node_last_name", " ");
						Crouton.makeText(activity, "You have successfully recommended " + nodeName + " to " + recommendedUserName + " for " + relationship_type + " relation.", Style.INFO).show();
					}
					//Toast.makeText(mContext, "You have successfully invited ", Toast.LENGTH_SHORT).show();
					((MainActivity) activity).changeFragment("HomeFragment");
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
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
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
