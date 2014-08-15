package org.envirocar.app.activity;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.apache.http.HttpResponse;
import org.envirocar.app.R;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.TrackDAO;
import org.envirocar.app.dao.DAOProvider.AsyncExecutionWithCallback;
import org.envirocar.app.dao.UserDAO;
import org.envirocar.app.dao.exception.DAOException;
import org.envirocar.app.logging.Logger;


import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class UserStatisticsFragment extends SherlockFragment {
	
	private LinearLayout chartCo2;
	private LinearLayout chartFuel;
	
	private LinkedHashMap<String,String>userStatistics[];
	protected static final Logger logger = Logger.getLogger(UserStatisticsFragment.class);
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		 View view = inflater.inflate(R.layout.user_statistics,null);
		
		 chartCo2=(LinearLayout)view.findViewById(R.id.chart_co2_emission);
		 chartFuel=(LinearLayout)view.findViewById(R.id.chart_fuel_consumption);
		
		return view;
		
	}	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		
		DAOProvider.async(new AsyncExecutionWithCallback<Void>() {

			@Override
			public Void execute()
					throws DAOException {
				
				UserDAO dao = DAOProvider.instance().getUserDAO();
				userStatistics=dao.getUserStatistics();
				return null;
			}

			@Override
			public Void onResult(Void result,
					boolean fail, Exception ex) {
				if (!fail) {
					
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							GraphicalView chart1=UserStatisticsFragment.this.execute(getActivity(),getString(R.string.Co2emission),userStatistics[0]);
							chartCo2.addView(chart1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
							
							GraphicalView chart2=UserStatisticsFragment.this.execute(getActivity(),getString(R.string.fuel_consumption),userStatistics[1]);
							chartFuel.addView(chart2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));					
						}
					});
					
					
				}
				else {
					logger.warn(ex.getMessage(), ex);
				}
				return null;
			}
			
		});
		
		
		
		
	}
	
	
	
	 public GraphicalView execute(Context context,String title,LinkedHashMap<String,String>map) {
		    String[] titles = new String[] {title };
		    List<Date[]> dates = new ArrayList<Date[]>();
		    List<double[]> values = new ArrayList<double[]>();
//		    Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
//		        new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
//		        new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
//		        new Date(97, 9, 1), new Date(98, 0, 1), new Date(98, 3, 1), new Date(98, 6, 1),
//		        new Date(98, 9, 1), new Date(99, 0, 1), new Date(99, 3, 1), new Date(99, 6, 1),
//		        new Date(99, 9, 1), new Date(100, 0, 1), new Date(100, 3, 1), new Date(100, 6, 1),
//		        new Date(100, 9, 1), new Date(100, 11, 1) };
		    
		    Date[] dateValues=new Date[map.size()];
		    double[] value=new double[map.size()];
		    int length=dateValues.length;
		    
		    Set<Map.Entry<String, String>> entries = map.entrySet();
		    Iterator i = entries.iterator();
		    
		    while(i.hasNext()) {
		    	
		         Map.Entry me = (Map.Entry)i.next();
		         value[length-1]=roundTwoDecimals(Double.parseDouble((String) me.getValue()));
		         String dateTimeArray[]=me.getKey().toString().split("T");
		         SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		         Date d = null;
				try {
					d = f.parse(dateTimeArray[0].substring(0, dateTimeArray[0].length()-1));
				} catch (ParseException e) {
					
					e.printStackTrace();
				}
		         dateValues[length-1]=d;
		         length-=1;
		      }
		    
		    dates.add(dateValues);
		    values.add(value);

	
		    int[] colors = new int[] { Color.BLUE };
		    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		    
		    AbstractDemoChart chart=new AbstractDemoChart();
		    XYMultipleSeriesRenderer renderer = chart.buildRenderer(colors, styles);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setMarginsColor(Color.WHITE);
		    renderer.setBackgroundColor(Color.WHITE);
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0, Color.BLACK);
		    renderer.setDisplayChartValues(true);
		    
		    //set the value in place of 11 that you need to display in one screen for the graph
		    chart.setChartSettings(renderer,title, "", "", dateValues[0].getTime(),
		        dateValues[dateValues.length - 1].getTime(), 0, 11, Color.BLACK, Color.BLACK
		        );
		    renderer.setYLabels(10);
		    return ChartFactory.getTimeChartView(context, chart.buildDateDataset(titles, dates, values),
		        renderer, "dd-MM-yy");
		    
		    
		    
		  }
	    
	 private double roundTwoDecimals(double d) {
		    DecimalFormat twoDForm = new DecimalFormat("#.##");
		    return Double.valueOf(twoDForm.format(d));
		}


}
