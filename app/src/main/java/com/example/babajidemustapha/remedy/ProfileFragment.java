package com.example.babajidemustapha.remedy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by Jide Mustapha on 4/17/2018.
 */

public class ProfileFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Map<String, ?> map = sharedPreferences.getAll();
        for (Map.Entry<String, ?> pref : map.entrySet()) {
            // if(pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) getPreferenceScreen().findPreference(pref.getKey());
            editTextPreference.setSummary(editTextPreference.getText());
            // }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.profile_view);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = getPreferenceScreen().findPreference(key);
        preference.setSummary(((EditTextPreference) preference).getText());
    }
}
