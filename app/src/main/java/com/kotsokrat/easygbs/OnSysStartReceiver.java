package com.kotsokrat.easygbs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class OnSysStartReceiver extends BroadcastReceiver {
    int delay;
    final String tag = "myTag";

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        delay = Integer.parseInt(prefs.getString("update_delay", "15"));

        long notificationDelay = 60000 * delay;

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d(tag, "Start alarm manager" + Long.toString(notificationDelay));
        Intent notificatorIntent = new Intent(context ,GBSNotificator.class);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, 0, notificatorIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), notificationDelay, notificationPendingIntent);



    }
}
