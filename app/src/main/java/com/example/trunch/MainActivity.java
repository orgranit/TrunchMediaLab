package com.example.trunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;


public class MainActivity extends Activity  {
    //=========================================
    //				Constants
    //=========================================

    private static final long MIN_TIME_BETWEEN_JSON_DOWNLOAD = 1000 * 60 * 60 * 24; //one day
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String urlGetTags = "http://www.mocky.io/v2/54ba8366e7c226ad0b446eff";
    private static final String urlGetRest = "http://www.mocky.io/v2/552421c1cb84087608d88880";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    View mSplashScreenView;
    TextView mTitleView;
    SearchView mTempView;
    //=========================================
    //				Activity Lifecycle
    //=========================================
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mSplashScreenView = findViewById(R.id.splash_screen);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mTempView = (SearchView) findViewById(R.id.tempView);

        // set daily reminder
        AlarmsUtils.setReminderAlarm(this);

        // check if the user isn't logged in
        if (!SharedPrefUtils.isLoggedIn(mSharedPreferences)) {
            // start linkedinConnectActivity
            linkedinConnect();
        } else {

            restOfTheActivity();
        }
    }


    private void restOfTheActivity() {
        // check difference between current time and last time of download.
        long lastTimeDownloaded = SharedPrefUtils.lastTimeDownloaded(mSharedPreferences);
        long timeDifference = System.currentTimeMillis() - lastTimeDownloaded;
        // Compare to MIN_TIME_BETWEEN_JSON_DOWNLOAD and act accordingly.
        if (timeDifference > MIN_TIME_BETWEEN_JSON_DOWNLOAD) {
            // show the splash screen
            mSplashScreenView.setVisibility(View.VISIBLE);
            // go get JSON from server
            downloadJSON();

        } else {
            // init tempView
            initTempView();
        }
    }


    //=========================================
    //				Private Methods
    //=========================================

    private void initTempView() {
        mTempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);
            }
        });

    }

    private void downloadJSON() {
        // create asyncTask which in on doInBackground makes an HTTPRequest to server to get JSON
        // onPostExecute if all went well it calls parseAndInit method
        new downloadJsonAsync().execute(urlGetTags, urlGetRest);
    }

    //=========================================
    //		    linkedinConnect
    //=========================================


    private void linkedinConnect() {
        Intent intent = new Intent(this, LinkedinConnectActivity.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                Log.d("ori", result);
                restOfTheActivity();
            }
            if (resultCode == RESULT_CANCELED) {
                linkedinConnect();
            }
        }
    }

    //=========================================
    //		JsonDownload AsyncTask Class
    //=========================================

    private class downloadJsonAsync extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            // Get json from server
            String jsonTags = RequestManger.requestGet(params[0]);
            String jsonRest = RequestManger.requestGet(params[1]);
            return new String[]{(jsonTags),(jsonRest)};
        }

        @Override
        protected void onPostExecute(String[] json) {
            String jsonTags =  json[0];
            String jsonRest = json[1];
            if ((jsonTags != null) && (jsonRest != null)) {
                // save json to sharePrefs
                SharedPrefUtils.saveRestData(mSharedPreferences, jsonTags, jsonRest);
                // init tempView
               initTempView();
                // remove splash
                mSplashScreenView.setVisibility(View.GONE);
            } else {
                retryDownloadJSON(); // somthing went wrong on server so we will try again
            }
        }
    }

    // if something went wrong
    private void retryDownloadJSON() {
        // if we fail to get JSON display an error screen and a retry button.
        // The retry button will repeat the downloadJSONasync when pressed.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Unavailable!");
        builder.setMessage("Sorry there was an error getting data from the Internet.");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downloadJSON();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}