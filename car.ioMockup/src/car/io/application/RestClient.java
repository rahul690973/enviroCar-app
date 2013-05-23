package car.io.application;

import java.util.ArrayList;

import car.io.adapter.Track;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class RestClient {
	
	private static final String BASE_URL = "http://giv-car.uni-muenster.de:8080/stable/rest/";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void downloadTracks(JsonHttpResponseHandler handler){
		client.get("http://gis-ts.uni-muenster.de/tracks.json", handler);
	}
	
	public static void downloadTrack(String url, ArrayList<Track> tracks, JsonHttpResponseHandler handler){
		client.get(url, handler);
	}

}
