package org.envirocar.app.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.envirocar.app.R;
import org.envirocar.app.model.User;
import org.envirocar.app.util.CommonUtils;
import org.envirocar.app.application.FriendsImageAdapter;
import org.envirocar.app.application.UserManager;
import org.envirocar.app.dao.DAOProvider;
import org.envirocar.app.dao.exception.FriendsRetrievalException;
import org.envirocar.app.event.ProgressBarHideEvent;

import de.keyboardsurfer.android.widget.crouton.Crouton;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class FriendListFragment extends DialogFragment implements
		ProgressBarHideEvent {

	/**
	 * 
	 */

	static ListView friendsList;
	static ArrayAdapter adapter;
	EditText searchView;
	static Context c;

	private static FriendsImageAdapter fia;

	private static View progressStatusView;
	private static View friendListView;
	private static TextView progressStatusMessageView;
	private static CommonUtils commonUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		getDialog().setTitle(getString(R.string.compare_wid_friends));
		View view = inflater.inflate(R.layout.friends_list, null);
		c = getActivity();

		commonUtils = new CommonUtils();
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

		commonUtils.showProgress(c, progressStatusView, friendListView,
				progressStatusMessageView,
				c.getResources().getString(R.string.getting_friends), true);
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

				String text = searchView.getText().toString()
						.toLowerCase(Locale.getDefault());
				fia.filter(text);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	public static void initializeList(ArrayList<String> friends) {

		fia = new FriendsImageAdapter(c, friends);
		friendsList.setAdapter(fia);
		commonUtils.showProgress(c, progressStatusView, friendListView,
				progressStatusMessageView,
				c.getResources().getString(R.string.getting_friends), false);
		handleClickOnList();

	}

	private static void handleClickOnList() {

		friendsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(commonUtils.isNetworkAvailable(c)){
				TextView tv = (TextView) view.findViewById(R.id.friends_name);
				String friendName = tv.getText().toString();

				// Bundle bundle = new Bundle();
				// bundle.putString("friend_name", friendName);
				ProgressBarHideEvent pbEvent = new FriendListFragment();
				FriendsGraphFragment fragobj = new FriendsGraphFragment(
						friendName, c, pbEvent);
				// progressStatusMessageView.setText(R.string.login_progress_signing_in);
				commonUtils.showProgress(c, progressStatusView, friendListView,
						progressStatusMessageView,
						c.getResources().getString(R.string.loading_graphs),
						true);
				
				}
				else
					Toast.makeText(c,c.getResources().getString(R.string.error_host_not_found),Toast.LENGTH_LONG).show();
				
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

		commonUtils.showProgress(c, progressStatusView, friendListView,
				progressStatusMessageView,
				c.getResources().getString(R.string.loading_graphs), false);

	}

}
