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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.envirocar.app.R;
import org.envirocar.app.activity.StartStopButtonUtil.OnTrackModeChangeListener;
import org.envirocar.app.application.CarManager;
import org.envirocar.app.application.ECApplication;
import org.envirocar.app.application.NavMenuItem;
import org.envirocar.app.application.TemporaryFileManager;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.application.service.AbstractBackgroundServiceStateReceiver;
import org.envirocar.app.application.service.AbstractBackgroundServiceStateReceiver.ServiceState;
import org.envirocar.app.application.service.BackgroundServiceImpl;
import org.envirocar.app.application.service.BackgroundServiceInteractor;
import org.envirocar.app.application.service.DeviceInRangeService;
import org.envirocar.app.application.service.DeviceInRangeServiceInteractor;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.AnnouncementsRetrievalException;
import org.envirocar.app.exception.InvalidObjectStateException;
import org.envirocar.app.logging.Logger;
import org.envirocar.app.model.Announcement;
import org.envirocar.app.storage.DbAdapterImpl;
import org.envirocar.app.util.Util;
import org.envirocar.app.util.VersionRange.Version;
import org.envirocar.app.views.TypefaceEC;
import org.envirocar.app.views.Utils;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Main UI application that cares about the auto-upload, auto-connect and global
 * UI elements
 * 
 * @author jakob
 * @author gerald
 * 
 * @param <AndroidAlarmService>
 */
public class MainActivity<AndroidAlarmService> extends SherlockFragmentActivity implements OnItemClickListener {

	public static final int TRACK_MODE_SINGLE = 0;
	public static final int TRACK_MODE_AUTO = 1;
	
	private int actionBarTitleID = 0;
	private ActionBar actionBar;

	public ECApplication application;

	private FragmentManager manager;
	//Navigation Drawer
	private DrawerLayout drawer;
	private ListView drawerList;
	private NavAdapter navDrawerAdapter;

	// Menu Items
	private NavMenuItem[] navDrawerItems;

	static final int DASHBOARD = 0;
	static final int LOGIN = 1;
	static final int MY_TRACKS = 2;
	static final int START_STOP_MEASUREMENT = 3;
	static final int SETTINGS = 4;
	static final int LOGBOOK = 5;
	static final int LANGUAGE=5;
	static final int HELP = 6;
	static final int SEND_LOG = 7;
	static final int SUBMENU_ABOUT=0;
	static final int SUBMENU_HELP=1;
	static final int SUBMENU_REPORT=2;
	static final int SUBMENU_LOGBOOK=3;
	static final int MENU_ID=4;
	
	static final String DASHBOARD_TAG = "DASHBOARD";
	static final String LOGIN_TAG = "LOGIN";
	static final String MY_TRACKS_TAG = "MY_TRACKS";
	static final String HELP_TAG = "HELP";
	static final String TROUBLESHOOTING_TAG = "TROUBLESHOOTING";
	static final String SEND_LOG_TAG = "SEND_LOG";
	static final String LOGBOOK_TAG ="LOGBOOK";

	public static final int REQUEST_MY_GARAGE = 1336;
	public static final int REQUEST_REDIRECT_TO_GARAGE = 1337;
	
	private static final Logger logger = Logger.getLogger(MainActivity.class);
	private static final String TRACK_MODE = "trackMode";
	private static final String SEEN_ANNOUNCEMENTS = "seenAnnouncements";
	
	
	// Include settings for auto upload and auto-connect
	
	private SharedPreferences preferences = null;
	boolean alwaysUpload = false;
	boolean uploadOnlyInWlan = true;
	private BroadcastReceiver serviceStateReceiver;
	private OnSharedPreferenceChangeListener settingsReceiver;
	protected ServiceState serviceState = ServiceState.SERVICE_STOPPED;
	private BroadcastReceiver bluetoothStateReceiver;
	private int trackMode = TRACK_MODE_SINGLE;
	private Runnable remainingTimeThread;
	private Handler remainingTimeHandler;
	private BroadcastReceiver errorInformationReceiver;
	private Set<String> seenAnnouncements = new HashSet<String>();
	private BroadcastReceiver deviceDiscoveryStateReceiver;
	protected BackgroundServiceInteractor backgroundService;
	protected DeviceInRangeServiceInteractor deviceInRangeService;
	protected long discoveryTargetTime;
		
