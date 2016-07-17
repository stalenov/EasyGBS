package com.kotsokrat.easygbs;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.CardView;

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
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Button clicked");
                new UpdateScreenData().execute(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new UpdateScreenData().execute(this);

    }

    class UpdateScreenData extends AsyncTask<Context, Void, Integer>{
        GBSLoader gbsLoader;
        JSONObject data;
        @Override
        protected Integer doInBackground(Context... contexts) {
            gbsLoader = new GBSLoader(contexts[0]);
            int status = gbsLoader.checkChanges();
            Log.d("bla", Integer.toString(status));
            switch (status) {
                case GBSLoader.CHNG_HASH_CHANGED:
                    data = gbsLoader.loadPrefs();
                    gbsLoader.savePrefs();
                    break;
                case GBSLoader.CHNG_FLAG_ENABLED:
                    gbsLoader.savePrefs();
                    data = gbsLoader.loadPrefs();
                    break;
                case GBSLoader.CHNG_ERR_CONNECT:
                    break;
                case GBSLoader.CHNG_HASH_EQUAL:
                    data = gbsLoader.loadPrefs();
                    break;
                default:
                    break;
            }
            return status;
        }


        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            switch (status) {

                case GBSLoader.CHNG_ERR_CONNECT:
                    updateDateTextView(LINK_DOWN);
                    break;
                case GBSLoader.CHNG_HASH_CHANGED:
                    updateDateTextView(LINK_OK);
                    //GBSLoader gbsLoader = new GBSLoader(MainActivity.this);
                    //JSONObject data = gbsLoader.loadPrefs();
                    try {
                        tvFirestTea.setText(data.getString(GBSLoader.DATA_FIRSTTEA));
                        tvLunch.setText(data.getString(GBSLoader.DATA_LUNCH));
                        tvInfo.setText(data.getString(GBSLoader.DATA_INFO));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GBSLoader.CHNG_HASH_EQUAL:
                case GBSLoader.CHNG_FLAG_ENABLED:
                    updateDateTextView(LINK_OK);
                    //gbsLoader = new GBSLoader(MainActivity.this);

                    try {
                        tvFirestTea.setText(getString(R.string.noDatayet));
                        tvLunch.setText(getString(R.string.noDatayet));
                        tvInfo.setText(data.getString(GBSLoader.DATA_INFO));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    void updateDateTextView(boolean linkOk){
        if (linkOk) {
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String curDate = getText(R.string.statusLoaded) + " " + df.format(Calendar.getInstance().getTime());
            tvStatus.setText(curDate);
        } else {
            tvStatus.setText(getText(R.string.network_error));
        }
    }
}
