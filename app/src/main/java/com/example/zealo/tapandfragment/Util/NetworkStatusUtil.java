package com.example.zealo.tapandfragment.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by USER on 2017-09-08.
 */
public class NetworkStatusUtil extends BroadcastReceiver {

    public static boolean isNetworkEnabled = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            
            if(mobile.isConnected() || wifi.isConnected()) {
                isNetworkEnabled = true;
            }
            else {
                isNetworkEnabled = false;
            }
        }
    }
    
}
