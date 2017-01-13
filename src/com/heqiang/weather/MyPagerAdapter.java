package com.heqiang.weather;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {
	private ArrayList<View> viewLists;
	private int length;
	public MyPagerAdapter(ArrayList<View> viewLists){
		this.viewLists = viewLists;
	}

	//important
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(View arg0, int position, Object arg2) {
		//((ViewGroup)arg0).removeView(viewLists.get(position));
	}

	@Override
	public int getCount() {		
		if (viewLists != null && viewLists.size() != 0) {
			length = viewLists.size();
		} else {
			length = 0;
		}
		return length;
	}

	@Override
	public Object instantiateItem(View arg0, int position) {
		View v = viewLists.get(position);
		if(v.getParent() != null){
			((ViewPager)v.getParent()).removeView(v);
		}
		((ViewPager)arg0).addView(v,0);
		return v;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
