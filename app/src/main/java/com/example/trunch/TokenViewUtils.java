package com.example.trunch;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class TokenViewUtils {

    public static void refreshRest(List<Object> tokens, Restaurant[] restTotal,
                            ArrayList<Restaurant> restAdapterList ,
                            ArrayAdapter<Restaurant> restAdapter) {
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



    public static boolean containTags(Restaurant rest, List<Object> tokens) {
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

    public static void foodTokenAdded(Object token , TagsCompletionView mCompletionView,
                                InputMethodManager mInputManger) {
        List<Object> objects = mCompletionView.getObjects();
        mInputManger.hideSoftInputFromWindow(mCompletionView.getWindowToken(), 0);
        mCompletionView.setCursorVisible(false);
        // remove rest
        FoodTag rest = (FoodTag) objects.get(0);
        if (rest.isRest()) {
            mCompletionView.removeObject(rest);
        }
    }

    public static void restTokenAdded(Object token, TagsCompletionView mCompletionView,
                                       InputMethodManager mInputManger) {
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

    public static void adjustTokenView(TagsCompletionView mTagsCompletionView) {

    }

}



/*private void foodTokenAdded(Object token) {
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
*/
