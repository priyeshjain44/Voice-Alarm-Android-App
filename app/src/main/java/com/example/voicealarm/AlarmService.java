package com.example.voicealarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import java.io.IOException;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class AlarmService extends Service {

    static MediaPlayer mediaPlayer;
    String title,AudioPath,reqCode;
    static NotificationManager alarmNotificationManager;
    NotificationCompat.Builder builder;

    private static final String channel_id = "CHANNEL_ID";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("abc", "AlarmService");
            AudioPath = intent.getStringExtra("audiopath");
            title = intent.getStringExtra("title");
            reqCode = intent.getStringExtra("reqCode");
            Log.d("abc", title);

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(AudioPath);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            Log.d("abc", reqCode);
            sendNotification(title);
            return START_STICKY;
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        stopForeground(true);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(reqCode), "Voice alarm", importance);
            channel.setDescription("My voice alarm notification channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String msg) {
        createNotificationChannel();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
        wl.acquire();
        Log.d("abc", "Preparing to send notification...: " + msg);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(reqCode), intent, 0);
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent i = new Intent(AlarmService.this, NotificationBroadcastReceiver.class);
        i.putExtra("not_id",Integer.parseInt(reqCode));
        PendingIntent stopIntent = pendingIntent.getBroadcast(this, Integer.parseInt(reqCode), i, PendingIntent.FLAG_ONE_SHOT);

        builder = new NotificationCompat.Builder(this, reqCode)
                .setSmallIcon(R.drawable.ic_notifications_on_white_24dp)
                .setContentTitle("Voice Alarm")
                .setContentText(title)
                .setContentIntent(stopIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_snooze_black_24dp, getString(R.string.stop), stopIntent);

        startForeground(Integer.parseInt(reqCode),builder.build());
        alarmNotificationManager.notify(Integer.parseInt(reqCode), builder.build());
        Log.d("abc", "Notification sent.");
    }

}
