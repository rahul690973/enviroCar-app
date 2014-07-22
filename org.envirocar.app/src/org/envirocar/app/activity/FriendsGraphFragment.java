package org.envirocar.app.activity;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;




import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.envirocar.app.R;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.model.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;


public class FriendsGraphFragment extends SherlockFragment
{
   

	private String friendName;
	private String FRIEND_URL="https://envirocar.org/api/stable/users/";
	private String USER_URL="https://envirocar.org/api/stable/users/";
	private HashMap<String, Double> friendStatistics;
	private HashMap<String, Double> userStatistics;
	
	private TreeMap<String, Double> friendStatisticsSorted;
	private TreeMap<String, Double> userStatisticsSorted;
	
	
	
	private User user;
	private String response=null;
	private String username=null;
	
	private String DOWNLOAD_FRIEND="friend";
	private String DOWNLOAD_USER="user";
	private String DOWNLOAD_TYPE;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
			friendName = getArguments().getString("friend_name"); 
			setHasOptionsMenu(true);
			View view = inflater.inflate(R.layout.friends_graph, null);	
			
		return view;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		 user = UserManager.instance().getUser();			
		 username=user.getUsername();
		 //String url_select = FRIEND_URL+username+"/friends/"+friendName+"/statistics";
		 String url_select = FRIEND_URL+username+"/statistics";
		 new downloadStatistics().execute(url_select,DOWNLOAD_USER);
		
	}
	
	class downloadStatistics extends AsyncTask<String, String, Void>
	{
	
	 		protected void onPreExecute() {

	 		}
	 		
	 		@Override
			protected Void doInBackground(String... params){
	 				
	 				DOWNLOAD_TYPE=params[1];
	 		        downLoadData(params[0]);	
				    
			return null;
	     }	

	 		protected void onPostExecute(Void v) {
	    		
	    	 if(response!=null){
	    		 
	    		 if(DOWNLOAD_TYPE.equalsIgnoreCase(DOWNLOAD_FRIEND)){
	    			 
	    			    friendStatistics=new HashMap<String,Double>();
						parseStatistics(friendStatistics);	
	    		 }
	    		 else if(DOWNLOAD_TYPE.equalsIgnoreCase(DOWNLOAD_USER)){
	    			 
	    			 	userStatistics=new HashMap<String,Double>();
	    			    parseStatistics(userStatistics);
	    		 }
	    		 
	    	 }
	    	 
            

	    }

	}
	
	private void notifyFriendsDownLoad(){
		
		String url_select = FRIEND_URL+username+"/friends/"+friendName+"/statistics";
		new downloadStatistics().execute(url_select,DOWNLOAD_FRIEND);
		
	}
	
	private void downLoadData(String url_select){
		
		DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpPost = new HttpGet(url_select);
      
        httpPost.setHeader("X-User", user.getUsername());
        httpPost.setHeader("X-Token", user.getToken());


	    try {
	    		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    		response = client.execute(httpPost,responseHandler);		    			                                    
	    }catch (IOException e) {

	    	e.printStackTrace();

		}
		
	}
	
	
	private void parseStatistics(HashMap<String, Double> statistics){
		
		try {
			JSONObject json=new JSONObject(response);
			if(json!=null){
				
				
				JSONArray jsonStatistics=json.getJSONArray("statistics");
				
				if(DOWNLOAD_TYPE.equalsIgnoreCase(DOWNLOAD_FRIEND)){
					
					for(int i=0;i<jsonStatistics.length();i++){
						
						String phenomenonName=jsonStatistics.getJSONObject(i).getJSONObject("phenomenon").getString("name");
						if(statistics.containsKey(phenomenonName)){
							Double phenomenonValue=Double.parseDouble(jsonStatistics.getJSONObject(i).getString("avg"));
							statistics.put(phenomenonName, phenomenonValue);
						}
								
					}
					
				    friendStatisticsSorted= new TreeMap<String, Double>(statistics);
				    Intent i=execute(getActivity());
					startActivity(i);
					
				}
			
				else if(DOWNLOAD_TYPE.equalsIgnoreCase(DOWNLOAD_USER)){
				for(int i=0;i<jsonStatistics.length();i++){
					
					String phenomenonName=jsonStatistics.getJSONObject(i).getJSONObject("phenomenon").getString("name");
					Double phenomenonValue=Double.parseDouble(jsonStatistics.getJSONObject(i).getString("avg"));
					statistics.put(phenomenonName, phenomenonValue);
					
							
				}
				
				userStatisticsSorted= new TreeMap<String, Double>(statistics);
				notifyFriendsDownLoad();
			 }
				
														
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	  public Intent execute(Context c) {
		    String[] titles = new String[] { username, friendName };
		    List<double[]> values = new ArrayList<double[]>();
		    double[] friendValues=getValuesFromMaps(friendStatisticsSorted);
		    double[] userValues=getValuesFromMaps(userStatisticsSorted);
		    
		   // values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500});
		    //values.add(new double[] { 14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500 });
		   
		    values.add(userValues);
		    values.add(friendValues);
		    int[] colors = new int[] { Color.RED, Color.BLUE };
		    AbstractDemoChart chart=new AbstractDemoChart();
		    XYMultipleSeriesRenderer renderer = chart.buildBarRenderer(colors);
		    renderer.setOrientation(Orientation.VERTICAL);
		    chart.setChartSettings(renderer, "Statistics", "", "", 0.5,
		        12.5, 0, 600, Color.GRAY, Color.LTGRAY);
		    //renderer.setXLabels(10);
		    //renderer.setYLabels(10);
		    renderer.addXTextLabel(1, "GPS Speed");
		    renderer.addXTextLabel(2, "Intake Temperature");
		    renderer.addXTextLabel(3, "Consumption");
		    renderer.addXTextLabel(4, "RPM");
		    renderer.addXTextLabel(5, "GPS Accuracy");
		    renderer.addXTextLabel(6, "CO2");
		    renderer.addXTextLabel(7, "GPS Altitude");
		    renderer.addXTextLabel(8, "Intake Pressure");
		    renderer.addXTextLabel(9, "Speed");
		    renderer.addXTextLabel(10, "Calculated MAF");
		    renderer.setMargins(new int[] { 30, 40, 100, 0 });
		    renderer.setXLabelsPadding(250);
		    
		    
		    renderer.setDisplayChartValues(true);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setBackgroundColor(Color.BLACK);
		    renderer.setBarSpacing(1.0);
		    return ChartFactory.getBarChartIntent(c, chart.buildBarDataset(titles, values), renderer,
		        Type.DEFAULT);
		  }
	
	
	 private double[] getValuesFromMaps(TreeMap<String,Double> t){
		 
		 double values[]=new double[t.size()];
		 Iterator<Entry<String, Double>> entries = t.entrySet().iterator();
		 int i=0;
		    while (entries.hasNext()) {
		        Entry entry = (Entry) entries.next();
		        values[i]=(Double)entry.getValue();
		        i++;
		    }
		    
		 return values;   
		 
	 }
    
}