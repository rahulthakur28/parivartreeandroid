package com.parivartree.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.models.Event;

@SuppressLint("ViewHolder")
public class CustomAdapter extends BaseAdapter {

	ArrayList<Event> events;
	Context context;

	public CustomAdapter(Context context, ArrayList<Event> events) {
		this.context = context;
		this.events = events;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.custom_list_view, parent, false);

		if (position % 2 == 0) {
			ll.setBackgroundColor(context.getResources().getColor(R.color.pt_grey_transparent));
		} else {
			ll.setBackgroundColor(context.getResources().getColor(R.color.pt_overlay));
		}
		ImageView imageview = (ImageView) ll.findViewById(R.id.imageVieweventlist);
		TextView txtName = (TextView) ll.findViewById(R.id.textViewName);
		TextView txtEventName = (TextView) ll.findViewById(R.id.textVieweventname);
		TextView txtdate = (TextView) ll.findViewById(R.id.textViewdate);

		UrlImageViewHelper.setUrlDrawable(imageview,
				"https://www.parivartree.com/profileimages/thumbs/" + events.get(position).getAuthorId()
						+ "PROFILE.jpeg", context.getResources().getDrawable(R.drawable.parivar_mobile_profile_image_vsmall), 0);
		txtName.setText(events.get(position).getName());
		txtEventName.setText(events.get(position).getEventName());
		txtdate.setText(events.get(position).getDate() + "  " + events.get(position).getTime());

		return ll;
	}

}
