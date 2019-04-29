package com.example.zealo.tapandfragment;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;

import java.io.File;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-11-06.
 */

public class ProfileReviseActivity extends AppCompatActivity{

    ImageView iv_profile_circle;
    EditText edt_profile_revise_name;
    EditText edt_profile_revise_text;

    int width;

    public static final int PROFILE_REVISE_ACTIVITY_REQUESTCODE = 2;

    private String REVISE_IMAGE_FILE = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_revise);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("프로필 수정");
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv_profile_circle = (ImageView)findViewById(R.id.Iv_profile_circle);
        edt_profile_revise_name = (EditText)findViewById(R.id.Edt_profile_revise_name);
        edt_profile_revise_text = (EditText)findViewById(R.id.Edt_profile_revise_text);

        edt_profile_revise_name.setText(getIntent().getStringExtra("USER_NICKNAME"));
        edt_profile_revise_text.setText(getIntent().getStringExtra("USER_STATUS"));

        int test_img = getIntent().getIntExtra("TEST_IMG", 0);

        width = DeviceUtil.getDevicewidth(ProfileReviseActivity.this);

        if(test_img != 0) {
            Glide.with(ProfileReviseActivity.this)
                    .load(test_img)
                    .override(width/2, width/2)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                    .into(iv_profile_circle);
        }
        else {
            Glide.with(ProfileReviseActivity.this)
                    .load(getIntent().getStringExtra("USER_PROFILE"))
                    .override(width/2, width/2)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                    .into(iv_profile_circle);
        }

        iv_profile_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean PERMISSION_CHECK = false;

                if(ContextCompat.checkSelfPermission(ProfileReviseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    PERMISSION_CHECK = true;
                }

                if(!PERMISSION_CHECK) {
                    ActivityCompat.requestPermissions(ProfileReviseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                else {
                    Intent intent = new Intent(ProfileReviseActivity.this, GalleryActivity.class);
                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_revise));
                    startActivityForResult(intent, PROFILE_REVISE_ACTIVITY_REQUESTCODE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home :
                finish();
                break;

            case R.id.action_confirm :

                String nickname = edt_profile_revise_name.getText().toString();
                String user_desc = edt_profile_revise_text.getText().toString();

                if(REVISE_IMAGE_FILE != null) {
                    Log.d("171128", "이미지 YES");
                    NetworkTask.updateMyProfile(ProfileReviseActivity.this,
                            nickname,
                            user_desc,
                            REVISE_IMAGE_FILE,
                            width,
                            new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            final String msg = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(msg.equals("OK")) {
                                        Toast.makeText(ProfileReviseActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("REVISED_NAME", edt_profile_revise_name.getText().toString());
                                        intent.putExtra("REVISED_TEXT", edt_profile_revise_text.getText().toString());
                                        intent.putExtra("IMAGE_FILE", REVISE_IMAGE_FILE);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ProfileReviseActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
                else {
                    Log.d("171128", "이미지 NO");
                    NetworkTask.updateMyProfile(ProfileReviseActivity.this, nickname, user_desc, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            final String msg = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(msg.equals("OK")) {
                                        Toast.makeText(ProfileReviseActivity.this, "변경되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("REVISED_NAME", edt_profile_revise_name.getText().toString());
                                        intent.putExtra("REVISED_TEXT", edt_profile_revise_text.getText().toString());
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(ProfileReviseActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PROFILE_REVISE_ACTIVITY_REQUESTCODE) {

            if(resultCode == RESULT_OK) {

                REVISE_IMAGE_FILE = data.getStringExtra("IMAGE_FILE");
                Log.d("171128", REVISE_IMAGE_FILE);

                Glide.with(ProfileReviseActivity.this)
                        .load(REVISE_IMAGE_FILE)
                        .override(width/2, width/2)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                        .into(iv_profile_circle);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if(grantResults.length > 0 ) {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(ProfileReviseActivity.this, GalleryActivity.class);
                    intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.profile_revise));
                    startActivityForResult(intent, PROFILE_REVISE_ACTIVITY_REQUESTCODE);
                }
                else {
                    Toast.makeText(ProfileReviseActivity.this, "갤러리를 실행하려면 저장공간 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
