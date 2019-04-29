package com.example.zealo.tapandfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.Adapter.MyGridAdapter;
import com.example.zealo.tapandfragment.Models.GymListItem;
import com.example.zealo.tapandfragment.Models.UserInfomation;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Ui.ProfileFragment;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-11-07.
 */

public class ProfileActivity extends AppCompatActivity {

    TextView tx_profile_friend_gym_num;
    TextView tx_profile_friend_other_num;
    TextView tx_profile_friend_write_num;
    TextView tx_profile_friend_text;
    TextView tx_profile_friend_name;

    ImageView iv_friend_avatar;
    GridView profile_friend_gridview;

    Button btn_profile_add_friend;

    private UserProfile profile;
    public static String profile_activity_name = "ProfileActivity";

    private UserInfomation[] user_info;

    private String my_ad_id;
    private int code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        my_ad_id = SharedPreferenceManager.getPreference(ProfileActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN");

        profile = (UserProfile)getIntent().getSerializableExtra("USER");
        final int width = DeviceUtil.getDevicewidth(ProfileActivity.this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(profile.getUser_nickname());

        tx_profile_friend_gym_num = (TextView)findViewById(R.id.Tx_profile_friend_gym_num);
        tx_profile_friend_other_num = (TextView)findViewById(R.id.Tx_profile_friend_other_num);
        tx_profile_friend_write_num = (TextView)findViewById(R.id.Tx_profile_friend_write_num);
        tx_profile_friend_name = (TextView)findViewById(R.id.Tx_profile_friend_name);
        tx_profile_friend_text = (TextView)findViewById(R.id.Tx_profile_friend_text);

        profile_friend_gridview = (GridView)findViewById(R.id.profile_friend_gridView);
        iv_friend_avatar = (ImageView)findViewById(R.id.Iv_friend_avatar);

        btn_profile_add_friend = (Button)findViewById(R.id.Btn_profile_add_friend);

        if(profile != null) {

            setButtonText(btn_profile_add_friend, profile.getUser_ad_id());

            NetworkTask.selectUserImg(profile.getUser_ad_id(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    if(json.equals("No Data")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProfileActivity.this, "이미지가 없어용.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {

                        Gson gson = new Gson();
                        user_info = gson.fromJson(json, UserInfomation[].class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyGridAdapter pAdapter = new MyGridAdapter(width, null, user_info, ProfileActivity.profile_activity_name);
                                profile_friend_gridview.setAdapter(pAdapter);
                                tx_profile_friend_write_num.setText(String.valueOf(user_info.length));
                            }
                        });
                    }
                }
            });

            if(profile.getUser_profile_img()!=null) {
                Glide.with(ProfileActivity.this)
                        .load(FriendRecyclerAdapter.PROFILE_IMG_URL + profile.getUser_profile_img())
                        .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                        .into(iv_friend_avatar);
            }
            else {
                Glide.with(ProfileActivity.this)
                        .load(R.drawable.default_avatar)
                        .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                        .into(iv_friend_avatar);
            }

            tx_profile_friend_name.setText(profile.getUser_nickname());

            if(profile.getUser_desc() == null) {
                tx_profile_friend_text.setText("아직 상태메시지를 설정하지 않았습니다.");
            }
            else {
                tx_profile_friend_text.setText(profile.getUser_desc());
            }

            NetworkTask.selectFavoriteList(profile.getUser_ad_id(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    Log.d("171117", json);

                    if(!json.equals("NaN")) {
                        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

                        JsonArray gym = object.get("gym").getAsJsonArray();
                        JsonArray friend = object.get("friend").getAsJsonArray();

                        Gson gson = new Gson();

                        final GymListItem[] gymlist;
                        final UserProfile[] users;

                        if(gym != null) {
                            gymlist = gson.fromJson(gym, GymListItem[].class);
                            setTextView(tx_profile_friend_gym_num, gymlist.length);
                            // 즐겨찾는 센터
                            tx_profile_friend_gym_num.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, ListActivity.class);
                                    intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.gym));
                                    intent.putExtra("USER_GYMS", gymlist);
                                    intent.putExtra("USER_NICK_NAME", profile.getUser_nickname());
                                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_activity));
                                    startActivity(intent);
                                }
                            });
                        }

                        if (friend != null) {
                            users = gson.fromJson(friend, UserProfile[].class);
                            setTextView(tx_profile_friend_other_num, users.length);
                            // 친구
                            tx_profile_friend_other_num.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProfileActivity.this, ListActivity.class);
                                    intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.friend));
                                    intent.putExtra("USER_FRIEDS", users);
                                    intent.putExtra("USER_NICK_NAME", profile.getUser_nickname());
                                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_activity));
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }
            });
        }

        profile_friend_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProfileActivity.this, PictureActivity.class);
                if (profile.getUser_profile_img() != null) {
                    intent.putExtra("USER_PROFILE", FriendRecyclerAdapter.PROFILE_IMG_URL + profile.getUser_profile_img());
                }
                else {
                    intent.putExtra("TEST_IMG", R.drawable.default_avatar);
                }
                intent.putExtra("IMG_DESC", user_info[position]);
                intent.putExtra("USER_NICK",profile.getUser_nickname());
                intent.putExtra(getResources().getString(R.string.class_name), ProfileActivity.profile_activity_name);
                startActivity(intent);
            }
        });

        btn_profile_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btn_profile_add_friend.getText().toString().equals("친구 추가")) {
                    code = 1;
                }
                else {
                    code = 0;
                }

                NetworkTask.addFriend(code, my_ad_id, profile.getUser_ad_id(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(code == 1) {
                                    btn_profile_add_friend.setText("친구 삭제");
                                }
                                else {
                                    btn_profile_add_friend.setText("친구 추가");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void setTextView(final TextView tx, final int num) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tx.setText(String.valueOf(num));
            }
        });
    }

    private void setButtonText(final Button button, String friend_id) {
        NetworkTask.chekingFriend(my_ad_id, friend_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String msg = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!msg.equals("1")) {
                            button.setText("친구 삭제");
                        }
                    }
                });
            }
        });
    }
}
