package com.example.zealo.tapandfragment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.zealo.tapandfragment.InfoActivity;
import com.example.zealo.tapandfragment.Models.GymLike;
import com.example.zealo.tapandfragment.Models.GymListItem;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.example.zealo.tapandfragment.Util.UtilBitmapPool;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-31.
 */

public class FavoriteGymAdapter extends RecyclerView.Adapter<FavoriteGymAdapter.GymHolder> {

    private Context context;
    private Activity activity;

    // We need Model Class!!!
    private ArrayList<GymListItem> items;
    private final String GYM_MAP_IMG = NetworkTask.BASE_URL + "hellofit/gym/";

    private String rate;
    private String name;

    public FavoriteGymAdapter(Context context, Activity activity, GymListItem[] items, String name) {
        this.context = context;
        this.activity = activity;
        this.items = new ArrayList<>();
        for(GymListItem i : items) {
            this.items.add(i);
        }
        this.name = name;
    }

    @Override
    public GymHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_gym, null);

        GymHolder holder = new GymHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final GymHolder holder, final int position) {

        Glide.with(context)
                .load(GYM_MAP_IMG + items.get(position).getGym_map_img())
                .bitmapTransform(new RoundedCornersTransformation(new UtilBitmapPool(), 10, 0))
                .into(holder.iv_favorite_gym_profile);
        holder.tx_favorite_gym_name.setText(items.get(position).getGym_name());

        rate = items.get(position).getGym_rate();

        if(rate.length() > 4) {
            rate = rate.substring(0,3);
        }

        if(rate.equals("") || rate == null) {
            rate = "0명";
            holder.rb_favorite_star.setRating(0);
        }
        else {
            holder.rb_favorite_star.setRating(Float.valueOf(rate));
        }
        holder.tx_favorite_num.setText(items.get(position).getGym_like());

        holder.iv_favorite_gym_profile.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("GYM_LIKE", holder.tx_favorite_num.getText().toString());
                context.startActivity(intent);
            }
        });

        // 버튼 텍스트는 사용자 상태에 따라서 삭제 or 추가
        if(name.equals("LISTACTIVITY")) {
            NetworkTask.checkingMyGym(SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN")
                    , items.get(position).getGym_name()
                    , new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String chk = response.body().string();

                    if(chk.equals("1")) {
                        changeButtonText(holder.btn_favorite_del, "추가");
                    }
                    else {
                        changeButtonText(holder.btn_favorite_del, "삭제");
                    }
                }
            });
        }

        //삭제버튼
        holder.btn_favorite_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btn_favorite_del.getText().toString().equals("삭제")) {

                    NetworkTask.addGym(0,
                            SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"),
                            items.get(position).getGym_name());

                    if(name.equals("LISTACTIVITY")) {
                        holder.btn_favorite_del.setText("추가");
                    }
                    else {
                        items.remove(position);
                        notifyDataSetChanged();
                    }
                }
                else {  // 추가
                    NetworkTask.addGym(1,
                            SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"),
                            items.get(position).getGym_name());

                    holder.btn_favorite_del.setText("삭제");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class GymHolder extends RecyclerView.ViewHolder {

        ImageView iv_favorite_gym_profile;
        TextView tx_favorite_gym_name;
        TextView tx_favorite_num;
        RatingBar rb_favorite_star;
        Button btn_favorite_del;

        public GymHolder(View itemView) {
            super(itemView);
            iv_favorite_gym_profile = (ImageView)itemView.findViewById(R.id.Iv_favorite_gym_profile);
            tx_favorite_gym_name = (TextView)itemView.findViewById(R.id.Tx_favorite_gym_name);
            tx_favorite_num = (TextView)itemView.findViewById(R.id.Tx_favorite_num);
            rb_favorite_star = (RatingBar)itemView.findViewById(R.id.Rb_favorite_star);
            btn_favorite_del = (Button)itemView.findViewById(R.id.Btn_favorite_del);
        }
    }

    private void changeButtonText(final Button btn, final String text) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setText(text);
            }
        });
    }
}
