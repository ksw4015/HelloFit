package com.example.zealo.tapandfragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.Ui.ChatFragment;
import com.example.zealo.tapandfragment.Ui.FavoriteFragment;
import com.example.zealo.tapandfragment.Ui.HomeFragment;
import com.example.zealo.tapandfragment.Ui.ProfileFragment;
import com.example.zealo.tapandfragment.Util.GPSUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static boolean main_page_loaded = false;

    public Button btn_map;
    public ImageView btn_tab1, btn_tab2, btn_tab3, btn_tab4;

    public LinearLayout lay_btn_1, lay_btn_2, lay_btn_3, lay_btn_4;

    private final int FRAGMENT_HOME = 1;
    private final int FRAGMENT_CHAT = 2;
    private final int FRAGMENT_FAVORITE = 3;
    private final int FRAGMENT_PROFILE = 4;

    private Toolbar mToolBar;
    private ActionBar actionBar;

    public static GPSUtil gpsutil = null;
    private Thread thread;

    private ProgressDialog mDialog;

    private EditText edt_search;
    private Button btn_search;
    private HomeFragment f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("171122", SharedPreferenceManager.getPreference(MainActivity.this, getResources().getString(R.string.user_email), "NaN"));

        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("로딩중...");
        mDialog.setCancelable(false);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(gpsutil.location.getLatitude() != 0.0) {
                        ready.sendEmptyMessage(0);
                        break;
                    }
                }
            }
        });

        boolean PERMISSION_CHECKED = true;

        // 퍼미션 체크 후 거부되어있으면 false값
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            PERMISSION_CHECKED = false;
        }

        if(!PERMISSION_CHECKED) {   // 퍼미션 거부되어있는경우 권한확인 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);   // 위치 퍼미션 체크 리퀘스트 코드 1
        }
        else {   // 위치 권환 확인되면 GPSUtil 클래스 생성(위치서비스)
            gpsutil = new GPSUtil(MainActivity.this);

            if(gpsutil.location == null) {
                showSettingsAlert();
            }
            else {
                mDialog.show();
                thread.start();
            }
        }

        btn_tab1 = (ImageView)findViewById(R.id.btn_tab1);
        btn_tab2 = (ImageView)findViewById(R.id.btn_tab2);
        btn_tab3 = (ImageView)findViewById(R.id.btn_tab3);
        btn_tab4 = (ImageView)findViewById(R.id.btn_tab4);

        lay_btn_1 = (LinearLayout)findViewById(R.id.Lay_btn_1);
        lay_btn_1.setOnClickListener(this);
        lay_btn_2 = (LinearLayout)findViewById(R.id.Lay_btn_2);
        lay_btn_2.setOnClickListener(this);
        lay_btn_3 = (LinearLayout)findViewById(R.id.Lay_btn_3);
        lay_btn_3.setOnClickListener(this);
        lay_btn_4 = (LinearLayout)findViewById(R.id.Lay_btn_4);
        lay_btn_4.setOnClickListener(this);
        btn_map = (Button)findViewById(R.id.Btn_map);
        btn_map.setOnClickListener(this);

        mToolBar = (Toolbar)findViewById(R.id.m_toolbar);
        setSupportActionBar(mToolBar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        edt_search = (EditText)findViewById(R.id.Edt_filter);
        btn_search = (Button)findViewById(R.id.Btn_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_search.getVisibility() == View.GONE) {
                    edt_search.setVisibility(View.VISIBLE);
                }
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f.mainRecyclerAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.Lay_btn_1 :
                changeFragment(FRAGMENT_HOME);
                break;

            case R.id.Lay_btn_2 :
                changeFragment(FRAGMENT_CHAT);
                break;

            case R.id.Lay_btn_3 :
                changeFragment(FRAGMENT_FAVORITE);
                break;

            case R.id.Lay_btn_4 :
                changeFragment(FRAGMENT_PROFILE);
                break;

            case R.id.Btn_map :
                goMap();
                break;
        }

    }

    public void toggleButton(int fragment) {        // for문 사용을 안하게끔 수정하면 좋을듯 (현재 프래그먼트번호와 이전 프래그먼트번호 사용 변수하나 추가해서)
        LinearLayout[] all_btns = {lay_btn_1, lay_btn_2, lay_btn_3, lay_btn_4};
        for(int i = 0 ; i < all_btns.length ; i++) {
            if(fragment == i) {
                all_btns[i].setAlpha(1);
            }
            else {
                all_btns[i].setAlpha(0.5f);
            }
        }
    }

    public void changeFragment(int fragment_Num) {

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        switch (fragment_Num) {

            case 1 :
                fm.popBackStack("HOME", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                HomeFragment homeFragment = new HomeFragment();
                transaction.replace(R.id.m_container, homeFragment, "HOME");   // pop으로 이전 프래그먼트 전부 제거해도 됨
                transaction.addToBackStack("HOME");
                transaction.commit();
                actionBarShow();
                f = homeFragment;
                break;

            case 2 :
                fm.popBackStack("CHAT", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ChatFragment chatFragment = new ChatFragment();
                transaction.replace(R.id.m_container, chatFragment, "CHAT");
                transaction.addToBackStack("CHAT");
                transaction.commit();
                actionBarHide();
                break;

            case 3 :
                fm.popBackStack("FAVORITE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FavoriteFragment favoriteFragment = new FavoriteFragment();
                transaction.replace(R.id.m_container, favoriteFragment, "FAVORITE");
                transaction.addToBackStack("FAVORITE");
                transaction.commit();
                actionBarHide();
                break;

            case 4 :
                fm.popBackStack("PROFILE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ProfileFragment profileFragment = new ProfileFragment();
                transaction.replace(R.id.m_container, profileFragment, "PROFILE");
                transaction.addToBackStack("PROFILE");
                transaction.commit();
                actionBarHide();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if(getSupportFragmentManager().getBackStackEntryCount() == 1) {  // HomeFragment만 있을 경우 앱 종료
                if(edt_search.getVisibility() == View.VISIBLE) {
                    edt_search.setVisibility(View.GONE);
                }
                else {
                    finish();
                }
            }
            else if(getSupportFragmentManager().getBackStackEntryCount() == 2) {
                getSupportFragmentManager().popBackStack();
                actionBarShow();
            }
            else {
                getSupportFragmentManager().popBackStack();
            }
        }
        else {
            super.onBackPressed();
        }
    }

    //
    private void actionBarShow() {
        actionBar.show();
        btn_map.setVisibility(View.VISIBLE);
        btn_search.setVisibility(View.VISIBLE);
        edt_search.setVisibility(View.VISIBLE);
    }

    private void actionBarHide() {
        actionBar.hide();
        btn_map.setVisibility(View.GONE);
        btn_search.setVisibility(View.GONE);
        edt_search.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1 :   // 메인액티비티 위치 퍼미션
                if(grantResults.length > 0 ) {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        gpsutil = new GPSUtil(MainActivity.this);

                        if(gpsutil.location == null) {
                            showSettingsAlert();
                        }
                        else {
                            thread.start();
                            mDialog.show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "위치 권환을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            case 2 :    // 프로필 프래그먼트에서 외부저장소 접근 퍼미션
                // 액티비티에서 프래그먼트로 값전달시 프래그먼트 직접참조
                Fragment resultfragment = getSupportFragmentManager().findFragmentByTag("PROFILE");
                resultfragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    public void goMap() {

        Context context = MainActivity.this;

        // 네트워크 연결상태확인
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // 위경도값 확인
        if (gpsutil.location == null || gpsutil.location.getLatitude() == 0.0) {
            gpsutil.getLocation();
        }

        if(gpsutil.location != null || gpsutil.location.getLatitude() != 0.0) {
            if(mobile.isConnected() || wifi.isConnected()) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("FROM_ACTIVITY", "MAIN");
                startActivity(intent);
            }
            else {
                Toast.makeText(context, "네트워크 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "위치정보를 가져오지 못했습니다.\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 101) {      // Gps 다이얼로그

            gpsutil.getLocation();
            Log.d("171229", "위치 확인");

            // 위치 권한을 키고 1초정도 지나야 현재 위경도값을 받아오기때문에 추가
            if(gpsutil.location == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("171229", "위치 재확인");
                        gpsutil.getLocation();
                    }
                }, 300);
            }

            new AsyncTask<Void, Void, Void>(){
                ProgressDialog gpsDialog;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    gpsDialog = new ProgressDialog(MainActivity.this);
                    gpsDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    gpsDialog.setMessage("위치확인 중...");
                    gpsDialog.setCancelable(false);
                    gpsDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    while (true) {
                        if(gpsutil.location != null) {
                            if(gpsutil.location.getLatitude() != 0.0) {  // 위치 기능 사용직후에는 0.0값이라서 추가
                                Log.d("171229", "Dismiss");
                                gpsDialog.dismiss();
                                break;
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Toast.makeText(MainActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                    changeFragment(FRAGMENT_HOME);
                }
            }.execute();

            /*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(gpsutil.location == null) {
                        showSettingsAlert();
                    }
                    else {
                        mDialog.show();
                        thread.start();
                    }
                }
            }, 300);
            */
        }
        else if(requestCode == ChatFragment.CHATFRAGMENT_CODE) {  // 채팅 액티비티
            Fragment resultfragment = getSupportFragmentManager().findFragmentByTag("CHAT");       // 액티비티에서 프래그먼트를 직접 참조후 프래그먼트의 onActivityResult메소드로 변수들 넘겨줌
            resultfragment.onActivityResult(requestCode, resultCode, data);
        }
        else if(requestCode == ProfileFragment.PROFILEFRAGMENT_CODE){
            Fragment resultfragment = getSupportFragmentManager().findFragmentByTag("PROFILE");
            resultfragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    Handler ready = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                mDialog.dismiss();
                Toast.makeText(MainActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                changeFragment(FRAGMENT_HOME);
            }
        }
    };

    public String[] getMainLocation() {
        String[] location= {String.valueOf(gpsutil.location.getLatitude()), String.valueOf(gpsutil.location.getLongitude())};
        return location;
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);  // 빌더 패턴

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");
                // OK 를 누르게 되면 설정창으로 이동합니다.
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        gpsutil = new GPSUtil(MainActivity.this);
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);      // 암시적 인텐트
                        startActivityForResult(intent, 101);
                        dialog.dismiss();
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "위치설정을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                finish();
            }
        });

        alertDialog.show();
    }

}
