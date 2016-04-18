package com.example.user.finalandroidproject.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.finalandroidproject.objects.FavoritePlaces;
import com.example.user.finalandroidproject.R;
import com.example.user.finalandroidproject.dataBase.DBHandlerFavorite;

import java.util.ArrayList;

/**
 * Created by user on 06/02/2016.
 */
public class FragFavorite extends Fragment implements Communicator {


    private FavoritePlaces favoritePlaces;
    private DBHandlerFavorite handlerFavorite;
    private ArrayList<FavoritePlaces> list;
    private Communicator comm;
    private SwipeRefreshLayout mSwipeRefresh;

    //--------------
    private RecyclerView mRecyclerView;
    private AdapterFavorite mAdapterFavorite;
    private RecyclerView.LayoutManager mLayoutManager;

    // inflate the fragment through pageView when the app starts.
    // after the first run the fragment will be managed trough onCreateView
    public static FragFavorite newInstance(int position){
        FragFavorite fragFavorite= new FragFavorite();
        Bundle bundle= new Bundle();
        bundle.putInt("fragmentPosition", position);
        fragFavorite.setArguments(bundle);
        return fragFavorite;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.frag_favorite, container, false);

        handlerFavorite= new DBHandlerFavorite(getActivity());

        mSwipeRefresh=(SwipeRefreshLayout)v.findViewById(R.id.swiperefresh_favorites);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        list=handlerFavorite.showAllPlacesArrayList();
        mAdapterFavorite = new AdapterFavorite(list, getActivity());
        mRecyclerView.setAdapter(mAdapterFavorite);

        list = new ArrayList<>();
        list=handlerFavorite.showAllPlacesArrayList();
        handlerFavorite.showAllPlacesArrayList();

        //add swipeToRefresh
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list= handlerFavorite.showAllPlacesArrayList();
                mAdapterFavorite.updateData(list);
                mRecyclerView.setAdapter(mAdapterFavorite);
                if (mSwipeRefresh.isRefreshing())
                    mSwipeRefresh.setRefreshing(false);
            }
        });
        return v;
    }

    public void deleteAllFavorites(){
        handlerFavorite.deleteAllPlaces();
        list=handlerFavorite.showAllPlacesArrayList();
        RecyclerView.Adapter mAdapter = new AdapterFavorite(list, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "All favorite places are gone", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        comm=(Communicator)context;
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