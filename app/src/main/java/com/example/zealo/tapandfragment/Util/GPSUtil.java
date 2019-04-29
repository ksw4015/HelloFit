package com.example.zealo.tapandfragment.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.zealo.tapandfragment.R;

import java.io.Serializable;

/**
 * Created by USER on 2017-09-07.
 */
public class GPSUtil extends Service implements LocationListener, Serializable {

    private final Context mContext;

    // GPS 사용유무
    boolean isGpsEnabled = false;

    // 네트워크 사용유무 (LTE, WiFi)
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    public static Location location;
    double Lat;
    double Lng;

    // GPS 업데이트 반경
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;     // 10m
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;    // 밀리세컨드이므로 1분

    protected LocationManager locationManager;

    public GPSUtil(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public Location getLocation() {

        try {

            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);

            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGpsEnabled && !isNetworkEnabled) {
                this.isGetLocation =false;
                location = null;
            }
            else {

                this.isGetLocation = true;

                if(isNetworkEnabled) {
                    try {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this
                        );

                        if(locationManager != null){

                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if(location != null) {
                                Lat = location.getLatitude();
                                Lng = location.getLongitude();
                            }

                        }
                    }
                    catch (SecurityException e) {

                    }

                }

                if(isGpsEnabled) {

                    if(location == null) {

                        try {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this
                            );

                            if(locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if(location != null) {
                                    Lat = location.getLatitude();
                                    Lng = location.getLongitude();
                                }
                            }

                        }
                        catch (SecurityException e) {

                        }

                    }

                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public Location getCurrentLocation() {
        return this.location;
    }

    public void stopGps() {
        if(locationManager != null) {

            try {
                locationManager.removeUpdates(GPSUtil.this);
            }
            catch (SecurityException e) {

            }

        }
    }

    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(mobile.isConnected() || wifi.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
