package com.parivartree.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.RectangularImageView;
import com.parivartree.models.MyObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AutocompleteCustomArrayAdapter extends ArrayAdapter<MyObject> {

	final String TAG = "AutocompleteCustomArrayAdapter";
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	Activity mContext;
	int layoutResourceId;
	ArrayList<MyObject> data = new ArrayList<MyObject>();
	// MyObject[] data = null;
	String userId;
	LayoutInflater inflater;
	public AutocompleteCustomArrayAdapter(Activity mContext, int layoutResourceId, ArrayList<MyObject> data) {
		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
		inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sharedPreferences = this.mContext.getApplicationContext().getSharedPreferences(
				mContext.getPackageName() + mContext.getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		sharedPreferencesEditor = sharedPreferences.edit();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder viewHolder;
		try {
			if (convertView == null) {
				convertView = inflater.inflate(layoutResourceId, parent, false);
				viewHolder = new Holder();
				viewHolder.invitesearchimageview = (RectangularImageView) convertView.findViewById(R.id.invitesearchimageview);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)(mContext.getResources().getDisplayMetrics().density * 70), (int)(mContext.getResources().getDisplayMetrics().density * 70));
				params.gravity = Gravity.CENTER;
				viewHolder.invitesearchimageview.setLayoutParams(params);
				
				viewHolder.textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
				viewHolder.buttonItem = (Button) convertView.findViewById(R.id.buttonItem);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (Holder) convertView.getTag();
			}
			
			final MyObject objectItem = data.get(position);
			
			
			if ((objectItem.deceased == 1) && (objectItem.gender == 1)) {
				
				viewHolder.invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_gold));
				UrlImageViewHelper.setUrlDrawable(viewHolder.invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg", mContext.getResources()
								.getDrawable(R.drawable.male), 10000);

				
			} else if ((objectItem.deceased == 1) && (objectItem.gender == 2)) {
				
				viewHolder.invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_gold));
				UrlImageViewHelper.setUrlDrawable(viewHolder.invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg", mContext.getResources()
								.getDrawable(R.drawable.female), 10000);	
				
				
			}else if (objectItem.gender == 1) {
				viewHolder.invitesearchimageview.setBorderColor(mContext.getResources().getColor(R.color.pt_blue));
				UrlImageViewHelper.setUrlDrawable(viewHolder.invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg", mContext.getResources()
								.getDrawable(R.drawable.male), 10000);
				
				
			} else if (objectItem.gender == 2) {
				viewHolder.invitesearchimageview.setBorderColor(Color.MAGENTA);
				UrlImageViewHelper.setUrlDrawable(viewHolder.invitesearchimageview,
						"https://www.parivartree.com/profileimages/thumbs/" + objectItem.objectId + "PROFILE.jpeg", mContext.getResources()
								.getDrawable(R.drawable.female), 10000);
				
				
			}
			
			viewHolder.textViewItem.setText(objectItem.objectName);
			if ((objectItem.objectStatus).equalsIgnoreCase("Unhide")) {
				viewHolder.invitesearchimageview.setVisibility(View.VISIBLE);
				viewHolder.buttonItem.setVisibility(View.VISIBLE);
				viewHolder.buttonItem.setText(objectItem.objectStatus);
			}else if(objectItem.objectName.equalsIgnoreCase("No results found")){
				viewHolder.buttonItem.setVisibility(View.INVISIBLE);
				viewHolder.invitesearchimageview.setVisibility(View.INVISIBLE);
			} else{
				viewHolder.invitesearchimageview.setVisibility(View.VISIBLE);
				viewHolder.buttonItem.setVisibility(View.INVISIBLE);
			}
			viewHolder.buttonItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (objectItem.objectStatus.equalsIgnoreCase("Unhide")) {
						final UnhideUserTask unhideTask = new UnhideUserTask();
						unhideTask.execute(objectItem.objectId, sharedPreferences.getString("user_id", "0"));
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
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}
	/** View holder for the views we need access to */
	private static class Holder {
		public TextView textViewItem;
		public Button buttonItem;
		public RectangularImageView invitesearchimageview;
	}

	public class UnhideUserTask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getHideUserResponse(
					params[0],
					params[1],
					mContext.getResources().getString(R.string.hostname)
							+ mContext.getResources().getString(R.string.url_unhide_user));
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

					((MainActivity) mContext).changeFragment("HomeFragment");

				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(mContext, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(mContext, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}
}