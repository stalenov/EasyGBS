package com.kotsokrat.easygbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GBSLoader {

    // адрес сервиса
    public final String URL_ADDRESS = "http://84.204.30.84:56565/restEasyGBS/index.php";

    // типы запросов к сервису
    public final static String HTTP_GET_TYPE_HASH = "hash";
    public final static String HTTP_GET_TYPE_DATA = "data";

    // поля данных в получаемых JSON и лоакльном файлике
    public final static String DATA_FIRSTTEA = "firsttea";
    public final static String DATA_LUNCH = "lunch";
    public final static String DATA_INFO = "info";
    public final static String DATA_FLAG = "flag";
    public final static String DATA_HASH = "hash";

    // состояния, возвращаемые методом checkChanges
    public final static int CHNG_HASH_EQUAL = 0;
    public final static int CHNG_HASH_CHANGED = 1;
    public final static int CHNG_FLAG_ENABLED = 2;
    public final static int CHNG_ERR_CONNECT = 3;

    SharedPreferences sPref;
    Context context;

    GBSLoader(Context context){
        this.context = context;
        //checkChanges();
    }

    // проверка изменения состояния
    public int checkChanges(){
        String hashPrefs = loadPrefsHash();

        try {
            JSONObject httpData = loadHTTP(HTTP_GET_TYPE_HASH);
            String hashHttp = httpData.getString(DATA_HASH);
            String flag = httpData.getString(DATA_FLAG);
            Log.d("myLog", "checkChanges method started");

            if (flag.equals(Integer.toString(1))) {
                if (!(hashHttp.equals(hashPrefs))) savePrefs();
                return CHNG_FLAG_ENABLED;
            }
            if (hashHttp.equals(hashPrefs)){
                return CHNG_HASH_EQUAL;
            } else {
                savePrefs();
                return CHNG_HASH_CHANGED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CHNG_ERR_CONNECT;
        }
    }

    public String loadPrefsHash(){
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sPref.getString(DATA_HASH, "");
    }

    // загрузка всех данных из локального файла
    public JSONObject loadPrefs(){
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put(DATA_FIRSTTEA, sPref.getString(DATA_FIRSTTEA, ""));
            jsonData.put(DATA_LUNCH, sPref.getString(DATA_LUNCH, ""));
            jsonData.put(DATA_INFO, sPref.getString(DATA_INFO, ""));
            jsonData.put(DATA_HASH, sPref.getString(DATA_HASH, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    // сохранение данных в локальный файл
    public boolean savePrefs(){
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sPref.edit();
        try {
            JSONObject jsonData = loadHTTP(HTTP_GET_TYPE_DATA);
            ed.putString(DATA_FIRSTTEA, jsonData.getString(DATA_FIRSTTEA));
            ed.putString(DATA_LUNCH, jsonData.getString(DATA_LUNCH));
            ed.putString(DATA_INFO, jsonData.getString(DATA_INFO));
            ed.putString(DATA_HASH, jsonData.getString(DATA_HASH));
            ed.commit();
            return true;
        }catch (Exception e){
            return false;
            //e.printStackTrace();
        }
    }



    protected JSONObject loadHTTP(String type) {
        try {
            URL url = new URL(new StringBuilder().append(URL_ADDRESS).append("?type=").append(type).toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            StringBuilder buf = new StringBuilder();
            while ((line = br.readLine()) != null) {
                buf.append(line);
            }
            urlConnection.disconnect();
            return new JSONObject(buf.toString());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
