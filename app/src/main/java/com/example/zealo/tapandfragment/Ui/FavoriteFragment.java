package com.example.zealo.tapandfragment.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.zealo.tapandfragment.Adapter.FavoriteGymAdapter;
import com.example.zealo.tapandfragment.Adapter.FriendRecyclerAdapter;
import com.example.zealo.tapandfragment.ListActivity;
import com.example.zealo.tapandfragment.MainActivity;
import com.example.zealo.tapandfragment.Models.GymListItem;
import com.example.zealo.tapandfragment.Models.UserProfile;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.ProfileActivity;
import com.example.zealo.tapandfragment.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-25.
 */

public class FavoriteFragment extends Fragment {

    public final static String tab_name_friendlist = "Favorite Friend";

    RecyclerView favorite_gym_list;
    RecyclerView favorite_friend_list;

    TextView tx_empty_friend;
    TextView tx_empty_gym;

    public FavoriteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        TabHost tabHost = (TabHost)view.findViewById(R.id.m_tabhost_favorite);
        tabHost.setup();

        TabHost.TabSpec tab_favorite_gym = tabHost.newTabSpec("Favorite Gym");
        tab_favorite_gym.setContent(R.id.content_favorite_gym_list);
        tab_favorite_gym.setIndicator("즐겨찾는 센터");
        tabHost.addTab(tab_favorite_gym);

        TabHost.TabSpec tab_favorite_friend = tabHost.newTabSpec(tab_name_friendlist);
        tab_favorite_friend.setContent(R.id.content_favorite_friend_list);
        tab_favorite_friend.setIndicator("친구목록");
        tabHost.addTab(tab_favorite_friend);

        favorite_gym_list = (RecyclerView)view.findViewById(R.id.favorite_gym_List);
        recyclerSetLayoutManager(favorite_gym_list);
        favorite_friend_list = (RecyclerView)view.findViewById(R.id.favorite_friend_List);
        recyclerSetLayoutManager(favorite_friend_list);

        tx_empty_friend = (TextView)view.findViewById(R.id.Tx_empty_friend);
        tx_empty_gym = (TextView)view.findViewById(R.id.Tx_empty_gym);

        NetworkTask.selectFavoriteList(SharedPreferenceManager.getPreference(getContext(), getResources().getString(R.string.Google_Play_ID), "NaN"), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("171120", json);

                if(!json.equals("NaN")) {
                    JsonObject object = new JsonParser().parse(json).getAsJsonObject();

                    Gson gson = new Gson();

                    final GymListItem[] gymlist;
                    final UserProfile[] users;

                    if(object.get("gym") != null) {
                        JsonArray gym = object.get("gym").getAsJsonArray();
                        gymlist = gson.fromJson(gym, GymListItem[].class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FavoriteGymAdapter gAdapter = new FavoriteGymAdapter(getContext(), getActivity(), gymlist, "FAVORITEFRAGMENT");
                                favorite_gym_list.setAdapter(gAdapter);
                            }
                        });
                    }
                    else {
                        setEmptyMsg(tx_empty_gym);  // NullPointer 발생
                    }

                    if(object.get("friend") != null) {
                        JsonArray friend = object.get("friend").getAsJsonArray();
                        users = gson.fromJson(friend, UserProfile[].class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FriendRecyclerAdapter favoriteAdapter = new FriendRecyclerAdapter(getContext(), getActivity(), tab_name_friendlist, users);
                                favorite_friend_list.setAdapter(favoriteAdapter);
                            }
                        });
                    }
                    else {
                        setEmptyMsg(tx_empty_friend);
                    }
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tx_empty_friend.setVisibility(View.VISIBLE);
                            tx_empty_gym.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        return view;
    }

    public void recyclerSetLayoutManager(RecyclerView mList) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(manager);
        mList.setHasFixedSize(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("171106", "favorite resume");
        ((MainActivity)getActivity()).toggleButton(2);
    }

    private void setEmptyMsg(final TextView tx) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tx.setVisibility(View.VISIBLE);
            }
        });
    }
}
