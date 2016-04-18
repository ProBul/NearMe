package com.example.user.finalandroidproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.user.finalandroidproject.fragments.FragControl;
import com.example.user.finalandroidproject.fragments.FragFavorite;

//receiver to catch when GPS status is changed (On/Off)
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "Gps status was changed",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, FragControl.class);
            context.startService(pushIntent);
        }
    }
}
