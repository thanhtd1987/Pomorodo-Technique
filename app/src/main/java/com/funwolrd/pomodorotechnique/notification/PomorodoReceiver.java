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

//    private BroadcastCallback mCallback;

    public PomorodoReceiver() {
    }

//    public void setCallback(BroadcastCallback callback) {
//        mCallback = callback;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_STOP:
                Toast.makeText(context, "Action stop!", Toast.LENGTH_SHORT).show();
//                mCallback.onStop();
                break;
        }
    }

//    public interface BroadcastCallback {
//        void onStop();
//    }
}
