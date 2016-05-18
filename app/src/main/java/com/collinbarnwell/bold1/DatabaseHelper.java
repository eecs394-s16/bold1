package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.DataPoint.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.DataPoint.DELETE_TABLE);
        onCreate(db);
    }

    public DataPoint[] getColumnDataPoints(SQLiteDatabase db, String column) {
        String query = "SELECT " + column + ", timestamp FROM data_point";
        Cursor cursor      = db.rawQuery(query, null);
        ArrayList<DataPoint> data = new ArrayList<DataPoint>();

        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {

                Double val = cursor.getDouble(cursor.getColumnIndex(column));
                String timestamp_string = cursor.getString(cursor.getColumnIndex("timestamp"));

                Timestamp dt = Timestamp.valueOf(timestamp_string);
                data.add(new DataPoint(dt, val));
                
            } while (cursor.moveToNext());
        }

        cursor.close();

        DataPoint[] dataArray = new DataPoint[data.size()];
        dataArray = data.toArray(dataArray);
        return dataArray;
    }

    public ArrayList<Double> getDataForPopup(SQLiteDatabase db, String timestamp) {
        String query = "SELECT systolic_pressure, diastolic_pressure, heart_rate FROM data_point WHERE timestamp = " + timestamp + ";";
        Cursor cursor = db.rawQuery("query", null);
        ArrayList<Double> data = new ArrayList<Double>();
        Double sp = cursor.getDouble(cursor.getColumnIndex("systolic_pressure"));
        Double dp = cursor.getDouble(cursor.getColumnIndex("diastolic_pressure"));
        Double hr = cursor.getDouble(cursor.getColumnIndex("heart_rate"));
        data.add(sp);
        data.add(dp);
        data.add(hr);
        return data;
    }

}
