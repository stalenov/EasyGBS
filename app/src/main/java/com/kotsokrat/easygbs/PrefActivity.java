package com.kotsokrat.easygbs;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlertDelayChanger adt = new AlertDelayChanger(this);
        adt.change();
    }
}
