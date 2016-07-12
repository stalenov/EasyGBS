package com.kotsokrat.easygbs;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    final String tag = "myTag";

    final boolean LINK_OK = true;
    final boolean LINK_DOWN = false;

    TextView tvLunch, tvFirestTea, tvInfo, tvStatus;
    Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLunch = (TextView) findViewById(R.id.tvLunch);
        tvFirestTea = (TextView) findViewById(R.id.tvFirstTea);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GBSLoader gbsLoader = new GBSLoader(this);
            switch (gbsLoader.checkChanges()) {
                case GBSLoader.CHNG_HASH_CHANGED:
                    // обновляем данные в файле
                    Log.d(tag, "HASH changed");
                    gbsLoader.savePrefs();
                    // обновляем данные в активити
                    try {
                        JSONObject data = gbsLoader.loadPrefs();
                        tvFirestTea.setText(data.getString(GBSLoader.DATA_FIRSTTEA));
                        tvLunch.setText(data.getString(GBSLoader.DATA_LUNCH));
                        tvInfo.setText(data.getString(GBSLoader.DATA_INFO));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    // TODO добавить нотифик в сервис

                    // выставляем последнюю дату обновления
                    updateDateTextView(LINK_OK);
                    break;
                case GBSLoader.CHNG_FLAG_ENABLED:
                    gbsLoader.savePrefs();
                    try {
                        JSONObject data = gbsLoader.loadPrefs();
                        tvFirestTea.setText(getString(R.string.noDatayet));
                        tvLunch.setText(getString(R.string.noDatayet));
                        tvInfo.setText(data.getString(GBSLoader.DATA_INFO));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    // выставляем последнюю дату обновления
                    updateDateTextView(LINK_OK);
                    Log.d(tag, "FLAG enabled");
                    break;
                case GBSLoader.CHNG_ERR_CONNECT:
                    updateDateTextView(LINK_DOWN);
                    Log.d(tag, "ERR connect");
                    break;
                case GBSLoader.CHNG_HASH_EQUAL:
                    updateDateTextView(LINK_OK);
                    // и не делаем нифига (ура-ура!) =)
                    Log.d(tag, "HASH equal");
                    break;
                default:
                    break;
            }
    }

    void updateDateTextView(boolean linkOk){
        if (linkOk) {
            DateFormat df = new SimpleDateFormat("HH:mm");
            String curDate = getText(R.string.statusLoaded) + " " + df.format(Calendar.getInstance().getTime());
            tvStatus.setText(curDate);
        } else {
            tvStatus.setText(getText(R.string.network_error));
        }
    }


}
