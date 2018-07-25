package com.funwolrd.pomodorotechnique.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by ThanhTD on 7/25/2018.
 */
public class NotificationService extends IntentService {

    private final String NOTIFICATION_SERVICE_ID = "notification_service";
    private final int NOTIFICATION_ID = 111;

    private NotificationController mNotificationController;

    public NotificationService() {
        super("notification_service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "":
                break;
        }
    }

    private void initNotificationController(String title) {
//        String title = mCurrentStep.name() + "-" + etTaskName.getText().toString();
        mNotificationController = new NotificationController(this, NOTIFICATION_ID, title);
    }
}
