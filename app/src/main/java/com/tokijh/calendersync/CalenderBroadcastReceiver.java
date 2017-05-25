package com.tokijh.calendersync;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tokijh on 2017. 5. 25..
 */

public class CalenderBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "CalenderBroadcastReceiver";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent != null) {
            Log.d(TAG, intent.toString());
            if (intent.getExtras() != null) {
                Log.d(TAG, intent.getExtras().toString());
            }
        }
    }
}
