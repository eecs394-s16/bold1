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

    // HELPER FUNCTIONS SECTION

    public Date doubleToDate(double dateDouble){
        return new Date((long)(dateDouble));
    }

    public static Date getDateFromYearMonthDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Calendar dateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public static int getDayOfMonthFromDate(Date date){
        Calendar cal=dateToCalendar(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private static String getFormattedStringFromDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    // return an array list of data points
    // By default this will be ordered by time.
    private ArrayList<DataPoint> getColumnArrayList(SQLiteDatabase db, String column) {
        String query = "SELECT " + column + ", timestamp FROM data_point ORDER BY timestamp";
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
        return data;
    }
    // return an array list of data points between the from and till time.
    // By default this will be ordered by time.
    private ArrayList<DataPoint> getColumnArrayListFromTillDate(SQLiteDatabase db, String column,Date from,Date till ) {
        String query = "SELECT " + column +
                ", timestamp FROM data_point WHERE timestamp >= strftime('"+ getFormattedStringFromDate(from)+"')"+
                " AND timestamp < strftime('"+getFormattedStringFromDate(till)+"') ORDER BY timestamp";
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
        return data;
    }

    ////////////////////////////////////// FUNCTIONS USED IN MAIN ACTIVITY

    public DataPoint[] getColumnDataPoints(SQLiteDatabase db, String column) {

        ArrayList<DataPoint> data = getColumnArrayList(db,column);

        DataPoint[] dataArray = new DataPoint[data.size()];
        dataArray = data.toArray(dataArray);
        return dataArray;
    }

    public double getAverageOverPastWeek(SQLiteDatabase db, String column) {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekAgo = cal.getTime();
        ArrayList<DataPoint> data = getColumnArrayListFromTillDate(db,column,oneWeekAgo,now);

        // Now iterate through the list and combine the data into one.
        double ret=0.0;

        int currDataPointCount=0;// Number of data points in the same day
        for (int i = 0; i<data.size();i++){
            ret+=data.get(i).getY();
            currDataPointCount++;
        }
        // Now take the average
        return ret/currDataPointCount;
    }


    public double getAverageOverPastMonth(SQLiteDatabase db, String column) {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        Date oneMonthAgo = cal.getTime();
        ArrayList<DataPoint> data = getColumnArrayListFromTillDate(db,column,oneMonthAgo,now);

        // Now iterate through the list and combine the data into one.
        double ret=0.0;

        int currDataPointCount=0;// Number of data points in the same day
        for (int i = 0; i<data.size();i++){
            ret+=data.get(i).getY();
            currDataPointCount++;
        }
        // Now take the average
        return ret/currDataPointCount;
    }

    public DataPoint[] getDailyAverageDataPoints(SQLiteDatabase db, String column) {
        ArrayList<DataPoint> data = getColumnArrayList(db,column);

        // Now iterate through the list and combine the data on the same day into one.
        ArrayList<DataPoint> dailyData = new ArrayList<DataPoint>();
        DataPoint currDataPoint=null;

        int currDataPointCount=0;// Number of data points in the same day
        for (int i = 0; i<data.size();i++){
            if (currDataPoint==null){
                // If it's the very first datapoint, just create a new one
                currDataPoint = new DataPoint(data.get(i).getX(),data.get(i).getY());
                currDataPointCount=1;
            }else if (getDayOfMonthFromDate(doubleToDate(currDataPoint.getX()))==getDayOfMonthFromDate(doubleToDate(data.get(i).getX()))){
                // If both data point is in the same day, then combine them into one by taking an average
                currDataPoint = new DataPoint(currDataPoint.getX(),currDataPoint.getY()+ data.get(i).getY());
                currDataPointCount++;
            }
            else{
                // Otherwise, set the x value to the date, and set y value to the average over the day
                // The time is 00:00 by default for all days
                Calendar currCalendar = dateToCalendar(doubleToDate(currDataPoint.getX()));
                Date currDate= getDateFromYearMonthDay(currCalendar.get(Calendar.YEAR),currCalendar.get(Calendar.MONTH),currCalendar.get(Calendar.DAY_OF_MONTH));

                currDataPoint= new DataPoint(currDate,currDataPoint.getY()/currDataPointCount);
                dailyData.add(new DataPoint(currDataPoint.getX(),currDataPoint.getY()));
                // now after we've added the data point to list, replace the current one with the new one

                currDataPoint = new DataPoint(data.get(i).getX(),data.get(i).getY());
                currDataPointCount=1;
            }
        }
        // Now don't forget to add the last currDatapoint to the list
        Calendar currCalendar = dateToCalendar(doubleToDate(currDataPoint.getX()));
        Date currDate= getDateFromYearMonthDay(currCalendar.get(Calendar.YEAR),currCalendar.get(Calendar.MONTH),currCalendar.get(Calendar.DAY_OF_MONTH));

        currDataPoint= new DataPoint(currDate,currDataPoint.getY()/currDataPointCount);
        dailyData.add(new DataPoint(currDataPoint.getX(),currDataPoint.getY()));

        // Convert to array
        DataPoint[] dataArray = new DataPoint[dailyData.size()];
        dataArray = dailyData.toArray(dataArray);
        return dataArray;
    }

    public int[] getHypertensionRiskLevelCounts(SQLiteDatabase db) {
        String query = "SELECT * FROM data_point";
        Cursor cursor      = db.rawQuery(query, null);
        ArrayList<DataPoint> data = new ArrayList<DataPoint>();

        int hypII = 0;
        int hypI = 0;
        int preHyp = 0;
        int normal = 0;

        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                Double sys = cursor.getDouble(cursor.getColumnIndex("systolic_pressure"));
                Double dias = cursor.getDouble(cursor.getColumnIndex("systolic_pressure"));

                if (sys > 160 || dias > 100) {
                    hypII++;
                } else if (sys > 139 || dias > 89) {
                    hypI++;
                } else if (sys > 120 || dias > 80) {
                    preHyp++;
                } else {
                    normal++;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return new int[] {hypII, hypI, preHyp, normal};
    }


}
