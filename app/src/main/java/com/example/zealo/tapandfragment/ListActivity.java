package com.example.zealo.tapandfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.FavoriteGymAdapter;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.Listener.RecyclerTouchListener;
import com.example.zealo.tapandfragment.Models.GymListItem;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.Ui.ChatFragment;
import com.example.zealo.tapandfragment.Ui.FavoriteFragment;
import com.example.zealo.tapandfragment.Ui.ProfileFragment;

/**
 * Created by zealo on 2017-11-03.
 */

public class ListActivity extends AppCompatActivity {

    RecyclerView list_activity;
    UserProfile[] friends;
    GymListItem[] gymlist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        String type = getIntent().getStringExtra(getResources().getString(R.string.list_type));
        String activity_name = getIntent().getStringExtra(getResources().getString(R.string.class_name));
        String user_nick = getIntent().getStringExtra("USER_NICK_NAME");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(user_nick);

        list_activity = (RecyclerView)findViewById(R.id.List_activity);

        LinearLayoutManager manager = new LinearLayoutManager(ListActivity.this);
        list_activity.setLayoutManager(manager);
        list_activity.setHasFixedSize(true);

        // 프래그먼트인지 액티비티인지 구분
        if(activity_name.equals(getResources().getString(R.string.profile_activity))) {
            if(type.equals(getResources().getString(R.string.friend))) {
                friends = (UserProfile[]) getIntent().getSerializableExtra("USER_FRIEDS");
                FriendRecyclerAdapter listAdapter = new FriendRecyclerAdapter(ListActivity.this, ListActivity.this, InfoActivity.tab_name_clientlist, friends);
                list_activity.setAdapter(listAdapter);
                actionBar.setTitle(user_nick + "님의 친구");
            }
            else {
                gymlist = (GymListItem[]) getIntent().getSerializableExtra("USER_GYMS");
                FavoriteGymAdapter gymListAdapter = new FavoriteGymAdapter(ListActivity.this, ListActivity.this, gymlist, "LISTACTIVITY");
                list_activity.setAdapter(gymListAdapter);
                actionBar.setTitle(user_nick + "님이 즐겨찾는 센터");
            }
        }
        else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
