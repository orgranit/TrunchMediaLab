package com.example.trunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements TokenCompleteTextView.TokenListener {
    //=========================================
    //				Constants
    //=========================================

    private static final long MIN_TIME_BETWEEN_JSON_DOWNLOAD = 1000 * 60 * 60 * 24; //one day
    private static final String SHARED_PREF_KEY_LAST_TIME_DOWNLOADED = "com.package.SHARED_PREF_KEY_LAST_TIME_DOWNLOADED";
    private static final String SHARED_PREF_KEY_FOOD_TAGS = "com.package.SHARED_PREF_KEY_FOOD_TAGS";
    private static final String SHARED_PREF_KEY_RESTAURANT = "com.package.SHARED_PREF_KEY_RESTAURANT";
    private static final String SHARED_PREF_USER_ID = "com.package.SHARED_PREF_USER_ID";
    private static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    private static final String urlGetTags = "http://www.mocky.io/v2/54ba8366e7c226ad0b446eff";
    private static final String urlGetRest = "http://www.mocky.io/v2/54ba8335e7c226a90b446efe";

    //=========================================
    //				Fields
    //=========================================
    SharedPreferences mSharedPreferences;
    View mSplashScreenView;
    TagsCompletionView mCompletionView;
    TextView mTitleView;
    LinearLayout mMainContainer;
    HorizontialListView mRestContainer;
    GridView tagsGrid;
    FoodTag[] foodTags;
    Restaurant[] restTotal;
    ArrayList<Restaurant> restAdapterList;
    ArrayAdapter<Restaurant> restAdapter;
    ObjectMapper mMapper;
    InputMethodManager mInputManger;
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
        mCompletionView = (TagsCompletionView) findViewById(R.id.searchView);
        mTitleView = (TextView) findViewById(R.id.titleView);
        mMainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        mRestContainer = (HorizontialListView) findViewById(R.id.restContainer);
        //tagsGrid = (GridView) findViewById(R.id.tagsGrid);
        mMapper = new ObjectMapper();
        mInputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        // check the user is logged in
        if (mSharedPreferences.getLong(SHARED_PREF_USER_ID, -1) < 0) {
            mSplashScreenView.setVisibility(View.VISIBLE);
            // start linkdin activity

        }

        // check difference between current time and last time of download. Compare to MIN_TIME_BETWEEN_JSON_DOWNLOAD and act accordingly.
        long lastTimeDownloaded = mSharedPreferences.getLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, -1);
        long timeDifference = System.currentTimeMillis() - lastTimeDownloaded;
        if (timeDifference > MIN_TIME_BETWEEN_JSON_DOWNLOAD) {
            // show the splash screen
            mSplashScreenView.setVisibility(View.VISIBLE);
            // go get JSON from server
            downloadJSON();
        } else {
            // get JSON form local storage
            getJSONFromSharedPref();
        }
    }


    //=========================================
    //				Private Methods
    //=========================================

    private void refreshRest(List<Object> tokens) {
        for (int i =0; i < restTotal.length; i++){
            Restaurant rest = restTotal[i];
            if (containTags(rest,tokens)){
                if (!restAdapterList.contains(rest)) {
                    restAdapterList.add(rest);
                }
            } else {
                restAdapterList.remove(rest);
            }
        }
        restAdapter.notifyDataSetChanged();

    }



    private boolean containTags(Restaurant rest, List<Object> tokens) {
        if(tokens.size() == 0){
            return false;
        }
        int matches = 0;
        String[] tags = rest.getTags();
        for (Object token : tokens) {
            FoodTag foodTag = (FoodTag) token;
            for(String tag : tags) {
                if (tag.toLowerCase().equals(foodTag.getTag().toLowerCase())){
                    matches++;
                }
            }
        }
        return (matches == tokens.size());
    }


    private void parseJsonRest(String jsonRest) {
        try {
            restTotal = mMapper.readValue(jsonRest,Restaurant[].class);
            //make rest container;
            initRestContainer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private void parseJsonTags(String json) {
        try {
            foodTags = mMapper.readValue(json,FoodTag[].class);
            // remove splash screen
            mSplashScreenView.setVisibility(View.GONE);
            // make tokenSearch view
            initTokenView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRestContainer() {
        restAdapterList = new ArrayList<Restaurant>();
        restAdapter = new ArrayAdapter<Restaurant>(this, R.layout.rest_item, restAdapterList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.rest_item, null);
                ImageButton restBtn = (ImageButton) retval.findViewById(R.id.imageButton);
                TextView restName = (TextView) retval.findViewById(R.id.restName);
                final Restaurant rest = getItem(position);
                restName.setText(rest.getName());
                String imgName = rest.getName().toLowerCase().replaceAll(" ","_");
                int path = getResources().getIdentifier(imgName, "drawable", getPackageName());
                restBtn.setImageResource(path);
                return retval;
            }
        };
        mRestContainer.setAdapter(restAdapter);
        mRestContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                TextView restName = (TextView) view.findViewById(R.id.restName);
                builder.setTitle(restName.getText());
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
                        waitForTrunch();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void initTokenView() {
        ArrayAdapter<FoodTag> adapter = new FilteredArrayAdapter<FoodTag>(this, R.layout.food_tag_layout, foodTags) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = (View)l.inflate(R.layout.food_tag_layout, parent, false);
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

        mCompletionView.setAdapter(adapter);
        mCompletionView.setTokenListener(this);
        mCompletionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        mCompletionView.allowDuplicates(false);
        mCompletionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setVisibility(View.GONE);
                mMainContainer.setVisibility(View.VISIBLE);
                if (mCompletionView.getObjects().size() > 2) {
                    mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
                } else {
                    mInputManger.showSoftInput(mCompletionView, 0);
                    mCompletionView.setCursorVisible(true);
                }
            }
        });
    }

    private void getJSONFromSharedPref() {
        String jsonRest = mSharedPreferences.getString(SHARED_PREF_KEY_RESTAURANT, "{'empty' : empty}");
        String jsonTags = mSharedPreferences.getString(SHARED_PREF_KEY_FOOD_TAGS, "{'empty' : empty}");
        parseJsonRest(jsonRest);
        parseJsonTags(jsonTags);

    }

    private void downloadJSON() {
        // create asyncTask which in on doInBackground makes an HTTPRequest to server to get JSON
        // onPostExecute if all went well it calls parseJSON method
        new downloadJsonAsync().execute(urlGetTags, urlGetRest);
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


    @Override
    public void onTokenAdded(Object token) {
        //completionView.setCursorVisible(false);
        //imm.hideSoftInputFromWindow(completionView.getWindowToken(), 0);
        if (((FoodTag) token).isRest()) {
            restTokenAdded(token);
        } else {
            foodTokenAdded(token);
        }
        List<Object> tokens = mCompletionView.getObjects();
        refreshRest(tokens);
    }

    private void foodTokenAdded(Object token) {
        List<Object> objects = mCompletionView.getObjects();
        mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
        mCompletionView.setCursorVisible(false);
        // remove rest
        FoodTag rest = (FoodTag) objects.get(0);
        if (rest.isRest()) {
            mCompletionView.removeObject(rest);
        }
    }

    private void restTokenAdded(Object token) {
        mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
        mCompletionView.setCursorVisible(false);
        List<Object> objects = mCompletionView.getObjects();
        int tokens = objects.size() - 1;
        // clear all
        while (tokens > 0) {
            tokens--;
            mCompletionView.removeObject(objects.get(tokens));
        }
    }




    @Override
    public void onTokenRemoved(Object token) {
        List<Object> tokens = mCompletionView.getObjects();
        if (tokens.size() == 0) {
            mMainContainer.setVisibility(View.GONE);
            mTitleView.setVisibility(View.VISIBLE);
            mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
        }
        refreshRest(tokens);
    }



    //=========================================
    //				Private Classes
    //=========================================
    private void linkedinConnect() {
        Intent intent = new Intent(this, LinkedinConnectActivity.class);
        startActivity(intent);
    }


    private void waitForTrunch() {
        Intent intent = new Intent(this, WaitForTrunchActivity.class);
        startActivity(intent);
    }

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
                SharedPreferences.Editor edit = mSharedPreferences.edit();
                edit.putString(SHARED_PREF_KEY_FOOD_TAGS, jsonTags);
                edit.putString(SHARED_PREF_KEY_RESTAURANT, jsonRest);
                edit.putLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, System.currentTimeMillis());
                edit.commit();
                // parse json
                parseJsonRest(jsonRest);
                parseJsonTags(jsonTags);
            } else {
                retryDownloadJSON(); // somthing went wrong on server so we will try again
            }
        }
    }


}
