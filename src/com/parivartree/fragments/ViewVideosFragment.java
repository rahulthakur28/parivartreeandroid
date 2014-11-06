package com.parivartree.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parivartree.R;

public class ViewVideosFragment extends Fragment implements OnClickListener {
	Activity activity;
	Context context;
	TextView textViewPhotos, textViewVideos;
	Button buttonCreatePhotos;
	FragmentManager fragmentManager;
	Fragment fragment;

	public ViewVideosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_view_videos, container, false);
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView.findViewById(R.id.btncreatestudio);
		
		textViewVideos.setBackgroundColor(getResources().getColor(R.color.pt_dark_grey));
		textViewPhotos.setOnClickListener(this);
		textViewVideos.setOnClickListener(this);
		buttonCreatePhotos.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.textviewphotos) {
			fragment = new ViewPhotosFragment();
		} else if (v.getId() == R.id.textviewvideos) {
			fragment = new ViewVideosFragment();
		} else if (v.getId() == R.id.btncreatestudio) {
			fragment = new CreateVideosFragment();
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
