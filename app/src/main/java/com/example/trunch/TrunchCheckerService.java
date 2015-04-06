package com.example.trunch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchCheckerService extends BroadcastReceiver {

    //=========================================
    //				Constants
    //=========================================
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    int mNotificationId = 001;

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    String mRestName;
    String mTrunchers;
    PendingIntent mNotificationPendingIntent;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean hasTrunch = SharedPrefUtils.hasTrunch(mSharedPreferences);
        mRestName = intent.getStringExtra("restName");
        if (!hasTrunch) {
            new AsynkTrunchChecker().execute("http://www.mocky.io/v2/551f2c7ede0201b30f690e3c");
        } else {
            mTrunchers = SharedPrefUtils.getTrunchers(mSharedPreferences);
            cancelAlarm(MainActivity.getSyncPendingIntent(context));
            showNotification(context);

        }



    }

    private void showNotification(Context context) {
        mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.trunch_logo_small)
                 .setContentTitle("You got Trunch!").setContentText("find out who are you eating with");
        // The PendingIntent to launch our activity if the user selects this
        // notification
        mNotificationPendingIntent = PendingIntent.getActivity(context, 0,
                trunchActivityIntent(context), PendingIntent.FLAG_CANCEL_CURRENT);
        // Send the notification.

        mBuilder.setContentIntent(mNotificationPendingIntent);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.GREEN, 3000, 3000);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mNotifyMgr = (NotificationManager) context.getApplicationContext()
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    private void cancelAlarm(PendingIntent syncPendingIntent) {
        syncPendingIntent.cancel();
    }

    private Intent trunchActivityIntent(Context context) {
        Intent intentForTrunch = new Intent(context, TrunchActivity.class);
        intentForTrunch.putExtra("trunchers", mTrunchers);
        intentForTrunch.putExtra("restName", mRestName);
        intentForTrunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intentForTrunch;
    }



    private class AsynkTrunchChecker extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = RequestManger.requestGet(params[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                SharedPrefUtils.UpdateTrunchResult(mSharedPreferences,response);
            }
        }
    }
}
