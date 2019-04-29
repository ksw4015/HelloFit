package com.example.zealo.tapandfragment.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-11-17.
 */

public class GymListItem implements Serializable{
    private String gym_name;
    private String gym_map_img;
    private String gym_rate;
    private String gym_like;

    public GymListItem(String gym_name, String gym_map_img, String gym_rate, String gym_like) {
        this.gym_name = gym_name;
        this.gym_map_img = gym_map_img;
        this.gym_rate = gym_rate;
        this.gym_like = gym_like;
    }

    public String getGym_name() {
        return gym_name;
    }

    public String getGym_map_img() {
        return gym_map_img;
    }

    public String getGym_rate() {
        return gym_rate;
    }

    public String getGym_like() {
        return gym_like;
    }
}
