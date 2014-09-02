package org.envirocar.app.activity;


import org.envirocar.app.R;
import org.envirocar.app.util.Util;

import android.graphics.drawable.Drawable;
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
	
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
	    view=inflater.inflate(R.layout.help_layout_new, container, false);
		setListeners(view);
		
		return view;

	}
	
	private OnClickListener mCorkyListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch(v.getId()){
			
				case R.id.about_envirocar:
					closeOrOpen(R.id.about_envirocar_text,R.id.about_envirocar);
					break;
					
				case R.id.getting_started:
					closeOrOpen(R.id.getting_started_text,R.id.getting_started);
					break;
				case R.id.register:
					closeOrOpen(R.id.register_text,R.id.register);
					break;
				case R.id.car_data:
					closeOrOpen(R.id.car_data_text,R.id.car_data);
					break;
				case R.id.connect_to_car:
					closeOrOpen(R.id.connect_to_car_text,R.id.connect_to_car);
					break;
				case R.id.record_track:
					closeOrOpen(R.id.record_track_text,R.id.record_track);
					break;
				case R.id.observe_track:
					closeOrOpen(R.id.observe_track_text,R.id.observe_track);
					break;
				case R.id.upload_track:
					closeOrOpen(R.id.upload_track_text,R.id.upload_track);
					break;
				case R.id.settings_part_1:
					closeOrOpen(R.id.settings_part_1_text,R.id.settings_part_1);
					break;
				case R.id.settings_part_2:
					closeOrOpen(R.id.settings_part_2_text,R.id.settings_part_2);
					break;
				case R.id.settings_part_3:
					closeOrOpen(R.id.settings_part_3_text,R.id.settings_part_3);
					break;
				case R.id.feedback:
					closeOrOpen(R.id.feedback_text,R.id.feedback);
					break;
				
			
			}
			

		}
	};
	
	
	private void closeOrOpen(int id,int idSelf){
		
		TextView textView=(TextView)view.findViewById(id);
		TextView selfView=(TextView)view.findViewById(idSelf);
		
		if(textView.getVisibility()==View.GONE){
			textView.setVisibility(View.VISIBLE);
			setDownImage(selfView,R.drawable.close_arrow,R.drawable.arrow_down,R.drawable.arrow_down_small);
			
			//selfView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
		}
		else{
			textView.setVisibility(View.GONE);
			setDownImage(selfView,R.drawable.arrow_down,R.drawable.close_arrow,R.drawable.close_arrow_small);
			
		}
			
		
		
	}
	
	private void setDownImage(TextView tv,int arrow_one,int arrow_two,int arrow_three){
		
		Drawable[] drawables=tv.getCompoundDrawables();
		if(drawables[2].getConstantState().equals(getResources().getDrawable(arrow_one).getConstantState()))
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_two, 0);
		else 
			tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrow_three, 0);
		
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
		
		
	    TextView versionTextview = (TextView) getActivity().findViewById(R.id.settings_part_3_text);

	    CharSequence versionString = getActivity().getText(R.string.help_text_6_3);
	    
	    versionString = versionString + " " + Util.getVersionString(getActivity());
	    
	    versionTextview.setText("You are using version"+" "+versionString);	    
	    
	}
}
