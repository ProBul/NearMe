package com.example.user.finalandroidproject.fragments;


//interface to allow communication between fragments
public interface Communicator {
    //methods to pass information of the place between Fragments
    public void onCallBaclDouble(Double lat, Double lng, String str);
    public void sharedPrefCallBack(String measureUnit);

    //method to inflate Favorite Fragment
    public void openFavorite();

}
