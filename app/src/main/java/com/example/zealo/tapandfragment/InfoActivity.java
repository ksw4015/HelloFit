package com.example.zealo.tapandfragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.Models.GymInfomation;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.Util.CustomDialog;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-11-02.
 */

public class InfoActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String tab_name_clientlist = "Client List";
    private final String GYM_IMAGES_URL_THUMBNAIL = NetworkTask.BASE_URL + "hellofit/gym/thumbnails/";
    private final String GYM_IMAGES_URL = NetworkTask.BASE_URL + "hellofit/gym/";

    private ArrayList<String> go_picture_img;

    private ImageView iv_info_gym_img, btn_info_like;
    //private ImageView[] iv_gym;
    private TextView tx_gym_phone, tx_info_gym_address, tx_gym_time, tx_gym_holiday
            ,tx_info_favorite_num, tx_info_gym_rate;

    private RecyclerView gym_client_list;
    private FriendRecyclerAdapter cAdapter;

    private String gym_name = null;
    private String GYM_IMG_URL = null;
    private String gym_rate = null;
    private String gym_like = null;
    private String gym_star = null;

    private LinearLayout btn_info_favorite, btn_info_map, btn_info_call;
    private RelativeLayout lay_img_6_container;

    private String my_gym_rate = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        gym_name = getIntent().getStringExtra("GYM_NAME");
        gym_like = getIntent().getStringExtra("GYM_LIKE");
        gym_rate = getIntent().getStringExtra("GYM_RATE");
        gym_star = getIntent().getStringExtra("GYM_STAR");

        GYM_IMG_URL = getIntent().getStringExtra("GYM_IMG");

        go_picture_img = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        if(gym_name != null) {
            actionBar.setTitle(gym_name);
        }
        else{
            actionBar.setTitle("가맹점 명");
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        TabHost tabHost = (TabHost)findViewById(R.id.m_tabhost_gym_info);
        tabHost.setup();

        TabHost.TabSpec tab_favorite_gym = tabHost.newTabSpec("Infomation");
        tab_favorite_gym.setContent(R.id.content_info_gym);
        tab_favorite_gym.setIndicator("정보");
        tabHost.addTab(tab_favorite_gym);

        TabHost.TabSpec tab_favorite_friend = tabHost.newTabSpec("Client List");
        tab_favorite_friend.setContent(R.id.content_info_gym_client);
        tab_favorite_friend.setIndicator("회원목록");
        tabHost.addTab(tab_favorite_friend);

        final int width = DeviceUtil.getDevicewidth(InfoActivity.this);

        iv_info_gym_img = (ImageView)findViewById(R.id.Iv_info_gym_img);
        if(GYM_IMG_URL != null) {
            Glide.with(InfoActivity.this).load(GYM_IMG_URL).override(width, width/2).centerCrop().into(iv_info_gym_img);
        }

        btn_info_map = (LinearLayout)findViewById(R.id.Btn_info_map);
        btn_info_map.setOnClickListener(this);
        btn_info_favorite = (LinearLayout)findViewById(R.id.Btn_info_favorite);
        btn_info_favorite.setOnClickListener(this);
        btn_info_call = (LinearLayout)findViewById(R.id.Btn_info_call);
        btn_info_call.setOnClickListener(this);
        tx_gym_phone = (TextView)findViewById(R.id.Tx_gym_phone);
        tx_info_gym_address = (TextView)findViewById(R.id.Tx_info_gym_address);
        tx_gym_holiday = (TextView)findViewById(R.id.Tx_gym_holiday);
        tx_gym_time = (TextView)findViewById(R.id.Tx_gym_time);
        lay_img_6_container = (RelativeLayout)findViewById(R.id.Lay_img_6_container);
        btn_info_like = (ImageView)findViewById(R.id.Btn_info_like);
        btn_info_like.setOnClickListener(this);
        if(gym_star!=null) {
            if(gym_star.equals("YES")) {
                btn_info_like.setImageResource(R.drawable.ic_star_white_36dp);
            }
        }

        tx_info_gym_rate = (TextView)findViewById(R.id.Tx_info_gym_rate);
        if(gym_rate != null) {
            tx_info_gym_rate.setText(gym_rate);
        }
        tx_info_favorite_num =(TextView)findViewById(R.id.Tx_info_favorite_num);
        if(gym_like != null) {
            tx_info_favorite_num.setText(gym_like);
        }

        NetworkTask.checkingMyGym(SharedPreferenceManager.getPreference(InfoActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN"), gym_name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String chk = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(chk.equals("2")) {
                            btn_info_like.setImageResource(R.drawable.ic_star_white_36dp);
                            btn_info_like.setTag("YES");
                        }
                        else {
                            btn_info_like.setImageResource(R.drawable.ic_star_border_white_38dp);
                            btn_info_like.setTag("NO");
                        }
                    }
                });
            }
        });

        gym_client_list = (RecyclerView)findViewById(R.id.gym_client_List);
        LinearLayoutManager manager = new LinearLayoutManager(InfoActivity.this);
        gym_client_list.setLayoutManager(manager);
        gym_client_list.setHasFixedSize(true);

        if(gym_name != null) {
            NetworkTask.selectInfoItem(gym_name, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonOb = response.body().string();

                    Log.d("171116", jsonOb);

                    Gson gson = new Gson();

                    JsonObject object = new JsonParser().parse(jsonOb).getAsJsonObject();
                    JsonArray jsonArray = object.get("gym_img").getAsJsonArray();
                    JsonObject info = object.getAsJsonObject("gym_info");

                    final GymInfomation gymInfo = gson.fromJson(info, GymInfomation.class);
                    Log.d("171116", gymInfo.getGym_phone());

                    final ArrayList<String> gym_img_arr = new ArrayList<String>();
                    for(int i = 0 ; i < jsonArray.size() ; i++) {
                        GymImage gym_img = gson.fromJson(jsonArray.get(i), GymImage.class);
                        gym_img_arr.add(gym_img.getGym_img());
                        Log.d("171116", gym_img_arr.get(i));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tx_gym_holiday.setText(gymInfo.getGym_holiday());
                            tx_gym_time.setText(gymInfo.getGym_open());
                            tx_info_gym_address.setText(gymInfo.getGym_address());
                            tx_gym_phone.setText(gymInfo.getGym_phone());

                            ImageView[] iv_gym;
                            iv_gym = new ImageView[6];

                            int[] iv_gym_id = {R.id.Iv_info_gym_1, R.id.Iv_info_gym_2, R.id.Iv_info_gym_3,
                                    R.id.Iv_info_gym_4, R.id.Iv_info_gym_5, R.id.Iv_info_gym_6};

                            if(gym_img_arr.size() > iv_gym.length) {        // 그림이 6개 이상일 경우
                                for(int i = 0; i < iv_gym.length ; i++) {
                                    go_picture_img.add(GYM_IMAGES_URL + gym_img_arr.get(i));
                                    iv_gym[i] = (ImageView)findViewById(iv_gym_id[i]);
                                    iv_gym[i].setOnClickListener(InfoActivity.this);
                                    Glide.with(InfoActivity.this).load(GYM_IMAGES_URL_THUMBNAIL + gym_img_arr.get(i)).override(width/3, width/3).centerCrop().into(iv_gym[i]);
                                }
                                lay_img_6_container.setVisibility(View.VISIBLE);
                            }
                            else {
                                for(int i = 0; i < gym_img_arr.size() ; i++) {
                                    go_picture_img.add(GYM_IMAGES_URL + gym_img_arr.get(i));
                                    iv_gym[i] = (ImageView)findViewById(iv_gym_id[i]);
                                    iv_gym[i].setOnClickListener(InfoActivity.this);
                                    Glide.with(InfoActivity.this).load(GYM_IMAGES_URL_THUMBNAIL + gym_img_arr.get(i)).override(width/3, width/3).centerCrop().into(iv_gym[i]);
                                }
                                lay_img_6_container.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });

            NetworkTask.selectGymClient(gym_name, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // 회원목록
                    String json = response.body().string();
                    Log.d("171117", json);

                    Gson gson = new Gson();

                    final UserProfile[] users = gson.fromJson(json, UserProfile[].class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cAdapter = new FriendRecyclerAdapter(InfoActivity.this, InfoActivity.this, tab_name_clientlist, users);
                            gym_client_list.setAdapter(cAdapter);
                        }
                    });

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Iv_info_gym_1 :
                goPicture(0);
                break;
            case R.id.Iv_info_gym_2 :
                goPicture(1);
                break;
            case R.id.Iv_info_gym_3 :
                goPicture(2);
                break;
            case R.id.Iv_info_gym_4 :
                goPicture(3);
                break;
            case R.id.Iv_info_gym_5 :
                goPicture(4);
                break;
            case R.id.Iv_info_gym_6 :
                Toast.makeText(InfoActivity.this, "전체보기 액티비티", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Btn_info_map :
                if(MapActivity.mapActivity != null) {
                    MapActivity.mapActivity.finish();
                    MapActivity.mapActivity = null;
                }
                Intent intent = new Intent(InfoActivity.this, MapActivity.class);
                intent.putExtra("FROM_ACTIVITY","INFO");
                intent.putExtra("GYM_NAME", gym_name);
                startActivity(intent);
                break;

            case R.id.Btn_info_favorite :
                NetworkTask.selectMyRate(SharedPreferenceManager.getPreference(InfoActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN"), gym_name, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        my_gym_rate = response.body().string();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(my_gym_rate != null) {
                                    AlertDialog dialog = CustomDialog.RatingDialog(InfoActivity.this, InfoActivity.this, gym_name, my_gym_rate);
                                    dialog.show();
                                }
                                else {

                                    Toast.makeText(InfoActivity.this, "다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
                break;

            case R.id.Btn_info_call : // 전화걸기
                Intent call = new Intent(Intent.ACTION_CALL);     // 암시적 인텐트
                call.setData(Uri.parse("tel:"+ tx_gym_phone.getText().toString()));

                try {
                    startActivity(call);
                }
                catch (SecurityException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.Btn_info_like :
                // Main어댑터 클래스에서 별버튼 눌렀을때 작동하는 코드 작성하면됨
                if(btn_info_like.getTag().equals("YES")) {
                    btn_info_like.setTag("NO");
                    Toast.makeText(InfoActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    btn_info_like.setImageResource(R.drawable.ic_star_border_white_38dp);
                    NetworkTask.addGym(0, SharedPreferenceManager.getPreference(InfoActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN"), gym_name);
                }
                else {
                    btn_info_like.setTag("YES");
                    Toast.makeText(InfoActivity.this, "즐겨찾기 추가", Toast.LENGTH_SHORT).show();
                    btn_info_like.setImageResource(R.drawable.ic_star_white_36dp);
                    NetworkTask.addGym(1, SharedPreferenceManager.getPreference(InfoActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN"), gym_name);
                }
                break;
        }
    }

    public void goPicture(int ImageViewNum) {           // 이미지 Url 주소
        Intent intent = new Intent(InfoActivity.this, PictureActivity.class);
        intent.putExtra("ID", go_picture_img.get(ImageViewNum));
        intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.info_activity));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GymImage {
        private String gym_img;

        public GymImage(String gym_img) {
            this.gym_img = gym_img;
        }

        public String getGym_img() {
            return gym_img;
        }
    }
}
