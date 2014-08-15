package org.envirocar.app.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.http.ParseException;
import org.envirocar.app.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CommonJSONDecoder {

	public JSONObject returnObject(InputStream response) throws IOException,
			JSONException {

		String result = Util.consumeInputStream(response).toString();
		JSONObject json = new JSONObject(result);

		return json;

	}

	public ArrayList<LinkedHashMap<String, String>> decodeLeaderboard(
			InputStream response) throws ParseException, IOException,
			JSONException {

		ArrayList<LinkedHashMap<String, String>> userStats = new ArrayList<LinkedHashMap<String,String>>();
		JSONObject json = returnObject(response);

		JSONArray leaderboards = json.getJSONArray("leaderboards");
		JSONObject consumptionObject = leaderboards.getJSONObject(0);
		JSONObject co2Object = leaderboards.getJSONObject(1);

		JSONArray consumptionArray = consumptionObject.getJSONArray("rankings");
		JSONArray co2Array = co2Object.getJSONArray("rankings");

		putValuesinMap(consumptionArray, userStats, 0);
		putValuesinMap(co2Array, userStats, 1);

		return userStats;

	}

	private void putValuesinMap(JSONArray array,
			ArrayList<LinkedHashMap<String, String>> map, int pos) throws JSONException {

		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < array.length(); i++) {

			JSONObject j = array.getJSONObject(i);
			hashMap.put(j.getString("name"), j.getString("value"));
		}
		
		map.add(hashMap);

	}

	public LinkedHashMap<String, String>[] decodeUserStatistics(
			InputStream response) throws ParseException, IOException,
			JSONException {

		LinkedHashMap<String, String> userStats[] = new LinkedHashMap[2];

		JSONObject json = returnObject(response);
		JSONArray statisticsArray = json.getJSONArray("statistics");

		JSONObject co2Object = statisticsArray.getJSONObject(3); // fuel
																	// consumption
		JSONObject fuelObject = statisticsArray.getJSONObject(2); // co2
																	// emission

		JSONArray co2Array = co2Object.getJSONArray("values");
		JSONArray fuelArray = fuelObject.getJSONArray("values");

		putValuesInOtherMap(co2Array, userStats, 0);
		putValuesInOtherMap(fuelArray, userStats, 1);

		return userStats;

	}

	private void putValuesInOtherMap(JSONArray array,
			LinkedHashMap<String, String> map[], int pos) throws JSONException {

		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < array.length(); i++) {

			JSONObject j = array.getJSONObject(i);
			hashMap.put(j.getString("time"), j.getString("avg"));
		}

		map[pos] = hashMap;

	}

}
