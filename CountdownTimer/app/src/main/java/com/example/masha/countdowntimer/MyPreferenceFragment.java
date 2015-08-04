package com.example.masha.countdowntimer;

/**
 * Created by asopkin on 8/1/2015.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_EXERCISES = "select_timing";
    public SharedPreferences reader;
    public String name = "Default";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            //reader= this.getActivity().getSharedPreferences("edittext_preference",0);

            //String name = PreferenceManager.getDefaultSharedPreferences(this).getString("VarName","defaultValue");
        }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Toast.makeText(getActivity().getApplicationContext(), "Changed key: " + key, Toast.LENGTH_LONG).show();
        name = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("edittext_preference", "Amanda");


    }



}
