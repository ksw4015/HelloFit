package com.example.zealo.tapandfragment.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.Models.UserInfomation;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.ProfileActivity;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Ui.ProfileFragment;

import java.util.ArrayList;

/**
 * Created by zealo on 2017-11-01.
 */

public class MyGridAdapter extends BaseAdapter {

    int width;

    String activity;
    ArrayList<String> fileNames;
    private UserInfomation[] userinfo;

    private final String USER_IMG_TUHMB = NetworkTask.BASE_URL + "hellofit/users/thumbnails/";

    public MyGridAdapter(int width, ArrayList<String> fileNames, UserInfomation[] user, String activity) {
        this.width = width;
        this.activity = activity;
        this.fileNames = fileNames;
        this.userinfo = user;
    }

    @Override
    public int getCount() {

        int count_items = 0;

        if(activity.equals(ProfileActivity.profile_activity_name) || activity.equals(ProfileFragment.fragment_name_profile)) {  // 프로필
            count_items = userinfo.length;
        }
        else {   // 갤러리
            count_items = fileNames.size();
        }
        return count_items;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        gridHolder holder;

        if (convertView == null) {
            holder = new gridHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, null);
            holder.iv_grid_item = (ImageView)convertView.findViewById(R.id.Iv_grid_item);
            convertView.setTag(holder);
        }
        else {
            holder = (gridHolder)convertView.getTag();
        }

        if(activity.equals(ProfileActivity.profile_activity_name) || activity.equals(ProfileFragment.fragment_name_profile)) {        // 프로필 액티비티
            Glide.with(parent.getContext()).load(USER_IMG_TUHMB + userinfo[position].getUser_img()).override(width/3, width/3).centerCrop().into(holder.iv_grid_item);
            Log.d("171127", USER_IMG_TUHMB + userinfo[position].getUser_img());
        }
        else {        // 갤러리
            Glide.with(parent.getContext()).load(fileNames.get(position)).override(width/4, width/4).centerCrop().into(holder.iv_grid_item);
        }

        return convertView;
    }

    public class gridHolder {
        ImageView iv_grid_item;
    }
}
