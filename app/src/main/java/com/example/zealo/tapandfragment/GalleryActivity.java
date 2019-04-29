package com.example.zealo.tapandfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.Adapter.MyGridAdapter;
import com.example.zealo.tapandfragment.Util.DeviceUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zealo on 2017-11-06.
 */

public class GalleryActivity extends AppCompatActivity {

    GridView camera_img_list;
    ImageView iv_selected_camera_img;

    private String SELECTED_IMG_FILE;

    public static final String activity_name_gallery = "GALLERY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("갤러리");

        iv_selected_camera_img = (ImageView)findViewById(R.id.Iv_selected_camera_img);
        camera_img_list = (GridView)findViewById(R.id.Camera_img_List);

        String DCIM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        String CAMERA_PATH = DCIM_PATH + "/DCIM/Camera";

        final int DEVICE_WIDTH = DeviceUtil.getDevicewidth(GalleryActivity.this);

        Log.d("171117", CAMERA_PATH);

        File CAMERA_DIRECTORY = new File(CAMERA_PATH);
        final File[] CAMERA_IMG_LIST = CAMERA_DIRECTORY.listFiles();

        final ArrayList<String> fileNames = new ArrayList<>();

        for(File file : CAMERA_IMG_LIST) {
            fileNames.add(file.getAbsolutePath());
        }

        MyGridAdapter mAdapter = new MyGridAdapter(DEVICE_WIDTH, fileNames, null, activity_name_gallery);
        camera_img_list.setAdapter(mAdapter);

        iv_selected_camera_img.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DEVICE_WIDTH));

        Glide.with(GalleryActivity.this).load(fileNames.get(0)).override(DEVICE_WIDTH, DEVICE_WIDTH).centerCrop().into(iv_selected_camera_img);
        SELECTED_IMG_FILE = fileNames.get(0);

        camera_img_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(GalleryActivity.this).load(fileNames.get(position)).override(DEVICE_WIDTH, DEVICE_WIDTH).centerCrop().into(iv_selected_camera_img);
                SELECTED_IMG_FILE = fileNames.get(position);
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
        if(getIntent().getStringExtra(getResources().getString(R.string.class_name)).equals(getResources().getString(R.string.profile_revise))) {
            menu.getItem(1).setEnabled(true);
            menu.getItem(0).setVisible(false);
        }
        else {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home :
                finish();
                break;

            case R.id.action_next :      // ShareActivity에서 업로드
                Intent intent = new Intent(GalleryActivity.this, ShareActivity.class);   //
                intent.putExtra("IMAGE_FILE", SELECTED_IMG_FILE);
                intent.putExtra(getResources().getString(R.string.class_name), getResources().getString(R.string.gallery_activity));
                startActivity(intent);
                break;

            case R.id.action_confirm :     // 프로필 수정  바로 업로드 필요
                Intent intent1 = new Intent();
                intent1.putExtra("IMAGE_FILE", SELECTED_IMG_FILE);
                setResult(RESULT_OK, intent1);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
