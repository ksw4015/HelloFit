package com.example.zealo.tapandfragment.Ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.ChatRoomAdapter;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.ChattingActivity;
import com.example.zealo.tapandfragment.DataBases.DBSqlData;
import com.example.zealo.tapandfragment.DataBases.DataBases;
import com.example.zealo.tapandfragment.Listener.RecyclerTouchListener;
import com.example.zealo.tapandfragment.MainActivity;
import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.Models.RoomData;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.ShareActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-25.
 */

public class ChatFragment extends Fragment {

    public static int CHATFRAGMENT_CODE = 201;

    RecyclerView chat_list;
    RecyclerView friend_list;

    TextView no_chat;

    private UserProfile[] friends;
    private ArrayList<UserProfile> chattings;

    private ChatRoomAdapter chatRoomAdapter;

    public static final String tab_name_chat = "Chatting";
    public static final String tab_name_friend = "Friend";

    private String my_ad_id;
    private ProgressDialog mDialog;

    private boolean chat_remain = false;

    private Thread friendList_ready;
    private Handler friendList_complete;

    public ChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mDialog = new ProgressDialog(getContext());
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("업로드 중...");
        mDialog.setCancelable(false);

        TabHost tabHost = (TabHost)view.findViewById(R.id.m_tabhost_chat);
        tabHost.setup();

        TabHost.TabSpec tab_friend = tabHost.newTabSpec(tab_name_friend);
        tab_friend.setContent(R.id.content_friend_list);
        tab_friend.setIndicator("친구");
        tabHost.addTab(tab_friend);

        TabHost.TabSpec tab_chatting = tabHost.newTabSpec(tab_name_chat);
        tab_chatting.setContent(R.id.content_chat_list);
        tab_chatting.setIndicator("채팅");
        tabHost.addTab(tab_chatting);

        friend_list = (RecyclerView)view.findViewById(R.id.friend_List);
        chat_list = (RecyclerView)view.findViewById(R.id.chat_List);
        no_chat = (TextView)view.findViewById(R.id.Tx_no_chat);

