package org.envirocar.app.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;

import org.envirocar.app.R;
import org.envirocar.app.application.ECApplication;
import org.envirocar.app.model.User;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;




public class CommonUtils {
	
	
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
									: View.INVISIBLE);
						}
					});

			formView.setVisibility(View.VISIBLE);
			formView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							formView.setVisibility(show ? View.INVISIBLE
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
	
	
	public Picasso getImageLoader(Context ctx,final User user) {
	    Picasso.Builder builder = new Picasso.Builder(ctx);
	    
	    builder.downloader(new OkHttpDownloader(ctx) {
	        @Override
	        protected HttpURLConnection openConnection(Uri uri) throws IOException {
	            HttpURLConnection connection = super.openConnection(uri);
	           
	            connection.setRequestProperty("X-User",user.getUsername());
	            connection.setRequestProperty("X-Token",user.getToken());
	            
	            return connection;
	        }
	    });
	    return builder.build();
	}
	
	public  Bitmap getRoundedBitmap(Bitmap bitmap, int pixels) {
		Bitmap result = null;
		try {
			result = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(result);

			int color = 0xff424242;
			Paint paint = new Paint();
			Rect rect = new Rect(0, 0, pixels, pixels);

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
	
	public void setImageOnView(final Context c,final User user,final ImageView view,final Uri uri,final int pixels) {

		
		
		
		new Thread() {
			@Override
			public void run() {
			
				try {
					
					final Bitmap m = getImageLoader(c, user).load(uri)
							.placeholder(R.drawable.profile_picture)
							.error(R.drawable.profile_picture).get();

					((Activity) c).runOnUiThread(new Runnable() {
						@Override
						public void run() {
								
							Bitmap roundedBitmap =getRoundedBitmap(m, pixels);
							view.setImageBitmap(roundedBitmap);
						}
					});

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}.start();

	}
	
	public void changeLanguage(Context context,String language) {
	    Resources res = context.getResources();
	    DisplayMetrics dm = res.getDisplayMetrics();
	    
	    android.content.res.Configuration conf = res.getConfiguration();
	    conf.locale = new Locale(language);
	    res.updateConfiguration(conf, dm);
	    
	}
	
	public void restartActivity(Activity activity){
		
		Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();

        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
		
	}
	
	public boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


}
