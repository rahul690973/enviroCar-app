package org.envirocar.app.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.envirocar.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderboardAdapter extends BaseAdapter {
	
	 private  LayoutInflater inflater=null;
	 private LinkedHashMap<String,String>usersAndScores=null;
	 private ArrayList<String>usersList=null;
	 
	 
	 public LeaderboardAdapter(Context c,LinkedHashMap<String,String>map,ArrayList<String>values){
		 
		 inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 this.usersAndScores=map;
		 this.usersList= values;
		 
	 }

	 @Override
		public int getCount() {
			
			return usersList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return usersList.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view=convertView;
        if(view==null)
            view = inflater.inflate(R.layout.leaderboard_list_item,null);
        
        TextView nameView=(TextView)view.findViewById(R.id.user_name);
        TextView scoreView=(TextView)view.findViewById(R.id.user_score);
        
        String username=usersList.get(position);
        nameView.setText(username);
        
        String score=usersAndScores.get(username.substring(username.lastIndexOf(" ")).trim());
        scoreView.setText(score);
           
		return view;
	}

}