        my_ad_id = SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.Google_Play_ID), "NaN");

        friendList_ready = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(friends.length != 0) {
                        friendList_complete.sendEmptyMessage(1);
                        break;
                    }
                }
            }
        });

        friendList_complete = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    receiveMsg(my_ad_id);
                }
            }
        };

        NetworkTask.selectChatUser(my_ad_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(json.equals("1")) {
                            Toast.makeText(getContext(), "친구가 없어요... 친구를 추가해 보세요", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Gson gson = new Gson();
                            friends = gson.fromJson(json, UserProfile[].class);
                            setRecyclerViewAdapter(friend_list, tab_name_friend, friends);
                            friendList_ready.start();

                        }

                    }
                });
            }
        });

        friend_list.addOnItemTouchListener(new RecyclerTouchListener(getContext(), friend_list, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {     // 어댑터를 만들때 넘기는 모델 배열의 Index로 position값을 넣어서 찾거나 뷰에서 가져오기
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("채팅시작");
                builder.setMessage(((TextView)view.findViewById(R.id.Tx_frined_nick)).getText().toString() + "님과 채팅을 시작하시겠습니까?");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "채팅 액티비티로~", Toast.LENGTH_SHORT).show();
                        RoomInfo info = getRoomKey(friends[position].getUser_ad_id(), false);

                        Intent intent = new Intent(getContext(), ChattingActivity.class);
                        intent.putExtra("Chat_User", friends[position]);
                        intent.putExtra("Chat_Room", info.getRoomNum());
                        intent.putExtra("Room_Exist", info.isExist());
                        //startActivity(intent);
                        startActivityForResult(intent, CHATFRAGMENT_CODE);
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

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals(tab_name_chat)) {

                    if(chatRoomAdapter != null) {
                        chat_remain = chatRoomAdapter.isRoomExist();
                    }

                    if(chat_remain) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                mDialog.show();
                            }

                            @Override
                            protected Void doInBackground(Void... params) {

                                while (true) {
                                    if(chattings.size() != 0) {
                                        Log.d("171221", "채팅목록 준비완료");
                                        break;
                                    }
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                mDialog.dismiss();

                                LinearLayoutManager manager =  new LinearLayoutManager(getContext());
                                chat_list.setLayoutManager(manager);
                                chat_list.setHasFixedSize(true);

                                chatRoomAdapter = new ChatRoomAdapter(chattings, getContext());
                                chat_list.setAdapter(chatRoomAdapter);
                            }
                        }.execute();
                    }
                    else {
                        no_chat.setVisibility(View.VISIBLE);
                        Log.d("171222", "채팅 목록 없음");
                    }
                }
            }
        });

        return view;
    }

    public void receiveMsg(String user_id) {
        NetworkTask.receiveRemainMsg(user_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                Log.d("171221", json);

                if(!json.equals("NaN")) {

                    JsonObject object = new JsonParser().parse(json).getAsJsonObject();
                    JsonArray message = object.getAsJsonArray("message");
                    JsonArray users = object.getAsJsonArray("users");

                    ChatItems[] remains = new GsonBuilder().serializeNulls().create().fromJson(message, ChatItems[].class);
                    UserProfile[] remainUsers = new GsonBuilder().serializeNulls().create().fromJson(users, UserProfile[].class);

                    // Save RoomDatas in SQLite
                    final DataBases mDB = new DataBases(getContext());
                    // Save Receive Messages in SQLite
                    mDB.dbOpen();

                    for (ChatItems remainMsgs : remains) {
                        // 유저 아이디로 룸넘버 부여 후 인설트
                  //      remainMsgs.setChat_room_num(String.valueOf(999));           // 채팅 액티비티에서 업데이트 쿼리로 처리하자
                        Log.d("171222", remainMsgs.getUser_ad_id());
                        remainMsgs.setChat_room_num(getRoomKey(remainMsgs.getUser_ad_id(), true).getRoomNum());
                        mDB.insertData(DBSqlData.SQL_DB_INSERT_DATA, remainMsgs);

                    }

                    // 소켓 연결이 끊어져 있을 때 들어온 메시지를 받고나서 내 친구 인지 비교 후 친구목록에 없을경우 임시 SQLite 데이터베이스에 삽입
                    ArrayList<UserProfile> remain = new ArrayList<>(Arrays.asList(remainUsers));
                    ArrayList<UserProfile> nofriend = mDB.getTempUsers(DBSqlData.SQL_DB_SELECT_ALL_TEMP_USER);

                    Iterator it = remain.iterator();
                    while (it.hasNext()) {

                        UserProfile temp = (UserProfile) it.next();

                        for(UserProfile f : friends) {
                            if(temp.getUser_ad_id().equals(f.getUser_ad_id())) {
                                Log.d("171222", "친구목록 중복 제거");
                                it.remove();
                                break;
                            }
                        }

                        if(nofriend.size() != 0) {
                            for(UserProfile n : nofriend) {
                                if(temp.getUser_ad_id().equals(n.getUser_ad_id())) {
                                    Log.d("171222", "임시 친구목록 중복제거");
                                    it.remove();
                                    break;
                                }
                            }
                        }
                    }

                    // temp_chat_user 테이블에서도 가져와서 비교해야됨

                    if(remain.size() != 0) {
                        for(UserProfile user : remain) {
                            Log.d("171222", "새로운 친구 : " + user.getUser_ad_id());
                            mDB.insertData(DBSqlData.SQL_DB_INSERT_TEMP_USER, user);
                        }
                    }
                    else {
                        Log.d("171222", "새 친구 없음");
                    }

                    mDB.dbClose();

                }
                chattings = makeChatList();
            }
        });
    }

    public void setRecyclerViewAdapter(RecyclerView mListView, String tab, UserProfile[] users) {

        LinearLayoutManager manager =  new LinearLayoutManager(getContext());
        mListView.setLayoutManager(manager);
        mListView.setHasFixedSize(true);

        FriendRecyclerAdapter mAdapter = new FriendRecyclerAdapter(getContext(), getActivity(), tab, users);
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("171106", "chat resume");
        ((MainActivity)getActivity()).toggleButton(1);
    }

    private RoomInfo getRoomKey(String friend_id, boolean insert) {

        DataBases myDB = new DataBases(getContext());

        myDB.dbOpen();   // RooData모델클래스 대신 HashMap이 좋을듯 <user_ad_id : roomNum>
        ArrayList<RoomData> roomNums = myDB.selectAllRoomNumber(DBSqlData.SQL_DB_SELECT_ALL_ROOM);      // 모든 생성된 방 데이터를 방번호 기준 오름차순으로 가져옴
        myDB.dbClose();

        int roomNum = new Random().nextInt(100);        // 0~99 랜덤 숫자

        if(roomNums.size() != 0) {       // 기존에 생선된 방이 없으면 그대로 1번 아니면 알고리즘

            for(RoomData room : roomNums) {

                if(room.getUser_ad_id().equals(friend_id)) {  // 채팅 상대방 아이디로 기존 방번호가 있으면 그 방번호를 return
                    Log.d("171222", "이미 방 있음");
                    return new RoomInfo(String.valueOf(room.getRoomNum()), true);  // 기존에 방번호가있으면 존재함 = true값
                }
                else {
                    // roomNum 과 RoomData의 roomNum과 비교 후 같은게 있으면 계속 랜덤숫자 생성 후 비교 반복....
                    if(room.getRoomNum() == roomNum) {
                        roomNum = new Random().nextInt(100);      // 혹시나 이게 중복일수 있는데 이건 어찌 처리함?? -> 나중에 HashSet사용하는게 좋을듯
                    }
                }
            }
        }

        // 룸넘버 인설트
        if(insert) {
            Log.d("171222", "방 정보 삽입");
            myDB.dbOpen();
            myDB.insertData(DBSqlData.SQL_DB_INSERT_ROOM_DATA, new String[]{friend_id, String.valueOf(roomNum)});
            myDB.dbClose();
        }

        return new RoomInfo(String.valueOf(roomNum), false);
    }

    private ArrayList<UserProfile> makeChatList() {        // 새로운 채팅이 없을 떄

        DataBases mDB = new DataBases(getContext());
        mDB.dbOpen();

        ArrayList<UserProfile> users = new ArrayList<>();

        ArrayList<RoomData> roomNums = mDB.selectAllRoomNumber(DBSqlData.SQL_DB_SELECT_ALL_ROOM);      // 모든 생성된 방 데이터를 방번호 기준 오름차순으로 가져옴

        mDB.dbClose();
        // 기존 방들의 Userprofile과 ChatItems 셋팅
        if(roomNums.size() != 0) {
            Log.d("171222", "Room Size : " + roomNums.size());
            Log.d("171222", "Friends : " + friends.length);

            for(int i = 0 ; i < roomNums.size() ; i++) {
                Log.d("171222", "id : " + roomNums.get(i).getUser_ad_id() + " num : " + roomNums.get(i).getRoomNum());
                String temp_id = roomNums.get(i).getUser_ad_id();

                for(int j = 0 ; j < friends.length ; j++) {
                    Log.d("171222", temp_id + "=" + friends[j].getUser_ad_id());
                    if(temp_id.equals(friends[j].getUser_ad_id())) {
                        users.add(friends[j]);
                        break;
                    }
                }

            }

            Log.d("171222", "for문 탈출");

            // 여기서 터짐
            mDB.dbOpen();
            ArrayList<UserProfile> nofriend = mDB.getTempUsers(DBSqlData.SQL_DB_SELECT_ALL_TEMP_USER);
            mDB.dbClose();

            Log.d("171222", "no Friends : " + nofriend.size());

            if(nofriend.size() != 0) {
                for(UserProfile no : nofriend) {
                    users.add(no);
                }
            }
            else {
                Log.d("171222", "임시 친구 없음");
            }

            chat_remain = true;

        }
        else {
            Log.d("171222", "방없음");
        }

        Log.d("171222", ""+users.size());

        return users;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHATFRAGMENT_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    for(UserProfile u : chattings) {
                        if(u.getUser_ad_id().equals(data.getStringExtra("USER"))) {
                            chattings.remove(u);
                            break;
                        }
                    }
                    chatRoomAdapter.notifyDataSetChanged();
                }

                if(chattings.size() == 0) {
                    chat_remain = false;
                }
            }
        }
    }

    public class RoomInfo {

        private String roomNum;
        private boolean exist;

        public RoomInfo(String roomNum, boolean exist) {
            this.roomNum = roomNum;
            this.exist = exist;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public boolean isExist() {
            return exist;
        }
    }
}
