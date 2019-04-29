package com.example.zealo.tapandfragment.Models;

/**
 * Created by zealo on 2017-10-26.
 */

public class StoreItem {

    private String name;
    private String address;
    private String phone;

    public StoreItem(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
