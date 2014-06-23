/* 
 * enviroCar 2013
 * Copyright (C) 2013  
 * Martin Dueren, Jakob Moellers, Gerald Pape, Christopher Stephan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * 
 */

package org.envirocar.app.activity;

import java.text.DecimalFormat;










import java.util.Map;

import org.envirocar.app.R;
import org.envirocar.app.activity.preference.CarSelectionPreference;
import org.envirocar.app.activity.preference.UnitSelectionPreference;
import org.envirocar.app.application.CarManager;
import org.envirocar.app.application.UnitsParser;
import org.envirocar.app.application.service.AbstractBackgroundServiceStateReceiver;
import org.envirocar.app.application.service.BackgroundServiceImpl;
import org.envirocar.app.application.service.AbstractBackgroundServiceStateReceiver.ServiceState;
import org.envirocar.app.application.service.BackgroundServiceInteractor;
import org.envirocar.app.event.CO2Event;
import org.envirocar.app.event.CO2EventListener;
import org.envirocar.app.event.EngineLoadEvent;
import org.envirocar.app.event.EngineLoadEventListener;
import org.envirocar.app.event.ConsumptionEvent;
import org.envirocar.app.event.ConsumptionEventListener;
import org.envirocar.app.event.EventBus;
import org.envirocar.app.event.GpsSatelliteFix;
import org.envirocar.app.event.GpsSatelliteFixEvent;
import org.envirocar.app.event.GpsSatelliteFixEventListener;
import org.envirocar.app.event.LocationEvent;
import org.envirocar.app.event.LocationEventListener;
import org.envirocar.app.event.SpeedEvent;
import org.envirocar.app.event.SpeedEventListener;
import org.envirocar.app.logging.Logger;
import org.envirocar.app.model.Car;
import org.envirocar.app.model.Car.FuelType;
import org.envirocar.app.model.UnitSelection;
import org.envirocar.app.views.LayeredImageRotateView;
import org.envirocar.app.views.TypefaceEC;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Dashboard page that displays the current speed, co2 and car.
 * 
 * @author jakob
 * @author gerald
 * 
 */
public class DashboardFragment extends SherlockFragment {

	private static final Logger logger = Logger
			.getLogger(DashboardFragment.class);
	public static final int SENSOR_CHANGED_RESULT = 1337;
	// private static final String SERVICE_STATE = "serviceState";
	private static final String LOCATION = "location";
	private static final String SPEED = "speed";
	private static final String CO2 = "co2";
	private static final String ENGINE_LOAD = "engineLoad";
	private static final String FUEL_CONSUMPTION = "fuelConsumption";
	private static int CAR_PREFERANCE_CHANGED=0;
	private static int UNITS_PREFERENCE_CHANGED=1;
	
	
	private static final String ENGINE_LOAD_INFO="engine load is how muchdemand is placed on the engine for power such as playing the music, rolling down the windows and running the A/C system and using the wipers while starting the vehicle for example";
	private static final String ENGINE_LOAD_SAFE_VALUE=" Engine load should not go beyond 45%";
	private static final String ENGINE_LOAD_INCREASE="The load on your engine will increase. The engine could blast";
	
	private static  final String CO2_EMISSION_INFO="Carbon dioxide (CO2) emissions are the common type of gas emitted from the burning of fossil fuels. The higher the carbon content in the fossil fuel or the more inefficient the burning process is, generally the more CO2 that is produced.";
	private static final String CO2_SAFE_VALUE="The safe value is around 10kg/h";
	private static final String CO2_INCREASE="Effect of increase in co2 levels";
	
	private static  final String FUEL_CONSUMPTION_INFO="Fuel consumption is the amount of fuel used per unit distance; for example, litres per 100 kilometers (L/100 km). In this case, the lower the value, the more economic a vehicle is (the less fuel it needs to travel a certain distance). Fuel consumption is a reciprocal of fuel economy.";
	private static  final String FUEL_CONSUMPTION_SAFE_VALUE="There is no safe value, but the lower the fuel consumption value is, the more better is performance of your car";
	
	
	String lengthUnit = null,timeUnit = null;
	String shortLengthUnit=null,shortTimeUnit=null;
	float conversion_length=0.0f,conversion_time=0.0f;

	// UI Items

	TextView speedTextView;
	TextView co2TextView;
	TextView engineLoadTextView;
	TextView fuelConsumptionTextView;
	
