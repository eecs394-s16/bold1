package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

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

    public Object[] getDataForPopup(SQLiteDatabase db, String timestamp) {
        String query = "SELECT systolic_pressure, diastolic_pressure, heart_rate, mean_arterial_pressure, " +
                "mood, exercise, tobacco, wake_up, food_intake, non_caffeine_fluid_intake, caffeine, " +
                "about_to_sleep, daily_activity, other FROM data_point WHERE timestamp = " + "'" + timestamp + "'" + ";";
        Cursor cursor = db.rawQuery(query, null);
        Object[] data = new Object[13];
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {

                data[0] = new Pair<String, String>("systolic_pressure", cursor.getString(cursor.getColumnIndex("systolic_pressure")));
                data[1] = new Pair<String, String>("diastolic_pressure", cursor.getString(cursor.getColumnIndex("diastolic_pressure")));
                data[2] = new Pair<String, String>("heart_rate", cursor.getString(cursor.getColumnIndex("heart_rate")));
                data[3] = new Pair<String, String>("mean_arterial_pressure", cursor.getString(cursor.getColumnIndex("mean_arterial_pressure")));
                data[4] = new Pair<String, String>("mood", cursor.getString(cursor.getColumnIndex("mood")));
                data[5] = new Pair<String, String>("exercise", cursor.getString(cursor.getColumnIndex("exercise")));
                data[6] = new Pair<String, String>("tobacco", cursor.getString(cursor.getColumnIndex("tobacco")));
                data[7] = new Pair<String, String>("wake_up", cursor.getString(cursor.getColumnIndex("wake_up")));
                data[8] = new Pair<String, String>("food_intake", cursor.getString(cursor.getColumnIndex("food_intake")));
                data[9] = new Pair<String, String>("non_caffeine_fluid_intake", cursor.getString(cursor.getColumnIndex("non_caffeine_fluid_intake")));
                data[10] = new Pair<String, String>("caffeine", cursor.getString(cursor.getColumnIndex("caffeine")));
                data[11] = new Pair<String, String>("about_to_sleep", cursor.getString(cursor.getColumnIndex("about_to_sleep")));
                data[12] = new Pair<String, String>("daily_activity", cursor.getString(cursor.getColumnIndex("daily_activity")));
            } while (cursor.moveToNext());
        }
        return data;
    }

}
