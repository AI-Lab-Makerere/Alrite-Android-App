package com.ug.air.alrite.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.BackendRequests;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Counter;
import com.ug.air.alrite.Utils.Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Intent i = null;
    LinearLayout logo;
    File[] contents;
    DatabaseHelper databaseHelper;
    public static final String APP_OPENING_COUNT = "app_opening_count";
    public static final String LEARN_OPENING_COUNT = "learn_opening_count";
    public static final String RR_COUNTER_COUNT = "rr_counter_count";
    public static final String MANUAL_COUNT = "manual_count";
    public static final String COUNTING_DATA = "counter_file";
    public static final String BRONCHODILATOR_COUNT = "bronchodilator_count";
    public static final String STRIDOR_COUNT = "stridor_count";
    public static final String NASAL_COUNT = "nasal_count";
    public static final String GRANT_COUNT = "grant_count";
    public static final String WHEEZING_COUNT = "wheezing_count";
    public static final String CHESTINDRWAING_COUNT = "chest_indrawing_count";
    public static final String ECZEMA_COUNT = "eczema_count";

    // TODO: change this if you want to get from online
    Boolean shouldGetNewAssessment = true;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.logo);

        databaseHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSplashTimeout(2000);
    }

    private void startSplashTimeout(int timeout){
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(1500);
        animation1.setStartOffset(100);
        animation1.setFillAfter(true);
        logo.startAnimation(animation1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createFileCounter();
            }
        }, timeout);
    }

    private void createFileCounter() {
        File file = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/counter_file.xml");
        if (!file.exists()) {
            sharedPreferences = getSharedPreferences(COUNTING_DATA, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString(APP_OPENING_COUNT, "0");
            editor.putString(LEARN_OPENING_COUNT, "0");
            editor.putString(RR_COUNTER_COUNT, "0");
            editor.putString(BRONCHODILATOR_COUNT, "0");
            editor.putString(STRIDOR_COUNT, "0");
            editor.putString(NASAL_COUNT, "0");
            editor.putString(GRANT_COUNT, "0");
            editor.putString(WHEEZING_COUNT, "0");
            editor.putString(CHESTINDRWAING_COUNT, "0");
            editor.putString(ECZEMA_COUNT, "0");
            editor.putString(MANUAL_COUNT, "0");
            editor.apply();
        }
        checkDatabase();
        getCurrentAssessmentIfConnectedToInternet();
    }

    private void checkDatabase() {
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/databases/alrite.db");
        if (src.exists()) {

            Credentials credentials = new Credentials();
            String username = credentials.creds(this).getUsername();
            if (!username.equals("None")) {
                Counter counter = new Counter();
                counter.Count(this, APP_OPENING_COUNT);
            }
        } else {
            databaseHelper.insertData(1, "None", "None", "None", "None", "None", "None");
        }
        i = new Intent(SplashActivity.this, Dashboard.class);
        startActivity(i);
        finish();
    }

    private void getCurrentAssessmentIfConnectedToInternet() {
        if (isNetworkAvailable() && shouldGetNewAssessment) {
            BackendRequests dtJson = ApiClient.getClient(ApiClient.REMOTE_URL_TEMP).create(BackendRequests.class);
            Call<String> call = dtJson.getJson("test");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (!response.isSuccessful()) {
                        System.out.println("no site, plase try again");
                        return;
                    }

                    // We've received a response! We can turn this into a JSONObject
                    // and store the result locally
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body());

                        File directory = getFilesDir();
                        File assessment = new File(directory, "assessment.json");
                        if (assessment.exists()) {
                            assessment.delete();
                            assessment = new File(directory, "assessment.json");
                        }
                        Writer output = null;
                        output = new BufferedWriter(new FileWriter(assessment));
                        output.write(json.toString());
                        output.close();

                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}