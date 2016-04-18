package com.example.user.finalandroidproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;


//Listener to catch when charging status was changed
public class ChargingStatusListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase("android.intent.action.ACTION_POWER_CONNECTED")) {
            Toast.makeText(context, "charging alex`s phone...", Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equalsIgnoreCase("android.intent.action.ACTION_POWER_DISCONNECTED")) {
            Toast.makeText(context, " alex`s phone is not charging...", Toast.LENGTH_LONG).show();
        }
    }
}