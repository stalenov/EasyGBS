package com.kotsokrat.easygbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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
    public final static String DATA_TIMESTAMP = "timestamp";

    // состояния, возвращаемые методом checkChanges
    public final static int CHNG_HASH_EQUAL = 0;
    public final static int CHNG_HASH_CHANGED = 1;
    public final static int CHNG_FLAG_ENABLED = 2;
    public final static int CHNG_ERR_CONNECT = 3;

    SharedPreferences sPref;
    Context context;

    GBSLoader(Context context){
        this.context = context;
    }

    // проверка изменения состояния
    public int checkChanges(){
        String hashPrefs = loadPrefsHash();

        try {
            JSONObject httpData = loadDataFromHTTP(HTTP_GET_TYPE_HASH);
            String hashHttp = httpData.getString(DATA_HASH);
            String flag = httpData.getString(DATA_FLAG);

            if (flag.equals(Integer.toString(1))) {
                if (!(hashHttp.equals(hashPrefs))) savePrefsToFile();
                return CHNG_FLAG_ENABLED;
            }
            if (hashHttp.equals(hashPrefs)){
                return CHNG_HASH_EQUAL;
            } else {
                savePrefsToFile();
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
    public HashMap<String, String> loadPrefsFromFile(){
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        // текущий timestamp
        Long timeStampLong = System.currentTimeMillis() / 1000;
        String timeStamp = timeStampLong.toString();

        HashMap<String,String> data = new HashMap<>();
        data.put(DATA_FIRSTTEA, sPref.getString(DATA_FIRSTTEA, ""));
        data.put(DATA_LUNCH, sPref.getString(DATA_LUNCH, ""));
        data.put(DATA_INFO, sPref.getString(DATA_INFO, ""));
        data.put(DATA_HASH, sPref.getString(DATA_HASH, ""));
        data.put(DATA_FLAG, sPref.getString(DATA_FLAG, ""));
        data.put(DATA_TIMESTAMP, timeStamp);

        return data;
    }

    // сохранение данных в локальный файл
    public boolean savePrefsToFile(){
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = sPref.edit();
        try {
            Long timeStampLong = System.currentTimeMillis() / 1000;
            String timeStamp = timeStampLong.toString();

            JSONObject jsonData = loadDataFromHTTP(HTTP_GET_TYPE_DATA);
            ed.putString(DATA_FIRSTTEA, jsonData.getString(DATA_FIRSTTEA));
            ed.putString(DATA_LUNCH, jsonData.getString(DATA_LUNCH));
            ed.putString(DATA_INFO, jsonData.getString(DATA_INFO));
            ed.putString(DATA_HASH, jsonData.getString(DATA_HASH));
            ed.putString(DATA_FLAG, jsonData.getString(DATA_FLAG));
            ed.putString(DATA_TIMESTAMP, timeStamp);
            ed.commit();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    protected JSONObject loadDataFromHTTP(String type) {
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
