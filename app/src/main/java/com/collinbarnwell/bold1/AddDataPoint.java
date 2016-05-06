package com.collinbarnwell.bold1;

import com.collinbarnwell.bold1.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;


public class AddDataPoint extends AppCompatActivity {

    private int currentDataPointId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_point);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.add_data_point_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void savePressureNumbers(View button) {
        final EditText dpress_field = (EditText) findViewById(R.id.dia_press);
        int dia_press = Integer.parseInt(dpress_field.getText().toString());

        final EditText spress_field = (EditText) findViewById(R.id.sys_press);
        int sys_press = Integer.parseInt(spress_field.getText().toString());

        final EditText mean_art_press_field = (EditText) findViewById(R.id.art_press);
        int mean_art_pres = Integer.parseInt(mean_art_press_field.getText().toString());

        final EditText heart_rate_field = (EditText) findViewById(R.id.heart_rate);
        int heart_rate = Integer.parseInt(heart_rate_field.getText().toString());

        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.DATAEntry.DIASTOLIC_PRESSURE, dia_press);
        values.put(DatabaseContract.DATAEntry.SYSTOLIC_PRESSURE, sys_press);
        values.put(DatabaseContract.DATAEntry.MEAN_ARTERIAL_PRESSURE, mean_art_pres);
        values.put(DatabaseContract.DATAEntry.HEART_RATE, heart_rate);

        long newRowId;
        newRowId = db.insert(DatabaseContract.DataPoint.TABLE_NAME, null, values);


    }

    public void saveDataPointDetails (View button) {
        String FILE = "Hello_file";
        String string = "hello_world";


        try {
            FileOutputStream fos = openFileOutput(FILE, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try{
            FileInputStream fin = openFileInput(FILE);
            int c;
            String temp="";

            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            Toast.makeText(getBaseContext(),temp,Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
        }
    }


}
