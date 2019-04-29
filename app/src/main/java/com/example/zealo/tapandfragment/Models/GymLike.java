package com.example.zealo.tapandfragment.Models;

/**
 * Created by zealo on 2017-11-17.
 */

public class GymLike {
    private String gym_rate;
    private String gym_like;

    public GymLike(String gym_rate, String gym_like) {
        this.gym_rate = gym_rate;
        this.gym_like = gym_like;
    }

    public String getGym_rate() {
        return gym_rate;
    }

    public String getGym_like() {
        return gym_like;
    }
}
