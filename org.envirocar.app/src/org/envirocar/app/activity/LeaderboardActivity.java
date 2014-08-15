package org.envirocar.app.activity;



import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.envirocar.app.R;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.TrackRetrievalException;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;






public class LeaderboardActivity extends SherlockFragmentActivity{
	
	 
	 private ArrayList<LinkedHashMap<String,String>>stats;
	 private SectionsPagerAdapter mSectionsPagerAdapter;
	 private ViewPager mViewPager;
	 private PagerTitleStrip mPager;
	 private static int NO_OF_PAGES=2;
	 

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leaderboard);

		Bundle bundle = this.getIntent().getExtras();
		if(bundle!=null){
			 
			String arrayList= bundle.getString("rank_map");
			Type listOfHashMaps = new TypeToken<ArrayList<LinkedHashMap<String,String>>>(){}.getType();
			//String s = new Gson().toJson(arrayList, listOfHashMaps);
			stats = new Gson().fromJson(arrayList, listOfHashMaps);
			//stats = (ArrayList<LinkedHashMap<String, String>>) getIntent().getSerializableExtra("rank_map");	 		   
		}
		
	
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mPager = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		mPager.setTextSpacing(75);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		

	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			LeaderboardFragment fragment = new LeaderboardFragment();
			Bundle args = new Bundle();
			Gson gson = new Gson();
			 
			
			switch(position){
			
			case 0:
				
				String co2 = gson.toJson(stats.get(1));
				args.putString("map",co2);
				fragment.setArguments(args);
				break;
			case 1:
				
				String fuel = gson.toJson(stats.get(0));
				args.putString("map",fuel);
				fragment.setArguments(args);
				break;
			
			}
			
			return fragment;
		}

		@Override
		public int getCount() {
			
			return NO_OF_PAGES;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.currentCO2).toUpperCase(l);
				
			case 1:
				return getString(R.string.fuel_consumption).toUpperCase(l);
			
			}
			return null;
		}
	}

	
	
	
	
	
	
}
