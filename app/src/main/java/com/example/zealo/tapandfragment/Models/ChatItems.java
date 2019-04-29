package com.example.zealo.tapandfragment.Models;

/**
 * Created by USER on 2017-12-04.
 */

public class ChatItems {

    private String user_ad_id;
    private String chat_room_num;
    private String chat_posted_date;
    private String chat_posted_time;
    private String chat_message_text;

    public ChatItems(String user_ad_id, String chat_room_num, String chat_posted_date, String chat_posted_time, String chat_message_text) {
        this.user_ad_id = user_ad_id;
        this.chat_room_num = chat_room_num;
        this.chat_posted_date = chat_posted_date;
        this.chat_posted_time = chat_posted_time;
        this.chat_message_text = chat_message_text;
    }

    public String getUser_ad_id() {
        return user_ad_id;
    }

    public String getChat_room_num() {
        return chat_room_num;
    }

    public String getChat_posted_date() {
        return chat_posted_date;
    }

    public String getChat_posted_time() {
        return chat_posted_time;
    }

    public String getChat_message_text() {
        return chat_message_text;
    }

    public void setUser_ad_id(String user_ad_id) {
        this.user_ad_id = user_ad_id;
    }

    public void setChat_room_num(String chat_room_num) {
        this.chat_room_num = chat_room_num;
    }

    public void setChat_posted_date(String chat_posted_date) {
        this.chat_posted_date = chat_posted_date;
    }

    public void setChat_posted_time(String chat_posted_time) {
        this.chat_posted_time = chat_posted_time;
    }

    public void setChat_message_text(String chat_message_text) {
        this.chat_message_text = chat_message_text;
    }

    public String[] chatArgs() {

        String[] args = {
                this.user_ad_id,
                this.chat_room_num,
                this.chat_posted_date,
                this.chat_posted_time,
                this.chat_message_text
        };

        return args;
    }
}
