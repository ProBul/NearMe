package com.example.user.finalandroidproject.sharedPref;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.example.user.finalandroidproject.R;
import com.example.user.finalandroidproject.fragments.Communicator;


/**
 * Created by user on 20/02/2016.
 */
public class Settings extends PreferenceActivity implements Preference.OnPreferenceClickListener, Communicator {
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }

    //option that allows the user to change the distance units in sharedPref
    @Override
    public boolean onPreferenceClick(Preference preference) {

        String str = "km";
        sharedPrefCallBack(str);
        return true;
    }

    //methods to send information between Fragments and other activities
    @Override
    public void onCallBaclDouble(Double lat, Double lng, String str) {

    }

    @Override
    public void sharedPrefCallBack(String measureUnit) {

    }

    @Override
    public void openFavorite() {

    }
}
