package com.example.zealo.tapandfragment.Models;

/**
 * Created by zealo on 2017-11-15.
 */

public class MainGymModel {

    private String gym_name;
    private String gym_main_img;
    private String earth_distance;
    private String gym_rate;
    private String gym_like;

    public MainGymModel(String gym_name, String gym_main_img, String earth_distance, String gym_rate, String gym_like) {
        this.gym_name = gym_name;
        this.gym_main_img = gym_main_img;
        this.earth_distance = earth_distance;
        this.gym_rate = gym_rate;
        this.gym_like = gym_like;
    }

    public String getGym_name() {
        return gym_name;
    }

    public String getGym_main_img() {
        return gym_main_img;
    }

    public String getDistance() {
        return earth_distance;
    }

    public String getGym_rate() {
        return gym_rate;
    }

    public String getGym_like() {
        return gym_like;
    }
}
