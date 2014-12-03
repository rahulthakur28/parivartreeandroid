package com.parivartree.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.helpers.RectangularImageView;

public class ImmediateFamilyAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> familyMembers;
	private Context context;
	private LayoutInflater inflater;
	public ImmediateFamilyAdapter(Context context, ArrayList<HashMap<String, String>> immediateFamily) {
		this.context = context;
		this.familyMembers = immediateFamily;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d("ImmediateFamilyAdapter", "initialized");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.d("ImmediateFamilyAdapter", "size: " + familyMembers.size());
		return familyMembers.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Log.d("ImmediateFamilyAdapter", "item at postion: " + position);
		return familyMembers.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Log.d("ImmediateFamilyAdapter", "item id: " + familyMembers.get(position).get("relnodeid"));
		return Long.parseLong(familyMembers.get(position).get("relnodeid"));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d("ImmediateFamilyAdapter", "getView() called");
		Holder holder;
	try {
	if (convertView == null) {
				// Inflate the view since it does not exist
				convertView = inflater.inflate(R.layout.item_immediate_family, parent, false);

				// Create and save off the holder in the tag so we get quick
				// access to inner fields
				// This must be done for performance reasons
				holder = new Holder();
				holder.image = (RectangularImageView) convertView.findViewById(R.id.imageView1);
				holder.image.setMaxWidth(180);
				holder.image.setMaxHeight(180);
				holder.image.setLayoutParams(new LayoutParams(180, 180));
				holder.firstName = (TextView) convertView.findViewById(R.id.textViewFirstName);
				holder.lastName = (TextView) convertView.findViewById(R.id.textViewLastName);

				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			Log.d("ImmediateFamilyAdapter", "holder object created");

			int id = Integer.parseInt(familyMembers.get(position).get("relnodeid"));
			int gender = Integer.parseInt(familyMembers.get(position).get("gender"));
			// get image
			if (gender == 1) {
				holder.image.setBorderColor(context.getResources().getColor(R.color.pt_blue));
				UrlImageViewHelper.setUrlDrawable(holder.image, "https://www.parivartree.com/profileimages/thumbs/"
						+ id + "PROFILE.jpeg", context.getResources().getDrawable(R.drawable.male), 10000);
			} else {
				holder.image.setBorderColor(Color.MAGENTA);
				UrlImageViewHelper.setUrlDrawable(holder.image, "https://www.parivartree.com/profileimages/thumbs/"
						+ id + "PROFILE.jpeg", context.getResources().getDrawable(R.drawable.female), 10000);
			}
			Log.d("ImmediateFamilyAdapter", "image set: gender - " + gender);
			Log.d("ImmediateFamilyAdapter---------------", "Firstname - "
					+ familyMembers.get(position).get("firstname"));

			// Populate the text
			String userName = (familyMembers.get(position).get("firstname") + " "
					+ familyMembers.get(position).get("lastname"));
			
			if (userName.length() > 9) {
				Log.d("Immediate-----------", ""+userName.length());
				holder.firstName.setText(userName.substring(0, 9) +"...");
			} else {
				holder.firstName.setText(userName);
			}
			String relationName = "NA";
			String relationId = (familyMembers.get(position).get("relationid"));
			if (relationId.equals("1")) {
				relationName = "Father";
			} else if (relationId.equals("2")) {
				relationName = "Mother";
			}  else if (relationId.equals("3")) {
				relationName = "Wife";
			}else if (relationId.equals("4")) {
				relationName = "Brother";
			} else if (relationId.equals("5")) {
				relationName = "Sister";
			} else if (relationId.equals("6")) {
				relationName = "Son";
			} else if (relationId.equals("7")) {
				relationName = "Daughter";
			} else if (relationId.equals("8")) {
				relationName = "Husband";
			}
			holder.lastName.setText("(" + relationName + ")");

			Log.d("ImmediateFamilyAdapter", "name added");
		
		} catch (Exception e) {
			Log.d("ImmediateFamilyAdapter", e.getMessage());
		}
		
		// Set the color
		// convertView.setBackgroundColor(getItem(position).getBackgroundColor());

		return convertView;
	}

	/** View holder for the views we need access to */
	private static class Holder {
		public TextView firstName, lastName;
		public RectangularImageView image;
	}
}
