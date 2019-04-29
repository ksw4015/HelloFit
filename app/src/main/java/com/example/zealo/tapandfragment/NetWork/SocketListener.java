package com.example.zealo.tapandfragment.NetWork;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.ChatRecyclerAdapter;
import com.example.zealo.tapandfragment.DataBases.DBSqlData;
import com.example.zealo.tapandfragment.DataBases.DataBases;
import com.example.zealo.tapandfragment.Models.ChatItems;
import com.example.zealo.tapandfragment.Util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by USER on 2017-12-06.
 */

public class SocketListener extends Thread {

    private String receiveMsg;
    private String room_num;
    private DataBases mDB;

    Socket socket;
    DataInputStream in;

    // Ui Update
    Activity activity;
    ChatRecyclerAdapter adapter;
    LinearLayoutManager manager;

    boolean COMPLETE_MESSAGE = true;
    public boolean SOCKET_CLOSED = true;

    public SocketListener(Socket socket, ChatRecyclerAdapter adapter, Activity activity, LinearLayoutManager manager, DataBases mDataBase, String room_num) {
        this.socket = socket;
        this.adapter = adapter;
        this.activity = activity;
        this.manager = manager;
        this.mDB = mDataBase;
        this.room_num = room_num;
        try {
            in = new DataInputStream(socket.getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        while (in != null) {

            if (socket.isClosed()) {       // 소켓 연결 해제 시 쓰레드 종료
                Log.d("171213", "listener thread interruped");
                break;
            } else {
                try {
                    receiveMsg = in.readUTF();
                    Log.d("171207", receiveMsg);
                } catch (IOException e) {
                    Log.d("171207", "" + e);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (receiveMsg != null) {

                            if (!receiveMsg.equals("NaN")) {
                                if(SOCKET_CLOSED) {
                                    String[] spilt = receiveMsg.split(":");

                                    ChatItems receiveItem = new ChatItems(
                                            spilt[1],                      // Sender's Nick name
                                            room_num,           // 소켓연결 된 상태인 ChattingActivity에서 실행되기 때문에 방의 룸 넘버를 받아올수 있음
                                            TimeUtil.getDate(),
                                            TimeUtil.getTime(),
                                            spilt[0]                       // Receive Message
                                    );

                                    adapter.addMsg(receiveItem);
                                    manager.scrollToPosition(adapter.getItemCount() - 1);

                                    // Save a Receive Message in SQLite
                                    mDB.dbOpen();
                                    mDB.insertData(DBSqlData.SQL_DB_INSERT_DATA, receiveItem);
                                    mDB.dbClose();

                                    Log.d("171219", "New Message");
                                }
                                else {
                                    Log.d("171219", "Socket Closed");
                                }
                            } else {
                                Log.d("171218", "No User");
                            }

                        }

                    }
                });
            }
        }
    }

    public void socketClosed() {
        SOCKET_CLOSED = false;
    }

}
