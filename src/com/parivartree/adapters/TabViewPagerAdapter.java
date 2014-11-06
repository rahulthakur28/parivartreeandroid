package com.parivartree.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.parivartree.fragments.ViewPhotosFragment;
import com.parivartree.fragments.ViewVideosFragment;

public class TabViewPagerAdapter  extends FragmentStatePagerAdapter {
    public TabViewPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
        case 0:
            //Fragement for Android Tab
            return new ViewPhotosFragment();
        case 1:
           //Fragment for Ios Tab
            return new ViewVideosFragment();
        }
		return null;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2; //No of Tabs
	}


    }