	TextView fuelConsumptionInfoView;
	TextView speedInfoView;
	
	
	ProgressBar engineLoadProgressView;
	View dashboardView;

	private LocationEventListener locationListener;
	private SpeedEventListener speedListener;
	private CO2EventListener co2Listener;
	private EngineLoadEventListener engineLoadListener;
	private ConsumptionEventListener consumptionListener;

	private SharedPreferences preferences;

	private long lastUIUpdate;
	private int speed;
	private Location location;
	private double co2;
	private double engineLoad;
	private double fuelConsumption;

	private BroadcastReceiver receiver;
	protected ServiceState serviceState = ServiceState.SERVICE_STOPPED;
	private OnSharedPreferenceChangeListener preferenceListener;
	private LayeredImageRotateView speedRotatableView;
	private LayeredImageRotateView co2RotableView;
	private LayeredImageRotateView consumptionRotatableView;
	protected BackgroundServiceInteractor backgroundService;
	private GpsSatelliteFixEventListener gpsFixListener;
	private GpsSatelliteFix fix = new GpsSatelliteFix(0, false);
	private ImageView gpsFixView;
	private ImageView carOkView;
	private Drawable carOkDrawable;
	private Drawable carNotOkDrawable;
	private Drawable gpsFix;
	private Drawable gpsNoFix;
	private Drawable btNotSelected;
	private Drawable btStopped;
	private Drawable btPending;
	private Drawable btActive;
	private ImageView connectionStateImage;
	
	AlertDialog.Builder alert;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logger.info("onCreateView. hash=" + System.identityHashCode(this));
	
