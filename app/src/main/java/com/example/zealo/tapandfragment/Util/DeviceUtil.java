package com.example.zealo.tapandfragment.Util;

import android.app.Activity;
import android.view.Display;

/**
 * Created by zealo on 2017-11-01.
 */

public class DeviceUtil {

    public static int getDevicewidth(Activity activity) {
        Display display =  activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

}
