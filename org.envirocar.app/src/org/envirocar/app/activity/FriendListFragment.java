package org.envirocar.app.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.envirocar.app.R;
import org.envirocar.app.model.User;
import org.envirocar.app.util.ShowProgressBar;
import org.envirocar.app.application.FriendsImageAdapter;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.FriendsRetrievalException;
import org.envirocar.app.dao.exception.NotConnectedException;
import org.envirocar.app.dao.exception.TrackRetrievalException;
import org.envirocar.app.dao.exception.UserRetrievalException;
import org.envirocar.app.dao.remote.RemoteTrackDAO;
import org.envirocar.app.dao.remote.RemoteUserDAO;
import org.envirocar.app.event.ProgressBarHideEvent;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendListFragment extends SherlockFragment implements
		ProgressBarHideEvent {

	static ListView friendsList;
	static ArrayAdapter adapter;
	EditText searchView;
	static Context c;

	private static View progressStatusView;
	private static View friendListView;
	private static TextView progressStatusMessageView;
	private static ShowProgressBar progressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.friends_list, null);
		c = getActivity();

		progressBar = new ShowProgressBar();
		progressStatusView = view.findViewById(R.id.progress_status);
		progressStatusMessageView = (TextView) view
				.findViewById(R.id.progress_status_message);
		friendListView = view.findViewById(R.id.friend_list_view);
		searchView = (EditText) view.findViewById(R.id.inputSearch);
		friendsList = (ListView) view.findViewById(R.id.friends_list);

		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		User user = UserManager.instance().getUser();
		try {
			DAOProvider.instance().getUserDAO().getFriends(user);

		} catch (FriendsRetrievalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		searchView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				FriendListFragment.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public static void initializeList(String[] friends) {

		// adapter = new ArrayAdapter<String>(c, R.layout.friends_list_item,
		// R.id.friends_name_list,friends);
		List<String> al = Arrays.asList(friends);
		FriendsImageAdapter fia = new FriendsImageAdapter(c, al);
		friendsList.setAdapter(fia);
		handleClickOnList();

	}

	private static void handleClickOnList() {

		friendsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tv = (TextView) view.findViewById(R.id.friends_name);
				String friendName = tv.getText().toString();

				// Bundle bundle = new Bundle();
				// bundle.putString("friend_name", friendName);
				ProgressBarHideEvent pbEvent=new FriendListFragment();
				FriendsGraphFragment fragobj = new FriendsGraphFragment(
						friendName, c,pbEvent);
				//progressStatusMessageView.setText(R.string.login_progress_signing_in);
				progressBar.showProgress(c, progressStatusView, friendListView,progressStatusMessageView,c.getResources().getString(R.string.loading_graphs),true);
				// fragobj.setArguments(bundle);
				// fragobj.downloadStatistics();

				// ((FragmentActivity)
				// c).getSupportFragmentManager().popBackStack(null,
				// FragmentManager.POP_BACK_STACK_INCLUSIVE);
				// ((FragmentActivity)
				// c).getSupportFragmentManager().beginTransaction()
				// .replace(R.id.content_frame, fragobj)
				// .commit();

			}

		});

	}

	@Override
	public void progressHideEvent() {

		progressBar.showProgress(c, progressStatusView, friendListView,progressStatusMessageView,c.getResources().getString(R.string.loading_graphs),false);
		
	}

}
