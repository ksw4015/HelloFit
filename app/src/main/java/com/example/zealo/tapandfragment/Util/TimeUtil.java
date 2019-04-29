package com.example.zealo.tapandfragment.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by USER on 2017-12-07.
 */

public class TimeUtil {

    public static Date refreshDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        return date;
    }

    public static String getTime() {

        SimpleDateFormat format = new SimpleDateFormat("hh:mm:aa");

        String[] time = format.format(refreshDate()).split(":");  // hh:mm:ss:aa

        if(time[0].contains("0")) {
            time[0] = time[0].replace("0", "");
        }

        if(time[2].equals("am")) {
            time[2] = "오전";
        }
        else {
            time[2] = "오후";
        }

        String correctTime = time[2]+ " " + time[0] + ":" + time[1];

        return correctTime;
    }

    public static String getDate() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String[] date = format.format(refreshDate()).split("-");  // hh:mm:ss:aa

        String correctDate = date[0] + "년 " + date[1] + "월 " + date[2] + "일";

        return correctDate;
    }

}
