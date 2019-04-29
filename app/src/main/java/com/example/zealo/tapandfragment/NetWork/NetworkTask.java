package com.example.zealo.tapandfragment.NetWork;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.example.zealo.tapandfragment.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zealo on 2017-10-26.
 */

public class NetworkTask {   // SingleTone
    private OkHttpClient client = new OkHttpClient();
    private static NetworkTask INSTANCE = new NetworkTask();

    public static NetworkTask getInstance() {

        if(INSTANCE == null) {
            INSTANCE = new NetworkTask();
        }

        if(INSTANCE.client == null) {
            INSTANCE.client = new OkHttpClient();
        }

        return INSTANCE;
    }

    public static final String BASE_URL = "http://218.155.154.110:8080/";
    private static final String LOGIN_GOOGLE = BASE_URL + "hellofit/php/login_google.php";
    private static final String SELECT_MAIN = BASE_URL + "hellofit/php/select_all_gyms.php";
    private static final String SELECT_MAP = BASE_URL + "hellofit/php/select_all_gym_map.php";
    private static final String SELECT_INFO_MAP = BASE_URL + "hellofit/php/select_gym_map.php";
    private static final String SELECT_GYM_INFO = BASE_URL + "hellofit/php/select_gym_info.php";
    private static final String SELECT_GYM_LIKE_AND_RATE = BASE_URL + "hellofit/php/select_rate_and_like.php";
    private static final String SELECT_GYM_CLIENT = BASE_URL + "hellofit/php/select_gym_client.php";
    private static final String SELECT_USER_IMG = BASE_URL + "hellofit/php/select_user_img.php";
    private static final String SELECT_FAVORITE = BASE_URL + "hellofit/php/select_friend_user.php";

    private static final String CHEKING_MY_FRIEND = BASE_URL + "hellofit/php/checking_my_friend.php";
    private static final String CHEKING_MY_GYM = BASE_URL + "hellofit/php/checking_my_gym.php";
    private static final String ADD_FRIEND = BASE_URL + "hellofit/php/add_friend.php";
    private static final String ADD_GYM = BASE_URL + "hellofit/php/add_gym.php";
    private static final String SELECT_CHAT_FRIEND = BASE_URL + "hellofit/php/select_chat_user.php";

    private static final String SELECT_MY_PROFILE = BASE_URL + "hellofit/php/select_my_profile.php";  //
    private static final String INSERT_GYM_RATE = BASE_URL + "hellofit/php/insert_gym_rate.php";
    private static final String SELECT_MY_RATE = BASE_URL + "hellofit/php/select_my_rate.php";

    private static final String UP_LOAD_USER_IMG = BASE_URL + "upload_user_image.php";
    private static final String UPDATE_IMG_DESC = BASE_URL + "hellofit/php/update_user_img_desc.php";
    private static final String UPDATE_MY_PROFILE = BASE_URL + "update_my_profile.php";

    private static final String REGISTER_FCM_TOKEN = BASE_URL + "hellofit/php/register_fcm_token.php";
    private static final String SENDING_FCM_MESSAGE = BASE_URL + "hellofit/php/send_fcm_msg.php";

    private static final String RECEIVE_REMAIN_MSG = BASE_URL + "hellofit/php/select_remain_msg.php";


