package com.parivartree.adapters;

import java.util.ArrayList;

import com.parivartree.models.PostItem;

import com.parivartree.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class WallPostAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<PostItem> postItems;
	private LayoutInflater inflater;
	
	WallPostAdapter (Context context, ArrayList<PostItem> postItems) {
		this.context = context;
		this.postItems = postItems;
		inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return postItems.size();
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return postItems.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return postItems.get(position).getId();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = (LinearLayout) inflater.inflate(R.layout.view_post_item, parent, false);
			viewHolder = new ViewHolder();
			//viewHolder.imagePostLayout
			//viewHolder.videoPostLayout
			//viewHolder.postStatusLayout
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(postItems.get(position).getPostType() == "textPost") {
			// set the view for text type post
			
		} else if (postItems.get(position).getPostType() == "imagePost") {
			// set the view for image type post
			
		} else if (postItems.get(position).getPostType() == "videoPost") {
			// set the view for video type post
			
		}
		
		return convertView;
	}
	
	class ViewHolder {
		public LinearLayout imagePostLayout; 
		public LinearLayout videoPostLayout;
		public LinearLayout postStatusLayout;
	}
	
}
