package com.parivartree.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.parivartree.R;
import com.parivartree.adapters.GridViewImageAdapter;
import com.parivartree.helpers.AppConstant;
import com.parivartree.helpers.Utils;

public class ViewPhotosFragment extends Fragment implements OnClickListener {
	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	Activity activity;
	Context context;
	TextView textViewPhotos, textViewVideos;
	Button buttonCreatePhotos;
	FragmentManager fragmentManager;
	Fragment fragment;

	public ViewPhotosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_view_photos, container, false);
		gridView = (GridView) rootView.findViewById(R.id.grid_view);
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView.findViewById(R.id.btncreatestudio);
		activity = getActivity();
		context = getActivity().getApplicationContext();
		utils = new Utils(activity);

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();

		// Gridview adapter
		adapter = new GridViewImageAdapter(activity, imagePaths, columnWidth);

		// setting grid view adapter
		gridView.setAdapter(adapter);
		textViewPhotos.setBackgroundColor(getResources().getColor(R.color.pt_dark_grey));
		textViewPhotos.setOnClickListener(this);
		textViewVideos.setOnClickListener(this);
		buttonCreatePhotos.setOnClickListener(this);
		return rootView;
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, AppConstant.GRID_PADDING,
				r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

		gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.textviewphotos) {
			fragment = new ViewPhotosFragment();
		} else if (v.getId() == R.id.textviewvideos) {
			fragment = new ViewVideosFragment();
		} else if (v.getId() == R.id.btncreatestudio) {
			fragment = new CreatePhotosFragment();
		}
		if (fragment != null) {
			fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.beginTransaction()
			// Add this transaction to the back stack
					.addToBackStack(null).replace(R.id.frame_container, fragment).commit();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
}
