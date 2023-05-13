package com.ug.air.alrite.Worker;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.REASON;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotifyWorker2 extends Worker {

    private NotificationManager notificationManager2;
    File[] contents;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    Uri alarmSound;
    Vibrator vibrator;
    String s, progress;
    int value = 0;

    public NotifyWorker2(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager2 = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), alarmSound);
//        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        readData2();
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mediaPlayer.stop();
//            }
//        });
//        Log.d("Background Task", "Executed successfully");
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readData2() {
        Date currentTime = Calendar.getInstance().getTime();
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (src.exists()){
            contents = src.listFiles();
            if (contents.length != 0) {
                for (File f : contents) {
                    if (f.isFile()) {
                        String name = f.getName().replace(".xml", "");
                        if (!name.equals("sharedPrefs") && !name.equals("counter_file")){
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(name, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String bron = sharedPreferences.getString(USED_BRONCHODILATOR, "");
                            String fin = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
                            String incomplete = sharedPreferences.getString(ASSESS_INCOMPLETE, "");
                            if (incomplete.isEmpty() && bron.equals("Bronchodialtor Given") && fin.isEmpty()){
                                String date = sharedPreferences.getString(DATE, "");
                                try {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                                    Date newDate = df.parse(date);
                                    long diff = currentTime.getTime() - newDate.getTime();
                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                                    if (minutes >= 30 && minutes < 240){
                                        value = value + 1;
                                    }else if (minutes >= 240){
                                        editor.putString(USED_BRONCHODILATOR, "Bronchodialtor Not Given");
                                        editor.putString(REASON, "A 4 hour time period elapsed");
                                        editor.apply();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                Log.d("Background Task", "Executed successfully");
                if (value > 0) {
                    if (value == 1){
                        s = "There is one patient ready for reassessment";
                    }else {
                        s = "There are " + value + " patients ready for reassessment";
                    }
//                    audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                    switch (audioManager.getRingerMode()){
//                        case AudioManager.RINGER_MODE_NORMAL:
//                            mediaPlayer.start();
//                            break;
//                        case AudioManager.RINGER_MODE_SILENT:
//                            break;
//                        case AudioManager.RINGER_MODE_VIBRATE:
//                            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
//                            break;
//                    }
                    createForegroundInfo2(s);
                }

            }
        }
    }

    private void createForegroundInfo2(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        Context context2 = getApplicationContext();
        String id2 = "alright_2";
        String title2 = "Other Patients Ready for Reassessment";
        String cancel2 = "Cancel";
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent2 = WorkManager.getInstance(context2)
                .createCancelPendingIntent(getId());

//        Intent intent1 = new Intent(context, Bronchodilator.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent1, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel2();
        }

        NotificationCompat.Builder notification2 = new NotificationCompat.Builder(context2, id2)
                .setContentTitle(title2)
                .setSmallIcon(R.drawable.alrite_logo)
                .setContentText(progress)
                .setAutoCancel(true)
//                .setContentIntent(contentIntent)
                .addAction(android.R.drawable.ic_delete, cancel2, intent2);

        NotificationManagerCompat notificationManagerCompat2 = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat2.notify(8, notification2.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel2() {
        // Create a Notification channel
        CharSequence name2 = "Checking patients ready for reassessment";
        String description2 = progress;
        int importance2 = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel2 = new NotificationChannel("alright_2", name2, importance2);
        channel2.setDescription(description2);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager2.createNotificationChannel(channel2);

    }
}
