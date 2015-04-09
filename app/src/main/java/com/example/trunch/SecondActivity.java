package com.example.trunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




public class SecondActivity extends Activity implements TokenCompleteTextView.TokenListener {
    //=========================================
    //				Constants
    //=========================================

    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    TagsCompletionView mTagsCompletionView;
    LinearLayout mMainContainer;
    HorizontialListView mRestContainer;
    FoodTag[] foodTags;
    Restaurant[] restTotal;
    ArrayList<Restaurant> restAdapterList;
    ArrayAdapter<Restaurant> restAdapter;
    ArrayAdapter<FoodTag> foodTagAdapter;
    ObjectMapper mMapper;
    InputMethodManager mInputManger;
    //=========================================
    //				Activity Lifecycle
    //=========================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);



        // Init Fields
        mSharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mTagsCompletionView = (TagsCompletionView) findViewById(R.id.searchView);
        mMainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        mRestContainer = (HorizontialListView) findViewById(R.id.restContainer);
        mMapper = new ObjectMapper();
        mInputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // parse json from SharedPref and init all components
        parseAndInit(SharedPrefUtils.getRests(mSharedPreferences)
        , SharedPrefUtils.getFoodTags(mSharedPreferences));

    }


    //=========================================
    //				Private Methods
    //=========================================


    private void parseAndInit(String jsonRest, String jsonTags) {
        // parse
        parseJsonRest(jsonRest);
        parseJsonTags(jsonTags);
        //init
        initRestContainer();
        initTokenView();
        adjustTokenView();
    }

    private void waitForTrunch(String restName, View view) {
        AlarmsUtils.startCheckerAlarm(view, this, restName);
    }

    private void showGreatChoice(String restName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Great Choice!");
        builder.setMessage("We'll let you know when you have a Trunch");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // for TrunchCheckerService
    public static PendingIntent getSyncPendingIntent(Context context) {
        Intent intent = new Intent(context, TrunchCheckerService.class);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }


    //=========================================
    //			Json Parser
    //=========================================


    private void parseJsonRest(String jsonRest) {
        try {
            restTotal = mMapper.readValue(jsonRest,Restaurant[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void parseJsonTags(String json) {
        try {
            foodTags = mMapper.readValue(json,FoodTag[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=========================================
    //		    HorizontalListView
    //=========================================

    private void initRestContainer() {
        restAdapterList = new ArrayList<>();
        restAdapter = new ArrayAdapter<Restaurant>(this, R.layout.rest_item, restAdapterList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Context context = parent.getContext();
                View retval = LayoutInflater.from(context).inflate(R.layout.rest_item, null);
                ImageButton restBtn = (ImageButton) retval.findViewById(R.id.imageButton);
                TextView restName = (TextView) retval.findViewById(R.id.restName);
                final Restaurant rest = getItem(position);
                restName.setText(rest.getName());
                String imgName = rest.getName().toLowerCase().replaceAll(" ","_");
                int path = getResources().getIdentifier(imgName, "drawable", getPackageName());
                restBtn.setImageResource(path);
                //Picasso.with(context).load(rest.getImage()).resize(350,350).into(restBtn);
                return retval;
            }
        };
        mRestContainer.setAdapter(restAdapter);
        mRestContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView restTitle = (TextView) view.findViewById(R.id.restName);
                final String restName = (String) restTitle.getText();
                builder.setTitle(restTitle.getText());
                builder.setMessage("Are you sure you want to Trunch here?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showGreatChoice(restName);
                        waitForTrunch(restName, view);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }




    //=========================================
    //	            TokenView
    //=========================================

    private void initTokenView() {
        foodTagAdapter = new FilteredArrayAdapter<FoodTag>(this, R.layout.food_tag_layout, foodTags) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.food_tag_layout, parent, false);
                }

                FoodTag ft = getItem(position);
                ((TextView)convertView.findViewById(R.id.name)).setText(ft.getTag());
                ((TextView)convertView.findViewById(R.id.rest)).setText(ft.getType());

                return convertView;
            }

            @Override
            protected boolean keepObject(FoodTag obj, String mask) {
                mask = mask.toLowerCase();
                int secondWord = obj.getTag().indexOf(" ") + 1;
                return obj.getTag().toLowerCase().startsWith(mask) || obj.getTag().toLowerCase().startsWith(mask,secondWord);
            }
        };


    }

    private void adjustTokenView() {
        mTagsCompletionView.setAdapter(foodTagAdapter);
        mTagsCompletionView.setTokenListener(this);
        mTagsCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        mTagsCompletionView.allowDuplicates(false);
        mTagsCompletionView.setCursorVisible(true);
        mInputManger.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        mTagsCompletionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainContainer.setVisibility(View.VISIBLE);
                if (mTagsCompletionView.getObjects().size() > 2) {
                    mInputManger.hideSoftInputFromWindow(mTagsCompletionView.getWindowToken(), 0);
                } else {
                    mInputManger.showSoftInput(mTagsCompletionView, 0);
                    mTagsCompletionView.setCursorVisible(true);
                }
            }
        });
    }

    @Override
    public void onTokenAdded(Object token) {
        if (((FoodTag) token).isRest()) {
            TokenViewUtils.restTokenAdded(token, mTagsCompletionView, mInputManger);
        } else {
            TokenViewUtils.foodTokenAdded(token, mTagsCompletionView, mInputManger);
        }
        List<Object> tokens = mTagsCompletionView.getObjects();
        TokenViewUtils.refreshRest(tokens, restTotal,
                restAdapterList, restAdapter);
    }


    @Override
    public void onTokenRemoved(Object token) {
        List<Object> tokens = mTagsCompletionView.getObjects();
        TokenViewUtils.refreshRest(tokens, restTotal,
                restAdapterList, restAdapter);
    }


}
