package com.example.zealo.tapandfragment.PreferencesManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.zealo.tapandfragment.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zealo on 2017-10-30.
 */

public class SharedPreferenceManager {

    public static void setPreference(Context context, String key, String value) {       // 구글 로그인 시 email 주소랑 구글 AD ID 저장하는데 사용중
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }
}