	private void prepareNavDrawerItems(){
		if(this.navDrawerItems == null){
			navDrawerItems = new NavMenuItem[6];
			navDrawerItems[LOGIN] = new NavMenuItem(LOGIN, getResources().getString(R.string.menu_login),R.drawable.device_access_accounts);
			//navDrawerItems[LOGBOOK] = new NavMenuItem(LOGBOOK, getResources().getString(R.string.menu_logbook), R.drawable.logbook);
			navDrawerItems[SETTINGS] = new NavMenuItem(SETTINGS, getResources().getString(R.string.menu_settings),R.drawable.action_settings);
			navDrawerItems[START_STOP_MEASUREMENT] = new NavMenuItem(START_STOP_MEASUREMENT, getResources().getString(R.string.menu_start),R.drawable.av_play);
			navDrawerItems[DASHBOARD] = new NavMenuItem(DASHBOARD, getResources().getString(R.string.dashboard), R.drawable.dashboard);
			navDrawerItems[MY_TRACKS] = new NavMenuItem(MY_TRACKS, getResources().getString(R.string.my_tracks),R.drawable.device_access_storage);
			navDrawerItems[LANGUAGE] = new NavMenuItem(LANGUAGE, getResources().getString(R.string.language),R.drawable.ic_action_labels);
			
			//navDrawerItems[HELP] = new NavMenuItem(HELP, getResources().getString(R.string.menu_help), R.drawable.action_help);
			//navDrawerItems[SEND_LOG] = new NavMenuItem(SEND_LOG, getResources().getString(R.string.menu_send_log), R.drawable.action_report);
		}
		
		if (UserManager.instance().isLoggedIn()) {
			navDrawerItems[LOGIN].setTitle(getResources().getString(R.string.menu_logout));
			navDrawerItems[LOGIN].setSubtitle(String.format(getResources().getString(R.string.logged_in_as),UserManager.instance().getUser().getUsername()));
		} else {
			navDrawerItems[LOGIN].setTitle(getResources().getString(R.string.menu_login));
			navDrawerItems[LOGIN].setSubtitle("");		
		}

		navDrawerAdapter.notifyDataSetChanged();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		readSavedState(savedInstanceState);
		
		this.setContentView(R.layout.main_layout);

		application = ((ECApplication) getApplication());
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		alwaysUpload = preferences.getBoolean(SettingsActivity.ALWAYS_UPLOAD, false);
        uploadOnlyInWlan = preferences.getBoolean(SettingsActivity.WIFI_UPLOAD, true);

        checkKeepScreenOn();
        
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");

		// font stuff
		actionBarTitleID = Utils.getActionBarId();
		if (Utils.getActionBarId() != 0) {
			TextView view = (TextView) this.findViewById(actionBarTitleID);
			if (view != null) {
				view.setTypeface(TypefaceEC.Newscycle(this));
			}
		}

		actionBar.setLogo(getResources().getDrawable(R.drawable.actionbarlogo_with_padding));
		
		manager = getSupportFragmentManager();

		DashboardFragment initialFragment = new DashboardFragment();
		manager.beginTransaction().replace(R.id.content_frame, initialFragment, DASHBOARD_TAG)
		.commit();
		
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		navDrawerAdapter = new NavAdapter();
		prepareNavDrawerItems();
		updateStartStopButton();
		drawerList.setAdapter(navDrawerAdapter);
		ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
				this, drawer, R.drawable.ic_drawer, R.string.open_drawer,
				R.string.close_drawer) {
		
			@Override
			public void onDrawerOpened(View drawerView) {
				prepareNavDrawerItems();
				super.onDrawerOpened(drawerView);
			}
		};

