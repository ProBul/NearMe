package com.example.user.finalandroidproject.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.user.finalandroidproject.objects.Place;

import java.util.ArrayList;

/**
 * Created by user on 04/12/2015.
 */
public class DBHandler {
    private DBHelper helper;
    ArrayList<Place> list = new ArrayList<>();
    public Context context;

    public DBHandler(Context context) {

        helper = new DBHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    //method that receives values and insert them in constructed table as new row
    public void addPlace(Place place) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_PlaceId, place.getPlaceId());
            values.put(Constants.COLUMN_Id, place.getId());
            values.put(Constants.COLUMN_Icon, place.getIcon());
            values.put(Constants.COLUMN_Is_Open, place.getIsOpenNow());
            values.put(Constants.COLUMN_Photo_Height, place.getPhotosHeight());
            values.put(Constants.COLUMN_Photo_width, place.getPhotosWidth());
            values.put(Constants.COLUMN_Rating, place.getRating());
            values.put(Constants.COLUMN_Vicinity, place.getVicinity());
            values.put(Constants.COLUMN_Address, place.getAddress());
            values.put(Constants.COLUMN_Name, place.getName());
            values.put(Constants.COLUMN_Type, place.getTypes());
            values.put(Constants.COLUMN_Photo_Reference, place.getPhotoReference());
            values.put(Constants.COLUMN_Lat, place.getLat());
            values.put(Constants.COLUMN_Lng, place.getLng());
            values.put(Constants.COLUMN_PriceLevel, place.getPrice_level());
            db.insert(Constants.TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    //method that receives values and updates the old ones
    public void updatePlaceList(Place place) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_PlaceId, place.getPlaceId());
            values.put(Constants.COLUMN_Id, place.getId());
            values.put(Constants.COLUMN_Icon, place.getIcon());
            values.put(Constants.COLUMN_Is_Open, place.getIsOpenNow());
            values.put(Constants.COLUMN_Photo_Height, place.getPhotosHeight());
            values.put(Constants.COLUMN_Photo_width, place.getPhotosWidth());
            values.put(Constants.COLUMN_Rating, place.getRating());
            values.put(Constants.COLUMN_Vicinity, place.getVicinity());
            values.put(Constants.COLUMN_Address, place.getAddress());
            values.put(Constants.COLUMN_Name, place.getName());
            values.put(Constants.COLUMN_Type, place.getTypes());
            values.put(Constants.COLUMN_Photo_Reference, place.getPhotoReference());
            values.put(Constants.COLUMN_Lat, place.getLat());
            values.put(Constants.COLUMN_Lng, place.getLng());
            values.put(Constants.COLUMN_PriceLevel, place.getPrice_level());

            //update the values on the position provided with an "id" value
            db.update(Constants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(place.getId())});

        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    //A method to delete one specified row
    public void deletePlace(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            //deletes the row on the position provided with an "id" value
            db.delete(Constants.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    //method to present all object stored in the dataBase (from table)
    public ArrayList<Place> showAllPlacesArrayList() {
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Place> list = new ArrayList<>();
        try {
            cursor = db.query(Constants.TABLE_NAME, null, null, null, null, null, null);

        } catch (SQLiteException e) {
            e.getMessage();
        } finally {

            while (cursor.moveToNext()) {
                Place place = new Place();
                place.setId("" + cursor.getInt(0));
                place.setName(cursor.getString(1));
                place.setTypes(cursor.getString(2));
                place.setPhotoReference(cursor.getString(14));
                place.setLat(cursor.getDouble(4));
                place.setLng(cursor.getDouble(5));
                place.setAddress(cursor.getString(13));

                list.add(place);
            }
            if (db.isOpen()) {
                db.close();
            }
        }
        return list;
    }

    //A method to delete all values from dataBase
    public void deleteAllPlaces() {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constants.TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen()) {
                db.close();
            }
        }
    }
}