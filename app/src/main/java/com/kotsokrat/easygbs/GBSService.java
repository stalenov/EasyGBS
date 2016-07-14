package com.kotsokrat.easygbs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GBSService extends Service {
    public Context ctx = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);



    }

    // TODO
    // TODO
    // TODO
    // TODO
    // TODO





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
