package org.envirocar.app.application;



import android.content.res.XmlResourceParser;



import java.util.LinkedHashMap;
import java.util.Map;


import android.content.Context;


import org.xmlpull.v1.XmlPullParser;


public class UnitsParser {
    public static Map<String,String>[] getHashMapResource(Context c, int hashMapResId) {
    	
    	LinkedHashMap<String,String>map_array[] = new LinkedHashMap[3];
        LinkedHashMap<String,String> map_speed = null;
        LinkedHashMap<String,String> map_co2 = null;
        LinkedHashMap<String,String> map_fuel = null;
      
        XmlResourceParser parser = c.getResources().getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    //Log.d("utils","Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("speed")) {
                       // boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map_speed = new LinkedHashMap<String, String>();
                    }
                    else if(parser.getName().equals("co2")){
                    	
                        map_co2 = new LinkedHashMap<String, String>();
                    }
                    
                    else if(parser.getName().equals("fuel")){
                    	
                        map_fuel = new LinkedHashMap<String, String>();
                    }
                    
                    
                    else if (parser.getName().equals("speedname")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                    else if (parser.getName().equals("co2name")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                    else if (parser.getName().equals("fuelname")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                    
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("speedname")) {
                        map_speed.put(key, value);
                        key = null;
                        value = null;
                    }
                    
                    if (parser.getName().equals("co2name")) {
                        map_co2.put(key, value);
                        key = null;
                        value = null;
                    }
                    if (parser.getName().equals("fuelname")) {
                        map_fuel.put(key, value);
                        key = null;
                        value = null;
                    }
                    
                } else if (eventType == XmlPullParser.TEXT) {
                    if (null != key) {
                        value = parser.getText();
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        map_array[0]=map_speed;
        map_array[1]=map_co2;
        map_array[2]=map_fuel;

        return map_array;
    }
}