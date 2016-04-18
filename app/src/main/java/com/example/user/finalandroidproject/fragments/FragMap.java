package com.example.user.finalandroidproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.finalandroidproject.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by user on 04/12/2015.
 */
public class FragMap extends Fragment {
    private GoogleMap mMap;
    MapView mapView;

    // inflate the fragment through pageView when the app starts.
    // after the first run the fragment will be managed in onCreateView method
    public static FragMap newInstance(int position) {
        FragMap fragMap = new FragMap();
        Bundle bundle = new Bundle();
        bundle.putInt("fragmentPosition", position);
        fragMap.setArguments(bundle);
        return fragMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_map, container, false);

        Bundle bundle = getArguments();
        if (bundle!=null){
            String s = bundle.getString("name");
        }

        //Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        //Gets the GoogleMap from the MapView and does initialization
        mMap = mapView.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);


        // need to call MapInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getContext());
        return v;
    }

    public void SendMapData(Double lat, Double lng,String str){
        onMapReady(lat,lng,str);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void onMapReady(Double lat, Double lng,String str) {

            //updates the location and zooms at the location with marker on the place`s location
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12);
            mMap.animateCamera(cameraUpdate);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title(str));
    }
}