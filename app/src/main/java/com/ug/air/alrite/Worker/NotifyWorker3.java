package com.ug.air.alrite.Worker;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Initials.STUDY_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Credentials;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifyWorker3 extends Worker {

    private NotificationManager notificationManager3;
    File[] contents;
    String s, progress;
    int value = 0;
    String token;
    public static JsonPlaceHolder jsonPlaceHolder;
    Call<String> call;
    DatabaseHelper databaseHelper;
    NotificationManagerCompat notificationManagerCompat22;
    Credentials credentials;

    public NotifyWorker3(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager3 = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        credentials = new Credentials();
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
//        createForeInfo();
        jsonPlaceHolder = ApiClient.getClient(ApiClient.BASE_URL).create(JsonPlaceHolder.class);
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (src.exists()){
            contents = src.listFiles();
            if (contents.length != 0) {
                for (File f : contents) {
                    if (f.isFile()) {
                        String name = f.getName();
                        if (!name.equals("sharedPrefs.xml") && !name.equals("counter_file.xml")) {
                            String names = name.replace(".xml", "");
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(names, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String bron = sharedPreferences.getString(USED_BRONCHODILATOR, "");
                            String fin = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
                            String incomplete = sharedPreferences.getString(ASSESS_INCOMPLETE, "");

                            if (!incomplete.isEmpty() || (bron.isEmpty() || bron.equals("Bronchodialtor Not Given") || !fin.isEmpty())){
                                String studyID = sharedPreferences.getString(STUDY_ID, "");
                                String code = credentials.creds2(getApplicationContext()).getCode();
                                String h_code = credentials.creds2(getApplicationContext()).getH_code();
                                String etCode = "AL"+h_code + "" + code + "" + studyID;
                                editor.putString(STUDY_ID, etCode);
                                editor.apply();

                                File patient = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + name);
                                RequestBody filePart = RequestBody.create(MediaType.parse("*/*"), patient);
                                MultipartBody.Part fileUpload = MultipartBody.Part.createFormData("patient", patient.getName() ,filePart);

                                Call<String> call = jsonPlaceHolder.sendFile("Token " + token, fileUpload);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()){
                                            Log.d("Alrite server issue", "There is an issue with the server" );
                                            return;
                                        }
                                        String message = response.body();
                                        Log.d("Alrite app: ", "Sever response: " + message);
                                        patient.delete();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("Alrite server issue", "No internet connection" );
//                                    Toast.makeText(getActivity(), "Please turn on your internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }
                }
//                notificationManagerCompat22.cancel(12);
                Log.d("Background Task", "Executed successfully");
            }
        }
    }

    private void createForeInfo() {
        Context context22 = getApplicationContext();
        String id22 = "alright_3";
        String title22 = "Data transfer";
        String cancel22 = "Cancel";
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent22 = WorkManager.getInstance(context22)
                .createCancelPendingIntent(getId());

//        Intent intent1 = new Intent(context, Bronchodilator.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent1, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChan();
        }

        NotificationCompat.Builder notification22 = new NotificationCompat.Builder(context22, id22)
                .setContentTitle(title22)
                .setSmallIcon(R.drawable.alrite_logo)
                .setContentText(progress)
                .setAutoCancel(true)
//                .setContentIntent(contentIntent)
                .addAction(android.R.drawable.ic_delete, cancel22, intent22);

        notificationManagerCompat22 = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat22.notify(12, notification22.build());

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChan() {
        // Create a Notification channel
        CharSequence nam = "Sending Data";
        String desc = "Data is being sent to the server";
        int imp = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel3 = new NotificationChannel("alright_3", nam, imp);
        channel3.setDescription(desc);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager3.createNotificationChannel(channel3);

    }
}
