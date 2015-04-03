package com.example.trunch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by or on 4/3/2015.
 */
public class WaitForTrunchActivity extends Activity {


    //=========================================
    //				Fields
    //=========================================
    ImageView matchScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wait_for_trunch);
        matchScreen = (ImageView) findViewById(R.id.matchScreen);


        final int startTime = 4000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                matchScreen.setVisibility(View.VISIBLE);
            }
        }
                ,startTime);

    }
}
