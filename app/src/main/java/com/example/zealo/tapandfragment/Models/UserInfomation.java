package com.example.zealo.tapandfragment.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-11-17.
 */

public class UserInfomation implements Serializable {

    private String user_img;
    private String user_img_desc;

    public UserInfomation(String user_img, String user_img_desc) {
        this.user_img = user_img;
        this.user_img_desc = user_img_desc;
    }

    public String getUser_img() {
        return user_img;
    }

    public String getUser_img_desc() {
        return user_img_desc;
    }
}
