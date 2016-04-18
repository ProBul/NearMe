package com.example.user.finalandroidproject.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 04/12/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //build a table when the app runs for the first time
        String cmd = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.COLUMN_Id + " INTEGER PRIMARY KEY, "
                + Constants.COLUMN_Name + " TEXT,"
                + Constants.COLUMN_Type + " TEXT,"
                + Constants.COLUMN_PlaceId + " TEXT,"
                + Constants.COLUMN_Lat + " REAL,"
                + Constants.COLUMN_Lng + " REAL,"
                + Constants.COLUMN_Icon + " TEXT,"
                + Constants.COLUMN_PriceLevel + " REAL,"
                + Constants.COLUMN_Is_Open + " TEXT,"
                + Constants.COLUMN_Photo_Height + " REAL,"
                + Constants.COLUMN_Photo_width + " REAL,"
                + Constants.COLUMN_Rating + " REAL,"
                + Constants.COLUMN_Vicinity + " TEXT,"
                + Constants.COLUMN_Address + " TEXT,"
                + Constants.COLUMN_Photo_Reference + " TEXT );";

        try {
            db.execSQL(cmd);
            Log.e("DATABASE OPERATIONS", "Database created/opened...");

        } catch (SQLiteException e) {
            e.getMessage();
        }
    }

    //checks if a new version exists and updates new feathers
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
