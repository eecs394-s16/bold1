package com.collinbarnwell.bold1;

import com.collinbarnwell.bold1.DatabaseHelper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;


public class AddDataPoint extends AppCompatActivity {

    private ViewFlipper VF;
    private int currentViewflipperChild = 0;

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
        VF = (ViewFlipper) findViewById(R.id.datapoint1);
    }

    @Override public void onBackPressed()
    {
        if (currentViewflipperChild == 0) {
            super.onBackPressed();
        } else {
            currentViewflipperChild -= 1;
            VF.setDisplayedChild(currentViewflipperChild);
        }
    }

    public void nextFlipper(View button) {
        currentViewflipperChild += 1;
        VF.setDisplayedChild(currentViewflipperChild);
    }

    public void saveDataPoint(View button) {
        final EditText dpress_field = (EditText) findViewById(R.id.dia_press);
        int dia_press = Integer.parseInt(dpress_field.getText().toString());

        final EditText spress_field = (EditText) findViewById(R.id.sys_press);
        int sys_press = Integer.parseInt(spress_field.getText().toString());

        final EditText mean_art_press_field = (EditText) findViewById(R.id.art_press);
        int mean_art_pres = Integer.parseInt(mean_art_press_field.getText().toString());

        final EditText heart_rate_field = (EditText) findViewById(R.id.heart_rate);
        int heart_rate = Integer.parseInt(heart_rate_field.getText().toString());

        String other = findViewById(R.id.otherThings).toString();

        boolean food_intake = findViewById(R.id.food_intake).isPressed();
        boolean caffeine = findViewById(R.id.caffeine).isPressed();
        boolean non_caffeine = findViewById(R.id.non_caffeine).isPressed();
        boolean tobacco = findViewById(R.id.tobacco).isPressed();
        boolean exercise = findViewById(R.id.exercise).isPressed();
        boolean daily_activity = findViewById(R.id.light_physical_activity).isPressed();
        boolean wake_up = findViewById(R.id.woke_up).isPressed();
        boolean about_to_sleep = findViewById(R.id.going_to_bed).isPressed();


        final RadioGroup moodGroup = (RadioGroup) findViewById(R.id.moodRadioGroup);
        int radioButtonID = moodGroup.getCheckedRadioButtonId();
        View radioButton = moodGroup.findViewById(radioButtonID);
        int idx = moodGroup.indexOfChild(radioButton);

        String mood = "";
        if (idx == 0) {
            mood = "good";
        } else if (idx == 1) {
            mood = "average";
        } else if (idx == 2) {
            mood = "bad";
        }

        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.DATAEntry.DIASTOLIC_PRESSURE, dia_press);
        values.put(DatabaseContract.DATAEntry.SYSTOLIC_PRESSURE, sys_press);
        values.put(DatabaseContract.DATAEntry.MEAN_ARTERIAL_PRESSURE, mean_art_pres);
        values.put(DatabaseContract.DATAEntry.HEART_RATE, heart_rate);

        values.put(DatabaseContract.DATAEntry.EXERCISE, exercise);
        values.put(DatabaseContract.DATAEntry.TOBACCO, tobacco);
        values.put(DatabaseContract.DATAEntry.WAKE_UP, wake_up);
        values.put(DatabaseContract.DATAEntry.FOOD_INTAKE, food_intake);
        values.put(DatabaseContract.DATAEntry.NON_CAFFEINE_FLUID_INTAKE, non_caffeine);
        values.put(DatabaseContract.DATAEntry.DAILY_ACTIVITY, daily_activity);
        values.put(DatabaseContract.DATAEntry.CAFFEINE, caffeine);
        values.put(DatabaseContract.DATAEntry.ABOUT_TO_SLEEP, about_to_sleep);

        values.put(DatabaseContract.DATAEntry.MOOD, mood);
        values.put(DatabaseContract.DATAEntry.OTHER, other);

        long newRowId;
        newRowId = db.insert(DatabaseContract.DataPoint.TABLE_NAME, null, values);

        finish();
    }
}
