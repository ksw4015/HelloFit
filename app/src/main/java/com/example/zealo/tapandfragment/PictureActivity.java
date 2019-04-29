package com.example.zealo.tapandfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.Models.UserInfomation;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by zealo on 2017-11-03.
 */

public class PictureActivity extends AppCompatActivity {

    private final String USER_IMG_URL = NetworkTask.BASE_URL + "hellofit/users/";

    ImageView iv_picture, iv_picture_profile;
    TextView tx_picture, tx_picture_nick;

    private UserInfomation img_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        String class_name = getIntent().getStringExtra(getResources().getString(R.string.class_name));
        img_info = (UserInfomation) getIntent().getSerializableExtra("IMG_DESC");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = #FFFFFF>사진</font>"));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        iv_picture = (ImageView)findViewById(R.id.Iv_picture);
        iv_picture_profile = (ImageView)findViewById(R.id.Iv_picture_profile);
        tx_picture_nick = (TextView)findViewById(R.id.Tx_picture_nick);
        tx_picture = (TextView)findViewById(R.id.Tx_picture);

        int width = DeviceUtil.getDevicewidth(PictureActivity.this);
        final String data = getIntent().getStringExtra("ID");             // 이미지 url로 변경

        if(data != null) {
            Glide.with(PictureActivity.this).load(data).override(width, width).centerCrop().into(iv_picture);
        }

        if (class_name != null) {
            if(!class_name.equals(getResources().getString(R.string.info_activity))) {
                if(class_name.equals(ProfileActivity.profile_activity_name)) {

                    Glide.with(PictureActivity.this)
                            .load(getIntent().getStringExtra("USER_PROFILE"))
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                            .into(iv_picture_profile);

                    Glide.with(PictureActivity.this)
                            .load(USER_IMG_URL + img_info.getUser_img())
                            .override(width, width)
                            .centerCrop()
                            .into(iv_picture);

                    tx_picture.setText(img_info.getUser_img_desc());
                    tx_picture_nick.setText(getIntent().getStringExtra("USER_NICK"));

                }
                else {

                    if(getIntent().getStringExtra("USER_PROFILE") == null) {
                        Glide.with(PictureActivity.this)
                                .load(R.drawable.default_avatar)
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                                .into(iv_picture_profile);
                    }
                    else {
                        Glide.with(PictureActivity.this)
                                .load(getIntent().getStringExtra("USER_PROFILE"))
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                                .into(iv_picture_profile);
                    }

                    Glide.with(PictureActivity.this)
                            .load(USER_IMG_URL + img_info.getUser_img())
                            .override(width, width)
                            .centerCrop()
                            .into(iv_picture);

                    tx_picture.setText(img_info.getUser_img_desc());
                    tx_picture_nick.setText(getIntent().getStringExtra("USER_NICK"));

                    tx_picture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
                            builder.setTitle("내용수정");
                            builder.setMessage("내용을 수정하시겠습니까?");
                            builder.setCancelable(false).setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PictureActivity.this, ShareActivity.class);
                                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.picture_activity));
                                    intent.putExtra("PICTURE_TEXT", tx_picture.getText().toString());     // 내용
                                    intent.putExtra("PICTURE_ID", USER_IMG_URL + img_info.getUser_img());             // Drawable ID
                                    startActivityForResult(intent, 3);
                                }
                            })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
            else {
                tx_picture.setVisibility(View.GONE);
                tx_picture_nick.setVisibility(View.GONE);
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3) {
            if(resultCode == RESULT_OK) {
                tx_picture.setText(data.getStringExtra("SHARE_TEXT"));
            }
        }
    }
}
