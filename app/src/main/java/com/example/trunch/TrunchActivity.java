package com.example.trunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by or on 4/3/2015.
 */
public class TrunchActivity extends Activity {


    //=========================================
    //				Constants
    //=========================================



    //=========================================
    //				Fields
    //=========================================
    ImageView mMatchScreen;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.its_a_trunch);
        mMatchScreen = (ImageView) findViewById(R.id.matchScreen);
        String restName = getIntent().getStringExtra("restName");
        String trunchers = getIntent().getStringExtra("trunchers");
        showTrunchDialog(trunchers,restName);

    }

    public void showTrunchDialog(String trunchers,String restaurant) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

}
