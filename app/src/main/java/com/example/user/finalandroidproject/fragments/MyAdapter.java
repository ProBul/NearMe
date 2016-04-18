package com.example.user.finalandroidproject.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.finalandroidproject.GPSTracker;
import com.example.user.finalandroidproject.R;
import com.example.user.finalandroidproject.dataBase.DBHandlerFavorite;
import com.example.user.finalandroidproject.objects.FavoritePlaces;
import com.example.user.finalandroidproject.objects.Place;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by user on 09/02/2016.
 */

////custom adapter for RecycleView located in FragControl

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Communicator {
    private Context context;
    private Communicator comm = (Communicator) context;
    private ArrayList<Place> mDataset;
    private double latA, lngA, latB, lngB;
    private GPSTracker gps;


    public ArrayList<Place> getmDataset() {
        return mDataset;
    }

    public void updateData(ArrayList<Place> places) {
        mDataset.clear();
        mDataset.addAll(places);
        notifyDataSetChanged();
    }

    public void setmDataset(ArrayList<Place> mDataset) {
        this.mDataset = mDataset;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Place> dataset, Context context) {
        this.mDataset = dataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getName());
        holder.mTextView2.setText(mDataset.get(position).getAddress());

        //get a photo reference from dataBase and using picasso and google service show the photo
        String photo = mDataset.get(position).getPhotoReference();
        if (photo.equals("")) {
            holder.mImagePlace.setImageResource(R.drawable.no_photo);
        } else {
            Picasso.with(context)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&" +
                            "maxheight=200&photoreference=" + photo +
                            "&key=AIzaSyAd9yLftR0dTMPOu3nRZfvSwk59-vjECDk")
                    .into(holder.mImagePlace);
        }

        //use location of user and place to show the distance
        gps = new GPSTracker(context);
        if (gps.canGetLocation()) {
            //get the user location
            latA = gps.getLatitude();
            lngA = gps.getLongitude();
            if (latA != 0.0 && lngA != 0.0) {

                Location locationA = new Location("point A");
                locationA.setLatitude(latA);
                locationA.setLongitude(lngA);

                //get the Place location
                latB = mDataset.get(position).getLat();
                lngB = mDataset.get(position).getLng();


                Location locationB = new Location("point B");
                locationB.setLatitude(latB);
                locationB.setLongitude(lngB);

                //the result= distance
                double distance = locationA.distanceTo(locationB);
                distance = distance / 1000;
                holder.mTextViewDistance.setText(String.format("%.2f", distance) + " km");
            }
        } else {
            gps.showSettingsAlert();
        }

            //define click on card
            holder.mCard_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Place place = mDataset.get(position);
                    double lat = place.getLat();
                    double lng = place.getLng();
                    String nameOfPlace = place.getName();
                    comm.onCallBaclDouble(lat, lng, nameOfPlace);
                }
            });

        //define long click on card
        holder.mCard_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder builderLong = new AlertDialog.Builder(context);
                Place place = mDataset.get(position);
                final double lat = place.getLat();
                final double lng = place.getLng();
                final String nameOfPlace = place.getName();

                //share
                builderLong.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is the place I was telling you about: " + nameOfPlace);
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                });

                //save
                builderLong.setNegativeButton("Save to favorites ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Place place = mDataset.get(position);
                        DBHandlerFavorite handlerFavorite = new DBHandlerFavorite(context);
                        handlerFavorite.addFavoritePlace(place);
                        Toast.makeText(context, "Place was saved", Toast.LENGTH_LONG).show();
                    }
                }).show();

                return true;
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView, mTextView2, mTextViewDistance;
        public ImageView mImagePlace, mImageDistance;
        public LinearLayout mCard_layout;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.textView_nameOfPlace);
            mTextView2 = (TextView) v.findViewById(R.id.textView_address);
            mTextViewDistance = (TextView) v.findViewById(R.id.textView_distance);
            mImagePlace = (ImageView) v.findViewById(R.id.imageView);
            mCard_layout = (LinearLayout) v.findViewById(R.id.card_layout);
            mImageDistance = (ImageView) v.findViewById(R.id.imageView_distace);
        }
    }

    //attach the interface to MyAdapter
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        comm = (Communicator) context;
    }

    //methods to send information between Fragments and other activities
    @Override
    public void onCallBaclDouble(Double lat, Double lng, String str) {
    }

    @Override
    public void sharedPrefCallBack(String measureUnit) {

    }

    @Override
    public void openFavorite() {

    }


}