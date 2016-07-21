package com.kotsokrat.easygbs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlertDelayChanger {
    //private static Context context;
    private static SharedPreferences prefs;
    private static AlarmManager am;
    //private static Intent notificatorIntent;
    private static PendingIntent notificationPendingIntent;
    private static final String tag = "myTag";

    AlertDelayChanger(Context context) {
        //this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent notificatorIntent = new Intent(context ,GBSNotificator.class);
        notificationPendingIntent = PendingIntent.getBroadcast(context, 0, notificatorIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public void set(){
        int delay = Integer.parseInt(prefs.getString("update_delay", "15"));
        long notificationDelay = 60000 * delay;
        if (delay != 0) am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), notificationDelay, notificationPendingIntent);
        Log.d(tag, "AlertDelayChanger setted, " + delay);
    }

    public void remove(){
        am.cancel(notificationPendingIntent);
        Log.d(tag, "AlertDelayChanger removed");
    }

    public void change(){
        remove();
        set();
        Log.d(tag, "AlertDelayChanger change started");
    }



}
