package org.envirocar.app.activity.preference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
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

public class UnitSelectionPreference extends DialogPreference {

	Spinner spinner_speed;
	Spinner spinner_co2_emission;
	Spinner spinner_fuel_consumption;
	AlertDialog.Builder alert;

	private static final Logger logger = Logger
			.getLogger(UnitSelectionPreference.class);

	UnitSelection units, unitsPrevious;
	// String UNIT_PREFERENCES="unit_preferences";

	private static SharedPreferences preferences;

	int spinnerCount;
	int initializedCount;

	public UnitSelectionPreference(Context c, AttributeSet attrs) {
		super(c, attrs);

		spinnerCount = 3;
		initializedCount = 0;
		preferences = callPreferences(c);
		unitsPrevious = new UnitSelection();
		unitsPrevious = instantiateUnits(preferences.getString(
				SettingsActivity.UNITS, null));
		units = new UnitSelection();
		units = instantiateUnits(preferences.getString(SettingsActivity.UNITS,
				null));

		alert = new AlertDialog.Builder(c);
		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View alertView = inflater.inflate(R.layout.units, null);
		alert.setView(alertView);
		alert.setTitle("Choose the units..");
		setupUIItems(alertView, c);

	}

	private void setupUIItems(View rootView, final Context c) {
		// TODO !fancy! search for sensors
		spinner_speed = (Spinner) rootView.findViewById(R.id.spinner_speed);
		spinner_co2_emission = (Spinner) rootView
				.findViewById(R.id.spinner_co2);
		spinner_fuel_consumption = (Spinner) rootView
				.findViewById(R.id.spinner_fuel);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// units=instantiateUnits(preferences.getString(
				// SettingsActivity.UNITS, null));

				if (!units.equals(unitsPrevious))
					persistUnits(units, c);



			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alert.create();
		alertDialog.show();

		Map<String, String> values_maps[] = UnitsParser.getHashMapResource(c,
				R.xml.unit_values_final);
		String speed_keys[] = getKeys(values_maps[0]);
		String co2_keys[] = getKeys(values_maps[1]);
		String fuel_keys[] = getKeys(values_maps[2]);

		ArrayAdapter<?> adapter_speed = generateAdapter(c, speed_keys);
		ArrayAdapter<?> adapter_co2 = generateAdapter(c, co2_keys);
		ArrayAdapter<?> adapter_fuel = generateAdapter(c, fuel_keys);

		spinner_speed.setAdapter(adapter_speed);
		spinner_co2_emission.setAdapter(adapter_co2);
		spinner_fuel_consumption.setAdapter(adapter_fuel);

		if (units != null) {

			spinner_speed.setSelection(Arrays.asList(speed_keys).indexOf(
					units.getSpeed()));
			spinner_co2_emission.setSelection(Arrays.asList(co2_keys).indexOf(
					units.getCo2Emission()));
			spinner_fuel_consumption.setSelection(Arrays.asList(fuel_keys)
					.indexOf(units.getFuelConsumption()));
		}

		OnItemSelectedListener spinnerSelect = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				if (initializedCount < spinnerCount)
					initializedCount++;
				else {
					if (parent.getId() == R.id.spinner_speed)
						units.setSpeed((String) parent.getItemAtPosition(pos));

					if (parent.getId() == R.id.spinner_co2)
						units.setCo2Emission((String) parent
								.getItemAtPosition(pos));

					if (parent.getId() == R.id.spinner_fuel)
						units.setFuelConsumption((String) parent
								.getItemAtPosition(pos));

				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		};

		spinner_speed.setOnItemSelectedListener(spinnerSelect);
		spinner_co2_emission.setOnItemSelectedListener(spinnerSelect);
		spinner_fuel_consumption.setOnItemSelectedListener(spinnerSelect);

	}

	private SharedPreferences callPreferences(Context c) {

		return PreferenceManager.getDefaultSharedPreferences(c
				.getApplicationContext());
	}

	private ArrayAdapter<?> generateAdapter(Context c, String keys[]) {

		return new ArrayAdapter(c,
				android.R.layout.simple_spinner_dropdown_item, keys);
	}

	protected String[] getKeys(Map<String, String> m) {

		String map_keys[] = new String[m.size()];
		Set<String> keys = m.keySet();
		int i = 0;
		for (String key : keys) {
			map_keys[i] = key;
			i += 1;
		}
		return map_keys;
	}

	public static void persistUnits(UnitSelection units, Context c) {

		preferences = PreferenceManager.getDefaultSharedPreferences(c
				.getApplicationContext());
		String x = serializeUnits(units);
		PreferenceManager
				.getDefaultSharedPreferences(c.getApplicationContext()).edit()
				.putString(SettingsActivity.UNITS, x).commit();

	}

	public static UnitSelection instantiateUnits(String object) {
		if (object == null)
			return null;

		ObjectInputStream ois = null;
		try {
			Base64InputStream b64 = new Base64InputStream(
					new ByteArrayInputStream(object.getBytes()), Base64.DEFAULT);
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
