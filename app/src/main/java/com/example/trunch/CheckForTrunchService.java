package com.example.trunch;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by or on 4/3/2015.
 */
public class CheckForTrunchService extends BroadcastReceiver {

    //=========================================
    //				Constants
    //=========================================
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String SHARED_PREF_HAS_TRUNCH = "com.package.SHARED_PREF_HAS_TRUNCH";
    private static final String SHARED_PREF_TRUNCHERS = "com.package.SHARED_PREF_TRUNCHERS";
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    String mRestName;
    String mTrunchers;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("broadcast receiver", "Debug");
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean hasTrunch = mSharedPreferences.getBoolean(SHARED_PREF_HAS_TRUNCH, false);
        mRestName = intent.getStringExtra("restName");
        if (!hasTrunch) {
            new AsynkTrunchChecker().execute("http://www.mocky.io/v2/551f2c7ede0201b30f690e3c");
        } else {
            mTrunchers = mSharedPreferences.getString(SHARED_PREF_TRUNCHERS, "No One!!");
            cancelAlarm(MainActivity.getSyncPendingIntent(context));
            startTrunchActivity(context);

        }



    }

    private void cancelAlarm(PendingIntent syncPendingIntent) {
        syncPendingIntent.cancel();
    }

    private void startTrunchActivity(Context context) {
        Intent intentForTrunch = new Intent(context, TrunchActivity.class);
        intentForTrunch.putExtra("trunchers", mTrunchers);
        intentForTrunch.putExtra("restName", mRestName);
        intentForTrunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentForTrunch);
    }

    public static void showTrunchDialog(String trunchers,String restaurant, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Trunch!");
        builder.setMessage("You are having lunch with: " + trunchers + "at "+ restaurant );
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putBoolean(SHARED_PREF_HAS_TRUNCH, true);
                edit.putString(SHARED_PREF_TRUNCHERS, response);
                edit.commit();
            }
        }
    }
}
