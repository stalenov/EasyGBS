package com.kotsokrat.easygbs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class GBSNotificator extends BroadcastReceiver {
    Context context;
    private static final int NOTIFY_ID = 5051;

    @Override
    public void onReceive(Context context,Intent intent){
        this.context = context;
        Log.d("myTag","event!!!");
        new CheckHttpState().execute(context);
    }


    private class CheckHttpState extends AsyncTask<Context, Void, Integer>{
        @Override
        protected Integer doInBackground(Context... context) {
            GBSLoader gbsLoader=new GBSLoader(context[0]);
            return gbsLoader.checkChanges();
        }

        @Override
        protected void onPostExecute(Integer state) {
            super.onPostExecute(state);
            if(state==GBSLoader.CHNG_HASH_CHANGED){
                Log.d("myTag","httpStateCHanged");

                Intent notificationIntent = new Intent(context, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                Resources res = context.getResources();
                Notification.Builder builder = new Notification.Builder(context);
                Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                builder.setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_local_gas_station_white_48dp)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setSound(soundUri)
                        .setTicker("Меню столовой обновлено")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setLights(Color.YELLOW, 10, 3000)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                        .setContentTitle("Меню столовой обновлено")
                        .setContentText("И вот что там обещают..");
                Notification notification = builder.build();

                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFY_ID, notification);
            }else{
                Log.d("myTag","Other state");
            }
        }
    }
}
