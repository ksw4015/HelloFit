package com.example.zealo.tapandfragment.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by USER on 2017-12-04.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatHolder>  {

    private Context context;
    private ArrayList<ChatItems> items;

    private String room_num;
    private String my_ad_id;

    private byte[] friend_profile = null;
    private String friend_url;

    private String friend_nickname;

    public ChatRecyclerAdapter(Context context, ArrayList<ChatItems> items, String room_num, String friend_url, String friend_nickname) {
        this.context = context;
        this.items = items;
        this.room_num = room_num;
        this.my_ad_id = SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN");
        this.friend_url = friend_url;
        this.friend_nickname = friend_nickname;

        Glide.with(context).load(FriendRecyclerAdapter.PROFILE_IMG_URL + friend_url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                friend_profile = stream.toByteArray();
            }
        });
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, null);

        ChatHolder holder = new ChatHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {

        String user_id = items.get(position).getUser_ad_id();

        if(position == 0) {
            holder.lay_date.setVisibility(View.VISIBLE);
            holder.tv_date.setText(items.get(0).getChat_posted_date());
        }
        else {
            String prevdate = items.get(position - 1).getChat_posted_date();

            if(prevdate.equals(items.get(position).getChat_posted_date())) {
                holder.lay_date.setVisibility(View.GONE);
            }
            else {
                holder.lay_date.setVisibility(View.VISIBLE);
                holder.tv_date.setText(items.get(position).getChat_posted_date());
            }

        }

        // 스크롤할때마다 말풍선과 프로필 이미지가 뒤섞여서 레이아웃 포지션 하드코딩함
        if(user_id.equals(my_ad_id)){            // user_ad_id

            holder.tv_nick.setVisibility(View.GONE);
            holder.iv_profile.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 10, 20, 0);
            holder.tv_chat.setLayoutParams(layoutParams);
            holder.tv_chat.setBackgroundResource(R.drawable.outbox2);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.LEFT_OF, holder.tv_chat.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, holder.tv_chat.getId());
            layoutParams.setMargins(0,0,0,8);
            holder.tv_time.setLayoutParams(layoutParams);
        }
        else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(160, 160);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.setMargins(30,0,0,0);
            holder.iv_profile.setLayoutParams(layoutParams);

            // 채팅 상대의 프로필 사진을 비트맵으로 저장해서 쓰려고함

            if(friend_profile == null){
                Glide.with(context).load(FriendRecyclerAdapter.PROFILE_IMG_URL + friend_url).centerCrop().bitmapTransform(new CropCircleTransformation(new BitmapPollUtil())).into(holder.iv_profile);
                Log.d("171214", "byteArray null");
            }
            else {
                Glide.with(context).load(friend_profile).centerCrop().bitmapTransform(new CropCircleTransformation(new BitmapPollUtil())).into(holder.iv_profile);
                Log.d("171214", "byteArray not null");
            }

            holder.iv_profile.setVisibility(View.VISIBLE);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, holder.iv_profile.getId());
            layoutParams.setMargins(34, 12, 0, 10);
            holder.tv_nick.setLayoutParams(layoutParams);

            holder.tv_nick.setVisibility(View.VISIBLE);
            holder.tv_nick.setText(friend_nickname);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, holder.tv_nick.getId());
            layoutParams.addRule(RelativeLayout.RIGHT_OF, holder.iv_profile.getId());
            holder.tv_chat.setLayoutParams(layoutParams);
            holder.tv_chat.setBackgroundResource(R.drawable.inbox2);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, holder.tv_chat.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, holder.tv_chat.getId());
            layoutParams.setMargins(0,0,0,8);
            holder.tv_time.setLayoutParams(layoutParams);
        }

        holder.tv_chat.setText(items.get(position).getChat_message_text());
        holder.tv_time.setText(items.get(position).getChat_posted_time());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        LinearLayout lay_date;

        TextView tv_chat;
        TextView tv_nick;
        TextView tv_time;
        TextView tv_date;
        ImageView iv_profile;

        public ChatHolder(View itemView) {
            super(itemView);
            lay_date = (LinearLayout)itemView.findViewById(R.id.Lay_Date);
            tv_chat = (TextView)itemView.findViewById(R.id.Tv_Chat);
            tv_nick = (TextView)itemView.findViewById(R.id.Tv_Nick);
            tv_time = (TextView)itemView.findViewById(R.id.Tv_Time);
            tv_date = (TextView)itemView.findViewById(R.id.Tv_Date);
            iv_profile = (ImageView)itemView.findViewById(R.id.Iv_Profile);
        }


    }

    public class BitmapPollUtil implements BitmapPool {
        @Override
        public int getMaxSize() {
            return 0;
        }

        @Override
        public void setSizeMultiplier(float sizeMultiplier) {

        }

        @Override
        public boolean put(Bitmap bitmap) {
            return false;
        }

        @Override
        public Bitmap get(int width, int height, Bitmap.Config config) {
            return null;
        }

        @Override
        public Bitmap getDirty(int width, int height, Bitmap.Config config) {
            return null;
        }

        @Override
        public void clearMemory() {

        }

        @Override
        public void trimMemory(int level) {

        }
    }

    public void addMsg(ChatItems newChat) {
        items.add(newChat);
        notifyDataSetChanged();
    }

    public void addMsgs(ChatItems[] newMsgs) {
        // 방번호를 어댑터의 생성자로
        for(ChatItems newChat : newMsgs) {
            newChat.setChat_room_num(room_num);
            items.add(newChat);
        }

        notifyDataSetChanged();
    }

    public String getMyLastMessage() {

        ArrayList<String> myMsgs = new ArrayList<>();
/*
        if(items.size() != 0) {

            Iterator<ChatItems> it = items.iterator();
            while (it.hasNext()) {
                if(it.next().getUser_ad_id().equals(my_ad_id)) {
                    myMsgs.add(it.next().getChat_message_text());
                }
            }

        }*/

        if(items.size() != 0) {
            for(ChatItems i : items) {
                if(i.getUser_ad_id().equals(my_ad_id)) {
                    myMsgs.add(i.getChat_message_text());
                }
            }
        }

        return (myMsgs.size() == 0) ? null : myMsgs.get(myMsgs.size() - 1);
    }
}