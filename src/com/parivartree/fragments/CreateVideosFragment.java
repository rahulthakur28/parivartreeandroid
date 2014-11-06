package com.parivartree.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.parivartree.R;

public class CreateVideosFragment extends Fragment implements OnClickListener {
	Activity activity;
	Context context;
	TextView textViewPhotos, textViewVideos;
	Button buttonCreatePhotos,browseVideo;
	FragmentManager fragmentManager;
	Fragment fragment;
	EditText editVideoName;
	EditText editVideoDescription;
	private Spinner spinnerReach;
	private ArrayList<String> spinnerReachList;
	public CreateVideosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_videos, container, false);
		activity = getActivity();
		context = getActivity().getApplicationContext();
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView.findViewById(R.id.btncreatestudio);
		spinnerReach = (Spinner) rootView.findViewById(R.id.createvideospinner);		
		editVideoName = (EditText) rootView.findViewById(R.id.edit_video_name);
		editVideoDescription = (EditText) rootView.findViewById(R.id.edit_video_description);
		browseVideo = (Button) rootView.findViewById(R.id.btnbrowsevideo);
		
		textViewVideos.setBackgroundColor(getResources().getColor(R.color.pt_dark_grey));
		textViewPhotos.setOnClickListener(this);
		textViewVideos.setOnClickListener(this);
		buttonCreatePhotos.setOnClickListener(this);
		
		spinnerReachList = new ArrayList<String>();
		spinnerReachList.add("Private");
		spinnerReachList.add("Family");
		spinnerReachList.add("Public");
		ArrayAdapter<String> spinnerReachAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, spinnerReachList);
		spinnerReach.setAdapter(spinnerReachAdapter);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.textviewphotos) {
			fragment = new CreatePhotosFragment();
		} else if (v.getId() == R.id.textviewvideos) {
			fragment = new CreateVideosFragment();
		} else if (v.getId() == R.id.btncreatestudio) {
			fragment = new ViewVideosFragment();
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
