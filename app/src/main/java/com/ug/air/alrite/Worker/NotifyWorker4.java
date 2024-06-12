package com.ug.air.alrite.Worker;

import static android.content.Context.NOTIFICATION_SERVICE;

import static com.ug.air.alrite.Activities.SplashActivity.APP_OPENING_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.BRONCHODILATOR_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.CHESTINDRWAING_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.COUNTING_DATA;
import static com.ug.air.alrite.Activities.SplashActivity.ECZEMA_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.GRANT_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.LEARN_OPENING_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.NASAL_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.RR_COUNTER_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.STRIDOR_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.WHEEZING_COUNT;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifyWorker4 extends Worker {

    private NotificationManager notificationManager3;
    File[] contents;
    String s, progress;
    int value = 0;
    String token;
    public static JsonPlaceHolder jsonPlaceHolder;
    Call<String> call;
    DatabaseHelper databaseHelper;
    NotificationManagerCompat notificationManagerCompat22;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public NotifyWorker4(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
//        notificationManager3 = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor res = databaseHelper.getData("1");
        while (res.moveToNext()){
            token =  res.getString(2);
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        readData3();
        return Result.success();
    }

    private void readData3() {
        jsonPlaceHolder = ApiClient.getClient(ApiClient.BASE_URL).create(JsonPlaceHolder.class);
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/counter_file.xml");
        if (src.exists()){
            RequestBody filePart = RequestBody.create(MediaType.parse("*/*"), src);
            MultipartBody.Part fileUpload = MultipartBody.Part.createFormData("counter", src.getName() ,filePart);

            Call<String> call = jsonPlaceHolder.sendCounterFile("Token " + token, fileUpload);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!response.isSuccessful()){
                        Log.d("Alrite server issue", "There is an issue with the server" );
                        return;
                    }
                    String message = response.body();
                    Log.d("Alrite app: ", "Sever response: " + message);
                    sharedPreferences = getApplicationContext().getSharedPreferences(COUNTING_DATA, Context.MODE_PRIVATE);
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
                    editor.apply();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Alrite server issue", "No internet connection" );
//                                    Toast.makeText(getActivity(), "Please turn on your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("Background Task", "Counter Executed successfully");
        }
    }
}
