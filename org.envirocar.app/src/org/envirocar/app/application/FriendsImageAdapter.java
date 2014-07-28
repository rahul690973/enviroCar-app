package org.envirocar.app.application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.envirocar.app.model.User;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;



import org.envirocar.app.R;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsImageAdapter  extends BaseAdapter{
	
	 private  LayoutInflater inflater=null;
	 private List<String>friends=null;
	 private Context context;
	 private User user;
	 private Object view;
	
	public FriendsImageAdapter( Context c,List<String> friendsNames) {
       
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friends=friendsNames;
        context=c;
        user=UserManager.instance().getUser();
        
    }

	@Override
	public int getCount() {
		
		return friends.size();
	}

	@Override
	public Object getItem(int position) {
		
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		 View view=convertView;
	        
			if(view==null)
	            view = inflater.inflate(R.layout.friends_list_item,null);
	        TextView tv=(TextView)view.findViewById(R.id.friends_name);
	        ImageView image=(ImageView)view.findViewById(R.id.friends_image);
	        
	        String friend=friends.get(position);
	        tv.setText(friend);
	        Uri absolute = Uri.parse(ECApplication.BASE_URL+"/users/friends/"+friend+"/avator?size=80");
	        getImageLoader(context).load(absolute).into(image);
	        
	        
		return view;
	}
	
	public Picasso getImageLoader(Context ctx) {
	    Picasso.Builder builder = new Picasso.Builder(ctx);
	    //OkHttpClient okHttpClient;
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
	
	

}
