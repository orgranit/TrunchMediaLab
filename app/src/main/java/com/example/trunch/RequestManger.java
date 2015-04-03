package com.example.trunch;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by or on 4/3/2015.
 */
public class RequestManger {

    public static String requestGet(String url) {
        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);
        // Execute the request
        HttpResponse response;
        String result = "";
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("RequestManager GET:", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }

        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static String requestPost(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        // Prepare a request object
        HttpPost httpPost = new HttpPost(url);
        // Execute the request
        HttpResponse response;
        String result = "";
        try {
            response = httpclient.execute(httpPost);
            // Examine the response status
            Log.i("RequestManager POST:", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }

        } catch (Exception e) {
            return null;
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
