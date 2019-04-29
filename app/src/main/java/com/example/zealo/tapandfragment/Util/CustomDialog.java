package com.example.zealo.tapandfragment.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.zealo.tapandfragment.NetWork.NetworkTask;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;

/**
 * Created by zealo on 2017-11-10.
 */

public class CustomDialog {

    public static AlertDialog RatingDialog(final Context context, Activity activity, final String gym_name, String my_rate) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rating, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        RatingBar rb_set_gym_rating = (RatingBar)view.findViewById(R.id.Rb_set_gym_rating);
        rb_set_gym_rating.setRating(Float.valueOf(my_rate));
        TextView tx_rating_name = (TextView)view.findViewById(R.id.Tx_rating_name);
        tx_rating_name.setText(gym_name);

        rb_set_gym_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                NetworkTask.insertGymRate(SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"), gym_name, String.valueOf(rating));
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        return dialog;
    }
}
