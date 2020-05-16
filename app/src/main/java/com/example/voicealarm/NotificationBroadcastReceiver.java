package com.example.voicealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("abc","NBR");
        AlarmService.mediaPlayer.stop();
        int notificationId = intent.getIntExtra( "not_id" , 0 ) ;
        AlarmService.alarmNotificationManager.cancel(notificationId);
        Intent i = new Intent(context, AlarmService.class);
        context.stopService(i);
    }
}
