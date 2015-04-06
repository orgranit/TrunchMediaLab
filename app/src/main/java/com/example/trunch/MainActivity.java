package com.example.trunch;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private static final long TWENTY_FOUR_HOURS = 1000 * 60 * 60 * 24; //one day
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
    FoodTag[] foodTags;
    Restaurant[] restTotal;
    ArrayList<Restaurant> restAdapterList;
    ArrayAdapter<Restaurant> restAdapter;
    ArrayAdapter<FoodTag> foodTagAdapter;
    ObjectMapper mMapper;
    InputMethodManager mInputManger;
    PendingIntent mPendingCheckerIntent;
    AlarmManager mTrunchCheckerAlarm;
    AlarmManager mTrunchReminderAlarm;
    PendingIntent mPendingReminderIntent;
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
        mMapper = new ObjectMapper();
        mInputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // set daily reminder
        AlarmsUtils.setReminderAlarm(this, mTrunchReminderAlarm, mPendingReminderIntent);

        // check the user is logged in
        if (!SharedPrefUtils.isLoggedIn(mSharedPreferences)) {
            mSplashScreenView.setVisibility(View.VISIBLE);
            // start linkdin activity

        }

        // check difference between current time and last time of download. Compare to MIN_TIME_BETWEEN_JSON_DOWNLOAD and act accordingly.
        long lastTimeDownloaded = SharedPrefUtils.lastTimeDownloaded(mSharedPreferences);
        long timeDifference = System.currentTimeMillis() - lastTimeDownloaded;
        if (timeDifference > TWENTY_FOUR_HOURS) {
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
            adjustTokenView();
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
                        waitForTrunch(restName, view);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void initTokenView() {
        foodTagAdapter = new FilteredArrayAdapter<FoodTag>(this, R.layout.food_tag_layout, foodTags) {
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


    }

    private void adjustTokenView() {
        mCompletionView.setAdapter(foodTagAdapter);
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
        String jsonRest = SharedPrefUtils.getRests(mSharedPreferences);
        String jsonTags = SharedPrefUtils.getFoodTags(mSharedPreferences);
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
        if (((FoodTag) token).isRest()) {
            TokenViewUtils.restTokenAdded(token, mCompletionView, mInputManger);
        } else {
            TokenViewUtils.foodTokenAdded(token, mCompletionView, mInputManger);
        }
        List<Object> tokens = mCompletionView.getObjects();
        TokenViewUtils.refreshRest(tokens, restTotal,
                restAdapterList, restAdapter);
    }


    @Override
    public void onTokenRemoved(Object token) {
        List<Object> tokens = mCompletionView.getObjects();
        if (tokens.size() == 0) {
            mMainContainer.setVisibility(View.GONE);
            mTitleView.setVisibility(View.VISIBLE);
            mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
        }
        TokenViewUtils.refreshRest(tokens, restTotal,
                restAdapterList, restAdapter);
    }


    private void waitForTrunch(String restName, View view) {
        Intent alarmIntent = new Intent(this, TrunchCheckerService.class);
        alarmIntent.putExtra("restName", restName);
        showGreatChoice(restName);
        mPendingCheckerIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmsUtils.startCheckerAlarm(view, this, mTrunchCheckerAlarm , mPendingCheckerIntent,
                mSharedPreferences);
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

    public static PendingIntent getSyncPendingIntent(Context context) {
        Intent intent = new Intent(context, TrunchCheckerService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    //=========================================
    //				Private Classes
    //=========================================
    private void linkedinConnect() {
        Intent intent = new Intent(this, LinkedinConnectActivity.class);
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
                SharedPrefUtils.saveRestData(mSharedPreferences, jsonTags, jsonRest);
                // parse json
                parseJsonRest(jsonRest);
                parseJsonTags(jsonTags);
            } else {
                retryDownloadJSON(); // somthing went wrong on server so we will try again
            }
        }
    }


}