			return inflater.inflate(R.layout.dashboard_new, container, false);
			
			
			
			
	}

	/**
	 * Updates the sensor-textview
	 */
	private void updateGpsStatus() {
		if (fix.isFix()) {
			gpsFixView.setImageDrawable(gpsFix);
		} else {
			gpsFixView.setImageDrawable(gpsNoFix);
		}
	}

	private void updateCarStatus() {
		if (CarManager.instance().getCar() != null) {
			carOkView.setImageDrawable(carOkDrawable);
		} else {
			carOkView.setImageDrawable(carNotOkDrawable);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		
		loadCommonDrawables();

		readSavedState(savedInstanceState);
		
		loadForGasolineOrDiesel();

		logger.info("onViewCreated. hash=" + System.identityHashCode(this));

		dashboardView = getView();

		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		
		if(preferences.getString(SettingsActivity.UNITS, null) == null){
			
			UnitSelection us=new UnitSelection(getString(R.string.speed_first),getString(R.string.speed_second),
					   getString(R.string.co2_first),getString(R.string.co2_second),
					   getString(R.string.fuel_first),getString(R.string.fuel_second));
			
		    UnitSelectionPreference.persistUnits(us,getActivity());
			
		}
		
		

		// Setup UI elements
		
		setUpUIElements();
		

		/*
		 * status images
		 */
		setupStatusImages();

		updateStatusElements();

		TypefaceEC.applyCustomFont((ViewGroup) view,
				TypefaceEC.Newscycle(getActivity()));

		receiver = new AbstractBackgroundServiceStateReceiver() {

			@Override
			public void onStateChanged(ServiceState state) {
				serviceState = state;
				updateStatusElements();
			}
		};
		getActivity().registerReceiver(
				receiver,
				new IntentFilter(
						AbstractBackgroundServiceStateReceiver.SERVICE_STATE));

		preferenceListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				if (key.equals(SettingsActivity.CAR)
						|| key.equals(SettingsActivity.CAR_HASH_CODE)) {
						
					
					CAR_PREFERANCE_CHANGED=1;
					updateCarStatus();
					
				} else if (key.equals(SettingsActivity.BLUETOOTH_KEY)) {
					updateStatusElements();
				}
				
				else if (key.equals(SettingsActivity.UNITS)){
					
					UNITS_PREFERENCE_CHANGED=1;
				}
				
				  
			}
		};

		preferences
				.registerOnSharedPreferenceChangeListener(preferenceListener);
		
		
		TextView engineLoadInfoView = (TextView) getView().findViewById(R.id.engine_load);
		TextView co2InfoView = (TextView) getView().findViewById(R.id.co2_emission);
		
		engineLoadInfoView.setOnClickListener(mCorkyListener);
		co2InfoView.setOnClickListener(mCorkyListener);
		
		
		
		
		
		
		

		bindToBackgroundService();
	}

	
	private void setUpUIElements(){
		
		Car car = CarManager.instance().getCar();
		if(car!=null)
			{
				if(car.getFuelType()==FuelType.GASOLINE){
				fuelConsumptionTextView = (TextView) getView().findViewById(
					R.id.textViewConsumptionDashboard);
				consumptionRotatableView = (LayeredImageRotateView) getView().findViewById(
						R.id.consumptionmeterView);
				
				fuelConsumptionInfoView = (TextView) getView().findViewById(
						R.id.fuel_consumption);
				fuelConsumptionInfoView.setOnClickListener(mCorkyListener);
				}
				
				else if(car.getFuelType()==FuelType.DIESEL){
					
				speedTextView = (TextView) getView().findViewById(
							R.id.textViewSpeedDashboard);
				speedRotatableView = (LayeredImageRotateView) getView().findViewById(
						R.id.speedometerView);
				speedInfoView=(TextView) getView().findViewById(
						R.id.speed);
				speedInfoView.setOnClickListener(mCorkyListener);
					
				}
		}
		co2TextView = (TextView) getView().findViewById(R.id.co2TextView);
//		speedTextView = (TextView) getView().findViewById(
//				R.id.textViewSpeedDashboard);
		engineLoadTextView=(TextView) getView().findViewById(R.id.textViewLoadDashboard);
		engineLoadProgressView=(ProgressBar) getView().findViewById(R.id.progress_engineLoad);
		
		co2RotableView = (LayeredImageRotateView) getView().findViewById(
				R.id.co2meterView);
//		speedRotatableView = (LayeredImageRotateView) getView().findViewById(
//				R.id.speedometerView);
		
		
	}
	
	private OnClickListener mCorkyListener = new OnClickListener() { 
	    

		@Override
		public void onClick(View v) {
			
			if(v.getId()==R.id.engine_load)
				showHelp("Engine Load",ENGINE_LOAD_INFO,ENGINE_LOAD_SAFE_VALUE,ENGINE_LOAD_INCREASE);
			else if(v.getId()==R.id.co2_emission)
				showHelp("CO2 Emission",CO2_EMISSION_INFO,CO2_SAFE_VALUE,CO2_INCREASE);
			else if(v.getId()==R.id.speed)
				showHelp("Current Speed",ENGINE_LOAD_INFO,ENGINE_LOAD_SAFE_VALUE,ENGINE_LOAD_INCREASE);
			else if(v.getId()==R.id.fuel_consumption)
				showHelp("Fuel Consumption",ENGINE_LOAD_INFO,ENGINE_LOAD_SAFE_VALUE,ENGINE_LOAD_INCREASE);
			
			
		} 
	};
	
	private void showHelp(String heading, String info,String safe_value,String increase_value){
		
		alert = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getView().getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		 
    	View view=inflater.inflate(R.layout.custom_title, null);
    	TextView head=(TextView)view.findViewById(R.id.heading);
    	head.setText(heading);
    	View alertView = inflater.inflate(R.layout.alert_dialog_body, null);
    	TextView indicator_text=(TextView)alertView.findViewById(R.id.indicator_text);
    	indicator_text.setText(info);
    	TextView safe_range_text=(TextView)alertView.findViewById(R.id.safe_range_text);
    	safe_range_text.setText(safe_value);
    	TextView increase_text=(TextView)alertView.findViewById(R.id.increase_value_text);
    	increase_text.setText(increase_value);
    	
    	
    	
    	alert.setCustomTitle(view);
    	alert.setView(alertView);
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                 //do things
            }
        });
    	//alert.setMessage("helo");
    	alert.show();
		
	}
	
	private void loadForGasolineOrDiesel(){
		
		Car car = CarManager.instance().getCar();
		if(car!=null && car.getFuelType()==FuelType.GASOLINE){
			View speed_view = getView().findViewById(R.id.speed_view);
			if(speed_view!=null){
				replaceView(speed_view,R.layout.dashboard_diesel);
			}
		}
		
		else if(car!=null && car.getFuelType()==FuelType.DIESEL){
			
			View consumption_view = getView().findViewById(R.id.consumption_view);
			if(consumption_view!=null)
				replaceView(consumption_view,R.layout.dashboard_gasoline);
			
		}
		
		
		
	}
	
	private void replaceView(View v,int r){
		
		 ViewGroup parent = (ViewGroup) v.getParent();
		 int index = parent.indexOfChild(v);
		 parent.removeView(v);
		 LayoutInflater inflater = (LayoutInflater) getView().getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		 v = inflater.inflate(r, parent, false);
		 parent.addView(v, index);
		 
		 
		
	}
	private void setupStatusImages() {
		gpsFixView = (ImageView) getView().findViewById(R.id.gpsFixView);

		carOkView = (ImageView) getView().findViewById(R.id.carOkView);
		carOkView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Car car = CarManager.instance().getCar();
				if (car != null) {
					Toast.makeText(getActivity(), car.toString(),
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.no_sensor_selected,
							Toast.LENGTH_SHORT).show();
					Intent i=new Intent(getActivity(),SettingsActivity.class);
					startActivity(i);
				}
			}
		});

		connectionStateImage = (ImageView) getView().findViewById(
				R.id.connectionStateImage);
		connectionStateImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String remoteDevice = preferences
						.getString(
								org.envirocar.app.activity.SettingsActivity.BLUETOOTH_KEY,
								null);

				if (remoteDevice == null) {
					Toast.makeText(getActivity(), R.string.no_device_selected,
							Toast.LENGTH_SHORT).show();
					Intent i=new Intent(getActivity(),SettingsActivity.class);
					startActivity(i);
				}
			}
		});
	}

	private void loadCommonDrawables() {
		carOkDrawable = getResources().getDrawable(R.drawable.car_ok);
		carNotOkDrawable = getResources().getDrawable(R.drawable.car_no);
		gpsFix = getResources().getDrawable(R.drawable.gps_fix);
		gpsNoFix = getResources().getDrawable(R.drawable.gps_nofix);
		btNotSelected = getResources().getDrawable(
				R.drawable.bt_device_not_selected);
		btStopped = getResources().getDrawable(R.drawable.bt_device_stopped);
		btPending = getResources().getDrawable(R.drawable.bt_device_pending);
		btActive = getResources().getDrawable(R.drawable.bt_device_active);
	}

	@Override
	public void onDestroy() {
		logger.info("onDestroy. hash=" + System.identityHashCode(this));
		super.onDestroy();

		try {
			getActivity().unregisterReceiver(receiver);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage(), e);
			logger.warn("Reconsider the Receiver registration lifecycle!");
		}
		if (preferences != null) {
			preferences
					.unregisterOnSharedPreferenceChangeListener(preferenceListener);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// outState.putSerializable(SERVICE_STATE, serviceState);
		outState.putParcelable(LOCATION, location);
		outState.putInt(SPEED, speed);
		outState.putDouble(CO2, co2);
		outState.putDouble(ENGINE_LOAD, engineLoad);
		outState.putDouble(FUEL_CONSUMPTION, fuelConsumption);
	}

	private void readSavedState(Bundle savedInstanceState) {
		if (savedInstanceState == null)
			return;

		// this.serviceState = (ServiceState)
		// savedInstanceState.getSerializable(SERVICE_STATE);
		this.location = savedInstanceState.getParcelable(LOCATION);
		this.speed = savedInstanceState.getInt(SPEED);
		this.co2 = savedInstanceState.getDouble(CO2);
		this.engineLoad=savedInstanceState.getDouble(ENGINE_LOAD);
		this.fuelConsumption=savedInstanceState.getDouble(FUEL_CONSUMPTION);
	}

	@Override
	public void onStop() {
		logger.info("onStop. hash=" + System.identityHashCode(this));
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		logger.info("onCreate. hash=" + System.identityHashCode(this));
		super.onCreate(savedInstanceState);

	}

	private void bindToBackgroundService() {
		if (!getActivity().bindService(
				new Intent(this.getActivity(), BackgroundServiceImpl.class),
				new ServiceConnection() {

					@Override
					public void onServiceDisconnected(ComponentName name) {
						logger.info(String.format(
								"BackgroundService %S disconnected!",
								name.flattenToString()));
					}

					@Override
					public void onServiceConnected(ComponentName name,
							IBinder service) {
						backgroundService = (BackgroundServiceInteractor) service;
						serviceState = backgroundService.getServiceState();
						updateStatusElements();
					}
				}, 0)) {
			logger.warn("Could not connect to BackgroundService.");
		}
	}

	@Override
	public void onPause() {
		logger.info("onPause. hash=" + System.identityHashCode(this));
		super.onPause();

		EventBus.getInstance().unregisterListener(this.locationListener);
		EventBus.getInstance().unregisterListener(this.speedListener);
		EventBus.getInstance().unregisterListener(this.co2Listener);
		EventBus.getInstance().unregisterListener(this.gpsFixListener);
		EventBus.getInstance().unregisterListener(this.engineLoadListener);
		EventBus.getInstance().unregisterListener(this.consumptionListener);
		
	}

	@Override
	public void onDestroyView() {
		logger.info("onDestroyView. hash=" + System.identityHashCode(this));
		super.onDestroyView();
	}

	@Override
	public void onStart() {
		logger.info("onStart. hash=" + System.identityHashCode(this));

		super.onStart();
	}

	@Override
	public void onResume() {
		logger.info("onResume. hash=" + System.identityHashCode(this));
		super.onResume();
		Toast.makeText(getActivity(), "HHH", Toast.LENGTH_SHORT).show();
		
		if(CAR_PREFERANCE_CHANGED==1){
			
		loadForGasolineOrDiesel();
		setUpUIElements();
				
		CAR_PREFERANCE_CHANGED=0;
		}
		initializeEventListeners();
		updateGpsStatus();

		updateCarStatus();

		Car car = CarManager.instance().getCar();
		if (car != null && car.getFuelType() == FuelType.DIESEL) {
			Crouton.makeText(getActivity(), R.string.diesel_not_yet_supported,
					de.keyboardsurfer.android.widget.crouton.Style.ALERT)
					.show();
			}
		
		

		bindToBackgroundService();
	}

	private void initializeEventListeners() {
		this.locationListener = new LocationEventListener() {
			@Override
			public void receiveEvent(LocationEvent event) {
				updateLocation(event.getPayload());
			}
		};
		this.speedListener = new SpeedEventListener() {
			@Override
			public void receiveEvent(SpeedEvent event) {
				//Toast.makeText(getActivity(), event.toString(), Toast.LENGTH_SHORT).show();
				Car car = CarManager.instance().getCar();
				if(car!=null && car.getFuelType()==FuelType.DIESEL)
				updateSpeed(event.getPayload());
			}
		};
		
		this.engineLoadListener=new EngineLoadEventListener(){

			@Override
			public void receiveEvent(EngineLoadEvent event) {
			//Toast.makeText(getActivity(), ""+event.getPayload(), Toast.LENGTH_SHORT).show();
				updateEngineLoad(event.getPayload());
				
			}
			
			
		};
		
		this.consumptionListener=new ConsumptionEventListener(){
		

			@Override
			public void receiveEvent(ConsumptionEvent event) {
				Car car = CarManager.instance().getCar();
				if(car!=null && car.getFuelType()==FuelType.GASOLINE)
					updateFuelConsumption(event.getPayload());
				
				
			}
			
			
		};
		this.co2Listener = new CO2EventListener() {
			@Override
			public void receiveEvent(CO2Event event) {
				updateCO2(event.getPayload());
			}
		};
		this.gpsFixListener = new GpsSatelliteFixEventListener() {
			@Override
			public void receiveEvent(GpsSatelliteFixEvent event) {
				updateGpsState(event.getPayload());
			}
		};
		EventBus.getInstance().registerListener(locationListener);
		EventBus.getInstance().registerListener(speedListener);
		EventBus.getInstance().registerListener(co2Listener);
		EventBus.getInstance().registerListener(gpsFixListener);
		EventBus.getInstance().registerListener(engineLoadListener);
		EventBus.getInstance().registerListener(consumptionListener);

		lastUIUpdate = System.currentTimeMillis();
	}

	protected void updateCO2(final Double co2) {
		this.co2 = co2;
		checkUIUpdate();
	}

	protected void updateGpsState(final GpsSatelliteFix fix) {
		if (this.fix == null || this.fix.isFix() != fix.isFix()) {
			this.fix = fix;

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					updateGpsStatus();
				}
			});
		} else {
			this.fix = fix;
		}
	}

	protected void updateSpeed(final Integer speed) {
		this.speed = speed;
		checkUIUpdate();
	}
	
	protected void updateEngineLoad(final double engineLoad){
		this.engineLoad=engineLoad;
		checkUIUpdate();
	
	}
	protected void updateFuelConsumption(final double fuelConsumption){
		this.fuelConsumption=fuelConsumption;
		checkUIUpdate();
	
	}

	protected void updateLocation(final Location location) {
		this.location = location;
		checkUIUpdate();
	}

	protected void updateStatusElements() {
		if (getView() == null || !isAdded())
			return;

		if (connectionStateImage == null)
			return;

		String remoteDevice = preferences
				.getString(
						org.envirocar.app.activity.SettingsActivity.BLUETOOTH_KEY,
						null);

		if (remoteDevice == null) {
			connectionStateImage.setImageDrawable(btNotSelected);
		} else if (serviceState == ServiceState.SERVICE_STARTED) {
			connectionStateImage.setImageDrawable(btActive);
		} else if (serviceState == ServiceState.SERVICE_STARTING) {
			connectionStateImage.setImageDrawable(btPending);
		} else {
			connectionStateImage.setImageDrawable(btStopped);
			co2 = 0.0;
			speed = 0;
			engineLoad=0;
			fuelConsumption=0;
			
//			Car car = CarManager.instance().getCar();
//			if(car!=null)
//				{
//					if(car.getFuelType()==FuelType.GASOLINE)
//							updateFuelConsumptionValue();
//					else if(car.getFuelType()==FuelType.DIESEL)
//							updateSpeedValue();
//				}
			updateCo2Value();
			
			updateEngineLoadValue();
			
		}

	}

	private void updateFuelConsumptionValue() {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		fuelConsumptionTextView.setText(twoDForm.format(fuelConsumption) + "l/h");
		consumptionRotatableView.submitScaleValue((float) fuelConsumption);
		
		
	}

	private synchronized void checkUIUpdate() {
		if (serviceState == ServiceState.SERVICE_STOPPED)
			return;

		if (getActivity() == null
				|| System.currentTimeMillis() - lastUIUpdate < 250)
			return;

		lastUIUpdate = System.currentTimeMillis();

		if (location != null || speed != 0 || co2 != 0.0 || engineLoad!=0 || fuelConsumption!=0) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					Car car = CarManager.instance().getCar();
					if(car!=null)
						{
							if(car.getFuelType()==FuelType.GASOLINE)
									updateFuelConsumptionValue();
							else if(car.getFuelType()==FuelType.DIESEL)
									updateSpeedValue();
						}

					updateCo2Value();
					updateEngineLoadValue();
				}
			});
		}
	}
	
	protected void updateEngineLoadValue(){
		
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		engineLoadTextView.setText(twoDForm.format(engineLoad) + "%");
		int integerEngineLoad=(int)engineLoad;
		engineLoadProgressView.setProgress(integerEngineLoad);
		
		
	}

	protected void updateCo2Value() {

		DecimalFormat twoDForm = new DecimalFormat("#.##");

		co2TextView.setText(twoDForm.format(co2) + " kg/h");
		co2RotableView.submitScaleValue((float) co2);

		if (co2 > 30) {
			dashboardView.setBackgroundColor(Color.RED);
		} else {
			dashboardView.setBackgroundColor(Color.WHITE);
		}
	}

	protected void updateSpeedValue() {
		
		
		
		if(UNITS_PREFERENCE_CHANGED==1){
			
			UnitSelection units = new UnitSelection();
			units = UnitSelectionPreference.instantiateUnits(preferences.getString(SettingsActivity.UNITS, null));
			Map<String,String> values_maps[]=UnitsParser.getHashMapResource(getActivity(), R.xml.unit_values_final);
				 
			conversion_length=Float.parseFloat(values_maps[0].get(lengthUnit));
			conversion_time=Float.parseFloat(values_maps[1].get(timeUnit));
			
			shortLengthUnit=extractSmallUnit(lengthUnit);
			shortTimeUnit=extractSmallUnit(timeUnit);
			
			
			UNITS_PREFERENCE_CHANGED=0;
		
		}
		
		
		
		
		if (!preferences.getBoolean(SettingsActivity.IMPERIAL_UNIT, false)) {
			
			int integerSpeed=(int) (speed*(conversion_length/conversion_time));
			speedTextView.setText(integerSpeed + shortLengthUnit+"/"+shortTimeUnit);
			speedRotatableView.submitScaleValue(speed);
		} else {
			speedTextView.setText(speed / 1.6f + " mph");
			speedRotatableView.submitScaleValue(speed / 1.6f);
		}
	}
	
	
	protected String extractSmallUnit(String s){
		
		return(s.substring(s.indexOf("(")+1,s.indexOf(")")));
		
	}
	
	

}
