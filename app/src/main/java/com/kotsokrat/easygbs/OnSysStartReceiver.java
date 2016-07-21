package com.kotsokrat.easygbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnSysStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertDelayChanger adc = new AlertDelayChanger(context);
        adc.set();
    }
}
