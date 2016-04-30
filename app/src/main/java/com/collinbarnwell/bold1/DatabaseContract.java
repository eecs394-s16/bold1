package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;


public final class DatabaseContract {
    /*
    Tons of help from:
    http://stackoverflow.com/questions/17451931/how-to-use-a-contract-class-in-android
    */
    public static final String AUTHORITY = "com.collinbarnwell.bold1";
    public static final String SCHEME = "content://";
    public static final String SLASH = "/";
    public static final String DATABASE_NAME = "bold1.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE          = " TEXT";
    private static final String COMMA_SEP          = ",";

    public static final String[] SQL_CREATE_TABLE_ARRAY = {
            DataPoint.CREATE_TABLE
    };

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static final class DataPoint implements BaseColumns {
        // Do not allow this class to be instantiated
        private DataPoint() {}

        public static final String TABLE_NAME = "DataPoint";
        public static final String KEY_DATA_POINT_ID = "DataPointId";
        public static final String KEY_DIASTOLIC_PRESSURE = "DiastolicPressure";
        public static final String KEY_SYSTOLIC_PRESSURE = "SystolicPressure";
        public static final String KEY_MEAN_ARTERIAL_PRESSURE = "MeanArterialPressure";
        public static final String KEY_HEART_RATE = "HeartRate";
        public static final String KEY_TIMESTAMP = "Timestamp";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                KEY_DATA_POINT_ID + TEXT_TYPE + COMMA_SEP +
                KEY_DIASTOLIC_PRESSURE + TEXT_TYPE + COMMA_SEP +
                KEY_MEAN_ARTERIAL_PRESSURE + TEXT_TYPE + COMMA_SEP +
                KEY_HEART_RATE + TEXT_TYPE + COMMA_SEP +
                KEY_TIMESTAMP + TEXT_TYPE + COMMA_SEP + " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        // Save record with a timestamp
        // String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        public static final String[] KEY_ARRAY = {
                KEY_DATA_POINT_ID,
                KEY_DIASTOLIC_PRESSURE,
                KEY_SYSTOLIC_PRESSURE,
                KEY_MEAN_ARTERIAL_PRESSURE,
                KEY_HEART_RATE,
                KEY_TIMESTAMP
        };
    }
}

