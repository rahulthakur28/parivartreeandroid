package com.parivartree.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parivartree.R;
import com.parivartree.adapters.TabPagerAdapter;

public class PhotosFragment extends Fragment {
	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;
	Activity activity;
	Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
//		activity = getActivity();
//		context = getActivity().getApplicationContext();
//		TabAdapter = new TabPagerAdapter(getActivity().getSupportFragmentManager());
//        
//        Tab = (ViewPager)rootView.findViewById(R.id.createpager);
//        Tab.setOnPageChangeListener(
//                new ViewPager.SimpleOnPageChangeListener() {
//                    @Override
//                    public void onPageSelected(int position) {
//                       
//                    	actionBar = activity.getActionBar();
//                    	actionBar.setSelectedNavigationItem(position);                    }
//                });
//        Tab.setAdapter(TabAdapter);
//        
//        actionBar = activity.getActionBar();
//        //Enable Tabs on Action Bar
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
//			actionBar.addTab(actionBar.newTab().setText("Create Photos").setTabListener(tabListener));
//			actionBar.addTab(actionBar.newTab().setText("Create Videos").setTabListener(tabListener));
		return rootView;
	}
}