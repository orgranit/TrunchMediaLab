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
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String SHARED_PREF_HAS_TRUNCH = "com.package.SHARED_PREF_HAS_TRUNCH";



    public static void startCheckerAlarm(View view, Context context ,
                                         AlarmManager mTrunchCheckerAlarm,
                                         PendingIntent mPendingCheckerIntent,
                                         SharedPreferences mSharedPreferences) {
        cancelAlarm(view, mTrunchCheckerAlarm, mPendingCheckerIntent);
        clearSharePref(context, mSharedPreferences );
        mTrunchCheckerAlarm = (AlarmManager) context.getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
        mTrunchCheckerAlarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REPEAT_TIME, mPendingCheckerIntent);
    }

    public static void cancelAlarm(View view,AlarmManager mTrunchCheckerAlarm,
                                   PendingIntent mPendingCheckerIntent) {
        if (mTrunchCheckerAlarm != null) {
            mTrunchCheckerAlarm.cancel(mPendingCheckerIntent);
        }
    }

    private static void clearSharePref(Context context, SharedPreferences mSharedPreferences) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, false);
        edit.commit();
    }


    public static void setReminderAlarm(Context context, AlarmManager mTrunchReminderAlarm,
            PendingIntent mPendingReminderIntent){
        mTrunchReminderAlarm = (AlarmManager) context.getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, TrunchReminderService.class);
        mPendingReminderIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.setTimeInMillis(System.currentTimeMillis());
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 11);
        alarmStartTime.set(Calendar.MINUTE, 00);
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
