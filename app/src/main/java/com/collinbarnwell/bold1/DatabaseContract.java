package com.collinbarnwell.bold1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import java.io.*;

import java.security.cert.CRLException;
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
    public static final int DATABASE_VERSION = 18;
    private static final String TEXT_TYPE          = " TEXT";
    private static final String INTEGER_TYPE       = " INTEGER";
    private static final String NUMERIC_TYPE       = " NUMERIC";
    private static final String REAL_TYPE          = " REAL";
    private static final String COMMA_SEP          = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    public static abstract class DATAEntry implements BaseColumns {
        public static final String ID = " id";
        public static final String SYSTOLIC_PRESSURE = "systolic_pressure";
        public static final String DIASTOLIC_PRESSURE = "diastolic_pressure";
        public static final String HEART_RATE = "heart_rate";
        public static final String MEAN_ARTERIAL_PRESSURE = "mean_arterial_pressure";
        public static final String TIMESTAMP = "timestamp";
        public static final String MOOD = "mood";
        public static final String EXERCISE = "exercise";
        public static final String TOBACCO = "tobacco";
        public static final String WAKE_UP = "wake_up";
        public static final String FOOD_INTAKE = "food_intake";
        public static final String NON_CAFFEINE_FLUID_INTAKE = "non_caffeine_fluid_intake";
        public static final String CAFFEINE = "caffeine";
        public static final String ABOUT_TO_SLEEP = "about_to_sleep";
        public static final String DAILY_ACTIVITY = "daily_activity";
        public static final String OTHER = "other";

    }

    /* Inner class that defines the table contents */
    public static final class DataPoint implements BaseColumns {
        // Do not allow this class to be instantiated
        private DataPoint() {}

        public static final String TABLE_NAME = "data_point";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                        " (" +
                        DATAEntry.ID + " INTEGER PRIMARY KEY," +
                        DATAEntry.SYSTOLIC_PRESSURE + REAL_TYPE + COMMA_SEP +
                        DATAEntry.DIASTOLIC_PRESSURE + REAL_TYPE + COMMA_SEP +
                        DATAEntry.MEAN_ARTERIAL_PRESSURE + REAL_TYPE + COMMA_SEP +
                        DATAEntry.HEART_RATE + INTEGER_TYPE + COMMA_SEP +
                        DATAEntry.TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + COMMA_SEP +
                        DATAEntry.MOOD + TEXT_TYPE + COMMA_SEP +
                        DATAEntry.EXERCISE + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.TOBACCO + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.WAKE_UP + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.FOOD_INTAKE + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.NON_CAFFEINE_FLUID_INTAKE + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.CAFFEINE + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.ABOUT_TO_SLEEP + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.DAILY_ACTIVITY + NUMERIC_TYPE + COMMA_SEP +
                        DATAEntry.OTHER + TEXT_TYPE +
                        " )" + ";";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

