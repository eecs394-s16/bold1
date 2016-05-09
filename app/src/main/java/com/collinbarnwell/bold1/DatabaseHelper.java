package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
        Log.d("hello", "hello");
    }

    // Method is called during an upgrade of the database
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.DataPoint.DELETE_TABLE);
        onCreate(db);
    }

    public DataPoint[] getColumnDataPoints(SQLiteDatabase db, String column) {
        String query = "SELECT '" + column + "', 'timestamp' FROM data_point";
        Cursor cursor      = db.rawQuery(query, null);
        ArrayList<DataPoint> data = new ArrayList<DataPoint>();

        while(!cursor.isAfterLast()) {
            Double val = cursor.getDouble(cursor.getColumnIndex(column));
            String timestamp_string = cursor.getString(cursor.getColumnIndex("timestamp"));

            Calendar t = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
            Date dt = null; //replace 4 with the column index
            try {
                dt = sdf.parse(cursor.getString(4));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            t.setTime(dt);
            data.add(new DataPoint(dt, val));
            cursor.moveToNext();
        }
        cursor.close();

        DataPoint[] dataArray = new DataPoint[data.size()];
        dataArray = data.toArray(dataArray);
        return dataArray;
    }
}
