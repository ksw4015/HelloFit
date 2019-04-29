package com.example.zealo.tapandfragment.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-11-17.
 */

public class UserProfile implements Serializable{
    private String user_ad_id;
    private String user_desc;
    private String user_nickname;
    private String user_profile_img;

    public UserProfile(String user_ad_id, String user_desc, String user_nickname, String user_profile_img) {
        this.user_ad_id = user_ad_id;
        this.user_desc = user_desc;
        this.user_nickname = user_nickname;
        this.user_profile_img = user_profile_img;
    }

    public String getUser_ad_id() {
        return user_ad_id;
    }

    public String getUser_desc() {
        return user_desc;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_profile_img() {
        return user_profile_img;
    }

    public void setUser_ad_id(String user_ad_id) {
        this.user_ad_id = user_ad_id;
    }

    public void setUser_desc(String user_desc) {
        this.user_desc = user_desc;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setUser_profile_img(String user_profile_img) {
        this.user_profile_img = user_profile_img;
    }
}
