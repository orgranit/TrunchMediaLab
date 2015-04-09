package com.example.trunch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by or on 4/3/2015.
 */



public class LinkedinConnectActivity extends Activity{

    //=========================================
    //				Constants
    //=========================================
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;



    //=========================================
    //				Activity Lifecycle
    //=========================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_activity);

        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);


        // linkdin connect button
        Button button = (Button) findViewById(R.id.linkedinButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to put linkdin logic here



                SharedPrefUtils.saveUserID(mSharedPreferences, System.currentTimeMillis());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","I came Back from linkdin");
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
