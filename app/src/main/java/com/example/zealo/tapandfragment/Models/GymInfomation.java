package com.example.zealo.tapandfragment.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-11-15.
 */

public class GymInfomation implements Serializable{

    private String gym_address;
    private String gym_phone;
    private String gym_open;
    private String gym_holiday;
    private String gym_lat;
    private String gym_lng;

    public GymInfomation(String gym_address, String gym_phone, String gym_open, String gym_holiday, String gym_lat, String gym_lng) {
        this.gym_address = gym_address;
        this.gym_phone = gym_phone;
        this.gym_open = gym_open;
        this.gym_holiday = gym_holiday;
        this.gym_lat = gym_lat;
        this.gym_lng = gym_lng;
    }

    public String getGym_address() {
        return gym_address;
    }

    public String getGym_phone() {
        return gym_phone;
    }

    public String getGym_open() {
        return gym_open;
    }

    public String getGym_holiday() {
        return gym_holiday;
    }

    public String getGym_lat() {
        return gym_lat;
    }

    public String getGym_lng() {
        return gym_lng;
    }
}
