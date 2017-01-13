package com.heqiang.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckNetwork {
    private ConnectivityManager connManager;
    private NetworkInfo networkInfo;
    
	public CheckNetwork(Context context){
		connManager = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
		networkInfo = connManager.getActiveNetworkInfo();
	}
	
	public boolean isNetworkAvailable(){    	
    	if(networkInfo != null){
    		if (networkInfo.isAvailable()) {
    			return true;
    		}else {
    			return false;
    		}
    	}else {
    		return false;
		}
    	
    }

}
