package com.collinbarnwell.bold1;

import com.collinbarnwell.bold1.DatabaseHelper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ViewFlipper;

public class AddDataPoint extends AppCompatActivity {

    private int currentDataPointId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_point);
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

        currentDataPointId = 4;
        values.put(DatabaseContract.DATAEntry.ID, 4);
        values.put(DatabaseContract.DATAEntry.DIASTOLIC_PRESSURE, dia_press);
        values.put(DatabaseContract.DATAEntry.SYSTOLIC_PRESSURE, sys_press);
        values.put(DatabaseContract.DATAEntry.MEAN_ARTERIAL_PRESSURE, mean_art_pres);
        values.put(DatabaseContract.DATAEntry.HEART_RATE, heart_rate);

        long newRowId;
        newRowId = db.insert(DatabaseContract.DataPoint.TABLE_NAME, null, values);

        // Flip to next form page
        flipper = (ViewFlipper) findViewById(R.id.);
        flipper.addView(myView,myViewIndex);

        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
    }

    public void saveDataPointDetails (View button) {


    }


}
