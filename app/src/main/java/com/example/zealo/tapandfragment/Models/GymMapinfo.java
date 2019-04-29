package com.example.zealo.tapandfragment.Models;

/**
 * Created by zealo on 2017-11-22.
 */

public class GymMapinfo {
    private String gym_name;
    private String gym_map_img;
    private String gym_address;
    private String gym_lat;
    private String gym_lng;
    private String earth_distance;
    private String gym_rate;
    private String gym_like;

    public GymMapinfo(String gym_name, String gym_map_img, String gym_address, String gym_lat, String gym_lng, String earth_distance, String gym_rate, String gym_like) {
        this.gym_name = gym_name;
        this.gym_map_img = gym_map_img;
        this.gym_address = gym_address;
        this.gym_lat = gym_lat;
        this.gym_lng = gym_lng;
        this.earth_distance = earth_distance;
        this.gym_rate = gym_rate;
        this.gym_like = gym_like;
    }

    public String getGym_name() {
        return gym_name;
    }

    public String getGym_map_img() {
        return gym_map_img;
    }

    public String getGym_address() {
        return gym_address;
    }

    public String getGym_lat() {
        return gym_lat;
    }

    public String getGym_lng() {
        return gym_lng;
    }

    public String getGym_rate() {
        return gym_rate;
    }

    public String getGym_like() {
        return gym_like;
    }
}
