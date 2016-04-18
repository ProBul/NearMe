package com.example.user.finalandroidproject.webContent;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


// This class handles extra requests from google API-  address of desired place
public class ExtraInfoFromPlace {
    private static final String TAG = "URL Connection";


    public String getExtraDetailes(String id) {
        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        InputStream input_stream = null;
        InputStreamReader input_stream_reader = null;
        StringBuilder respones = new StringBuilder();

        try {

            //this is the url the app should provide for the google place API
            String urlString = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + id + "&key=AIzaSyAd9yLftR0dTMPOu3nRZfvSwk59-vjECDk";

            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();
            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Cannot connect to: " + urlString);
                return null;
            }
            //fetch desired information
            input_stream = httpCon.getInputStream();
            input_stream_reader = new InputStreamReader(input_stream);
            input = new BufferedReader(input_stream_reader);
            String line;
            while ((line = input.readLine()) != null) {
                respones.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                //close the stream if it exists
                try {
                    input_stream_reader.close();
                    input_stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpCon != null) {
                    //close the connection if it exists
                    httpCon.disconnect();
                }
            }
        }
        //store the desired information in a String format in StringBuilder- object named "response"
        return respones.toString();
    }
}