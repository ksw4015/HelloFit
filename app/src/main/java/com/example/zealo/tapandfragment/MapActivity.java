package com.example.zealo.tapandfragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.ViewPagerAdapter;
import com.example.zealo.tapandfragment.Models.GymMapinfo;
import com.example.zealo.tapandfragment.Models.MarkerItem;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.Util.GPSUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-27.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
                            , GoogleMap.OnMarkerClickListener
                            , GoogleMap.OnMapClickListener
                            , GoogleMap.OnCameraIdleListener
                            , GoogleMap.OnCameraMoveStartedListener
                            , View.OnClickListener {

    // Map Control
    private GoogleMap mMap;
    //private GPSUtil gpsUtil;
    private Circle circle = null;
    private Location currentLocation = null;

    private Marker prevMarker = null;
    private Marker myLocation = null;

    //View Pager
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    // We need Marker Model Class!!!
    private ArrayList<Marker> mapMarkers;
    private GymMapinfo[] mapItems;

    // Seekbar Control 삭제 예정... ㅠ
    private RelativeLayout lay_map_scope;
    private Button btn_map_scope;
    private Button btn_my_location;
    private SeekBar sb_map_distance;
    private TextView tx_map_distance;

    private Button btn_search_center;

    public static MapActivity mapActivity = null;

    private String from_activity = null;
    private String gym_name = null;

    private boolean MAP_IDLE_STATUS = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        from_activity = getIntent().getStringExtra("FROM_ACTIVITY");       // MAIN, INFO
        gym_name = getIntent().getStringExtra("GYM_NAME");

        mapActivity = MapActivity.this;

        lay_map_scope = (RelativeLayout)findViewById(R.id.Lay_map_scope);
        btn_map_scope = (Button)findViewById(R.id.Btn_map_scope);
        btn_map_scope.setOnClickListener(this);
        btn_my_location = (Button)findViewById(R.id.Btn_my_location);
        btn_my_location.setOnClickListener(this);
        sb_map_distance = (SeekBar)findViewById(R.id.Sb_map_distance);
        tx_map_distance = (TextView)findViewById(R.id.Tx_map_distance);
        btn_search_center = (Button)findViewById(R.id.Btn_search_center);
        btn_search_center.setOnClickListener(this);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.m_map);
        mapFragment.getMapAsync(this);

        mViewPager = (ViewPager)findViewById(R.id.v_pager_container);

        lay_map_scope.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // ViewPager Item Chang Listener Man~
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(MAP_IDLE_STATUS) {
                    MAP_IDLE_STATUS = false;
                }
                // 이전에 선택한 마커 이미지 변경 및 인포윈도우 삭제
                if(prevMarker != null) {
                    prevMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_non_choice)));
                    prevMarker.hideInfoWindow();
                }

                mapMarkers.get(position).showInfoWindow();
                mapMarkers.get(position).setIcon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_choice)));
                prevMarker = mapMarkers.get(position);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(mapMarkers.get(position).getPosition()), 300, null);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Seekbar Progress Change Listener Man~~       Progess Man 1000!
        sb_map_distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("171106", "onProgressChanged");
                String distance_meter = "";

                if(progress < 125) {
                    distance_meter = "300m";
                }
                else if(125 <= progress && progress < 375) {
                    distance_meter = "500m";
                }
                else if(375 <= progress && progress < 625) {
                    distance_meter = "1Km";
                }
                else if(625 <= progress && progress < 875) {
                    distance_meter = "2Km";
                }
                else if(875 <= progress){
                    distance_meter = "3Km";
                }

                tx_map_distance.setText(distance_meter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String distance_meter = "";
                currentLocation = MainActivity.gpsutil.location;
                LatLng refresh = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                double distance = 0;
                float zoom = 14;

                int progress = seekBar.getProgress();

                if(progress < 125) {
                    seekBar.setProgress(0);
                    distance_meter = "300m";
                    distance = 300;
                    zoom = 15;
                }
                else if(125 <= progress && progress < 375) {
                    seekBar.setProgress(250);
                    distance_meter = "500m";
                    distance = 500;
                    zoom = 15;
                }
                else if(375 <= progress && progress < 625) {
                    seekBar.setProgress(500);
                    distance_meter = "1Km";
                    distance = 1000;
                    zoom = 14;
                }
                else if(625 <= progress && progress < 875) {
                    seekBar.setProgress(750);
                    distance_meter = "2Km";
                    distance = 2000;
                    zoom = 13;
                }
                else if(875 <= progress){
                    seekBar.setProgress(1000);
                    distance_meter = "3Km";
                    distance = 3000;
                    zoom = 12.5f;
                }

                Toast.makeText(MapActivity.this, distance_meter, Toast.LENGTH_SHORT).show();
                btn_map_scope.setText(distance_meter);

                if(circle != null) {
                    circle.remove();
                }
                circle = mMap.addCircle(createCircle(distance, refresh));

                selectMapitems(currentLocation.getLatitude(), currentLocation.getLongitude(), String.valueOf(distance-100));          // 맵 중심 -> 현재위치로 수정

                MAP_IDLE_STATUS = false;

                mMap.moveCamera(CameraUpdateFactory.newLatLng(refresh));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));

                lay_map_scope.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Btn_map_scope :
                lay_map_scope.setVisibility(View.VISIBLE);

                if(btn_search_center.getVisibility() == View.VISIBLE) {
                    btn_search_center.setVisibility(View.GONE);
                }
                break;

            case R.id.Btn_my_location :
                if(MainActivity.gpsutil.isGetLocation()) {

                    currentLocation = MainActivity.gpsutil.location;

                    if(currentLocation.getLatitude() == 0.0) {
                        Toast.makeText(MapActivity.this, "위치를 잡지 못 했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        LatLng currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                        MAP_IDLE_STATUS = false;

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

                        sb_map_distance.setProgress(250);
                        tx_map_distance.setText("500m");
                        btn_map_scope.setText("500m");

                        selectMapitems(currentLocation.getLatitude(), currentLocation.getLongitude(), "400");

                        if(circle != null) {
                            circle.remove();
                        }
                        circle = mMap.addCircle(createCircle(500, currentPosition));

                        myLocation = mMap.addMarker(new MarkerOptions()
                                .position(currentPosition).title("현재위치")
                                .icon(BitmapDescriptorFactory.fromBitmap(createMyMarker(R.drawable.marker_mylocation))));
                    }
                }
                else {
                    Toast.makeText(MapActivity.this, "위치 설정을 켜주세요.", Toast.LENGTH_SHORT).show();
                    MainActivity.gpsutil.getLocation();
                }
                break;

            case R.id.Btn_search_center :

                if(circle != null) {
                    circle.remove();
                }

                if(myLocation != null) {
                    myLocation.remove();
                }

                sb_map_distance.setProgress(500);
                tx_map_distance.setText("1Km");
                btn_map_scope.setText("1Km");

                LatLng center = mMap.getCameraPosition().target;

                selectMapitems(center.latitude, center.longitude, "900");
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // InfoActivity에서 위치 버튼으로 왔을경우 코드

        if(from_activity.equals("MAIN")) {
            LatLng position;
            MarkerOptions markerOptions = new MarkerOptions();

            if((MainActivity.gpsutil.location == null) || (MainActivity.gpsutil.location.getLongitude() == 0.0)) {
                MainActivity.gpsutil.getLocation();
            }

            if (MainActivity.gpsutil.location != null) {

                position = new LatLng(MainActivity.gpsutil.location.getLatitude(), MainActivity.gpsutil.location.getLongitude());
                markerOptions.title("현재위치");
                markerOptions.snippet("나라고!");

                circle = mMap.addCircle(createCircle(500, position));

            }
            else {                  // 현재 위치를 못잡을 경우
                position = new LatLng(37.56, 126.97);
                markerOptions.title("서울");
                markerOptions.snippet("한국의 수도");
            }

            markerOptions.position(position);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createMyMarker(R.drawable.marker_mylocation)));

            myLocation = mMap.addMarker(markerOptions);
            selectMapitems(MainActivity.gpsutil.location.getLatitude(), MainActivity.gpsutil.location.getLongitude(), "400");

            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        }
        else {
            if(gym_name != null) {
                setGyminMap(gym_name);
            }
        }

        mMap.getUiSettings().setMapToolbarEnabled(false);   // 툴바 제거 (길찾기!)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));  // 줌 레벨
        // Map Listnerer
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(int i = 0 ; i < mapItems.length ; i++) {
            if(marker.getTitle().equals(mapItems[i].getGym_name())) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        if(mViewPager.getVisibility() == View.GONE) {
            mViewPager.setVisibility(View.VISIBLE);
        }

        if(btn_search_center.getVisibility() == View.VISIBLE) {
            btn_search_center.setVisibility(View.GONE);
        }
        return false;
    }

    public Bitmap createMarker(int drawable_id) {

        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(drawable_id);
        Bitmap tempBitmap = drawable.getBitmap();
        Bitmap Marker_Img = Bitmap.createScaledBitmap(tempBitmap, 50, 70, false);

        return Marker_Img;
    }

    public Bitmap createMyMarker(int drawable_id) {

        BitmapDrawable drawable = (BitmapDrawable)getResources().getDrawable(drawable_id);
        Bitmap tempBitmap = drawable.getBitmap();
        Bitmap Marker_Img = Bitmap.createScaledBitmap(tempBitmap, 64, 90, false);

        return Marker_Img;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(prevMarker != null) {
            prevMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_non_choice)));
            prevMarker.hideInfoWindow();
            prevMarker = null;
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            MAP_IDLE_STATUS = true;
            Log.d("171129", "TRUE");
        }
        if(reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            MAP_IDLE_STATUS = false;
            Log.d("171129", "FALSE");
        }
    }

    @Override
    public void onCameraIdle() {

        if(MAP_IDLE_STATUS) {
            if(mViewPager.getVisibility() == View.VISIBLE) {
                mViewPager.setVisibility(View.GONE);
            }

            if(btn_search_center.getVisibility() == View.GONE) {
                btn_search_center.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(lay_map_scope.getVisibility() == View.VISIBLE) {
            lay_map_scope.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }

    public CircleOptions createCircle(double radius, LatLng position) {
        // Circle~~
        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .radius(radius)
                .strokeColor(Color.argb(255, 102, 102, 255))
                .strokeWidth(5)
                .fillColor(Color.argb(128, 102, 102, 255));

        return circleOptions;
    }

    private void selectMapitems(double lat, double lng, String radius) {

        NetworkTask.selectGymMap(String.valueOf(lat), String.valueOf(lng), radius, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                Gson gson = new Gson();

                if(json.equals("NaN")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();

                            if(mViewPager.getVisibility() == View.VISIBLE) {
                                mViewPager.setVisibility(View.GONE);
                            }
                           if(btn_search_center.getVisibility() == View.GONE) {
                               btn_search_center.setVisibility(View.VISIBLE);
                           }

                            circle = null;
                            myLocation = null;

                            if(mapMarkers == null){
                                mapMarkers = new ArrayList<>();
                            }
                            else {
                                if(mapMarkers.size() > 0) {
                                    for(Marker m : mapMarkers) {
                                        m.remove();
                                    }
                                    mapMarkers.clear();
                                }
                            }

                            Toast.makeText(MapActivity.this, "범위 내에 가맹점이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    mapItems = gson.fromJson(json, GymMapinfo[].class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mAdapter = new ViewPagerAdapter(MapActivity.this, MapActivity.this, getLayoutInflater(), mapItems);

                            mViewPager.setAdapter(mAdapter);
                            mViewPager.setClipToPadding(false);
                            mViewPager.setPadding(50, 0, 50, 0);
                            mViewPager.setPageMargin(30);
                            mViewPager.setVisibility(View.VISIBLE);

                            if(mapMarkers == null) {
                                mapMarkers = new ArrayList<>();
                            }
                            else {
                                if(mapMarkers.size() > 0)
                                {
                                    for(Marker m : mapMarkers) {
                                        m.remove();
                                    }
                                    mapMarkers.clear();
                                }
                            }

                            for(GymMapinfo i : mapItems) {
                                mapMarkers.add(mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(i.getGym_lat()), Double.valueOf(i.getGym_lng())))
                                        .icon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_non_choice)))
                                        .title(i.getGym_name())
                                ));
                            }

                            mapMarkers.get(0).setIcon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_choice)));
                            mapMarkers.get(0).showInfoWindow();
                            prevMarker = mapMarkers.get(0);

                            if(mViewPager.getVisibility() == View.GONE) {
                                mViewPager.setVisibility(View.VISIBLE);
                            }

                            if(btn_search_center.getVisibility() == View.VISIBLE) {
                                btn_search_center.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void setGyminMap(String gym_name) {
        NetworkTask.selectInfoMap(gym_name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();

                Gson gson = new Gson();

                final GymMapinfo item = gson.fromJson(json, GymMapinfo.class);
                GymMapinfo[] v_pager_item = {item};

                mAdapter = new ViewPagerAdapter(MapActivity.this, MapActivity.this, getLayoutInflater(), v_pager_item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setAdapter(mAdapter);
                        mViewPager.setClipToPadding(false);
                        mViewPager.setPadding(50, 0, 50, 0);
                        mViewPager.setPageMargin(30);

                        if(mapMarkers == null) {
                            mapMarkers = new ArrayList<>();
                        }
                        else {
                            if(mapMarkers.size() > 0) {
                                mapMarkers.clear();
                            }
                        }

                        mapMarkers.add(mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.valueOf(item.getGym_lat()), Double.valueOf(item.getGym_lng())))
                                .icon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_non_choice)))
                                .title(item.getGym_name())));

                        mapMarkers.get(0).setIcon(BitmapDescriptorFactory.fromBitmap(createMarker(R.drawable.marker_choice)));
                        mapMarkers.get(0).showInfoWindow();
                        prevMarker = mapMarkers.get(0);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(item.getGym_lat()), Double.valueOf(item.getGym_lng()))));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    }
                });
            }
        });
    }
}
