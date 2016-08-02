package com.kotsokrat.easygbs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AlertDelayChanger {
    private static SharedPreferences prefs;
    private static AlarmManager am;
    private static PendingIntent notificationPendingIntent;

    AlertDelayChanger(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent notificatorIntent = new Intent(context ,GBSNotificator.class);
        notificationPendingIntent = PendingIntent.getBroadcast(context, 0, notificatorIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void set(){
        int delay = Integer.parseInt(prefs.getString("update_delay", "15"));
        long notificationDelay = 60000 * delay;
        if (delay != 0) am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), notificationDelay, notificationPendingIntent);
    }

    public void remove(){
        am.cancel(notificationPendingIntent);
    }

    public void change(){
        remove();
        set();
    }
}