    public static void receiveRemainMsg(final String user_id, final Callback callback) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).build();

                Request request = new Request.Builder().url(RECEIVE_REMAIN_MSG).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void sendingFcmMessage(final String receive_user_id, final String send_user_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("receiver_id", receive_user_id)
                        .add("sender_id", send_user_id)
                        .build();

                Request request = new Request.Builder().url(SENDING_FCM_MESSAGE).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void registerFcmToken(final String user_id, final String token, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).add("token", token).build();

                Request request = new Request.Builder().url(REGISTER_FCM_TOKEN).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void downLoadImage(final String ImgURLs, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                Request request = new Request.Builder().url(ImgURLs).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void updateMyProfile(final Context context, final String user_nick, final String user_desc, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("user_id", SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"))
                        .add("user_desc", user_desc)
                        .add("user_nick", user_nick)
                        .build();

                Request request = new Request.Builder().url(UPDATE_MY_PROFILE).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void updateMyProfile(final Context context, final String user_nick, final String user_desc, final String user_profile_img, final int width, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                byte[] byteArray = null;

                try {
                    Bitmap up_img = Glide.with(context).load(user_profile_img).asBitmap().into(width/2, width/2).get();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    up_img.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byteArray = stream.toByteArray();
                }
                catch (InterruptedException e) {
                    Log.d("171124", "InterruptedException");
                }
                catch (ExecutionException e) {
                    Log.d("171124", "ExecutionException");
                }

                String user_profile = SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN") + "_" + "profile.jpg";
                Log.d("171128", user_profile);
                OkHttpClient client = getInstance().client;

                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("user_profile", user_profile, RequestBody.create(MediaType.parse("image/*"), byteArray))
                        .addFormDataPart("user_id", SharedPreferenceManager.getPreference(context, context.getResources().getString(R.string.Google_Play_ID), "NaN"))
                        .addFormDataPart("user_desc", user_desc)
                        .addFormDataPart("user_nick", user_nick)
                        .build();

                Request request = new Request.Builder().url(UPDATE_MY_PROFILE).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void updateUserImgDesc(final String user_id, final String user_desc, final String user_img, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("user_id", user_id)
                        .add("user_desc", user_desc)
                        .add("user_img", user_img)
                        .build();

                Request request = new Request.Builder().url(UPDATE_IMG_DESC).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void upLoadUserImg(final String user_id, final String filename, final String user_desc, final int width,final Context context, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                byte[] byteArray = null;
                byte[] thumbArray = null;

                try {
                    Bitmap up_img = Glide.with(context).load(filename).asBitmap().into(width, width).get();     // 정사각형 모양으로 보여주기때문에 정사각형모양으로 Crop
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    up_img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byteArray = stream.toByteArray();

                    Bitmap thumb = Glide.with(context).load(filename).asBitmap().into(width/3, width/3).get();   // 썸네일은 1/3크기로 그리드뷰에 3개씩 보여주기때문에
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    thumb.compress(Bitmap.CompressFormat.JPEG, 80, stream1);
                    thumbArray = stream1.toByteArray();
                    Log.d("171124", "" + byteArray.length);
                }
                catch (InterruptedException e) {
                    Log.d("171124", "InterruptedException");
                }
                catch (ExecutionException e) {
                    Log.d("171124", "ExecutionException");
                }

                String user_img_name = filename.substring(filename.lastIndexOf("/") + 1);     // 실제 업로드 파일명은 .jpg파일명만 올리게 subString
                Log.d("171124", user_img_name);

                OkHttpClient client = getInstance().client;

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_img", user_img_name, RequestBody.create(MediaType.parse("image/*"), byteArray))
                        .addFormDataPart("user_thumb", user_img_name, RequestBody.create(MediaType.parse("image/*"), thumbArray))
                        .addFormDataPart("user_id", user_id)
                        .addFormDataPart("user_desc", user_desc)
                        .build();

                Request request = new Request.Builder().url(UP_LOAD_USER_IMG).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectInfoMap(final String gym_name, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("gym_name", gym_name).build();

                Request request = new Request.Builder().url(SELECT_INFO_MAP).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void selectGymMap(final String lat, final String lng, final String radius, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("lat", lat).add("lng", lng).add("radius", radius).build();

                Request request = new Request.Builder().url(SELECT_MAP).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectMyRate(final String user_id, final String gym_name, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).add("gym_name", gym_name).build();

                Request request = new Request.Builder().url(SELECT_MY_RATE).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void insertGymRate(final String user_id, final String gym_name, final String gym_star) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("user_id", user_id)
                        .add("gym_name", gym_name)
                        .add("gym_star", gym_star)
                        .build();

                Request request = new Request.Builder().url(INSERT_GYM_RATE).post(body).build();

                try {
                    client.newCall(request).execute();
                }
                catch (IOException e) {

                }

                return null;
            }
        }.execute();
    }

    public static void selectMyProfile(final String user_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).build();

                Request request = new Request.Builder().url(SELECT_MY_PROFILE).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectChatUser(final String user_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).build();

                Request request = new Request.Builder().url(SELECT_CHAT_FRIEND).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void addFriend(final int code, final String user_id, final String friend_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("code", String.valueOf(code)).add("user_id", user_id).add("friend_id", friend_id).build();

                Request request = new Request.Builder().url(ADD_FRIEND).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void addGym(final int code, final String user_id, final String gym_name) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("code", String.valueOf(code)).add("user_id", user_id).add("gym_name", gym_name).build();

                Request request = new Request.Builder().url(ADD_GYM).post(body).build();

                try{
                    client.newCall(request).execute();
                }
                catch (IOException e) {

                }

                return null;
            }
        }.execute();
    }

    public static void chekingFriend(final String user_id, final String friend_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).add("friend_id", friend_id).build();

                Request request = new Request.Builder().url(CHEKING_MY_FRIEND).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void LoginGoogle(final String user_ad_id, final String user_mail, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_ad_id).add("user_email", user_mail).build();

                Request request = new Request.Builder().url(LOGIN_GOOGLE).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void selectMainItem(final String lat, final String lng,final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                                                .add("lat", lat)
                                                .add("lng", lng)
                                                .build();

                Request request = new Request.Builder().url(SELECT_MAIN).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectInfoItem(final String gym_name, final Callback callback) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("gym_name", gym_name)
                        .build();

                Request request = new Request.Builder().url(SELECT_GYM_INFO).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectGymLike(final String gym_name, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("gym_name", gym_name)
                        .build();

                Request request = new Request.Builder().url(SELECT_GYM_LIKE_AND_RATE).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void checkingMyGym(final String user_id, final String gym_name, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("gym_name", gym_name)
                        .add("user_id", user_id)
                        .build();

                Request request = new Request.Builder().url(CHEKING_MY_GYM).post(body).build();

                client.newCall(request).enqueue(callback);
                return null;
            }
        }.execute();
    }

    public static void selectGymClient(final String gym_name, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder()
                        .add("gym_name", gym_name)
                        .build();

                Request request = new Request.Builder().url(SELECT_GYM_CLIENT).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectUserImg(final String user_id, final Callback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).build();

                Request request = new Request.Builder().url(SELECT_USER_IMG).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }

    public static void selectFavoriteList(final String user_id, final Callback callback) {
        new AsyncTask<Void, Void ,Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = getInstance().client;

                RequestBody body = new FormBody.Builder().add("user_id", user_id).build();

                Request request = new Request.Builder().url(SELECT_FAVORITE).post(body).build();

                client.newCall(request).enqueue(callback);

                return null;
            }
        }.execute();
    }
}
