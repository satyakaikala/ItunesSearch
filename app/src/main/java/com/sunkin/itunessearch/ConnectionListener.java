package com.sunkin.itunessearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by kaika on 6/22/2017.
 */

public class ConnectionListener extends BroadcastReceiver {

    private static final String TAG = ConnectionListener.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Change in Network connectivity");
        if (intent.getExtras() != null) {
            if (Utility.isOnline(context)) {
                Utility.showNotification(context);
            }
            Log.d(TAG, "There's no network connectivity");
        }
    }
}
