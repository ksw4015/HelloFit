package com.example.zealo.tapandfragment.Ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zealo.tapandfragment.Adapter.MainRecyclerAdapter;
import com.example.zealo.tapandfragment.FCMMessage.MyFirebaseInstanceIDService;
import com.example.zealo.tapandfragment.MainActivity;
import com.example.zealo.tapandfragment.Models.MainGymModel;
import com.example.zealo.tapandfragment.Models.StoreItem;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Util.DeviceUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-25.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{

    private RecyclerView list_main;
    public MainRecyclerAdapter mainRecyclerAdapter;

    private boolean HOME_STARTED = false;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d("171120", "Home Fragment Created");

        if(!MainActivity.main_page_loaded) {     // 홈 프래그먼트 버튼을 누를때마다 실행되는것을 방지하기위해 boolean값 추가
            // 사용자 닉네임 설정 전 임시닉네임으로 구글계정 아이디 사용
            String temp_nick = SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.user_email), "NaN");

            if(!temp_nick.equals("NaN")) {
                temp_nick = temp_nick.substring(0, temp_nick.indexOf("@"));    // @gmail.com 부분 버림
            }

            NetworkTask.LoginGoogle(
                    SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.Google_Play_ID), "NaN"),
                    temp_nick,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            final String msg = response.body().string();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(msg.equals("1")) {
                                        Toast.makeText(getContext(), "가입을 축하합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "다시 오셨네요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            MainActivity.main_page_loaded = true;
                        }
                    });

            MyFirebaseInstanceIDService.sendRegistrationToServer(getContext());
        }

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        list_main = (RecyclerView)view.findViewById(R.id.List_main_gym);
        list_main.setLayoutManager(manager);
        list_main.setHasFixedSize(true);

        String[] location = ((MainActivity)getActivity()).getMainLocation();

        NetworkTask.selectMainItem(location[0], location[1], new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String json = response.body().string();
                Log.d("171115", json);

                getActivity().runOnUiThread(new Runnable() {    // Null
                    @Override
                    public void run() {
                        if(json.equals("NaN")) {        // 서버에 연결전 위치값 체크를 하게 바꾸는게 좋을듯
                            Toast.makeText(getContext(), "위치정보가 올바르지 않습니다. 앱을 재실행 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Gson gson = new Gson();
                            final MainGymModel[] items = gson.fromJson(json, MainGymModel[].class);

                            mainRecyclerAdapter = new MainRecyclerAdapter(items, getContext(), DeviceUtil.getDevicewidth(getActivity()), getActivity());
                            list_main.setAdapter(mainRecyclerAdapter);
                        }
                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).toggleButton(0);
        if(!HOME_STARTED) {
            HOME_STARTED = true;
        }
        else {
            Log.d("171122", "notifyDataSetChanged");
            mainRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
