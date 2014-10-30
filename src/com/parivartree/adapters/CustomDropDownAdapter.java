package com.parivartree.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parivartree.R;
import com.parivartree.models.SpinnerItem;

public class CustomDropDownAdapter extends BaseAdapter {

	ArrayList<SpinnerItem> items;
	Context context;

	public CustomDropDownAdapter(Context context, ArrayList<SpinnerItem> items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return (long) items.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View spinnerView = inflater.inflate(R.layout.list_item_profile, parent, false);
		TextView spinnerText = (TextView) spinnerView.findViewById(R.id.textView1);
		spinnerText.setText(items.get(position).getValue());

		return spinnerView;
	}

}
