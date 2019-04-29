package com.example.zealo.tapandfragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.Util.DeviceUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-11-06.
 */

public class ShareActivity extends AppCompatActivity {

    ImageView iv_share_img;
    EditText edt_share_text;

    private String class_name;
    private String filename;
    private String img_desc;
    private int width;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        class_name = getIntent().getStringExtra(getResources().getString(R.string.class_name));

        mDialog = new ProgressDialog(ShareActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("업로드 중...");
        mDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("공유하기");
        actionBar.setDisplayHomeAsUpEnabled(true);

        width = DeviceUtil.getDevicewidth(ShareActivity.this);

        iv_share_img = (ImageView)findViewById(R.id.Iv_share_img);
        edt_share_text = (EditText)findViewById(R.id.Edt_share_text);

        int WIDTH = DeviceUtil.getDevicewidth(ShareActivity.this);

        if(class_name.equals(getResources().getString(R.string.gallery_activity))) {
            filename = getIntent().getStringExtra("IMAGE_FILE");        // 스트링으로 변경
            Glide.with(ShareActivity.this).load(filename).override(WIDTH, WIDTH).centerCrop().into(iv_share_img);
        }
        else {  // 픽쳐액티비티
            filename = getIntent().getStringExtra("PICTURE_ID");       // 이미지 url로 변경
            Glide.with(ShareActivity.this).load(filename).override(WIDTH, WIDTH).centerCrop().into(iv_share_img);
            img_desc = getIntent().getStringExtra("PICTURE_TEXT");
            edt_share_text.setText(img_desc);
        }

        // 입력한 내용과 이미지 파일 명(파일도)을 서버로 전송
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setEnabled(true);
        menu.getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home :
                finish();
                break;

            case R.id.action_confirm :
                if(class_name.equals(getResources().getString(R.string.gallery_activity))) {
                    // 내용 업뎃 추가 -> 총 올라가야되는 데이터 : 파일업로드, 파일명(user_img), user_id, user_img_desc
                    String user_desc = edt_share_text.getText().toString();
                    mDialog.show();

                    NetworkTask.upLoadUserImg(SharedPreferenceManager.getPreference(ShareActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN")
                            , filename          // sd카드에서 이미지 파일 경로
                            , user_desc
                            , width,
                            ShareActivity.this, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    mDialog.dismiss();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String msg = response.body().string();
                                    Log.d("171126", msg);
                                    if(msg.equals("OK")) {
                                        MsgToast("이미지가 업로드 되었습니다.");
                                        mDialog.dismiss();
                                        finish();
                                    }
                                    else {
                                        MsgToast(msg);
                                    }
                                }
                            });

                }
                else {
                    String new_file_name = filename.substring(filename.lastIndexOf("/") + 1);
                    img_desc = edt_share_text.getText().toString();

                    mDialog.show();

                    NetworkTask.updateUserImgDesc(SharedPreferenceManager.getPreference(ShareActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN")
                            , img_desc
                            , new_file_name
                            , new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    mDialog.dismiss();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String msg1 = response.body().string();
                                    if(msg1.equals("OK")) {
                                        Intent intent = new Intent();
                                        intent.putExtra("SHARE_TEXT", edt_share_text.getText().toString());
                                        setResult(RESULT_OK, intent);
                                        mDialog.dismiss();
                                        finish();
                                        MsgToast("수정되었습니다.");
                                    }
                                    else {
                                        MsgToast(msg1);
                                    }
                                }
                            });
                    // 이미지 글만 서버에 업뎃
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void MsgToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShareActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
