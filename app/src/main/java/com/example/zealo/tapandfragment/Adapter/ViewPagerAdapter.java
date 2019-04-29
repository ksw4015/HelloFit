package com.example.zealo.tapandfragment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.InfoActivity;
import com.example.zealo.tapandfragment.Models.GymLike;
import com.example.zealo.tapandfragment.Models.GymMapinfo;
import com.example.zealo.tapandfragment.Models.MarkerItem;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-28.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private final String GYM_MAP_IMG = NetworkTask.BASE_URL + "hellofit/gym/";

    LayoutInflater inflater;
    ArrayList<GymMapinfo> items = new ArrayList<>();

    Context context;
    Activity activity;

    String rate;

    public ViewPagerAdapter(Activity activity, Context context, LayoutInflater inflater, GymMapinfo[] items) {
        this.activity = activity;
        this.context = context;
        this.inflater = inflater;
        for(GymMapinfo i : items) {
            this.items.add(i);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = null;

        view = inflater.inflate(R.layout.viewpager_item, null);

        TextView v_pager_name = (TextView)view.findViewById(R.id.v_pager_name);
        v_pager_name.setText(items.get(position).getGym_name());

        TextView v_pager_address = (TextView)view.findViewById(R.id.v_pager_address);
        v_pager_address.setText(items.get(position).getGym_address());

        ImageView v_pager_img = (ImageView)view.findViewById(R.id.v_pager_img);
        Glide.with(context).load(GYM_MAP_IMG + items.get(position).getGym_map_img()).into(v_pager_img);

        final TextView v_pager_rate = (TextView)view.findViewById(R.id.v_pager_rate);
        final TextView v_pager_like = (TextView)view.findViewById(R.id.v_pager_like);

        rate = items.get(position).getGym_rate();

        if(rate.length() > 4) {   // 3.75..... 0~3번째까지 즉 소숫점 2번째자리 까지만 출력
            rate = rate.substring(0,3);
        }

        if(rate.equals("") || rate == null) {
            v_pager_rate.setText("0");
        }
        else {
            v_pager_rate.setText(rate);
        }

        v_pager_like.setText(items.get(position).getGym_like());

//        NetworkTask.selectGymLike(items.get(position).getGym_name(), new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//
//                Gson gson = new Gson();
//
//                final GymLike like = gson.fromJson(json, GymLike.class);
//
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        rate = like.getGym_rate();
//
//                        if(rate.length() > 4) {
//                            rate = rate.substring(0,3);
//                        }
//
//                        if(rate.equals("") || rate == null) {
//                            v_pager_rate.setText("0");
//                        }
//                        else {
//                            v_pager_rate.setText(rate);
//                        }
//
//                        v_pager_like.setText(like.getGym_like());
//                    }
//                });
//            }
//        });

        v_pager_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String main = items.get(position).getGym_map_img().replace("map", "main");

                if(main.contains("png")) {
                    main = main.replace("png", "jpg");
                }

                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra("GYM_NAME", items.get(position).getGym_name());
                intent.putExtra("GYM_IMG", GYM_MAP_IMG + main);
                intent.putExtra("GYM_RATE", rate);
                intent.putExtra("GYM_LIKE", v_pager_like.getText().toString());
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
