package com.example.zealo.tapandfragment.NetWork;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by USER on 2017-12-06.
 */

public class SocketSender extends Thread {

    Socket socket;
    DataOutputStream out;
    String name;

    public SocketSender(Socket socket, String name) {

        this.socket = socket;
        this.name = name;

        try{
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            if(out != null) {
                out.writeUTF(name);    // 첫 연결 시 sender:receiver 로 문자열 전달
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToMsg(String msg) {

        try {
            if(out != null) {
                out.writeUTF(msg);
            }
            else{
                Log.d("171207", "out : null");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
