package com.parivartree.adapters;

import java.util.ArrayList;

import com.parivartree.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationHintAdapter extends ArrayAdapter<String> {

	//private Context context;
	private int textViewResourceId;
	private ArrayList<String> objects = new ArrayList<String>();
	private LayoutInflater inflater;
	
	public LocationHintAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		//this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.objects = objects;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		TextView locationView;
		if(convertView == null) {
			convertView = (LinearLayout) inflater.inflate(textViewResourceId, parent, false);
			locationView = (TextView) convertView.findViewById(R.id.textLocation);
		}
		else {
			locationView = (TextView) convertView.findViewById(R.id.textLocation);
		}
		
		locationView.setText(objects.get(position));

		return convertView;

	}

}
