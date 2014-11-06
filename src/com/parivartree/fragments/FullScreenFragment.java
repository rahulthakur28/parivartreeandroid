package com.parivartree.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parivartree.R;
import com.parivartree.adapters.FullScreenImageAdapter;
import com.parivartree.helpers.Utils;

public class FullScreenFragment extends Fragment{
	Activity activity;
	Context context;
	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	public FullScreenFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_full_screen, container, false);
		activity = getActivity();
		context = getActivity().getApplicationContext();
		viewPager = (ViewPager) rootView.findViewById(R.id.pager);

		utils = new Utils(context);
		Bundle bndle = getArguments();
		int position = bndle.getInt("position");
//		Intent i = activity.getIntent();
//		int position = i.getIntExtra("position", 0);

		adapter = new FullScreenImageAdapter(activity,
				utils.getFilePaths());

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
		return rootView;
	}
}
