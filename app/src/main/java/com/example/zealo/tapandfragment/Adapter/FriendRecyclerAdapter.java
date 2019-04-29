package com.example.zealo.tapandfragment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.zealo.tapandfragment.InfoActivity;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Ui.ChatFragment;
import com.example.zealo.tapandfragment.Ui.FavoriteFragment;
import com.example.zealo.tapandfragment.ProfileActivity;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-31.
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.FriendHolder> {

    //We need Model class!!!
    private Context context;
    private Activity activity;
    private String tab;                  // 리스트뷰 레이아웃 구분

    public static String PROFILE_IMG_URL = NetworkTask.BASE_URL + "hellofit/users/";
    public ArrayList<UserProfile> profileItem;           // 유저 아이디로 SQLite에서 검색 후 방 없으면 방번호 부여 및 마지막 메시지 View에 셋팅
    private String user_id;

    private int code = 0;

    public FriendRecyclerAdapter(Context context, Activity activity, String tab, UserProfile[] item) {
        this.context = context;
        this.tab = tab;
        this.activity = activity;
        this.user_id = SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN");
        this.profileItem = new ArrayList<>();
        for(int i = 0 ; i < item.length ; i++){
            if (!SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN").equals(item[i].getUser_ad_id())) {
                this.profileItem.add(item[i]);
            }
        }
    }

    @Override
    public FriendRecyclerAdapter.FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, null);

        FriendHolder holder = new FriendHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FriendRecyclerAdapter.FriendHolder holder, final int position) {

        Glide.with(context)
                .load(PROFILE_IMG_URL + profileItem.get(position).getUser_profile_img())
                .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                .into(holder.iv_friend_profile);
        // 채팅프래그먼트에서는 프로필 액티비티로 가지 않음
        if(tab.equals(ChatFragment.tab_name_friend)) {
            holder.btn_add_friend.setVisibility(View.GONE);
            holder.tx_recieve_date.setVisibility(View.GONE);
            holder.tx_friend_nick.setText(profileItem.get(position).getUser_nickname());
            holder.tx_friend_status.setText(profileItem.get(position).getUser_desc());
        }
        else if(tab.equals(ChatFragment.tab_name_chat)) {
            holder.btn_add_friend.setVisibility(View.GONE);
        }
        else if(tab.equals(FavoriteFragment.tab_name_friendlist)) { //  프로필 프래그먼트
            holder.tx_recieve_date.setVisibility(View.GONE);
            holder.tx_friend_nick.setText(profileItem.get(position).getUser_nickname());
            holder.tx_friend_status.setText(profileItem.get(position).getUser_desc());
            holder.btn_add_friend.setText("삭제");            // 내 친구니까 삭제로 통일
        }
        else if(tab.equals(InfoActivity.tab_name_clientlist)) {    // 인포액티비티 or 프로필 액티비티
            holder.tx_recieve_date.setVisibility(View.GONE);
            holder.tx_friend_nick.setText(profileItem.get(position).getUser_nickname());
            holder.tx_friend_status.setText(profileItem.get(position).getUser_desc());

            setButtonText(holder.btn_add_friend, profileItem.get(position).getUser_ad_id());
        }
        else {
            holder.tx_recieve_date.setVisibility(View.GONE);
        }

        holder.iv_friend_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tab.equals(FavoriteFragment.tab_name_friendlist)) {        // 즐찾 친구목록 and ProfileFragment -> ListActivity
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("USER", profileItem.get(position));
                    context.startActivity(intent);
                }
                else if(tab.equals(InfoActivity.tab_name_clientlist)) {         // InfoActivity
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("USER", profileItem.get(position));
                    context.startActivity(intent);
                }
            }
        });

        holder.btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.btn_add_friend.getText().toString().equals("추가")) {
                    code = 1;
                }
                else {
                    code = 0;
                }

                NetworkTask.addFriend(code, user_id, profileItem.get(position).getUser_ad_id(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(code == 1) {
                                    holder.btn_add_friend.setText("삭제");
                                }
                                else {
                                    if(tab.equals(FavoriteFragment.tab_name_friendlist)) {
                                        profileItem.remove(position);
                                        notifyDataSetChanged();
                                    }
                                    else {
                                        holder.btn_add_friend.setText("추가");
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileItem.size();
    }

    public class FriendHolder extends RecyclerView.ViewHolder {

        ImageView iv_friend_profile;
        TextView tx_friend_nick;
        TextView tx_friend_status;
        TextView tx_recieve_date;
        Button btn_add_friend;

        public FriendHolder(View itemView) {
            super(itemView);
            iv_friend_profile = (ImageView)itemView.findViewById(R.id.Iv_friend_profile);
            tx_friend_nick = (TextView)itemView.findViewById(R.id.Tx_frined_nick);
            tx_friend_status = (TextView)itemView.findViewById(R.id.Tx_friend_status);
            tx_recieve_date = (TextView)itemView.findViewById(R.id.Tx_recieve_date);
            btn_add_friend = (Button)itemView.findViewById(R.id.Btn_add_friend);
        }
    }

    private void setButtonText(final Button button, String friend_id) {
        NetworkTask.chekingFriend(user_id, friend_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String msg = response.body().string();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!msg.equals("1")) {
                            button.setText("삭제");
                        }
                    }
                });
            }
        });
    }
}
