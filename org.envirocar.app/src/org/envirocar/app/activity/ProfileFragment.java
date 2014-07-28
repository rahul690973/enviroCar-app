package org.envirocar.app.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.envirocar.app.R;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.TrackRetrievalException;
import org.envirocar.app.dao.exception.UnauthorizedException;
import org.envirocar.app.dao.exception.UserRetrievalException;
import org.envirocar.app.dao.remote.RemoteTrackDAO;
import org.envirocar.app.logging.Logger;
import org.envirocar.app.model.User;
import org.envirocar.app.views.TypefaceEC;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;

public class ProfileFragment extends SherlockFragment {
	TextView totalTracksView, ownTracksView, compareWithFriendsView,
			viewStatisticsView,leaderBoardView;
	static ImageView profilePicView;
	String TOTAL_TRACK_NUMBER = "number_of_tracks";
	String OWN_TRACK_NUMBER = "own-tracks";
	String prefTotalTrack, prefOwnTrack;
	String whichTrack;

	static User user;
	static String root;
	int x;

	SharedPreferences preferences;
	private static final Logger logger = Logger
			.getLogger(ProfileFragment.class);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.profile_page, null);

		totalTracksView = (TextView) view.findViewById(R.id.total_tracks);
		ownTracksView = (TextView) view.findViewById(R.id.your_tracks);
		compareWithFriendsView = (TextView) view
				.findViewById(R.id.compare_friends);
		profilePicView = (ImageView) view.findViewById(R.id.profile_picture);
		viewStatisticsView = (TextView) view.findViewById(R.id.view_statistics);
		leaderBoardView = (TextView) view.findViewById(R.id.view_leaderboard);
		compareWithFriendsView.setOnClickListener(textViewClickListener);
		viewStatisticsView.setOnClickListener(textViewClickListener);
		leaderBoardView.setOnClickListener(textViewClickListener);
		

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		user = UserManager.instance().getUser();
		root = Environment.getExternalStorageDirectory().toString();

		File file = new File(root + "/enviroCar/images/", user.getUsername()
				+ ".jpg");

		if (file.exists()) {

			setImageOnView();
		}

		try {

			DAOProvider.instance().getUserDAO().getProfilePicture(user);
			setImageOnView();

		} catch (UserRetrievalException e) {
			e.printStackTrace();
		}

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());
		prefTotalTrack = preferences.getString(TOTAL_TRACK_NUMBER, null);
		prefOwnTrack = preferences.getString(OWN_TRACK_NUMBER, null);

		if (prefTotalTrack == null) {
		}

		else {
			totalTracksView.setText("Total Tracks :" + " " + prefTotalTrack);
			ownTracksView.setText("Your Tracks :" + " " + prefOwnTrack);
		}

		new displayNumberOfTracks().execute();

	}

	private OnClickListener textViewClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.compare_friends) {

				getActivity().getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				FriendListFragment friendsFragment = new FriendListFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, friendsFragment).commit();

			}

			else if (v.getId() == R.id.view_statistics) {

				getActivity().getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, userStatisticsFragment)
						.commit();

			}
			
			else if(v.getId()==R.id.view_leaderboard){
				
				getActivity().getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
				LeaderboardFragment lbFragment = new LeaderboardFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, lbFragment)
						.commit();
				
				
			}

		}
	};

	public static void setImageOnView() {

		Bitmap bitmap = BitmapFactory.decodeFile(root + "/enviroCar/images/"
				+ user.getUsername() + ".jpg");
		Bitmap roundedBitmap = getRoundedBitmap(bitmap, 200);
		profilePicView.setImageBitmap(roundedBitmap);
	}

	public static Bitmap getRoundedBitmap(Bitmap bitmap, int pixels) {
		Bitmap result = null;
		try {
			result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(result);

			int color = 0xff424242;
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, 200, 200);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
					bitmap.getWidth() / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

		} catch (NullPointerException e) {
		} catch (OutOfMemoryError o) {
		}
		return result;
	}

	class displayNumberOfTracks extends AsyncTask<String, String, Void> {
		InputStream is = null;
		HttpResponse response;
		int totalTracks, userTracks;

		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... params) {

			try {

				totalTracks = DAOProvider.instance().getTrackDAO()
						.getTotalTrackCount();
				userTracks = DAOProvider.instance().getTrackDAO()
						.getUserTrackCount();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TrackRetrievalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void v) {

			if (prefTotalTrack == null
					|| !prefTotalTrack.equalsIgnoreCase(String
							.valueOf(totalTracks))) {

				totalTracksView.setText("Total Tracks :" + " " + totalTracks);
				ownTracksView.setText("Your Tracks :" + " " + userTracks);

				preferences
						.edit()
						.putString(TOTAL_TRACK_NUMBER,
								String.valueOf(totalTracks)).commit();
				preferences
						.edit()
						.putString(OWN_TRACK_NUMBER, String.valueOf(userTracks))
						.commit();

			}

		}

	}

}