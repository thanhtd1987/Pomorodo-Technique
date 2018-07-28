package com.funwolrd.pomodorotechnique.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.funwolrd.pomodorotechnique.R;
import com.funwolrd.pomodorotechnique.main.MainActivity;

/**
 * Created by ThanhTD on 7/25/2018.
 */
public class NotificationController {

    NotificationManagerCompat notificationManagerCompat;
    NotificationCompat.Builder builder;
    Context mContext;
    int mNotificationId;
    String mTitle;

    private boolean isWakeUpLockScreen = true; //temp for setting
    private boolean isEnableOnce = false;

    public NotificationController(Context context, int notificationId, String title) {
        mContext = context;
        mNotificationId = notificationId;
        mTitle = title;
        initNotification();
    }

    private void initNotification() {
        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        notificationManagerCompat = NotificationManagerCompat.from(mContext);
        builder = new NotificationCompat.Builder(mContext, "channel");
        builder.setContentTitle(mTitle)
                .setContentText("progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setColor(mContext.getResources().getColor(R.color.colorPrimary))
//                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setProgress(100, 0, false);
        addAction();

        //TODO : recheck display push condition
    }

    private void addAction() {
        Intent intentStop = new Intent(mContext, NotificationService.class);
        intentStop.setAction(PomorodoReceiver.ACTION_STOP);
//        intentStop.putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);
        PendingIntent stopPendingIntent = PendingIntent.getService(mContext, 0, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(R.drawable.ic_selected, mContext.getString(R.string.text_stop), stopPendingIntent);
    }

    public void sendNotification() {
        notificationManagerCompat.notify(mNotificationId, builder.build());
        wakeUpLockScreen();
    }

    public void updateTimerProgress(String title, String remainTime, int progress) {
        builder.setProgress(100, progress, false);
        String[] temp = remainTime.split(":");
        builder.setContentText("Remain: " + temp[0] + "m:" + temp[1] + "s " + progress + "%");
        builder.setContentTitle(title);

        sendNotification();
    }

    public void cancelNotification() {
        if (notificationManagerCompat != null)
            notificationManagerCompat.cancel(mNotificationId);
    }

    public void enableOnceTimeFunctions(boolean enable) {
        if (isEnableOnce != enable) {
            isEnableOnce = enable;
            if (Build.VERSION.SDK_INT >= 21)
                builder.setVibrate(enable ? new long[]{1000, 1000, 1000} : null);
            //LED
//        builder.setLights(mContext.getResources().getColor(R.color.warning_color), 3000, 3000);

            //Ton
//        builder.setSound(Uri.parse("uri://sadfasdfasdf.mp3"));
        }
    }

    public void wakeUpLockScreen() {
        if (!(isWakeUpLockScreen && isEnableOnce)) return;
        // Wake up screen
        PowerManager powerManager = (PowerManager) mContext.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT >= 20) {
            isScreenOn = powerManager.isInteractive();
        } else {
            isScreenOn = powerManager.isScreenOn();
        }
        if (!isScreenOn) {
            PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MH24_SCREENLOCK");
            wl.acquire(2000);
            wl.release();
            PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MH24_SCREENLOCK");
            wl_cpu.acquire(2000);
//            wl.release();
        }
    }
}
