package com.example.zealo.tapandfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.ChatRecyclerAdapter;
import com.example.zealo.tapandfragment.DataBases.DBSqlData;
import com.example.zealo.tapandfragment.DataBases.DataBases;
import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.Models.RoomData;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.NetWork.SocketListener;
import com.example.zealo.tapandfragment.NetWork.SocketSender;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.Util.TimeUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-12-11.
 */

public class ChattingActivity extends AppCompatActivity {

    // 리사이클러뷰 어댑터 관련 변수선언
    ChatRecyclerAdapter mAdapter;
    LinearLayoutManager manager;

    // SQLite 오픈헬퍼 클래스
    DataBases myDataBase;
    // 모델 클래스
    ArrayList<ChatItems> recentData;
    // Socket NetWork
    Socket socket;
    private final String serverIP = "218.155.154.110";
    private final int serverPort = 7777;

    SocketListener listener;
    SocketSender sender;

    Thread thread;
//    Thread ready;

    UserProfile chat_user;        // 상대방 프로필 정보
    String chat_room;          // 채팅방 번호

    EditText edt_chat;
    Button btn_send;

    RecyclerView chat_list;

    String my_ad_id;
    String friend_ad_id;
    boolean room_exist;

//    byte[] friend_imgbyte;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("채팅방");

        chat_user = (UserProfile)getIntent().getSerializableExtra("Chat_User");
        chat_room = getIntent().getStringExtra("Chat_Room");
        room_exist = getIntent().getBooleanExtra("Room_Exist", true);

        Log.d("171220", "user : " + chat_user.getUser_ad_id() + " room : " + chat_room);

        edt_chat = (EditText)findViewById(R.id.Edt_Chat);
        btn_send = (Button)findViewById(R.id.Btn_Send);

        chat_list = (RecyclerView)findViewById(R.id.Chat_List);

        manager = new LinearLayoutManager(ChattingActivity.this);
        chat_list.setLayoutManager(manager);
        chat_list.setHasFixedSize(true);

        recentData = new ArrayList<>();
        myDataBase = new DataBases(ChattingActivity.this);

        // 방번호 업데이트문 한번 실행
        myDataBase.dbOpen();
        myDataBase.upDateRoomNumber(chat_user.getUser_ad_id(), chat_room);
        myDataBase.dbClose();

        my_ad_id = SharedPreferenceManager.getPreference(ChattingActivity.this, getResources().getString(R.string.Google_Play_ID), "NaN"); // 구글 AD ID 거의 모든 테이블에서 쓰이는 user_ad_id
        friend_ad_id = chat_user.getUser_ad_id();

        if(checkDB(chat_room)) {
            Toast.makeText(ChattingActivity.this, "채팅을 시작합니다~!", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!room_exist) {      // 기존의 없는 방번호를 받은 경우 룸데이터를 인설트
                Log.d("171221", "방 정보 삽입");
                myDataBase.dbOpen();
                myDataBase.insertData(DBSqlData.SQL_DB_INSERT_ROOM_DATA, new String[]{chat_user.getUser_ad_id(), chat_room});
                myDataBase.dbClose();
            }
        }

        mAdapter = new ChatRecyclerAdapter(ChattingActivity.this, recentData, chat_room, chat_user.getUser_profile_img(), chat_user.getUser_nickname());
        chat_list.setAdapter(mAdapter);

        // 마지막 채팅 내용으로 리스트뷰 스크롤
        manager.scrollToPosition(mAdapter.getItemCount() - 1);

        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    socket = new Socket(serverIP, serverPort);

                    sender = new SocketSender(socket, my_ad_id + ":" + friend_ad_id);    // sendUser:receiveUser
                    listener = new SocketListener(socket, mAdapter, ChattingActivity.this, manager, myDataBase, chat_room);

                    Thread send = new Thread(sender);
                    Thread listen = new Thread(listener);

                    send.start();
                    listen.start();

                }
                catch (IOException e) {
                    Log.d("171207", "" + e);
                }
            }
        };
        thread.start();

        // initialized

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_chat.getText().toString().equals("")) {
                    Toast.makeText(ChattingActivity.this, "메시지를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 친구목록에서 넘어올때 방번호 넘겨받기 중요!
                    final ChatItems msg = new ChatItems(my_ad_id, chat_room, TimeUtil.getDate(), TimeUtil.getTime(), edt_chat.getText().toString());

                    myDataBase.dbOpen();
                    myDataBase.insertData(DBSqlData.SQL_DB_INSERT_DATA, msg);       // SQLite에 Insert
                    myDataBase.dbClose();

                    final String currentmsg = edt_chat.getText().toString();

                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            sender.sendToMsg(currentmsg);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            mAdapter.addMsg(msg);            // 리스트 뷰에 추가 (채팅 UI)
                            manager.scrollToPosition(mAdapter.getItemCount() - 1);
                            edt_chat.setText("");           // 입력창 리셋
                        }

                    }.execute();
                }
            }
        });
    }

    private boolean checkDB(String chat_room) {

        myDataBase.dbOpen();
        recentData = myDataBase.selectAll(DBSqlData.SQL_DB_SELECT_DATA, chat_room);
        myDataBase.dbClose();

        Log.d("171220", "Chat Size = " + recentData.size());

        return (recentData.size() == 0) ? true : false;            // 현재 이 방에서 첫 채팅인 경우 true 아닐경우 false
    }

    public void SocketThreadStop() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(socket != null) {
            try {
                socket.close();
                Log.d("171219", "Socket disconnect");
            }
            catch (IOException e) {

            }
        }
            }
        }, 200);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(listener != null) {
            listener.socketClosed();
        }

        SocketThreadStop();
        // 메시지를 여기서 보내도 될듯싶다
        NetworkTask.sendingFcmMessage(friend_ad_id, my_ad_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String re = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(re.equals("NaN")) {
                            Toast.makeText(ChattingActivity.this, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("171218", re);
                        }
                    }
                });
            }
        });
        recentData.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home :
                finish();
                break;

            case R.id.action_chat :      // ShareActivity에서 업로드
                if(myDataBase != null) {

                    Intent intent = new Intent();
                    intent.putExtra("USER", friend_ad_id);      // ChatFragment에서 chatting ArrayList에서 삭제
                    setResult(RESULT_OK, intent);

                    // SQLite에서 삭제 (방 정보 테이블, 채팅 테이블)
                    myDataBase.dbOpen();
                    myDataBase.deleteData(DBSqlData.SQL_DB_DELETE_ROOM_DATA, chat_room);
                    myDataBase.deleteData(DBSqlData.SQL_DB_DELETE_DATA, chat_room);
                    myDataBase.deleteData(DBSqlData.SQL_DB_DELETE_TEMP_USER, friend_ad_id);
                    myDataBase.dbClose();

                    finish();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteChatRoom(String chat_room) {
        DataBases db = new DataBases(ChattingActivity.this);
        db.dbOpen();
        db.deleteData(DBSqlData.SQL_DB_DELETE_ROOM_DATA, chat_room);
        db.deleteData(DBSqlData.SQL_DB_DELETE_DATA, chat_room);
        db.dbClose();
    }
}
