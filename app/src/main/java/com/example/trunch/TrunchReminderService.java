package com.example.trunch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by or on 4/4/2015.
 */
public class TrunchReminderService extends BroadcastReceiver {

    //=========================================
    //				Fields
    //=========================================

    PendingIntent mNotificationPendingIntent;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    private int mNotificationId = 002;

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }


    private void showNotification(Context context) {
        mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.trunch_logo_small)
                .setContentTitle("Its Trunch time!").setContentText("Have you decided what to eat already?");
        // The PendingIntent to launch our activity if the user selects this
        // notification
        mNotificationPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        // Send the notification.

        mBuilder.setContentIntent(mNotificationPendingIntent);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setLights(Color.GREEN, 3000, 3000);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotifyMgr = (NotificationManager) context.getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
