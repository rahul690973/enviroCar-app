package org.envirocar.app.activity;



import java.io.IOException;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;





import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
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



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;




public class FriendsGraphFragment
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
	private Context context;
	
	
	public FriendsGraphFragment(){
		
		
		
	}
	
	public FriendsGraphFragment(String friendName,Context c){
		
		this.friendName=friendName;
		this.context=c;
	    downloadStatistics();
	    
		
	}
	
	public void downloadStatistics(){
		
		 //friendName = getArguments().getString("friend_name");  
		 user = UserManager.instance().getUser();			
		 username=user.getUsername();
		 //String url_select = FRIEND_URL+username+"/friends/"+friendName+"/statistics";
		 String url_select = FRIEND_URL+username+"/statistics";
		 new downloadStatistics().execute(url_select,DOWNLOAD_USER);
	}
	
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		
//			friendName = getArguments().getString("friend_name"); 
//			setHasOptionsMenu(true);
//			View view = null;	
//			
//			user = UserManager.instance().getUser();			
//			 username=user.getUsername();
//			 //String url_select = FRIEND_URL+username+"/friends/"+friendName+"/statistics";
//			 String url_select = FRIEND_URL+username+"/statistics";
//			 new downloadStatistics().execute(url_select,DOWNLOAD_USER);
//			
//			
//			
//		return view;
//	}
	
	
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		
//		
//		 user = UserManager.instance().getUser();			
//		 username=user.getUsername();
//		 //String url_select = FRIEND_URL+username+"/friends/"+friendName+"/statistics";
//		 String url_select = FRIEND_URL+username+"/statistics";
//		 new downloadStatistics().execute(url_select,DOWNLOAD_USER);
//		
//	}
	
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
						if(userStatistics.containsKey(phenomenonName)){
							Double phenValue=Double.parseDouble(jsonStatistics.getJSONObject(i).getString("avg"));
							int phenomenonValue= decimalFormat(phenValue);
							statistics.put(phenomenonName, (double) phenomenonValue);
						}
								
					}
					
				    friendStatisticsSorted= new TreeMap<String, Double>(statistics);
				    Intent i=execute(context);
					context.startActivity(i);
					
				}
			
				else if(DOWNLOAD_TYPE.equalsIgnoreCase(DOWNLOAD_USER)){
				for(int i=0;i<jsonStatistics.length();i++){
					
					String phenomenonName=jsonStatistics.getJSONObject(i).getJSONObject("phenomenon").getString("name");
					Double phenValue=Double.parseDouble(jsonStatistics.getJSONObject(i).getString("avg"));
				    int phenomenonValue= decimalFormat(phenValue);
					statistics.put(phenomenonName, (double) phenomenonValue);
					
							
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
	
	public int decimalFormat(double d){
		
		
		int value = (int)Math.round(d);
		return value;
		
	}
	
	
	  public Intent execute(Context c) {
		  
		    String[] titles = new String[] { username, friendName };
		    List<Double[]> values = new ArrayList<Double[]>();
		    Double friendValues[]=getValuesFromMaps(friendStatisticsSorted);
		    Double userValues[]=getValuesFromMaps(userStatisticsSorted);
		    
		    Double maxFriendValue=Collections.max(Arrays.asList(friendValues));
		    Double maxUserValue=Collections.max(Arrays.asList(userValues));
		    Double maxYRange=(maxFriendValue>maxUserValue)? maxFriendValue :maxUserValue;
		    
		    
		   		   
		    values.add(friendValues);
		    values.add(userValues);
		    		    
		    
		    int[] colors = new int[] { Color.RED, Color.BLUE };
		    AbstractDemoChart chart=new AbstractDemoChart();
		    XYMultipleSeriesRenderer renderer = chart.buildBarRenderer(colors);
		    //renderer.setOrientation(Orientation.VERTICAL);
		    chart.setChartSettings(renderer, "", "", "", 0.5,
		        12.5, 0, maxYRange/3, Color.BLACK, Color.BLACK);
		    //renderer.setXLabels(10);
		    //renderer.setYLabels(10);
		    renderer.addXTextLabel(1, c.getString(R.string.currentCO2));
		    renderer.addXTextLabel(2,c.getString(R.string.maf) );
		    renderer.addXTextLabel(3, c.getString(R.string.fuel_consumption));
		    renderer.addXTextLabel(4, c.getString(R.string.gps_accuracy));
		    renderer.addXTextLabel(5, c.getString(R.string.gps_altitude));
		    renderer.addXTextLabel(6, c.getString(R.string.gps_speed));
		    renderer.addXTextLabel(7, c.getString(R.string.intake_pressure));
		    renderer.addXTextLabel(8, c.getString(R.string.intake_temperature));
		    renderer.addXTextLabel(9, c.getString(R.string.rpm));
		    renderer.addXTextLabel(10, c.getString(R.string.speed));
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0, Color.BLACK);
		    renderer.setPanEnabled(false, true);
		    renderer.setPanLimits(new double[]{0,0,0,50000});
		    
		    
		    //renderer.setMargins(new int[] { 30, 40, 100, 0 });
		    renderer.setXLabelsPadding(250);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setXLabelsAngle(-60);
		    
		    renderer.setMarginsColor(Color.WHITE);
		    renderer.setBackgroundColor(Color.WHITE);
		    
		    
		   
		    
		    //renderer.setDisplayChartValues(true);
		    
		    //renderer.setApplyBackgroundColor(true);
		    //renderer.setBackgroundColor(Color.BLACK);
		    renderer.setBarWidth(26);
		    renderer.setBarSpacing(2);
		   
		    return ChartFactory.getBarChartIntent(c, chart.buildBarDataset(titles, values), renderer,
		        Type.DEFAULT);
		  }
	
	
	 private Double[] getValuesFromMaps(TreeMap<String,Double> t){
		 
		 Double values[]=new Double[t.size()];
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