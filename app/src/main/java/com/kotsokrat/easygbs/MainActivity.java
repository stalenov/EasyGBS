package com.kotsokrat.easygbs;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    final String tag = "myTag";
    final int LINK_OK = 0;
    final int LINK_DOWN = 1;
    final int UPDATE_ALL_DATA = 0;
    final int UPDATE_ONLY_INFO = 1;
    AlertDelayChanger adc;
    TextView tvLunch, tvFirestTea, tvInfo, tvStatus;
    Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLunch = (TextView) findViewById(R.id.tvLunch);
        tvFirestTea = (TextView) findViewById(R.id.tvFirstTea);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        /*tvStatus = (TextView) findViewById(R.id.tvStatus);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Button clicked");
                new UpdateData().execute(MainActivity.this);
            }
        }); */
        adc = new AlertDelayChanger(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adc.remove();
        new UpdateData().execute(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        adc.set();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //handler.removeCallbacks(runnableDataUpdater);
        adc.set();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.add(1, 1, 1, getString(R.string.menu_pref));
        getMenuInflater().inflate(R.menu.generalmenu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_preferences:
                //Intent prefIntent = new Intent(this, PrefActivity.class);
                Intent prefIntent = new Intent(this, PreferActivity.class);
                startActivity(prefIntent);
                break;
            case R.id.action_refresh:
                new UpdateData().execute(MainActivity.this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class UpdateData extends AsyncTask<Context, Void, Integer>{
        GBSLoader gbsLoader = new GBSLoader(MainActivity.this);
        HashMap<String,String> data;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = gbsLoader.loadPrefsFromFile();
            if (data.containsKey(GBSLoader.DATA_TIMESTAMP)) {     // иначе при первом запуске ляжет, т.к. файл пустой
                Long currentTimeStamp = System.currentTimeMillis() / 1000;
                Long savedTimeStamp = Long.parseLong(data.get(GBSLoader.DATA_TIMESTAMP));
                if (currentTimeStamp < savedTimeStamp + 3600) {
                    updateDataInViews(data);
                }
            }
        }


        @Override
        protected Integer doInBackground(Context... contexts) {

            int status = gbsLoader.checkChanges();
            switch (status) {
                case GBSLoader.CHNG_HASH_CHANGED:
                    data = gbsLoader.loadPrefsFromFile();
                    break;
                case GBSLoader.CHNG_FLAG_ENABLED:
                    data = gbsLoader.loadPrefsFromFile();
                    break;
                case GBSLoader.CHNG_ERR_CONNECT:
                    break;
                case GBSLoader.CHNG_HASH_EQUAL:
                    data = gbsLoader.loadPrefsFromFile();
                    break;
                default:
                    break;
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            data = gbsLoader.loadPrefsFromFile();
            switch (status) {
                case GBSLoader.CHNG_ERR_CONNECT:
                    updateDateTextView(LINK_DOWN);
                    break;
                case GBSLoader.CHNG_HASH_EQUAL:
                case GBSLoader.CHNG_HASH_CHANGED:
                    updateDateTextView(LINK_OK);
                    updateDataInViews(data);
                    break;
                case GBSLoader.CHNG_FLAG_ENABLED:
                    updateDateTextView(LINK_OK);
                    updateDataInViews(data);
                    break;
            }
        }
    }

    void updateDataInViews(HashMap<String, String> data){
        if (data.get(GBSLoader.DATA_FLAG) == "") return; // если запускается первый раз, то и данных не будет
        int flagType = Integer.parseInt(data.get(GBSLoader.DATA_FLAG));
        switch (flagType) {
            case UPDATE_ONLY_INFO:
                Log.d(tag, "Update views method: only info");
                tvFirestTea.setText(getString(R.string.noDatayet));
                tvLunch.setText(getString(R.string.noDatayet));
                tvInfo.setText(data.get(GBSLoader.DATA_INFO));
                break;
            case UPDATE_ALL_DATA:
                Log.d(tag, "Update views method: all data");
                tvFirestTea.setText(data.get(GBSLoader.DATA_FIRSTTEA));
                tvLunch.setText(data.get(GBSLoader.DATA_LUNCH));
                tvInfo.setText(data.get(GBSLoader.DATA_INFO));
                break;
            default:
                break;
        }
    }

    void updateDateTextView(int inetLinkState){
        switch (inetLinkState){
            case LINK_OK:
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                String curDate = getText(R.string.data_updated_at) + " " + df.format(Calendar.getInstance().getTime());
                //tvStatus.setText(curDate);
                Toast.makeText(MainActivity.this, curDate, Toast.LENGTH_SHORT).show();
                break;
            case LINK_DOWN:
                Toast.makeText(MainActivity.this, getText(R.string.network_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
