package org.envirocar.app.activity.preference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;
import java.util.Set;

import org.envirocar.app.R;
import org.envirocar.app.activity.SettingsActivity;
import org.envirocar.app.application.UnitsParser;
import org.envirocar.app.logging.Logger;
import org.envirocar.app.model.UnitSelection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UnitSelectionPreference extends DialogPreference{
	
	Spinner speed_first,speed_second;
	Spinner co2_first,co2_second;
	Spinner fuel_first,fuel_second;
	AlertDialog.Builder alert;
	
	private static final Logger logger = Logger.getLogger(UnitSelectionPreference.class);
	
	UnitSelection units,unitsPrevious;
	//String UNIT_PREFERENCES="unit_preferences";
	
	private static SharedPreferences preferences;
	
	
	public UnitSelectionPreference(Context c, AttributeSet attrs) {
		super(c, attrs);
		
		 alert= new AlertDialog.Builder(c);
		 LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		 View alertView = inflater.inflate(R.layout.units, null);
		 alert.setView(alertView);
		 alert.setTitle("Choose the units..");
		 setupUIItems(alertView,c);
        
        
	}
	
	


	private void setupUIItems(View rootView,final Context c) {
		//TODO !fancy! search for sensors
		speed_first = (Spinner) rootView.findViewById(R.id.spinner_speed1);
		speed_second = (Spinner) rootView.findViewById(R.id.spinner_speed2);
		
		co2_first= (Spinner) rootView.findViewById(R.id.spinner_co21);
		co2_second= (Spinner) rootView.findViewById(R.id.spinner_co22);
		
		fuel_first= (Spinner) rootView.findViewById(R.id.spinner_fuel1);
		fuel_second= (Spinner) rootView.findViewById(R.id.spinner_fuel2);
		
		alert.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				
				if(!units.equals(unitsPrevious))
				persistUnits(units,c);
				
//				UnitSelection units = new UnitSelection();
//				units = UnitSelectionPreference.instantiateUnits(preferences.getString(SettingsActivity.UNITS, null));
//				Map<String,String> values_maps[]=UnitsParser.getHashMapResource(c, R.xml.unit_values_final);
//				
//				 String lengthUnit=units.getSpeed_first();
//				 String timeUnit=units.getSpeed_second();
//				
//				 float conversion_length=Float.parseFloat(values_maps[0].get(lengthUnit));
//				 float conversion_time=Float.parseFloat(values_maps[1].get(timeUnit));
//				 
//				 Toast.makeText(c,lengthUnit+"hh"+timeUnit, Toast.LENGTH_LONG).show();
				
				//shortLengthUnit=extractSmallUnit(lengthUnit);
				//shortTimeUnit=extractSmallUnit(timeUnit);
				
			}
		  })
		.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				
				dialog.cancel();
			}
		});
		
		AlertDialog alertDialog = alert.create();
		alertDialog.show();
		
		
		Map<String,String> values_maps[]=UnitsParser.getHashMapResource(c, R.xml.unit_values_final);
		String length_keys[]=getKeys(values_maps[0]);
		String time_keys[]=getKeys(values_maps[1]);
		
		ArrayAdapter<?> adapter = new ArrayAdapter(c,
		        android.R.layout.simple_spinner_dropdown_item, length_keys);
		
		ArrayAdapter<?> adapter2 = new ArrayAdapter(c,
		        android.R.layout.simple_spinner_dropdown_item, time_keys);
		
	    speed_first.setAdapter(adapter);
	    speed_second.setAdapter(adapter2);
		
		
		 units=new UnitSelection();
		 unitsPrevious=new UnitSelection();
		
		OnItemSelectedListener spinnerSelect=new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				
				if(parent.getId()==R.id.spinner_speed1)
					units.setSpeed_first((String) parent.getItemAtPosition(pos));
				
				if(parent.getId()==R.id.spinner_speed2)
					units.setSpeed_second((String) parent.getItemAtPosition(pos));
				
				if(parent.getId()==R.id.spinner_co21)
					units.setCo2_first((String) parent.getItemAtPosition(pos));
				
				if(parent.getId()==R.id.spinner_co22)
					units.setCo2_second((String) parent.getItemAtPosition(pos));
				
				if(parent.getId()==R.id.spinner_fuel1)
					units.setFuel_first((String) parent.getItemAtPosition(pos));
				
				if(parent.getId()==R.id.spinner_fuel2)
					units.setFuel_second((String) parent.getItemAtPosition(pos));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			
				
			}
			
			
		};
		
		speed_first.setOnItemSelectedListener(spinnerSelect);
		speed_second.setOnItemSelectedListener(spinnerSelect);
		
		co2_first.setOnItemSelectedListener(spinnerSelect);
		co2_second.setOnItemSelectedListener(spinnerSelect);
		
		fuel_first.setOnItemSelectedListener(spinnerSelect);
		fuel_second.setOnItemSelectedListener(spinnerSelect);
		

		
		
		
	}
	
	
	protected String[] getKeys(Map<String,String> m){
		
		String map_keys[]=new String[m.size()];
		Set<String> keys = m.keySet();
		int i=0;
		 for(String key: keys){
	            map_keys[i]=key;
	            i+=1;
	        }
		return map_keys;
	}
	
	
	public static void persistUnits(UnitSelection units,Context c){
		
		preferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		String x=serializeUnits(units);
		PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext()).edit().putString(SettingsActivity.UNITS,x).commit();
	       
		
	}
	
	public static UnitSelection instantiateUnits(String object) {
		if (object == null) return null;
		
		ObjectInputStream ois = null;
		try {
			Base64InputStream b64 = new Base64InputStream(new ByteArrayInputStream(object.getBytes()), Base64.DEFAULT);
			ois = new ObjectInputStream(b64);
			UnitSelection units = (UnitSelection) ois.readObject();
			return units;
		} catch (StreamCorruptedException e) {
			logger.warn(e.getMessage(), e);
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			logger.warn(e.getMessage(), e);
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
		}
		return null;
	}
	
	public static String serializeUnits(UnitSelection obj) {
		ObjectOutputStream oos = null;
		Base64OutputStream b64 = null;
		try {
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(byteArrayOut);
			oos.writeObject(obj);
			oos.flush();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        b64 = new Base64OutputStream(out, Base64.DEFAULT);
	        b64.write(byteArrayOut.toByteArray());
	        b64.flush();
	        b64.close();
	        out.flush();
	        out.close();
			
	        String result = new String(out.toByteArray());
			return result;
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
		} finally {
			if (oos != null)
				try {
					b64.close();
					oos.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
		}
		return null;
	}
	
	
	
	

}
