package com.parivartree.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parivartree.R;
import com.parivartree.adapters.TabViewPagerAdapter;

public class ViewStudioFragment extends Fragment implements OnClickListener {
	ViewPager Tab;
	TabViewPagerAdapter TabAdapter;
	//ActionBar actionBar;
	Activity activity;
	Context context;
	TextView textViewPhotos,textViewVideos;
	Button buttonCreatePhotos;
	FragmentManager fragmentManager;
	Fragment fragment;
	public ViewStudioFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_view_studio, container, false);

		activity = getActivity();
		context = getActivity().getApplicationContext();
		TabAdapter = new TabViewPagerAdapter(getActivity().getSupportFragmentManager());
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView.findViewById(R.id.btncreatestudio);
        Tab = (ViewPager)rootView.findViewById(R.id.createpager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                       
//                    	actionBar = activity.getActionBar();
//                    	actionBar.setSelectedNavigationItem(position);                    
                    	}
                });
        Tab.setAdapter(TabAdapter);
        
       // actionBar = activity.getActionBar();
        //Enable Tabs on Action Bar
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
//
//			@Override
//			public void onTabReselected(android.app.ActionBar.Tab tab,
//					FragmentTransaction ft) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			 public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//	          
//	            Tab.setCurrentItem(tab.getPosition());
//	        }
//
//			@Override
//			public void onTabUnselected(android.app.ActionBar.Tab tab,
//					FragmentTransaction ft) {
//				// TODO Auto-generated method stub
//				
//			}};
//			//Add New Tab
//			actionBar.addTab(actionBar.newTab().setText("View Photos").setTabListener(tabListener));
//			actionBar.addTab(actionBar.newTab().setText("View Videos").setTabListener(tabListener));
        textViewPhotos.setOnClickListener(this);
        textViewVideos.setOnClickListener(this);
        buttonCreatePhotos.setOnClickListener(this);
		return rootView;
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.textviewphotos){
			 fragment = new ViewStudioFragment();
		}else if(v.getId() == R.id.textviewvideos){
			 fragment = new ViewVideosFragment();
		}else if(v.getId() == R.id.btncreatestudio){
			 fragment = new EditProfileFragment();
		}
		if (fragment != null) {
			fragmentManager = getActivity().getSupportFragmentManager();
			   fragmentManager .beginTransaction()
			   // Add this transaction to the back stack
			            .addToBackStack(null)
			   .replace(R.id.frame_container, fragment)
			   .commit();
	}
	
}
}