package org.envirocar.app.application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.methods.HttpGet;
import org.envirocar.app.model.User;
import org.envirocar.app.util.CommonUtils;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;





import org.envirocar.app.R;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsImageAdapter  extends BaseAdapter{
	
	 private  LayoutInflater inflater=null;
	 private List<String>friends=null;
	 private ArrayList<String>friendsClone;
	 private Context context;
	 private User user;
	 private Object view;
	 private CommonUtils cu;
	
	public FriendsImageAdapter( Context c,ArrayList<String> friendsNames) {
       
        this.inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friends=friendsNames;
        this.friendsClone=new ArrayList<String>();
        this.friendsClone.addAll(this.friends);
        this.context=c;
        this.user=UserManager.instance().getUser();
        this.cu=new CommonUtils();
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
	        Uri absolute = Uri.parse(ECApplication.BASE_URL+"/users/"+user.getUsername()+"/friends/"+friend+"/avatar?size=50");
	        cu.setImageOnView(context, user, image,absolute,50);
	        
	        
		return view;
	}
	
	public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        friends.clear();
        if (charText.length() == 0) {
            friends.addAll(friendsClone);
        } 
        else 
        {
            for (String s :friendsClone) 
            {
                if (s.toLowerCase(Locale.getDefault()).contains(charText)) 
                {
                    friends.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }
	
	
	
	

}
