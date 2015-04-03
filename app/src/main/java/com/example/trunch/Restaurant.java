package com.example.trunch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by or on 4/3/2015.
 */
public class Restaurant implements Parcelable {

    private String name;
    private String[] tags;


    public Restaurant(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringArray(tags);
    }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return name;
    }
}
