package com.example.zealo.tapandfragment.FCMMessage;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-12-14.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String refreshedToken = null;

    public MyFirebaseInstanceIDService() {

    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("171215", "Refreshed token: " + refreshedToken);
    }

    public static void sendRegistrationToServer(final Context context) {
        // TODO: Implement this method to send token to your app server.
        if (refreshedToken!=null) {            // 앱을 재설치하거나 데이터 삭제를 하는 경우가 아닐 때 Null값을 반환하기때문에 잡아줘야 한다.
            String user_ad_id = SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN");
            NetworkTask.registerFcmToken(user_ad_id, refreshedToken, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String re = response.body().string();

                    if(re.equals("FAIL")) {
                        Log.d("171215", "Token register failed");
                    }
                    else {
                        Log.d("171215", "Token registered");
                    }
                }
            });
        }
    }
}
