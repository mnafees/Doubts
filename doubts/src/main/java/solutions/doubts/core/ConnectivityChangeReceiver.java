/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import solutions.doubts.DoubtsApplication;
import solutions.doubts.core.events.ConnectivityChangedEvent;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean result = false;
        final ConnectivityManager manager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            result = info.isConnected();
        }
        DoubtsApplication.getInstance().getBus().post(new ConnectivityChangedEvent(result));
    }

}
