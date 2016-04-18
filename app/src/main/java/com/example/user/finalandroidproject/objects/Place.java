package com.example.user.finalandroidproject.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by user on 04/12/2015.
 */

// class to create an object of a Place
public class Place {

        private LatLng location;// the location of the place in LatLng object
        private double lat, lng , rating, price_level, radius;
        private int photosWidth, photosHeight;
        private boolean IsopenNow;//is the place open or closed
        private String types, name, photoReference, placeId, id, icon, vicinity, address;//the type of place, name of place, reference to the photo of place

    //empty constructor
        public Place() {
        }

    //constructor with the full data
    public Place(double lat, double lng, int photosHeight, int photosWidth, double rating,
                 double price_level, boolean IsopenNow, String types, String name,
                 String photoReference, String placeId, String icon, String vicinity, String address) {
        this.lat = lat;
        this.lng = lng;
        this.photosHeight = photosHeight;
        this.photosWidth = photosWidth;
        this.rating = rating;
        this.price_level = price_level;
        this.IsopenNow = IsopenNow;
        this.types = types;
        this.name = name;
        this.photoReference = photoReference;
        this.placeId = placeId;
        //this.id = id;
        this.icon = icon;
        this.vicinity=vicinity;
        this.address= address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getPhotosHeight() {
        return photosHeight;
    }

    public void setPhotosHeight(int photosHeight) {
        this.photosHeight = photosHeight;
    }

    public int getPhotosWidth() {
        return photosWidth;
    }

    public void setPhotosWidth(int photosWidth) {
        this.photosWidth = photosWidth;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(Double lat){this.lat=lat;}

    public double getLng() {
        return lng;
    }


    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice_level() {
        return price_level;
    }

    public void setPrice_level(double price_level) {
        this.price_level = price_level;
    }

    public LatLng getLocation() {
            return location;
        }

        public void setLocation(LatLng location) {
            this.location = location;
        }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean getIsOpenNow() {
        return IsopenNow;
    }

    public void setIsOpenNow(boolean IsopenNow) {
        this.IsopenNow = IsopenNow;
    }

    public String getTypes() {
            return types;
        }

        public void setTypes(String types) {
            this.types = types;
        }
    //method returns the name of the place (used in listView)
    public String toString(){

        return name;
    }

}
