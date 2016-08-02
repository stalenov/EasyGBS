package com.kotsokrat.easygbs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

public class PreferActivity extends AppCompatActivity{
    RadioGroup rdDelay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        rdDelay = (RadioGroup)findViewById(R.id.rdDelay);
        rdDelay.setOnCheckedChangeListener(new OnSelectNewDelay());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentDelay = prefs.getString("update_delay", "15");

        switch (currentDelay) {
            case "0":
                rdDelay.check(R.id.rb0);
                break;
            case "5":
                rdDelay.check(R.id.rb5);
                break;
            case "30":
                rdDelay.check(R.id.rb30);
                break;
            case "15":
            default:
                rdDelay.check(R.id.rb15);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlertDelayChanger adt = new AlertDelayChanger(this);
        adt.change();
    }

    private class OnSelectNewDelay implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String delay;
            if (group == rdDelay) {
                switch (checkedId) {
                    case R.id.rb0:
                        delay = "0";
                        break;
                    case R.id.rb5:
                        delay = "5";
                        break;
                    default:
                    case R.id.rb15:
                        delay = "15";
                        break;
                    case R.id.rb30:
                        delay = "30";
                        break;
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PreferActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("update_delay", delay);
                editor.commit();
            }
        }
    }
}
