package com.parivartree.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.parivartree.R;

public class DiscussionFragment extends Fragment implements OnClickListener {

	public DiscussionFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);

		return rootView;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}
