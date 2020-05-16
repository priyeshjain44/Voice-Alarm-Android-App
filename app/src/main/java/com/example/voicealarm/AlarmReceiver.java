package com.example.voicealarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    int id;
    String AudioPath, reqCode, title;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("abc", "AlarmReceiver");

        AudioPath = intent.getStringExtra("audiopath");
        title = intent.getStringExtra("title");
        reqCode = intent.getStringExtra("reqCode");

        Intent i = new Intent(context, AlarmService.class);
        i.putExtra("audiopath", AudioPath);
        i.putExtra("reqCode", reqCode);
        i.putExtra("title", title);
        context.startService(i);

        }
}
