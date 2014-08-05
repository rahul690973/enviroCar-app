package org.envirocar.app.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;




public class ShowProgressBar {
	
	
	/**
	 * Show the circular progress bar
	 *
	 * @param Context    context
	 * @param statusView    to show the status text
	 * @param formView   it contains the complete layout 
	 * @param ststusText to show in the statusView
	 * @param show  indicates when to show and when to stop the progress bar
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(Context c, final View statusView,final View formView,TextView statusMessageView,String statusText,final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		statusMessageView.setText(statusText);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = c.getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			statusView.setVisibility(View.VISIBLE);
			statusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							statusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			formView.setVisibility(View.VISIBLE);
			formView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							formView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			statusView.setVisibility(show ? View.VISIBLE : View.GONE);
			formView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	

}
