package com.example.zealo.tapandfragment.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.ChattingActivity;
import com.example.zealo.tapandfragment.DataBases.DBSqlData;
import com.example.zealo.tapandfragment.DataBases.DataBases;
import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Ui.ChatFragment;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by zealo on 2017-12-20.
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.RoomHolder>{

    private ArrayList<UserProfile> items;
    private Context context;

    public ChatRoomAdapter(ArrayList<UserProfile> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public RoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room_list, null);

        RoomHolder holder = new RoomHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RoomHolder holder, int position) {

        Glide.with(context)
                .load(FriendRecyclerAdapter.PROFILE_IMG_URL + items.get(position).getUser_profile_img())
                .bitmapTransform(new CropCircleTransformation(new UtilBitmapPool()))
                .into(holder.chat_friend_profile);

        final ChatItems current = getRecentMessage(items.get(position).getUser_ad_id());

        holder.chat_friend_message.setText(current.getChat_message_text());
        holder.chat_friend_nick.setText(items.get(position).getUser_nickname());
        holder.chat_recieve_date.setText(current.getChat_posted_date());

        final int i = position;

        holder.btn_lay.setOnClickListener(new View.OnClickListener() {      // 방이 있는 상태!
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "채팅 액티비티로~", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("Chat_User", items.get(i));
                intent.putExtra("Chat_Room", current.getChat_room_num());
                intent.putExtra("Room_Exist", true);
                //context.startActivity(intent);
                ((Activity)context).startActivityForResult(intent, ChatFragment.CHATFRAGMENT_CODE);
            }
        });

        holder.btn_lay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("채팅방 삭제");
                builder.setMessage("정말 채팅방을 삭제하시겠습니까?");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteChatRoom(current.getChat_room_num(), items.get(i).getUser_ad_id());
                        items.remove(i);
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder {

        ImageView chat_friend_profile;
        TextView chat_friend_nick;
        TextView chat_friend_message;
        TextView chat_recieve_date;

        RelativeLayout btn_lay;

        public RoomHolder(View itemView) {
            super(itemView);
            chat_friend_profile = (ImageView)itemView.findViewById(R.id.chat_friend_profile);
            chat_friend_nick = (TextView)itemView.findViewById(R.id.chat_friend_nick);
            chat_friend_message = (TextView)itemView.findViewById(R.id.chat_friend_message);
            chat_recieve_date = (TextView)itemView.findViewById(R.id.chat_recieve_date);
            btn_lay = (RelativeLayout)itemView.findViewById(R.id.Btn_Layout);
        }
    }

    private ChatItems getRecentMessage(String user_ad_id) {

        DataBases db = new DataBases(context);
        db.dbOpen();

        ChatItems msg = db.selectRecentMessage(DBSqlData.SQL_DB_SELECT_LAST_MESSAGE, user_ad_id);

        db.dbClose();

        return msg;
    }

    public void deleteChatRoom(String chat_room, String user_ad_id) {
        DataBases db = new DataBases(context);
        db.dbOpen();
        db.deleteData(DBSqlData.SQL_DB_DELETE_ROOM_DATA, chat_room);
        db.deleteData(DBSqlData.SQL_DB_DELETE_DATA, chat_room);
        db.deleteData(DBSqlData.SQL_DB_DELETE_TEMP_USER, user_ad_id);
        db.dbClose();
    }

    public boolean isRoomExist() {
        return (items.size() == 0) ? false : true;
    }

}