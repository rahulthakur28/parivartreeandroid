package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parivartree.R;
import com.parivartree.adapters.CustomNotificationAdapter;
import com.parivartree.adapters.CustomSearchReationAdapter;
import com.parivartree.models.SearchRecords;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the
 * {@link SearchCreateRelationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events. Use the
 * {@link SearchCreateRelationFragment#newInstance} factory method to create an
 * instance of this fragment.
 *
 */
public class SearchCreateRelationFragment extends Fragment implements OnClickListener {

	Activity activity;
	Button refineSearchbtn, createNewBtn;
	TextView textResultTitle;
	private ArrayList<SearchRecords> searchRecordsArrayList;
	private ArrayList<HashMap<String, String>> relationRecordsArrayList;
	String myNodeId, myRelationId, searchName,userId;
	int sizeSearchList;
	
	CustomSearchReationAdapter SearchReationAdapter;
	ListView searchListView;
	
	private SharedPreferences sharedPreferences;
	
	public SearchCreateRelationFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_search_create_relation, container, false);
		activity= getActivity();
		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", "NA");
		
		Bundle bndle = getArguments();
		if (bndle != null) {
			if (bndle.containsKey("searchrelationList")) {
				searchRecordsArrayList = bndle.getParcelableArrayList("searchrelationList");
				sizeSearchList = searchRecordsArrayList.size();
			}
			if (bndle.containsKey("searchname")) {
				searchName = bndle.getString("searchname");
			}
			if (bndle.containsKey("mynodeid")) {
				myNodeId = bndle.getString("mynodeid");
			}
			if (bndle.containsKey("myrelationid")) {
				myRelationId = bndle.getString("myrelationid");
			}
		}

		textResultTitle = (TextView) rootView.findViewById(R.id.textresulttitle);
		refineSearchbtn = (Button) rootView.findViewById(R.id.btnrefinesearch);
		createNewBtn = (Button) rootView.findViewById(R.id.btncreatenewrelation);
		searchListView = (ListView) rootView.findViewById(R.id.searchlistview);

		refineSearchbtn.setOnClickListener(this);
		createNewBtn.setOnClickListener(this);

		textResultTitle.setText("Parivartree has found "+sizeSearchList+" Results for "+searchName);
		SearchReationAdapter = new CustomSearchReationAdapter(activity, searchRecordsArrayList,searchName,myNodeId,myRelationId,userId);
		searchListView.setAdapter(SearchReationAdapter);
		SearchReationAdapter.notifyDataSetChanged();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnrefinesearch) {
			showRefineSearchDialog();
		}else if (v.getId() == R.id.btncreatenewrelation) {

		}
	}

	private void showRefineSearchDialog() {
		Dialog refineDialog = new Dialog(activity);
		refineDialog.setContentView(R.layout.refine_search_popup);
		refineDialog.show();
	}
}