		drawer.setDrawerListener(actionBarDrawerToggle);
		drawerList.setOnItemClickListener(this);
		
		manager.executePendingTransactions();

		serviceStateReceiver = new AbstractBackgroundServiceStateReceiver() {
			@Override
			public void onStateChanged(ServiceState state) {
				serviceState = state;
				
				if (serviceState == ServiceState.SERVICE_STOPPED && trackMode == TRACK_MODE_AUTO) {
					/*
					 * we need to start the DeviceInRangeService
					 */
					startService(new Intent(getApplicationContext(), DeviceInRangeService.class));
				}
				
				updateStartStopButton();
			}
		};
		
		registerReceiver(serviceStateReceiver, new IntentFilter(AbstractBackgroundServiceStateReceiver.SERVICE_STATE));

		deviceDiscoveryStateReceiver = new AbstractBackgroundServiceStateReceiver() {
			
			@Override
			public void onStateChanged(ServiceState state) {
				if (state == ServiceState.SERVICE_DEVICE_DISCOVERY_PENDING) {
					discoveryTargetTime = deviceInRangeService.getNextDiscoveryTargetTime();
					invokeRemainingTimeThread();
				}
				else if (state == ServiceState.SERVICE_DEVICE_DISCOVERY_RUNNING) {
					
				}
			}
		};
		registerReceiver(deviceDiscoveryStateReceiver, new IntentFilter(AbstractBackgroundServiceStateReceiver.SERVICE_STATE));
		
		bluetoothStateReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateStartStopButton();
			}
		};
		
		
		registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		
		settingsReceiver = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				if (key.equals(SettingsActivity.BLUETOOTH_NAME)) {
					updateStartStopButton();
				}
				if (key.equals(SettingsActivity.CAR) || key.equals(SettingsActivity.CAR_HASH_CODE)) {
					updateStartStopButton();
				}
			}
		};
		
		preferences.registerOnSharedPreferenceChangeListener(settingsReceiver);
		
		errorInformationReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
	        	Fragment fragment = getSupportFragmentManager().findFragmentByTag(TROUBLESHOOTING_TAG);
	        	if (fragment == null) {
	        		fragment = new TroubleshootingFragment();
	        	}
	        	fragment.setArguments(intent.getExtras());
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment)
						.commit();
			}
		};
		
		registerReceiver(errorInformationReceiver, new IntentFilter(TroubleshootingFragment.INTENT));
		
		resolvePersistentSeenAnnouncements();
		
		
	}
	
	private void readSavedState(Bundle savedInstanceState) {
		if (savedInstanceState == null) return;
		
		this.trackMode = savedInstanceState.getInt(TRACK_MODE);
		
		String[] arr = (String[]) savedInstanceState.getSerializable(SEEN_ANNOUNCEMENTS);
		if (arr != null) {
			for (String string : arr) {
				this.seenAnnouncements.add(string);
			}
		}
	}
	
	private void bindToBackgroundService() {
		if (!bindService(new Intent(this, BackgroundServiceImpl.class),
				new ServiceConnection() {
					
					@Override
					public void onServiceDisconnected(ComponentName name) {
						logger.info(String.format("BackgroundService %S disconnected!", name.flattenToString()));
					}
					
					@Override
					public void onServiceConnected(ComponentName name, IBinder service) {
						backgroundService = (BackgroundServiceInteractor) service;
						serviceState = backgroundService.getServiceState();
						updateStartStopButton();
					}
				}, 0)) {
			logger.warn("Could not connect to BackgroundService.");
		}		
	}
	
	private void bindToDeviceInRangeService() {
		if (!bindService(new Intent(this, DeviceInRangeService.class),
				new ServiceConnection() {
					
					@Override
					public void onServiceDisconnected(ComponentName name) {
						logger.info(String.format("DeviceInRangeService %S disconnected!", name.flattenToString()));
					}
					
					@Override
					public void onServiceConnected(ComponentName name, IBinder service) {
						deviceInRangeService = (DeviceInRangeServiceInteractor) service;
						if (deviceInRangeService.isDiscoveryPending()) {
							serviceState = ServiceState.SERVICE_DEVICE_DISCOVERY_PENDING;
						}
						updateStartStopButton();
						discoveryTargetTime = deviceInRangeService.getNextDiscoveryTargetTime();
						invokeRemainingTimeThread();
					}
				}, 0)) {
			logger.warn("Could not connect to DeviceInRangeService.");
		}		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putInt(TRACK_MODE, trackMode);
		
		outState.putSerializable(SEEN_ANNOUNCEMENTS, this.seenAnnouncements.toArray(new String[0]));
	}

	protected void updateStartStopButton() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null && adapter.isEnabled()) { // was requirementsFulfilled
			createStartStopUtil().updateStartStopButtonOnServiceStateChange(navDrawerItems[START_STOP_MEASUREMENT]);
		} else {
			createStartStopUtil().defineButtonContents(navDrawerItems[START_STOP_MEASUREMENT],
					false, R.drawable.not_available, getString(R.string.pref_bluetooth_disabled),
					getString(R.string.menu_start));
		}
		
		navDrawerAdapter.notifyDataSetChanged();
	}

	/**
	 * start a thread that updates the UI until the device was
	 * discovered
	 */
	private void invokeRemainingTimeThread() {
		if (remainingTimeThread == null || discoveryTargetTime > System.currentTimeMillis()) {
			remainingTimeHandler = new Handler();
			remainingTimeThread = new Runnable() {
				@Override
				public void run() {
					final long deltaSec = (discoveryTargetTime - System.currentTimeMillis()) / 1000;
					final long minutes = deltaSec / 60;
					final long secs = deltaSec - (minutes*60);
					if (serviceState == ServiceState.SERVICE_DEVICE_DISCOVERY_PENDING && deltaSec > 0) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								navDrawerItems[START_STOP_MEASUREMENT].setSubtitle(
												String.format(getString(R.string.device_discovery_next_try), 
														String.format("%02d", minutes), String.format("%02d", secs)
												));
								navDrawerAdapter.notifyDataSetChanged();
							}
						});
						
						/*
						 * re-invoke the painting
						 */
						remainingTimeHandler.postDelayed(remainingTimeThread, 1000);
					} else {
						logger.info("NOT SHOWING!");
					}
				}
			};
			remainingTimeHandler.post(remainingTimeThread);
		}
		else {
			logger.info("not invoking the discovery time painting thread: "+
					(remainingTimeThread == null) +", "+ (discoveryTargetTime - System.currentTimeMillis()));
		}
	}


	private class NavAdapter extends BaseAdapter {
		

		@Override
		public boolean isEnabled(int position) {
			//to allow things like start bluetooth or go to settings from "disabled" action
			return (position == START_STOP_MEASUREMENT ? true : navDrawerItems[position].isEnabled());
		}
		
		@Override
		public int getCount() {
			return navDrawerItems.length;
		}

		@Override
		public Object getItem(int position) {
			return navDrawerItems[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NavMenuItem currentItem = ((NavMenuItem) getItem(position));
			View item;
			if(currentItem.getSubtitle().equals("")){
				item = View.inflate(MainActivity.this,R.layout.nav_item_1, null);
				
			} else {
				item = View.inflate(MainActivity.this,R.layout.nav_item_2, null);
				TextView textView2 = (TextView) item.findViewById(android.R.id.text2);
				textView2.setText(currentItem.getSubtitle());
				if(!currentItem.isEnabled()) textView2.setTextColor(Color.GRAY);
			}
			ImageView icon = ((ImageView) item.findViewById(R.id.nav_item_icon));
			icon.setImageResource(currentItem.getIconRes());
			TextView textView = (TextView) item.findViewById(android.R.id.text1);
			textView.setText(currentItem.getTitle());
			if(!currentItem.isEnabled()){
				textView.setTextColor(Color.GRAY);
				icon.setColorFilter(Color.GRAY);
			}
			TypefaceEC.applyCustomFont((ViewGroup) item, TypefaceEC.Raleway(MainActivity.this));
			return item;
		}

	}
	
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openFragment(position);
    }

    private void openFragment(int position) {
        FragmentManager manager = getSupportFragmentManager();

        switch (position) {
        
        // Go to the dashboard
        
        case DASHBOARD:
        	
        	if(isFragmentVisible(DASHBOARD_TAG)){
            	break;
            }
        	Fragment dashboardFragment = getSupportFragmentManager().findFragmentByTag(DASHBOARD_TAG);
        	if (dashboardFragment == null) {
        		dashboardFragment = new DashboardFragment();
        	}
        	manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.beginTransaction().replace(R.id.content_frame, dashboardFragment, DASHBOARD_TAG).commit();
            break;
            
        //Start the Login activity
            
        case LOGIN:
        	if(UserManager.instance().isLoggedIn()){
        		UserManager.instance().logOut();
    			ListTracksFragment listMeasurementsFragment = (ListTracksFragment) getSupportFragmentManager().findFragmentByTag("MY_TRACKS");
    			// check if this fragment is initialized
    			if (listMeasurementsFragment != null) {
    				listMeasurementsFragment.clearRemoteTracks();
    			}else{
    				//the remote tracks need to be removed in any case
            		DbAdapterImpl.instance().deleteAllRemoteTracks();
    			}
        		Crouton.makeText(this, R.string.bye_bye, Style.CONFIRM).show();
        	} else {
            	if(isFragmentVisible(LOGIN_TAG)){
                	break;
                }
                LoginFragment loginFragment = new LoginFragment();
                manager.beginTransaction().replace(R.id.content_frame, loginFragment, LOGIN_TAG).addToBackStack(null).commit();
        	}
            break;
            
        // Go to the settings
            
        case SETTINGS:
			Intent configIntent = new Intent(this, SettingsActivity.class);
			startActivity(configIntent);
            break;
            
        // Go to the track list
            
        case MY_TRACKS:
        	
        	if(isFragmentVisible(MY_TRACKS_TAG)){
            	break;
            }
            ListTracksFragment listMeasurementFragment = new ListTracksFragment();
            manager.beginTransaction().replace(R.id.content_frame, listMeasurementFragment, MY_TRACKS_TAG).addToBackStack(null).commit();
            break;
            
        // Start or stop the measurement process
            
		case START_STOP_MEASUREMENT:
			if (!navDrawerItems[position].isEnabled()) return;
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

			String remoteDevice = preferences.getString(org.envirocar.app.activity.SettingsActivity.BLUETOOTH_KEY,null);

			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null && adapter.isEnabled() && remoteDevice != null) {
				if(CarManager.instance().getCar() == null){
					Intent settingsIntent = new Intent(this, SettingsActivity.class);
					startActivity(settingsIntent);
				} else {
					/*
					 * We are good to go. process the state and stuff
					 */
					OnTrackModeChangeListener trackModeListener = new OnTrackModeChangeListener() {
						@Override
						public void onTrackModeChange(int tm) {
							trackMode = tm;
						}
					};
					
					createStartStopUtil().processButtonClick(trackModeListener);
				}
			} else {
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivity(settingsIntent);
			}
			break;
		case HELP:
        	
        	if(isFragmentVisible(HELP_TAG)){
            	break;
            }
			HelpFragment helpFragment = new HelpFragment();
            manager.beginTransaction().replace(R.id.content_frame, helpFragment, HELP_TAG).addToBackStack(null).commit();
			break;
		case SEND_LOG:
        	
        	if(isFragmentVisible(SEND_LOG_TAG)){
            	break;
            }
			SendLogFileFragment logFragment = new SendLogFileFragment();
			manager.beginTransaction().replace(R.id.content_frame, logFragment, SEND_LOG_TAG).addToBackStack(null).commit();
        default:
            break;
            
		case LOGBOOK:
        	
        	if(isFragmentVisible(LOGBOOK_TAG)){
            	break;
            }
			LogbookFragment logbookFragment = new LogbookFragment();
            manager.beginTransaction().replace(R.id.content_frame, logbookFragment, LOGBOOK_TAG).addToBackStack(null).commit();
			break;
        }
        drawer.closeDrawer(drawerList);

    }


    	
	private StartStopButtonUtil createStartStopUtil() {
		return new StartStopButtonUtil(application, this, trackMode, serviceState,
				serviceState == ServiceState.SERVICE_DEVICE_DISCOVERY_PENDING);
	}

	/**
     * This method checks, whether a Fragment with a certain tag is visible.
     * @param tag The tag of the Fragment.
     * @return True if the Fragment is visible, false if not.
     */
    public boolean isFragmentVisible(String tag){
        
    	Fragment tmpFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(tmpFragment != null && tmpFragment.isVisible()){
        	logger.info("Fragment with tag: " + tag + " is already visible.");
        	return true;
        }
        return false;
    	
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();

		logger.info("onDestroy called");
		
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(Void... params) {
//				HTTPClient.shutdown();
//				return null;
//			}
//		}.execute();
		
		// Close db connection

//		application.closeDb();

		// Remove the services etc.

//		application.destroyStuff();
		
		Crouton.cancelAllCroutons();
		
//		this.unregisterReceiver(application.getBluetoothChangeReceiver());

		this.unregisterReceiver(bluetoothStateReceiver);
		this.unregisterReceiver(deviceDiscoveryStateReceiver);
		
		this.unregisterReceiver(serviceStateReceiver);
		
		this.unregisterReceiver(errorInformationReceiver);
		
		if (remainingTimeHandler != null) {
			remainingTimeHandler.removeCallbacks(remainingTimeThread);
			discoveryTargetTime = 0;
			remainingTimeThread = null;
		}
		
		try {
			TemporaryFileManager.instance().shutdown();
		} catch (InvalidObjectStateException e) {
			logger.warn(e.getMessage(), e);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		drawer.closeDrawer(drawerList);
	    //first init
	    firstInit();
	    
	    application.setActivity(this);
	    
	    checkKeepScreenOn();
	    
		alwaysUpload = preferences.getBoolean(SettingsActivity.ALWAYS_UPLOAD, false);
        uploadOnlyInWlan = preferences.getBoolean(SettingsActivity.WIFI_UPLOAD, true);
        
        new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				checkAffectingAnnouncements();
				return null;
			}
		}.execute();
        
		bindToBackgroundService();
		
		bindToDeviceInRangeService();
	}


	protected void resolvePersistentSeenAnnouncements() {
		String pers = preferences.getString(SettingsActivity.PERSISTENT_SEEN_ANNOUNCEMENTS, "");
		
		if (!pers.isEmpty()) {
			if (pers.contains(",")) {
				String[] arr = pers.split(",");
				for (String string : arr) {
					seenAnnouncements.add(string);
				}
			}
			else {
				seenAnnouncements.add(pers);
			}
			
		}
	}

	private void checkAffectingAnnouncements() {
		final List<Announcement> annos;
		try {
			annos = DAOProvider.instance().getAnnouncementsDAO().getAllAnnouncements();
		} catch (AnnouncementsRetrievalException e) {
			logger.warn(e.getMessage(), e);
			return;
		}
		
		final Version version;
		try {
			String versionShort = Util.getVersionStringShort(getApplicationContext());
			version = Version.fromString(versionShort);
		} catch (NameNotFoundException e) {
			logger.warn(e.getMessage());
			return;
		}
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				for (Announcement announcement : annos) {
					if (!seenAnnouncements.contains(announcement.getId())) {
						if (announcement.getVersionRange().isInRange(version)) {
							showAnnouncement(announcement);
						}
					}
				}
			}
		});
	}

	private void showAnnouncement(final Announcement announcement) {
		String title = announcement.createUITitle(this);
		String content = announcement.getContent();
		
		DialogUtil.createTitleMessageInfoDialog(title, Html.fromHtml(content), true, new DialogUtil.PositiveNegativeCallback() {
			@Override
			public void negative() {
				seenAnnouncements.add(announcement.getId());
			}

			@Override
			public void positive() {
				addPersistentSeenAnnouncement(announcement.getId());
				seenAnnouncements.add(announcement.getId());
			}
		}, this);
	}

	protected void addPersistentSeenAnnouncement(String id) {
		String currentPersisted = preferences.getString(SettingsActivity.PERSISTENT_SEEN_ANNOUNCEMENTS, "");
		
		StringBuilder sb = new StringBuilder(currentPersisted);
		if (!currentPersisted.isEmpty()) {
			sb.append(",");
		}
		sb.append(id);
		
		preferences.edit().putString(SettingsActivity.PERSISTENT_SEEN_ANNOUNCEMENTS, sb.toString()).commit();
	}

	private void checkKeepScreenOn() {
		if (preferences.getBoolean(SettingsActivity.DISPLAY_STAYS_ACTIV, false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}		
	}

	/**
	 * Determine what the menu buttons do
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		switch (item.getItemId()) {

		case android.R.id.home:
			if (drawer.isDrawerOpen(drawerList)) {
				drawer.closeDrawer(drawerList);
			} else {
				drawer.openDrawer(drawerList);
			}
			return true;
			
		case SUBMENU_HELP:
			if(isFragmentVisible(HELP_TAG)){
            	break;
            }
			HelpFragment helpFragment = new HelpFragment();
            manager.beginTransaction().replace(R.id.content_frame, helpFragment, HELP_TAG).addToBackStack(null).commit();
            return true;
            
		case SUBMENU_REPORT:
			if(isFragmentVisible(SEND_LOG_TAG)){
            	break;
            }
			SendLogFileFragment logFragment = new SendLogFileFragment();
			manager.beginTransaction().replace(R.id.content_frame, logFragment, SEND_LOG_TAG).addToBackStack(null).commit();
			return true;
			
		case SUBMENU_LOGBOOK:
			if(isFragmentVisible(LOGBOOK_TAG)){
            	break;
            }
			LogbookFragment logbookFragment = new LogbookFragment();
            manager.beginTransaction().replace(R.id.content_frame, logbookFragment, LOGBOOK_TAG).addToBackStack(null).commit();
            return true;
			
		}
		
		
		return false;
	}
	
	private void firstInit(){
		if (!preferences.contains("first_init")) {
			drawer.openDrawer(drawerList);
			
			Editor e = preferences.edit();
			e.putString("first_init", "seen");
			e.putBoolean("pref_privacy", true);
			e.commit();
		}
	}
	
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
		 
		 //SubMenu overflowMenu = menu.addSubMenu("Action Item");
		 SubMenu overflowMenu=menu.addSubMenu(0, MENU_ID, 300, "Action Item");
		 overflowMenu.add(0,SUBMENU_ABOUT,14,"About");
		 overflowMenu.add(0,SUBMENU_HELP,14,"Help");
		 overflowMenu.add(0,SUBMENU_REPORT,14,"Send Report");
		 overflowMenu.add(0,SUBMENU_LOGBOOK,14,"LogBook");
	        
	        
	        

	        MenuItem subMenu1Item = overflowMenu.getItem();
	        subMenu1Item.setIcon(R.drawable.overflow_menu_trans);
	        
	        //subMenu1Item.getActionView().setBackgroundColor(Color.RED);
	        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		 
		 return super.onCreateOptionsMenu(menu);
	 }
	 }
	
	

