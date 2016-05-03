package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.DataPoint.CREATE_TABLE);
        Log.d("hello", "hello");
    }

    // Method is called during an upgrade of the database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.DataPoint.DELETE_TABLE);
        onCreate(db);
    }

}
