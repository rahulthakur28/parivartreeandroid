package com.parivartree.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.message.BasicNameValuePair;
import com.parivartree.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomAutoCompleteAdapter extends ArrayAdapter<BasicNameValuePair> {

	ArrayList<BasicNameValuePair> userHashMap;
	Context context;
	int textViewResourceId;

	public CustomAutoCompleteAdapter(Context context, int textViewResourceId, ArrayList<BasicNameValuePair> listuser) {
		super(context, textViewResourceId, listuser);
		// TODO Auto-generated constructor stub
		this.textViewResourceId = textViewResourceId;
		this.userHashMap = listuser;
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll = (LinearLayout) inflater.inflate(textViewResourceId, parent, false);
		// TextView tv = (TextView) ll.findViewById(R.id.textViewauto);
		// tv.setText("name");

		ll.setTag(0, userHashMap.get(position).getName().toString());

		return parent;

	}
}
