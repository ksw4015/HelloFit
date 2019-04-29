package com.example.zealo.tapandfragment.Models;

/**
 * Created by zealo on 2017-12-13.
 */

public class RoomData {

    private int roomNum;
    private String user_ad_id;

    public RoomData(int roomNum, String user_ad_id) {
        this.roomNum = roomNum;
        this.user_ad_id = user_ad_id;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public String getUser_ad_id() {
        return user_ad_id;
    }

    public void setRoomNum(int roomNum) {
        this.roomNum = roomNum;
    }

    public void setUser_ad_id(String user_ad_id) {
        this.user_ad_id = user_ad_id;
    }
}
