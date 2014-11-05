package com.parivartree.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parivartree.R;

public class FindOnMapFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_find_on_map, container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// initializeMap();
	}

}
