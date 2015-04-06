package com.example.trunch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import java.util.Calendar;

/**
 * Created by or on 4/6/2015.
 */
public class AlarmsUtils {

    private static final long REPEAT_TIME = 1000 * 5;
    private static final long TWENTY_FOUR_HOURS = 1000 * 60 * 60 * 24; //one day
    private static final int HOUR_OF_REMINDER = 11;
    private static final int MINUTE_OF_REMINDER = 00;
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";



    public static void startCheckerAlarm(View view, Context context,String restName) {
        Intent alarmIntent = new Intent(context, TrunchCheckerService.class);
        alarmIntent.putExtra("restName", restName);
        PendingIntent mPendingCheckerIntent = PendingIntent.getBroadcast(context, 0, alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mTrunchCheckerAlarm = (AlarmManager) context.getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
        cancelAlarm(view, mTrunchCheckerAlarm, mPendingCheckerIntent);
        clearSharePref(context);
        mTrunchCheckerAlarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_TIME, mPendingCheckerIntent);
    }

    public static void cancelAlarm(View view,AlarmManager mTrunchCheckerAlarm,
                                   PendingIntent mPendingCheckerIntent) {
        if (mTrunchCheckerAlarm != null) {
            mTrunchCheckerAlarm.cancel(mPendingCheckerIntent);
        }
    }

    private static void clearSharePref(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPrefUtils.clearTrunched(mSharedPreferences);
    }


    public static void setReminderAlarm(Context context){
        AlarmManager mTrunchReminderAlarm = (AlarmManager) context.getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, TrunchReminderService.class);
        PendingIntent mPendingReminderIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.setTimeInMillis(System.currentTimeMillis());
        alarmStartTime.set(Calendar.HOUR_OF_DAY, HOUR_OF_REMINDER);
        alarmStartTime.set(Calendar.MINUTE, MINUTE_OF_REMINDER);
        mTrunchReminderAlarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                TWENTY_FOUR_HOURS, mPendingReminderIntent);
    }


}



/*public void startCheckerAlarm(View view) {
        cancelAlarm(view);
        clearSharePref();
        mTrunchCheckerAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mTrunchCheckerAlarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_TIME, mPendingCheckerIntent);
    }

    public void cancelAlarm(View view) {
        if (mTrunchCheckerAlarm != null) {
            mTrunchCheckerAlarm.cancel(mPendingCheckerIntent);
        }
    }

    private void clearSharePref() {
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, false);
        edit.commit();
    }


    public void setReminderAlarm(){
        mTrunchReminderAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TrunchReminderService.class);
        mPendingReminderIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.setTimeInMillis(System.currentTimeMillis());
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 11);
        //alarmStartTime.set(Calendar.MINUTE, 30);
        mTrunchReminderAlarm.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                TWENTY_FOUR_HOURS, mPendingReminderIntent);
    }
*/
