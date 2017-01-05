package com.example.rached.memory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingActivity.MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
            Preference connectionPref;
            String trivial = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("trivialsleeptime", "");

            if (!trivial.equals("Never")&&!trivial.equals("")) {
                connectionPref = findPreference("trivial_seconds");
                connectionPref.setEnabled(true);
            }
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference connectionPref;
            if(key.equals("trivialsleeptime")) {
                String trivial = sharedPreferences.getString(key, "");
                if (!trivial.equals("Never") && !trivial.equals("")) {
                    connectionPref = findPreference("trivial_seconds");
                    connectionPref.setEnabled(true);
                } else {
                    connectionPref = findPreference("trivial_seconds");
                    connectionPref.setEnabled(false);
                }
            }
        }
    }

}