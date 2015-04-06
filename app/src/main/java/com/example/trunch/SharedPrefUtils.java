package com.example.trunch;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by or on 4/6/2015.
 */
public class SharedPrefUtils {

    private static final String SHARED_PREF_KEY_LAST_TIME_DOWNLOADED = "com.package.SHARED_PREF_KEY_LAST_TIME_DOWNLOADED";
    private static final String SHARED_PREF_KEY_FOOD_TAGS = "com.package.SHARED_PREF_KEY_FOOD_TAGS";
    private static final String SHARED_PREF_KEY_RESTAURANT = "com.package.SHARED_PREF_KEY_RESTAURANT";
    private static final String SHARED_PREF_USER_ID = "com.package.SHARED_PREF_USER_ID";
    private static final String SHARED_PREF_HAS_TRUNCH = "com.package.SHARED_PREF_HAS_TRUNCH";
    private static final String SHARED_PREF_TRUNCHERS = "com.package.SHARED_PREF_TRUNCHERS";


    public static boolean hasTrunch(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getBoolean(SHARED_PREF_HAS_TRUNCH, false);
    }

    public static String getTrunchers(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_TRUNCHERS, "No One!!");
    }

    public static String getRests(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_KEY_RESTAURANT, "{'empty' : empty}");
    }

    public static String getFoodTags(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_KEY_FOOD_TAGS, "{'empty' : empty}");
    }

    public static void UpdateTrunchResult(SharedPreferences mSharedPreferences, String trunchers) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, true);
        edit.putString(SHARED_PREF_TRUNCHERS, trunchers);
        edit.commit();
    }

    public static void saveRestData(SharedPreferences mSharedPreferences, String jsonTags,
                                          String jsonRest) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(SHARED_PREF_KEY_FOOD_TAGS, jsonTags);
        edit.putString(SHARED_PREF_KEY_RESTAURANT, jsonRest);
        edit.putLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, System.currentTimeMillis());
        edit.commit();
    }



    public static boolean isLoggedIn (SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getLong(SHARED_PREF_USER_ID, -1) >= 0;
    }

    public static long lastTimeDownloaded (SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, -1);
    }

    public static void clearTrunched(SharedPreferences mSharedPreferences) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, false);
        edit.commit();
    }



}
