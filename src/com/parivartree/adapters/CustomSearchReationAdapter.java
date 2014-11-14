package com.parivartree.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.helpers.RectangularImageView;
import com.parivartree.models.SearchRecords;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CustomSearchReationAdapter extends BaseAdapter implements OnClickListener {
	Activity activity;
	String searchName, myNodeId, myRelationId,userId;
	ArrayList<SearchRecords> searchRecordsArrayList;
	private ArrayList<HashMap<String, String>> relationRecordsArrayList;
	LayoutInflater inflater;

	public CustomSearchReationAdapter(Activity activity, ArrayList<SearchRecords> searchRecordsArrayList,
			String searchName, String myNodeId, String myRelationId, String userId) {
		this.activity = activity;
		this.searchRecordsArrayList = searchRecordsArrayList;
		this.searchName = searchName;
		this.myNodeId = myNodeId;
		this.myRelationId = myRelationId;
		this.userId = userId;
		inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(int position, View convertView, ViewGroup parent) {
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

		} else if (gender == 1) {
			holder.searchimageview.setBorderColor(activity.getResources().getColor(R.color.pt_blue));
			UrlImageViewHelper.setUrlDrawable(holder.searchimageview,
					"https://www.parivartree.com/profileimages/thumbs/" + id + "PROFILE.jpeg", activity.getResources()
							.getDrawable(R.drawable.male), 10000);
		} else if (gender == 2) {
			holder.searchimageview.setBorderColor(Color.MAGENTA);
			UrlImageViewHelper.setUrlDrawable(holder.searchimageview,
					"https://www.parivartree.com/profileimages/thumbs/" + id + "PROFILE.jpeg", activity.getResources()
							.getDrawable(R.drawable.female), 10000);
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
		}else if(invite == 1){
			holder.searchInviteBtn.setText("Already Invited");
			holder.searchInviteBtn.setTextColor(activity.getResources().getColor(R.color.ll_black));
			holder.searchInviteBtn.setBackgroundColor(activity.getResources().getColor(R.color.pt_grey));
		}else if(myNodeId.equals(userId)){
			holder.searchInviteBtn.setText("Invite");
			holder.searchInviteBtn.setOnClickListener(CustomSearchReationAdapter.this);
		}else if(!myNodeId.equals(userId)){
			holder.searchInviteBtn.setText("Recommend");
			holder.searchInviteBtn.setOnClickListener(CustomSearchReationAdapter.this);
		}
		

		return convertView;
	}

	private static class Holder {
		public TextView textSearchName, textsearchlocation, textsearchrelation,textSearchRelationTitle;
		public Button searchInviteBtn;
		RectangularImageView searchimageview;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.searchinvitebtn) {
			Crouton.makeText(activity, "You click "+v.getTag(), Style.ALERT).show();
		}
	}
}
