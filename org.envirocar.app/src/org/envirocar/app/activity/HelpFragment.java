package org.envirocar.app.activity;


import org.envirocar.app.R;
import org.envirocar.app.util.Util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Help page
 * 
 * @author jakob
 * 
 */
public class HelpFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		View view=inflater.inflate(R.layout.help_layout_new, container, false);
		setListeners(view);
		
		
		return view;

	}
	
	private OnClickListener mCorkyListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			
			

		}
	};
	
	
	private void closeOrOpen(int id){
		
		
		
		
	}
	
	private void setListeners(View view){
		
		
		view.findViewById(R.id.about_envirocar).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.about_envirocar_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.getting_started).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.getting_started_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.register).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.register_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.car_data).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.car_data_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.connect_to_car).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.connect_to_car_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.record_track).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.record_track_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.observe_track).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.observe_track_text).setOnClickListener(mCorkyListener);
		
		
		view.findViewById(R.id.upload_track).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.upload_track_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.settings).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.settings_part_1).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.settings_part_1_text).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.settings_part_2).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.settings_part_2_text).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.settings_part_3).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.settings_part_3_text).setOnClickListener(mCorkyListener);
		
		view.findViewById(R.id.feedback).setOnClickListener(mCorkyListener);
		view.findViewById(R.id.feedback_text).setOnClickListener(mCorkyListener);
		
		
		
		
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	    //TODO: this could possibly be used to make links in the text able to be opened in the browser
	    //gave me an error at first try, though...
	    //t2.setMovementMethod(LinkMovementMethod.getInstance());
		
//		SpannableString text = new SpannableString(getActivity().getText(R.string.help_view_track_text_1));
//		
//		Locale locale = Locale.getDefault();
//		
//		//add symbol in text (position differs depending on language)
//		//TODO you will probably also have to change the data_privacy image here (text on the image is in german right now) 
//		if(locale.equals(Locale.GERMANY)){
//			text.setSpan(is, 103, 117, 0);
//		}else if(locale.equals(Locale.UK) || locale.equals(Locale.US)){
//			text.setSpan(is, 103, 117, 0);
//			
//		}
		
		
//	    TextView versionTextview = (TextView) getActivity().findViewById(R.id.textView22);
//
//	    CharSequence versionString = getActivity().getText(R.string.help_text_6_3);
//	    
//	    versionString = versionString + " " + Util.getVersionString(getActivity());
//	    
//	    versionTextview.setText(versionString);	    
	    
	}
}
