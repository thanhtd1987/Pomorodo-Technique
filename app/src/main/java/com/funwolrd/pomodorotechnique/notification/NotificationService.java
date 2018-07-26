package com.funwolrd.pomodorotechnique.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by ThanhTD on 7/25/2018.
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("notification_service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction() == null) return;
        String action = intent.getAction();
        Intent stopIntent = new Intent();
        switch (action) {
            case PomorodoReceiver.ACTION_STOP:
                stopIntent.setAction(action);
                break;
            case PomorodoReceiver.ACTION_START:
                stopIntent.setAction(action);
                break;
        }
        sendBroadcast(stopIntent);
    }
}
