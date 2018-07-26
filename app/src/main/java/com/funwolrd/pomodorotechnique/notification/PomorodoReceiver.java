package com.funwolrd.pomodorotechnique.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ThanhTD on 7/25/2018.
 */
public class PomorodoReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_START = "action_start";

    public PomorodoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
