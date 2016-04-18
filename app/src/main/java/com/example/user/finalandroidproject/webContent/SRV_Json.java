package com.example.user.finalandroidproject.webContent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.user.finalandroidproject.dataBase.Constants;
import com.example.user.finalandroidproject.dataBase.DBHandler;
import com.example.user.finalandroidproject.objects.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// An {@link IntentService} subclass for handling asynchronous task requests in
// a service on a separate handler thread.
public class SRV_Json extends IntentService {
    Place place;
    private String jasonFlag;
    DBHandler handler = new DBHandler(SRV_Json.this);

    private static final String TAG = "URL Connection";
    public static final String FLAG_BROADCAST = "com.example.user.finaljohnbryceproject.SRV_Json";
    private static final String ACTION_FOO = "com.example.user.finaljohnbryceproject.action.FOO";
    private static final String EXTRA_PARAM1 = "example.jbt.com.servicetest.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "example.jbt.com.servicetest.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "example.jbt.com.servicetest.extra.PARAM3";
    private static final String EXTRA_PARAM4 = "example.jbt.com.servicetest.extra.PARAM4";
    private static final String EXTRA_PARAM5 = "example.jbt.com.servicetest.extra.PARAM5";

    //Starts this service to perform action Foo with the given parameters. If
    //the service is already performing a task this action will be queued.
    public static void startActionFoo(Context context, String param1, String param2,
                                      String param3, String param4, String param5) {
        Intent intent = new Intent(context, SRV_Json.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        intent.putExtra(EXTRA_PARAM3, param3);
        intent.putExtra(EXTRA_PARAM4, param4);
        intent.putExtra(EXTRA_PARAM5, param5);
        context.startService(intent);
    }

    public SRV_Json() {
        super("SRV_Json");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                final String param3 = intent.getStringExtra(EXTRA_PARAM3);
                final String param4 = intent.getStringExtra(EXTRA_PARAM4);
                final String param5 = intent.getStringExtra(EXTRA_PARAM5);


                handleActionFoo(param1, param2, param3, param4, param5);
            }
        }
    }


    //    Handle action Foo in the provided background thread with the provided
    // parameters.

    private void handleActionFoo(String param1, String param2, String param3, String param4, String param5) {

        Intent intent = new Intent(FLAG_BROADCAST);
        intent.putExtra("JSON TYPE", param1);
        intent.putExtra("JSON RAD", param2);
        intent.putExtra("JSON LAT", param3);
        intent.putExtra("JSON LNG", param4);
        intent.putExtra("JSON SEARCH_FLAG", param5);

        String response = sendHttpRequest(param1, param2, param3, param4, param5);
        breakJason(response);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void breakJason(String s) {
        //getting the JSON (from response String) and breaking it down for desired attributes
        ExtraInfoFromPlace extra = new ExtraInfoFromPlace();

        try {
            JSONObject object = new JSONObject(s);
            JSONArray array = object.getJSONArray("results");
            //loop that will run for all Json attributes on all Json array
            if (array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {

                    String placeId = "";
                    if (array.getJSONObject(i).has(Constants.COLUMN_PlaceId)) {
                        placeId = array.getJSONObject(i).getString(Constants.COLUMN_PlaceId);
                    }

                    //request for additional information using "placeId" variable
                    String extraInfoAnswer = extra.getExtraDetailes(placeId);
                    JSONObject objectExtraInfo = new JSONObject(extraInfoAnswer);
                    JSONObject object1 = objectExtraInfo.getJSONObject("result");

                    String address = "";
                    if (object1.has(Constants.COLUMN_Address)) {
                        address = object1.getString(Constants.COLUMN_Address);
                    }

                    Double lat = 0.0;
                    if (array.getJSONObject(i).getJSONObject(Constants.COLUMN_Geometry)
                            .getJSONObject(Constants.COLUMN_Location).has(Constants.COLUMN_Lat)) {
                        lat = array.getJSONObject(i).getJSONObject(Constants.COLUMN_Geometry)
                                .getJSONObject(Constants.COLUMN_Location).getDouble(Constants.COLUMN_Lat);
                    }

                    Double lng = 0.0;
                    if (array.getJSONObject(i).getJSONObject(Constants.COLUMN_Geometry)
                            .getJSONObject(Constants.COLUMN_Location).has(Constants.COLUMN_Lng)) {
                        lng = array.getJSONObject(i).getJSONObject(Constants.COLUMN_Geometry)
                                .getJSONObject(Constants.COLUMN_Location).getDouble(Constants.COLUMN_Lng);
                    }

                    String icon = "";
                    if (array.getJSONObject(i).has(Constants.COLUMN_Icon)) {
                        icon = array.getJSONObject(i).getString(Constants.COLUMN_Icon);
                    }

                    String name = "";
                    if (array.getJSONObject(i).has(Constants.COLUMN_Name)) {
                        name = array.getJSONObject(i).getString(Constants.COLUMN_Name);
                    }

                    String types = "";
                    if (array.getJSONObject(i).has(Constants.COLUMN_Type)) {
                        types = array.getJSONObject(i).getString(Constants.COLUMN_Type);
                    }

                    String vicinity = "";
                    if (array.getJSONObject(i).has(Constants.COLUMN_Vicinity)) {
                        vicinity = array.getJSONObject(i).getString(Constants.COLUMN_Vicinity);
                    }

                    Double price_level = 0.0;
                    if (array.getJSONObject(i).has(Constants.COLUMN_PriceLevel))
                        price_level = array.getJSONObject(i).getDouble(Constants.COLUMN_PriceLevel);

                    Double rating = 0.0;
                    if (array.getJSONObject(i).has(Constants.COLUMN_Rating))
                        rating = array.getJSONObject(i).getDouble(Constants.COLUMN_Rating);

                    Boolean IsopenNow = false;
                    if (array.getJSONObject(i).has(Constants.COLUMN_Opening_Hours))
                        IsopenNow = array.getJSONObject(i).getJSONObject(Constants.COLUMN_Opening_Hours)
                                .getBoolean(Constants.COLUMN_Is_Open);

                    String photoReference = "";
                    int photosHeight = 0;
                    int photosWidth = 0;
                    JSONObject item = array.getJSONObject(i);
                    if (item.has("photos")) {
                        JSONObject photos = item.getJSONArray("photos").getJSONObject(0);
                        JSONArray photo = array.getJSONObject(i).getJSONArray("photos");
                        if (photo.getJSONObject(0).has("photo_reference"))
                            photoReference = photo.getJSONObject(0).getString("photo_reference");
                        if (photo.getJSONObject(0).has("width")) ;
                        photosWidth = photo.getJSONObject(0).getInt("width");
                        if (photo.getJSONObject(0).has("height"))
                            photosHeight = photo.getJSONObject(0).getInt("height");
                    }
                    //send all attributes to a new object of MoviesSearchResult
                    place = new Place(lat, lng, photosHeight, photosWidth, rating, price_level,
                            IsopenNow, types, name, photoReference, placeId/*, id*/, icon, vicinity, address);
                    //add object of movieSearch to listView
                    handler.addPlace(place);

                }
            } else {
                Toast.makeText(SRV_Json.this, "No result", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //method- connect to an Api located on a Remote server
    private String sendHttpRequest(String param1, String param2, String param3, String param4, String param5) {


        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        InputStream input_stream = null;
        InputStreamReader input_stream_reader = null;
        StringBuilder respones = new StringBuilder();
        //open connection to server
        try {

            String urlString = null;
            //replace all white space with "%20" so a search
            // can be commence even if the user have inserted more than one search word
            param1 = param1.replaceAll(" ", "%20");
            if (param5.equals("searchByLocation")) {

                //search places near my location
                urlString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        + param3 + "," + param4 + "&radius=" + param2 + "&types=" + param1
                        + "&key=AIzaSyAd9yLftR0dTMPOu3nRZfvSwk59-vjECDk";
            } else if (param5.equals("searchByText")) {

                //search places by text
                urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="
                        + param1 + "&key=AIzaSyAd9yLftR0dTMPOu3nRZfvSwk59-vjECDk";
            }

            URL url = new URL(urlString);
            httpCon = (HttpURLConnection) url.openConnection();
            //check if connection established
            if (httpCon.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Cannot connect to: " + urlString);
                return null;
            }
            //fetch desired information
            input_stream = httpCon.getInputStream();
            input_stream_reader = new InputStreamReader(input_stream);
            input = new BufferedReader(input_stream_reader);
            String line;
            //arrange the info received from source
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
