package com.ug.air.alrite.Worker;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.REASSESS;
import static com.ug.air.alrite.Fragments.Patient.Initials.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Initials.PARENT_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;

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

public class NotifyWorker extends Worker {

    private NotificationManager notificationManager;
    File[] contents;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;
    Uri alarmSound;
    Vibrator vibrator;
    String s, progress;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), alarmSound);
//        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Result doWork() {
        readData();
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mediaPlayer.stop();
//            }
//        });
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readData() {
        String filename = getInputData().getString("filename");
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (src.exists()){
            contents = src.listFiles();
            if (contents.length != 0) {
                for (File f : contents) {
                    if (f.isFile()) {
                        String name = f.getName().replace(".xml", "");
                        if (name.equals(filename)){
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(name, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String cin = sharedPreferences.getString(CHILD_INITIALS, "");
                            String pin = sharedPreferences.getString(PARENT_INITIALS, "");
                            String age = sharedPreferences.getString(AGE_IN_YEARS, "");
                            String gender = sharedPreferences.getString(SEX, "");
                            String[] split = age.split("\\.");
                            String ag = split[0] + " years and " + split[1] + " months";
                            editor.putBoolean(REASSESS, true);
                            editor.apply();
                            Log.d("Background Task", "Executed successfully");
//                            audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                            switch (audioManager.getRingerMode()){
//                                case AudioManager.RINGER_MODE_NORMAL:
//                                    mediaPlayer.start();
//                                    break;
//                                case AudioManager.RINGER_MODE_SILENT:
//                                    break;
//                                case AudioManager.RINGER_MODE_VIBRATE:
//                                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
//                                    break;
//                            }
                            s = "Patient " + cin + " of Age " + ag + " is ready for reassessment";
                            createForegroundInfo(s);
                        }
                    }
                }
            }
        }
    }

    private void createForegroundInfo(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        Context context = getApplicationContext();
        String id = "alright_1";
        String title = "Patient Ready for Reassessment";
        String cancel = "Cancel";
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

//        Intent intent1 = new Intent(context, Bronchodilator.class);
//        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent1, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.alrite_logo)
                .setContentText(progress)
                .setAutoCancel(true)
//                .setContentIntent(contentIntent)
                .addAction(android.R.drawable.ic_delete, cancel, intent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(4, notification.build());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel
        CharSequence name = "Worker Channel";
        String description = progress;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("alright_1", name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel);

    }

}
