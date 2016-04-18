package com.example.user.finalandroidproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalandroidproject.GPSTracker;
import com.example.user.finalandroidproject.objects.Place;
import com.example.user.finalandroidproject.R;
import com.example.user.finalandroidproject.webContent.SRV_Json;
import com.example.user.finalandroidproject.dataBase.DBHandler;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class FragControl extends Fragment implements Communicator, AdapterView.OnItemSelectedListener {
    private ArrayList<Place> list;
    private boolean mSearchByText, mSearchByLocation, mListContentEmpty = true;
    private boolean tabletSize;
    private Spinner spinner;
    private ProgressBar bar;
    private Communicator comm;
    private EditText edType;
    private ImageView addDistance, removeDistance;
    private Button btn_favorite;
    private GPSTracker gps;
    private TextView txLat, txLong, txUnits, tvRadius;
    private String searchBy, unitItem;
    private DBHandler handler;
    public static final String TAG_SERVICE_JSON = "finnished breaking the Json";
    private FragmentManager manager;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;

    // inflate the fragment through pageView when the app starts.
    // after the first run the fragmant will be managed in onCreateView method
    public static FragControl newInstance(int position) {
        FragControl fragControl = new FragControl();
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentPosition", position);
        fragControl.setArguments(bundle);
        return fragControl;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_control, container, false);


        handler = new DBHandler(getActivity());

        // register the broadcast receiver to get the answer after SRV_Json finished his run
        IntentFilter filter = new IntentFilter(SRV_Json.FLAG_BROADCAST);
        MyReciver reciver = new MyReciver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reciver, filter);

        //get the SharedPreferences after the user picked if he wants the "distance units" in MILES or KM
        //and use this unit type to calculate the distances
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        unitItem = getPrefs.getString("measurUnit", "0");
        txUnits = (TextView) v.findViewById(R.id.textView_units);
        if (unitItem.equals("0")) {
            txUnits.setText("KM");
        } else {
            txUnits.setText("MILES");
        }

        //define the Tags from xml Layout
        edType = (EditText) v.findViewById(R.id.editText_type);
        txLat = (TextView) v.findViewById(R.id.textView_lat);
        txLong = (TextView) v.findViewById(R.id.textView_long);
        tvRadius = (TextView) v.findViewById(R.id.textView_radius);
        addDistance = (ImageView) v.findViewById(R.id.imageButton_add);
        removeDistance = (ImageView) v.findViewById(R.id.imageButton_remove);
        spinner = (Spinner) v.findViewById(R.id.spinner);
        mSwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        bar = (ProgressBar) v.findViewById(R.id.progressBar);
        bar.getIndeterminateDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);//set different color to ProgressBar
        btn_favorite = (Button) v.findViewById(R.id.button_favorite);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        list = handler.showAllPlacesArrayList();
        mAdapter = new MyAdapter(list, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //start void method to get my location from GPS class and insert the latitude and longitude into textView.
        getMyLocation();

        try {
            //determine if the user using a tablet, if he is then set boolean "tabletSize" variable
            tabletSize = getResources().getBoolean(R.bool.isTablet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // in tablet mode an extra button will appear that will take the user to "Favorite" fragment
        if (tabletSize)
            btn_favorite.setVisibility(View.VISIBLE);

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.openFavorite();
            }
        });

        //add swipeToRefresh and search with the last known parameters in the same search category
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mListContentEmpty) {
                    if (mSearchByLocation) {

                        mRecyclerView.setVisibility(View.GONE);

                        String type = spinner.getSelectedItem().toString();
                        String rad = tvRadius.getText().toString();
                        String latitude = txLat.getText().toString();
                        String longitude = txLong.getText().toString();
                        searchBy = "searchByLocation";// used as flag to send search request by "location"

                        //calculation of distance from km to meters
                        double distanceValue = Double.parseDouble(rad);
                        distanceValue = distanceValue * 1000;

                        // calculation of distance in miles if the user picked "miles" as his setting option
                        if (txUnits.getText() == "MILES") {
                            distanceValue = distanceValue / 0.62137;
                        }
                        rad = String.valueOf(distanceValue);

                        //set progressBar to visible so the user can get indication that
                        //a search is being conducted
                        bar.setVisibility(ProgressBar.VISIBLE);

                        //start service with search request
                        SRV_Json.startActionFoo(getActivity(), type, rad, latitude, longitude, searchBy);

                        //reset the flag
                        mSearchByLocation = false;
                    } else {

                        mRecyclerView.setVisibility(View.GONE);

                        String type = edType.getText().toString();
                        String rad = tvRadius.getText().toString();
                        String latitude = txLat.getText().toString();
                        String longitude = txLong.getText().toString();
                        searchBy = "searchByText";//used as flag to send search request by "key word"

                        //calculation of distance from km to meters- google API demand meters as distance
                        double distanceValue = Double.parseDouble(rad);
                        distanceValue = distanceValue * 1000;

                        // calculation of distance in miles if the user picked "miles" as his setting option
                        if (txUnits.getText() == "MILES") {
                            distanceValue = distanceValue / 0.62137;
                        }
                        rad = String.valueOf(distanceValue);

                        //set progressBar to visible so the user can get indication that
                        //a search is being conducted
                        bar.setVisibility(ProgressBar.VISIBLE);

                        SRV_Json.startActionFoo(getActivity(), type, rad, latitude, longitude, searchBy);

                        //reset the flag- next time the swipeToRefresh will get a new flag depending on the user`s pick
                        mSearchByText = false;
                    }

                } else
                    Toast.makeText(getContext(), "Nothing to refresh", Toast.LENGTH_LONG).show();

                //stop showing the refresh sign when the service finished to run
                if (mSwipeRefresh.isRefreshing())
                    mSwipeRefresh.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //adds 0.5km/miles to the radius from the user after each push on "+"
        addDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double distanceValue;
                String distance = tvRadius.getText().toString();
                if (distance.length() >= 0)
                    try {
                        distanceValue = Double.parseDouble(distance);
                        if (distanceValue <= 49.5 && distanceValue >= 0.0) {
                            distanceValue = distanceValue + 0.5;
                            distance = Double.toString(distanceValue);
                            tvRadius.setText(distance);
                        } else
                            Toast.makeText(getContext(), "You have reached the limited distance",
                                    Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

        //reduces 0.5km/miles of the radius from the user after each push on "-"
        removeDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distanceValue;
                String distance = tvRadius.getText().toString();
                if (distance.length() >= 0)
                    try {
                        distanceValue = Double.parseDouble(distance);
                        if (distanceValue <= 49.5 && distanceValue >= 0.5) {
                            distanceValue = distanceValue - 0.5;
                            distance = Double.toString(distanceValue);
                            tvRadius.setText(distance);
                        } else
                            Toast.makeText(getContext(), "You have reached MIN distance",
                                    Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

        //create the spinner with default Adapter
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.types,
                android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        //creates floating action button- material design
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.search);

        FloatingActionButton actionButton = new FloatingActionButton.Builder((Activity) getContext())
                .setContentView(imageView)
                .setTheme(FloatingActionButton.THEME_LIGHT)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_LEFT)
                .build();

        //set what will happen when user clicked on floating button
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //show alertDialog with 2 options
                AlertDialog.Builder builderLong = new AlertDialog.Builder(getContext());
                builderLong.setTitle("what should we do?");
                builderLong.setMessage("would you like to search places Near you or by Keyword?");

                //set what will happen when the user clicked on "By Keywords" option-
                // Service with search "By Keywords" will comanche
                builderLong.setPositiveButton("By Keywords", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = edType.getText().toString();

                        //works only if the user set input in the editText
                        if (!type.isEmpty()) {
                            //clear the DataBase so a new search could commence
                            handler.deleteAllPlaces();
                            mRecyclerView.setVisibility(View.INVISIBLE);
                            bar.setVisibility(ProgressBar.VISIBLE);//progressBar

                            String rad = tvRadius.getText().toString();
                            String latitude = txLat.getText().toString();
                            String longitude = txLong.getText().toString();
                            searchBy = "searchByText";

                            //calculation of distance from km to meters
                            double distanceValue = Double.parseDouble(rad);
                            distanceValue = distanceValue * 1000;

                            // calculation of distance in Miles to KM if the user picked "miles" as his setting option
                            if (txUnits.getText() == "MILES") {
                                distanceValue = distanceValue / 0.62137;
                            }
                            rad = String.valueOf(distanceValue);
                            mSearchByText = true;

                            SRV_Json.startActionFoo(getActivity(), type, rad, latitude, longitude, searchBy);
                        } else {
                            Snackbar.make(v, "please type a place of interest", Snackbar.LENGTH_LONG).show();
                        }

                    }
                });

                //set what will happen when the user clicked on "Near me" option-
                // Service with search "Near me" option will comanche
                builderLong.setNegativeButton("Near me ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String type = spinner.getSelectedItem().toString();
                        if (!String.valueOf(type).equals("category")) {

                            //clear the DataBase so a new search could commence
                            handler.deleteAllPlaces();
                            mRecyclerView.setVisibility(View.INVISIBLE);

                            //progressBar is visible as long
                            // the service is running
                            bar.setVisibility(ProgressBar.VISIBLE);

                            String rad = tvRadius.getText().toString();
                            String latitude = txLat.getText().toString();
                            String longitude = txLong.getText().toString();
                            searchBy = "searchByLocation";

                            //calculation of distance from km to meters
                            double distanceValue = Double.parseDouble(rad);
                            distanceValue = distanceValue * 1000;

                            // calculation of distance in miles if the user picked "miles" as his setting option
                            if (txUnits.getText() == "MILES") {
                                distanceValue = distanceValue / 0.62137;
                            }
                            rad = String.valueOf(distanceValue);
                            mSearchByLocation = true;

                            SRV_Json.startActionFoo(getActivity(), type, rad, latitude, longitude, searchBy);
                        } else {
                            Snackbar.make(v, "please choose category", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).show();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    //attach the communicator to the context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm = (Communicator) context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCallBaclDouble(Double lat, Double lng, String str) {

    }

    @Override
    public void sharedPrefCallBack(String measureUnit) {

    }

    //send a signal with interface to open "Favorite" fragment- will be used in tablet mode
    @Override
    public void openFavorite() {

    }

    //choose the category to search in Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerType = spinner.getSelectedItem().toString();
                // edType.setText(spinnerType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Receiver to receive the processed JSON and updating the RecycleView
    private class MyReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            bar.setVisibility(ProgressBar.INVISIBLE);// change the visibility of ProgressBar if the service was completed
            mRecyclerView.setVisibility(View.VISIBLE);// change the visibility of RecycleView with the data that was received

            //update the list with the data from the service
            list = handler.showAllPlacesArrayList();
            mAdapter.updateData(list);
            mRecyclerView.setAdapter(mAdapter);

            //set a flag that the list is full
            if (!list.isEmpty())
                mListContentEmpty = false;

            // stop the refreshing sign
            if (mSwipeRefresh.isRefreshing())
                mSwipeRefresh.setRefreshing(false);
        }
    }

    //method that gets the coordinates of the user and set them in textViews
    public void getMyLocation() {
        gps = new GPSTracker(getContext());

        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps.showSettingsAlert();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "getting location", Toast.LENGTH_LONG).show();
                }
            }, 5000);

            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                txLat.setText(new DecimalFormat("###.####").format(latitude));
                txLong.setText(new DecimalFormat("###.####").format(longitude));

            } else {
                gps.showSettingsAlert();
            }

        } else {
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                txLat.setText(new DecimalFormat("###.####").format(latitude));
                txLong.setText(new DecimalFormat("###.####").format(longitude));
            } else {
                gps.showSettingsAlert();
            }
        }
    }
}