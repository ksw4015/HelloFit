package com.example.zealo.tapandfragment.Ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.Adapter.MyGridAdapter;
import com.example.zealo.tapandfragment.GalleryActivity;
import com.example.zealo.tapandfragment.ListActivity;
import com.example.zealo.tapandfragment.MainActivity;
import com.example.zealo.tapandfragment.Models.GymListItem;
import com.example.zealo.tapandfragment.Models.UserInfomation;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PictureActivity;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.ProfileActivity;
import com.example.zealo.tapandfragment.ProfileReviseActivity;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-25.
 */

public class ProfileFragment extends Fragment {

    public static int PROFILEFRAGMENT_CODE = 301;

    ImageView iv_my_avatar;
    GridView profile_gridview;

    TextView tx_profile_gym_num, tx_profile_friend_num;
    TextView tx_profile_my_name, tx_profile_my_text;
    TextView tx_profile_write_num;
    Button btn_profile_revise;

    public final static String fragment_name_profile = "PROFILE";

    RelativeLayout lay_btn_upload;

    String my_ad_id;
    private UserProfile my;
    private UserInfomation[] my_info;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        my_ad_id = SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.Google_Play_ID), "NaN");

        iv_my_avatar = (ImageView)view.findViewById(R.id.Iv_my_avatar);

        tx_profile_friend_num = (TextView)view.findViewById(R.id.Tx_profile_friend_num);
        tx_profile_gym_num = (TextView)view.findViewById(R.id.Tx_profile_gym_num);

        tx_profile_my_name = (TextView)view.findViewById(R.id.Tx_profile_my_name);
        tx_profile_my_text = (TextView)view.findViewById(R.id.Tx_profile_my_text);

        tx_profile_write_num = (TextView)view.findViewById(R.id.Tx_profile_write_num);

        btn_profile_revise = (Button)view.findViewById(R.id.Btn_profile_revise);

        NetworkTask.selectMyProfile(my_ad_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("171121", json);

                Gson gson = new Gson();

                my = gson.fromJson(json, UserProfile.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tx_profile_my_name.setText(my.getUser_nickname());

                        if(my.getUser_desc() == null) {
                            tx_profile_my_text.setText("상태메시지를 입력해 주세요.");
                        }
                        else {
                            tx_profile_my_text.setText(my.getUser_desc());
                        }

                        if(my.getUser_profile_img() == null) {
                            Glide.with(getContext())
                                    .load(R.drawable.default_avatar)
                                    .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                                    .into(iv_my_avatar);
                        }
                        else {
                            Glide.with(getContext())
                                    .load(FriendRecyclerAdapter.PROFILE_IMG_URL + my.getUser_profile_img())
                                    .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                                    .into(iv_my_avatar);
                        }
                    }
                });
            }
        });

        NetworkTask.selectFavoriteList(my_ad_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();

                if(!json.equals("NaN")) {
                    JsonObject object = new JsonParser().parse(json).getAsJsonObject();

                    Gson gson = new Gson();

                    final GymListItem[] gymlist;
                    final UserProfile[] users;

                    if(object.get("gym") != null) {
                        JsonArray gym = object.get("gym").getAsJsonArray();
                        gymlist = gson.fromJson(gym, GymListItem[].class);
                        setTextView(tx_profile_gym_num, gymlist.length);
                        // 즐겨찾는 센터
                        tx_profile_gym_num.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ListActivity.class);
                                intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.gym));
                                intent.putExtra("USER_GYMS", gymlist);
                                intent.putExtra("USER_NICK_NAME", my.getUser_nickname());
                                intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_activity));
                                startActivity(intent);
                            }
                        });
                    }

                    if (object.get("friend") != null) {
                        JsonArray friend = object.get("friend").getAsJsonArray();
                        users = gson.fromJson(friend, UserProfile[].class);
                        setTextView(tx_profile_friend_num, users.length);
                        // 친구
                        tx_profile_friend_num.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ListActivity.class);
                                intent.putExtra(getResources().getString(R.string.list_type), getResources().getString(R.string.friend));
                                intent.putExtra("USER_FRIEDS", users);
                                intent.putExtra("USER_NICK_NAME", my.getUser_nickname());
                                intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_activity));
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });

        btn_profile_revise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileReviseActivity.class);
                if (my.getUser_profile_img() == null) {
                    intent.putExtra("TEST_IMG", R.drawable.default_avatar);         // 현재 프로필 이미지
                }
                else {
                    intent.putExtra("USER_PROFILE", FriendRecyclerAdapter.PROFILE_IMG_URL + my.getUser_profile_img());
                }
                intent.putExtra("USER_NICKNAME", tx_profile_my_name.getText().toString());
                intent.putExtra("USER_STATUS", tx_profile_my_text.getText().toString());

                getActivity().startActivityForResult(intent, PROFILEFRAGMENT_CODE);
            }
        });

        lay_btn_upload = (RelativeLayout)view.findViewById(R.id.Lay_btn_upload);

        profile_gridview = (GridView)view.findViewById(R.id.profile_gridView);

        NetworkTask.selectUserImg(SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.Google_Play_ID), "NaN")
                , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();

                if(json.equals("No Data")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "이미지가 없어용", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {

                    Gson gson = new Gson();
                    my_info = gson.fromJson(json, UserInfomation[].class);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final MyGridAdapter myGridAdapter = new MyGridAdapter(DeviceUtil.getDevicewidth(getActivity()), null, my_info, fragment_name_profile);
                            profile_gridview.setAdapter(myGridAdapter);
                            tx_profile_write_num.setText(String.valueOf(my_info.length));

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myGridAdapter.notifyDataSetChanged();  // 그리드 아이템중에 이미지 파일이 100kb 이상 되면 로딩시간도 있고 한번 더 눌러야 보임
                                }
                            }, 500);
                        }
                    });
                }
            }
        });

        profile_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), PictureActivity.class);
                intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_fragment));
                if (my.getUser_profile_img() != null) {
                    intent.putExtra("USER_PROFILE", FriendRecyclerAdapter.PROFILE_IMG_URL + my.getUser_profile_img());
                }
                intent.putExtra("IMG_DESC", my_info[position]);
                intent.putExtra("USER_NICK", my.getUser_nickname());
                intent.putExtra(getResources().getString(R.string.class_name), fragment_name_profile);
                getContext().startActivity(intent);
            }
        });

        lay_btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 저장공간 엑세스 퍼미션 필요
                boolean PERMISSION_CHECK = false;

                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    PERMISSION_CHECK = true;
                }

                if(!PERMISSION_CHECK) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                }
                else {
                    Intent intent = new Intent(getContext(), GalleryActivity.class);
                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_fragment));
                    getContext().startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).toggleButton(3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PROFILEFRAGMENT_CODE) {

            if(resultCode == Activity.RESULT_OK){

                String desc = data.getStringExtra("REVISED_TEXT");
                String nick = data.getStringExtra("REVISED_NAME");

                if(data.getStringExtra("IMAGE_FILE") != null) {
                    Glide.with(getContext())
                            .load(data.getStringExtra("IMAGE_FILE"))
                            .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                            .into(iv_my_avatar);
                }

                tx_profile_my_text.setText(desc);
                tx_profile_my_name.setText(nick);

                my.setUser_desc(desc);
                my.setUser_nickname(nick);
            }
        }
    }

    private void setTextView(final TextView tx, final int num) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tx.setText(String.valueOf(num));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 2) {

            boolean PERMISSION_CHECKED = true;

            for(int grantResult : grantResults) {
                if(grantResult == PackageManager.PERMISSION_DENIED) {
                    PERMISSION_CHECKED = false;
                    break;
                }
            }

            if(PERMISSION_CHECKED) {
                Intent intent = new Intent(getContext(), GalleryActivity.class);
                intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_fragment));
                getContext().startActivity(intent);
            }
            else {
                Toast.makeText(getContext(), "갤러리를 실행하려면 저장공간 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
