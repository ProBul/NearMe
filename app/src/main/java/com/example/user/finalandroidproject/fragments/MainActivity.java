package com.example.user.finalandroidproject.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalandroidproject.GPSTracker;
import com.example.user.finalandroidproject.R;
import com.example.user.finalandroidproject.dataBase.DBHandler;
import com.example.user.finalandroidproject.dataBase.DBHandlerFavorite;
import com.example.user.finalandroidproject.objects.Place;
import com.example.user.finalandroidproject.sharedPref.Settings;


public class MainActivity extends AppCompatActivity implements Communicator {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private boolean tabletSize;
    final String PREFS_NAME = "shared_prefs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define the Tags from xml Layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);//ActionBar

        //filter for charging and battery status
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        //filter for gps
        IntentFilter gpsFilter = new IntentFilter(Intent.ACTION_PROVIDER_CHANGED);


        //use of SharedPreferences to check if app runs for first time to build the DataBase
        DBHandler handler = new DBHandler(MainActivity.this);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //insert random info into the Table to initiate the handler for the first time
        if (settings.getBoolean("my_first_time", true)) {
            handler.addPlace(new Place(1.1, 1.1, 1, 1, 1.1, 1.1, true, "store", "alex", "www",
                    "place id", "icon", "near", "new address"));
            handler.deleteAllPlaces();
            settings.edit().putBoolean("my_first_time", false).commit();
        }

        //get the connectivity status of my device
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobileService = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo mWifiService = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //inform the user if he has no network connection or clean his search history
        if (mMobileService.isConnected() || mWifiService.isConnected()) {
            handler.deleteAllPlaces();
        } else if (!mMobileService.isConnected() && !mWifiService.isConnected()) {
            Toast.makeText(this, "No connection to Network, some of the features will not be " +
                    "available", Toast.LENGTH_LONG).show();
        }

        //determine if app runs on tablet or phone and outputs the correct layout
        try {
            tabletSize = getResources().getBoolean(R.bool.isTablet);

            // if the device in Tablet mode, inflate Fragment "control" dynamically
            if (tabletSize) {
                FragControl fragControl = new FragControl();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().add(R.id.fragment_container, fragControl, "FRAGCONTROL")
                        .addToBackStack("FRAGCONTROL").commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //set the UI with ViewPager only if its not tablet/big Screen device
        if (!tabletSize) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(0);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }

    }

    //inner class to deal with tabs in ViewPager
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return FragControl.newInstance(position + 1);
            } else if (position == 1) {
                return FragMap.newInstance(position + 1);
            } else return FragFavorite.newInstance(position + 2);
        }

        @Override
        // Show 3 total pages- in ViewPager.
        public int getCount() {
            return 3;
        }

        @Override
        // name of tabs
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "control";
                case 1:
                    return " map";
                case 2:
                    return "Favorite";
            }
            return null;
        }
    }

    //method to communicate between Fragments
    @Override
    public void onCallBaclDouble(Double lat, Double lng, String str) {
        //use this statement when in tablet mode
        FragMap fm = (FragMap) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (fm != null) {
            fm.onMapReady(lat, lng, str); //start method onMapReady with given variables
        } else {
            //use this statement when in phone mode
            mViewPager.setCurrentItem(1);//move to second tab
            FragMap mMap = (FragMap) getSupportFragmentManager().findFragmentByTag
                    ("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
            mMap.SendMapData(lat, lng, str);// send data to method in FragMap fragment
        }
    }

    @Override
    public void sharedPrefCallBack(String measureUnit) {

    }

    //this method will work only in tablet mode when the user clicked on "Favorite" button
    @Override
    public void openFavorite() {

        FragFavorite fragFav = new FragFavorite();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container, fragFav, "FRAGFAV").addToBackStack("FRAGFAV").commit();

    }

    @Override
    //create optionMenu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_cons, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                break;

            case R.id.deleteAllFromMenu:
                final AlertDialog.Builder builderLong = new AlertDialog.Builder(this);
                builderLong.setTitle("you are about to delete all favorite places");
                builderLong.setMessage("would you like to proceed?");

                builderLong.setPositiveButton("Yes", new DialogPreference(this) {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //will delete all favorite places and move tab to "Favorite" Fragment
                        if (!tabletSize) {
                            mViewPager.setCurrentItem(2);//move to third tab
                            FragFavorite fragFav = (FragFavorite) getSupportFragmentManager().findFragmentByTag
                                    ("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
                            fragFav.deleteAllFavorites();
                        }
                    }
                })
                        .setNegativeButton("Cancel", new DialogPreference(this) {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // this method handel situation when the GPS tracker is turned off and suggests the user to turn it on
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
}