package org.envirocar.app.application;



import android.content.res.XmlResourceParser;



import java.util.LinkedHashMap;
import java.util.Map;


import android.content.Context;


import org.xmlpull.v1.XmlPullParser;


public class UnitsParser {
    public static Map<String,String>[] getHashMapResource(Context c, int hashMapResId) {
    	
    	LinkedHashMap<String,String>map_array[] = new LinkedHashMap[2];
        LinkedHashMap<String,String> map_length = null;
        LinkedHashMap<String,String> map_time = null;
      
        XmlResourceParser parser = c.getResources().getXml(hashMapResId);

        String key = null, value = null;

        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    //Log.d("utils","Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("length")) {
                       // boolean isLinked = parser.getAttributeBooleanValue(null, "linked", false);

                        map_length = new LinkedHashMap<String, String>();
                    }
                    else if(parser.getName().equals("time")){
                    	
                        map_time = new LinkedHashMap<String, String>();
                    }
                    	
                    else if (parser.getName().equals("lengthname")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                    else if (parser.getName().equals("timename")) {
                        key = parser.getAttributeValue(null, "key");

                        if (null == key) {
                            parser.close();
                            return null;
                        }
                    }
                    
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("lengthname")) {
                        map_length.put(key, value);
                        key = null;
                        value = null;
                    }
                    
                    if (parser.getName().equals("timename")) {
                        map_time.put(key, value);
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
        
        map_array[0]=map_length;
        map_array[1]=map_time;

        return map_array;
    }
}