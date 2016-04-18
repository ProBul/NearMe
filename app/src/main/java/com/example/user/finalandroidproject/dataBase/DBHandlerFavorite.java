package com.example.user.finalandroidproject.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.example.user.finalandroidproject.objects.FavoritePlaces;
import com.example.user.finalandroidproject.objects.Place;

import java.util.ArrayList;

/**
 * Created by user on 31/12/2015.
 */
public class DBHandlerFavorite {
    private DBHelperFavorite helperFavorite;

    public DBHandlerFavorite(Context context) {
        helperFavorite = new DBHelperFavorite(context, Constants.DATABASE_NAME_FAVORITE, null, Constants.DATABASE_VERSION);
    }

    //method that receives values and insert them in constructed table as new row
    public void addFavoritePlace(Place favoritePlaces) {
        SQLiteDatabase db = helperFavorite.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_PlaceId, favoritePlaces.getPlaceId());
            values.put(Constants.COLUMN_Id, favoritePlaces.getId());
            values.put(Constants.COLUMN_Icon, favoritePlaces.getIcon());
            values.put(Constants.COLUMN_Is_Open, favoritePlaces.getIsOpenNow());
            values.put(Constants.COLUMN_Photo_Height, favoritePlaces.getPhotosHeight());
            values.put(Constants.COLUMN_Photo_width, favoritePlaces.getPhotosWidth());
            values.put(Constants.COLUMN_Rating, favoritePlaces.getRating());
            values.put(Constants.COLUMN_Vicinity, favoritePlaces.getVicinity());
            values.put(Constants.COLUMN_Address, favoritePlaces.getAddress());
            values.put(Constants.COLUMN_Name, favoritePlaces.getName());
            values.put(Constants.COLUMN_Type, favoritePlaces.getTypes());
            values.put(Constants.COLUMN_Photo_Reference, favoritePlaces.getPhotoReference());
            values.put(Constants.COLUMN_Lat, favoritePlaces.getLat());
            values.put(Constants.COLUMN_Lng, favoritePlaces.getLng());
            values.put(Constants.COLUMN_PriceLevel, favoritePlaces.getPrice_level());
            db.insert(Constants.TABLE_NAME, null, values);
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

        SQLiteDatabase db = helperFavorite.getWritableDatabase();
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
    public ArrayList<FavoritePlaces> showAllPlacesArrayList() {
        Cursor cursor = null;
        SQLiteDatabase db = helperFavorite.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME, null, null, null, null, null, null);

        } catch (SQLiteException e) {
            e.getMessage();
        }
        ArrayList<FavoritePlaces> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            FavoritePlaces favoritePlaces = new FavoritePlaces();
            favoritePlaces.setId("" + cursor.getInt(0));
            favoritePlaces.setName(cursor.getString(1));
            favoritePlaces.setTypes(cursor.getString(2));
            favoritePlaces.setPhotoReference(cursor.getString(14));
            favoritePlaces.setLat(cursor.getDouble(4));
            favoritePlaces.setLng(cursor.getDouble(5));
            favoritePlaces.setAddress(cursor.getString(13));

            list.add(favoritePlaces);
        }
        return list;
    }

    //A method to delete all values from dataBase
    public void deleteAllPlaces() {
        SQLiteDatabase db = helperFavorite.getWritableDatabase();
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