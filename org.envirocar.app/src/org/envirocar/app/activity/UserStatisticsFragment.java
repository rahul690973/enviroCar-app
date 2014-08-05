package org.envirocar.app.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.apache.http.HttpResponse;
import org.envirocar.app.R;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.TrackRetrievalException;
import org.envirocar.app.dao.exception.UnauthorizedException;

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
		
		GraphicalView chart1=execute(getActivity(),getString(R.string.Co2emission));
		chartCo2.addView(chart1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		GraphicalView chart2=execute(getActivity(),getString(R.string.fuel_consumption));
		chartFuel.addView(chart2, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		
		
	}
	
	
	
	 public GraphicalView execute(Context context,String title) {
		    String[] titles = new String[] {title };
		    List<Date[]> dates = new ArrayList<Date[]>();
		    List<double[]> values = new ArrayList<double[]>();
		    Date[] dateValues = new Date[] { new Date(95, 0, 1), new Date(95, 3, 1), new Date(95, 6, 1),
		        new Date(95, 9, 1), new Date(96, 0, 1), new Date(96, 3, 1), new Date(96, 6, 1),
		        new Date(96, 9, 1), new Date(97, 0, 1), new Date(97, 3, 1), new Date(97, 6, 1),
		        new Date(97, 9, 1), new Date(98, 0, 1), new Date(98, 3, 1), new Date(98, 6, 1),
		        new Date(98, 9, 1), new Date(99, 0, 1), new Date(99, 3, 1), new Date(99, 6, 1),
		        new Date(99, 9, 1), new Date(100, 0, 1), new Date(100, 3, 1), new Date(100, 6, 1),
		        new Date(100, 9, 1), new Date(100, 11, 1) };
		    dates.add(dateValues);

		    // for transforming this values into local values, fetch the units from sharedpreferences and
		    // divide these numbers by the unit's values. It has to be done at the time of inserting the
		    // values into the array
		    values.add(new double[] { 4.9, 5.3, 3.2, 4.5, 6.5, 4.7, 5.8, 4.3, 4, 2.3, -0.5, -2.9, 3.2, 5.5,
		        4.6, 9.4, 4.3, 1.2, 0, 0.4, 4.5, 3.4, 4.5, 4.3, 4 });
		    int[] colors = new int[] { Color.BLUE };
		    PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
		    
		    AbstractDemoChart chart=new AbstractDemoChart();
		    XYMultipleSeriesRenderer renderer = chart.buildRenderer(colors, styles);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setMarginsColor(Color.WHITE);
		    renderer.setBackgroundColor(Color.WHITE);
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0, Color.BLACK);
		    
		    chart.setChartSettings(renderer,title, "", "", dateValues[0].getTime(),
		        dateValues[dateValues.length - 1].getTime(), -4, 11, Color.BLACK, Color.BLACK
		        );
		    renderer.setYLabels(10);
		    return ChartFactory.getTimeChartView(context, chart.buildDateDataset(titles, dates, values),
		        renderer, "dd-MM-yy");
		    
		    
		    
		  }
	    


}
