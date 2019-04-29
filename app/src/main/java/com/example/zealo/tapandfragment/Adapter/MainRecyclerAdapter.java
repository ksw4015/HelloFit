package com.example.zealo.tapandfragment.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.InfoActivity;
import com.example.zealo.tapandfragment.Models.GymLike;
import com.example.zealo.tapandfragment.Models.MainGymModel;
import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zealo on 2017-10-30.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyHolder> implements Filterable{

    // Need Model Class
    private MainGymModel[] items;                         // 오리지널 아이템 (거리순으로 Max 20개)
    private ArrayList<MainGymModel> filteredItems;       // 필터링 된 아이템
    private Context context;
    private int imgWidth;
    private Activity activity;

    private final String GYM_MAIN_IMG = NetworkTask.BASE_URL + "hellofit/gym/";
    private ItemFilter mFilter = new ItemFilter();

    public MainRecyclerAdapter(MainGymModel[] items, Context context, int imgWidth, Activity activity) {
        this.items = items;
        this.context = context;
        this.imgWidth = imgWidth;
        this.activity = activity;
        this.filteredItems = new ArrayList<>(Arrays.asList(items));    // 배열 -> List
    }

    @Override
    public MainRecyclerAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, null);

        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MainRecyclerAdapter.MyHolder holder, final int position) {
//
//        Glide.with(context).load(GYM_MAIN_IMG + items[position].getGym_main_img()).override(imgWidth, imgWidth/2).centerCrop().into(holder.iv_gym_img);
//        holder.tx_gym_name.setText(items[position].getGym_name());
//        holder.tx_distance.setText(items[position].getDistance().substring(0, 5) + "m");
//
//        String rate = items[position].getGym_rate();
        Glide.with(context).load(GYM_MAIN_IMG + filteredItems.get(position).getGym_main_img()).override(imgWidth, imgWidth/2).centerCrop().into(holder.iv_gym_img);
        holder.tx_gym_name.setText(filteredItems.get(position).getGym_name());
        holder.tx_distance.setText(filteredItems.get(position).getDistance().substring(0, 5) + "m");

        String rate = filteredItems.get(position).getGym_rate();

        if(rate.length() > 4) {
            rate = rate.substring(0,3);
        }

        if(rate.equals("") || rate == null) {
            holder.tx_avg_star.setText("0명");
        }
        else {
            holder.tx_avg_star.setText(rate);
        }
//        holder.tx_client_num.setText(items[position].getGym_like());
        holder.tx_client_num.setText(filteredItems.get(position).getGym_like());

        holder.iv_gym_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, InfoActivity.class);
//                i.putExtra("GYM_NAME", items[position].getGym_name());
//                i.putExtra("GYM_IMG", GYM_MAIN_IMG + items[position].getGym_main_img());
                i.putExtra("GYM_NAME", filteredItems.get(position).getGym_name());
                i.putExtra("GYM_IMG", GYM_MAIN_IMG + filteredItems.get(position).getGym_main_img());
                i.putExtra("GYM_RATE", holder.tx_avg_star.getText().toString());
                i.putExtra("GYM_LIKE", holder.tx_client_num.getText().toString());
                i.putExtra("GYM_STAR", String.valueOf(holder.btn_favorite.getTag()));
                // 각 텍스트가 비어 있을 수 있음...
                context.startActivity(i);
            }
        });

        NetworkTask.checkingMyGym(SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN")
                , items[position].getGym_name()
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        final String check = response.body().string();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(check.equals("2")) {
                                    holder.btn_favorite.setImageResource(R.drawable.ic_star_white_36dp);
                                    holder.btn_favorite.setTag("YES");
                                }
                                else {
                                    holder.btn_favorite.setImageResource(R.drawable.ic_star_border_white_38dp);
                                    holder.btn_favorite.setTag("NO");
                                }
                            }
                        });
                    }
                });

        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btn_favorite.getTag().equals("YES")) {
                    holder.btn_favorite.setTag("NO");
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    changeStar(holder.btn_favorite, R.drawable.ic_star_border_white_38dp);
//                    NetworkTask.addGym(0, SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"), items[position].getGym_name());
                    NetworkTask.addGym(0, SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"), filteredItems.get(position).getGym_name());
                }
                else {
                    holder.btn_favorite.setTag("YES");
                    Toast.makeText(context, "즐겨찾기 추가", Toast.LENGTH_SHORT).show();
                    changeStar(holder.btn_favorite, R.drawable.ic_star_white_36dp);
//                    NetworkTask.addGym(1, SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"), items[position].getGym_name());
                    NetworkTask.addGym(1, SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"), filteredItems.get(position).getGym_name());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView iv_gym_img;
        TextView tx_gym_name;
        TextView tx_avg_star;
        TextView tx_client_num;
        TextView tx_distance;
        ImageView btn_favorite;

        public MyHolder(View itemView) {
            super(itemView);
            iv_gym_img = (ImageView)itemView.findViewById(R.id.Iv_gym_img);
            tx_gym_name = (TextView)itemView.findViewById(R.id.Tx_gym_name);
            tx_avg_star = (TextView)itemView.findViewById(R.id.Tx_avg_star);
            tx_client_num = (TextView)itemView.findViewById(R.id.Tx_client_num);
            tx_distance = (TextView)itemView.findViewById(R.id.Tx_distance);
            btn_favorite = (ImageView)itemView.findViewById(R.id.Btn_favorite);
        }
    }

    private void changeStar(final ImageView imageView, final int star_img) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(star_img);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString();

            FilterResults filterResults = new FilterResults();

            ArrayList<MainGymModel> nList = new ArrayList<>();

            for(MainGymModel i : items) {
                if(i.getGym_name().contains(filterString)) {
                    nList.add(i);
                }
            }

            filterResults.values = nList;
            filterResults.count = nList.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (ArrayList<MainGymModel>) results.values;
            notifyDataSetChanged();
        }
    }
}
