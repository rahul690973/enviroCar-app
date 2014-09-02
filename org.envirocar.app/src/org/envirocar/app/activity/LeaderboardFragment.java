package org.envirocar.app.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.envirocar.app.R;
import org.envirocar.app.application.FriendsImageAdapter;
import org.envirocar.app.application.LeaderboardAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LeaderboardFragment extends SherlockFragment {

	private ArrayList<String> data;
	LeaderboardAdapter adapter;
	private int pageCount;
	private int increment = 0;

	public int TOTAL_LIST_ITEMS = 0;
	public int NUM_ITEMS_PAGE = 50;

	private TextView previousButton;
	private TextView nextButton;
	private ListView leadersList;
	private LinearLayout buttonLayout;
	private LinkedHashMap<String, String> userAndScore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		
		if (getArguments() != null) {	
			String str=  getArguments().getString("map");
		     Gson gson = new Gson();

		     Type entityType = new TypeToken< LinkedHashMap<String,String>>(){}.getType();
		     userAndScore = gson.fromJson(str, entityType);

		}
		
		
		TOTAL_LIST_ITEMS = userAndScore.size();
		View view = inflater.inflate(R.layout.leaderboard_fragment_new, null);
		leadersList = (ListView) view.findViewById(R.id.leaderboard_list);
		previousButton = (TextView) view.findViewById(R.id.previous);
		nextButton = (TextView) view.findViewById(R.id.next);
		buttonLayout=(LinearLayout)view.findViewById(R.id.prev_next_layout);

		// getLeaderBoardFromServer();

		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		previousButton.setEnabled(false);
		data = new ArrayList<String>();


		data = new ArrayList<String>(userAndScore.keySet());
		loadList(0);

		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				increment++;
				loadList(increment);
				
			}
		});

		previousButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				increment--;
				loadList(increment);
				
			}
		});

	}


	/**
	 * Method for loading data in listview
	 * 
	 * @param number
	 */
	private void loadList(int number) {
		ArrayList<String> sort = new ArrayList<String>();

		int start = number * NUM_ITEMS_PAGE;
		for (int i = start; i < (start) + NUM_ITEMS_PAGE; i++) {
			if (i < data.size()) {
				sort.add((i + 1) + " " + " " + data.get(i));
			} else {
				break;
			}
		}
		
		if(sort.size()>0){
			adapter = new LeaderboardAdapter(getActivity(), userAndScore, sort);
			leadersList.setAdapter(adapter);
			showPrevNextButtons(sort,number);
		}

	}
	
	private void showPrevNextButtons(ArrayList<String>sort,int number){

		
		if(sort.size()>NUM_ITEMS_PAGE){
			
			previousButton.setVisibility(View.VISIBLE);
			nextButton.setVisibility(View.VISIBLE);
			if(number==0){
				
				previousButton.setVisibility(View.GONE);
				
			}
			
			
		}
		
		
		else if(sort.size()<=NUM_ITEMS_PAGE){
			
			previousButton.setVisibility(View.VISIBLE);
			nextButton.setVisibility(View.GONE);
			
			if(number==0)
				buttonLayout.setVisibility(View.GONE);
			
		}
		
		
		
	}

}
