package com.example.zealo.tapandfragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.zealo.tapandfragment.PreferencesManager.SharedPreferenceManager;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

/**
 * Created by zealo on 2017-10-30.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    LinearLayout lay_login_button;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!SharedPreferenceManager.getPreference(LoginActivity.this, getString(R.string.Google_Play_ID), getString(R.string.NaN)).equals(getString(R.string.NaN))){
            GoMain();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                        .requestEmail()
                                                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                                            .enableAutoManage(LoginActivity.this, this)
                                            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                            .build();

        lay_login_button = (LinearLayout)findViewById(R.id.lay_login);

        lay_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPreferenceManager.getPreference(LoginActivity.this, getString(R.string.Google_Play_ID), getString(R.string.NaN)).equals(getString(R.string.NaN))){
                    signIn();
                }
                else {
                    GoMain();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {   // RC_SIGN_IN = 9001

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            final GoogleSignInAccount account = result.getSignInAccount();

            if(result.isSuccess()) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            SharedPreferenceManager.setPreference(LoginActivity.this, getString(R.string.Google_Play_ID), AdvertisingIdClient.getAdvertisingIdInfo(LoginActivity.this).getId());
                            SharedPreferenceManager.setPreference(LoginActivity.this, getString(R.string.user_email), account.getEmail());
                        }
                        catch (IOException e) {

                        }
                        catch (GooglePlayServicesNotAvailableException e) {
                            Toast.makeText(LoginActivity.this, "Google Play Services error", Toast.LENGTH_SHORT).show();
                        }
                        catch (GooglePlayServicesRepairableException e) {

                        }
                        return null;
                    }
                }.execute();

                GoMain();
                signOut();

                Log.d("171030", "Email : " + account.getEmail() + "\n" +
                                "Name : " + account.getDisplayName());

                // Email 주소를 User_info 테이블에 Insert하는 코드도 추가
            }
            else {
                Toast.makeText(LoginActivity.this, "구글 계정으로 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GoMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {       // 구글 계정 선택 창 오픈
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(LoginActivity.this, "Google Play Services error", Toast.LENGTH_SHORT).show();
    }
}
