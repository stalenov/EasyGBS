package com.kotsokrat.easygbs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class GBSService extends Service {
    public Context ctx = this;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);

        GBSLoader gbsLoader = new GBSLoader(this);
        switch (gbsLoader.checkChanges()) {
            case GBSLoader.CHNG_HASH_CHANGED:
                // обновляем данные в файле
                gbsLoader.savePrefs();
                JSONObject data = gbsLoader.loadPrefs();
                // обновляем данные в активити

                // генерим нотифик
                break;

            case GBSLoader.CHNG_FLAG_ENABLED:

                break;
            case GBSLoader.CHNG_ERR_CONNECT:

                break;
            case GBSLoader.CHNG_HASH_EQUAL:
                // не делаем нифига (ура-ура!) =)
                break;
            default:
                break;
        }



/*        try {


            new GBSLoader().checkChanges(ctx);
        } catch (Exception e) {

        }*/



    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
