package org.envirocar.app.activity;

import java.io.InputStream;





import org.apache.http.HttpResponse;
import org.envirocar.app.R;
import org.envirocar.app.model.User;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.FriendsRetrievalException;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.TrackRetrievalException;
import org.envirocar.app.dao.exception.UserRetrievalException;
import org.envirocar.app.dao.remote.RemoteTrackDAO;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

public class FriendListActivity extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friends_list);
		
		
		User user = UserManager.instance().getUser();
		try {
			DAOProvider.instance().getUserDAO().getFriends(user);
		} catch (FriendsRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
