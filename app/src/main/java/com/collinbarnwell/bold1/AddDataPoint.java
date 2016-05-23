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
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.HashMap;


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
        myChildToolbar.setTitle("");
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
        boolean validData=true;
        switch (button.getId()){
            case R.id.datapoint1_next_button:
                HashMap<String,Integer> bloodPressureData=  getBloodPressureData();
                if ((bloodPressureData.get("dia_press") <40) ||
                        (bloodPressureData.get("dia_press") >150)){
                    Toast.makeText(getBaseContext(),"Please enter reasonable diastolic pressure in mm Hg",Toast.LENGTH_LONG).show();
                    validData=false;
                }else if ((bloodPressureData.get("sys_press") <80) ||
                        (bloodPressureData.get("sys_press")>220)){
                    Toast.makeText(getBaseContext(),"Please enter reasonable systolic pressure in mm Hg",Toast.LENGTH_LONG).show();
                    validData=false;
                }else if ((bloodPressureData.get("heart_rate")<20) ||
                        (bloodPressureData.get("heart_rate")>300)){
                    Toast.makeText(getBaseContext(),"Please enter reasonable heart rate in beats per minute",Toast.LENGTH_LONG).show();
                    validData=false;
                }
                break;
            case R.id.datapoint2_next_button:
                // HashMap<String,Boolean> relatedData = getRelatedData();
                // For now Don't do anything. They don't have to answer those.
                break;
            default:
                break;
        }
        if (validData){
            currentViewflipperChild += 1;
            VF.setDisplayedChild(currentViewflipperChild);
        }
    }

    public void saveDataPoint(View button) {
        // First check if the mood is filled out
        HashMap<String,String> moodData=getMoodData();
        String mood = moodData.get("mood");
        if (mood.isEmpty()){
            Toast.makeText(getBaseContext(),"Please choose a mood.",Toast.LENGTH_LONG).show();
            return;
        }


        HashMap<String,Integer> bloodPressureData=  getBloodPressureData();
        Integer dia_press = bloodPressureData.get("dia_press");
        Integer sys_press = bloodPressureData.get("sys_press");
        Integer heart_rate = bloodPressureData.get("heart_rate");


        String other = ((EditText) findViewById(R.id.otherThings)).getText().toString();

        HashMap<String,Boolean> relatedData = getRelatedData();
        boolean food_intake = relatedData.get("food_intake");
        boolean caffeine = relatedData.get("caffeine");
        boolean non_caffeine = relatedData.get("non_caffeine");
        boolean tobacco = relatedData.get("tobacco");
        boolean exercise = relatedData.get("exercise");
        boolean daily_activity = relatedData.get("daily_activity");
        boolean wake_up = relatedData.get("wake_up");
        boolean about_to_sleep = relatedData.get("about_to_sleep");


        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseContract.DATAEntry.DIASTOLIC_PRESSURE, dia_press);
        values.put(DatabaseContract.DATAEntry.SYSTOLIC_PRESSURE, sys_press);
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


    public HashMap<String,Integer> getBloodPressureData(){
        HashMap<String,Integer> data = new HashMap<String,Integer>();

        final EditText dpress_field = (EditText) findViewById(R.id.dia_press);
        Integer dia_press;
        try {
            dia_press = Integer.parseInt(dpress_field.getText().toString());
        } catch(NumberFormatException nfe) {
            dia_press = -1;
        }
        data.put("dia_press",dia_press);

        final EditText spress_field = (EditText) findViewById(R.id.sys_press);
        Integer sys_press;
        try {
            sys_press = Integer.parseInt(spress_field.getText().toString());
        } catch(NumberFormatException nfe) {
            sys_press = -1;
        }
        data.put("sys_press",sys_press);

        final EditText heart_rate_field = (EditText) findViewById(R.id.heart_rate);
        Integer heart_rate;
        try {
            heart_rate = Integer.parseInt(heart_rate_field.getText().toString());
        } catch(NumberFormatException nfe) {
            heart_rate = -1;
        }
        data.put("heart_rate",heart_rate);

        return data;
    }


    public HashMap<String,Boolean> getRelatedData(){
        HashMap<String,Boolean> data = new HashMap<String,Boolean>();
        boolean food_intake = findViewById(R.id.food_intake).isPressed();
        boolean caffeine = findViewById(R.id.caffeine).isPressed();
        boolean non_caffeine = findViewById(R.id.non_caffeine).isPressed();
        boolean tobacco = findViewById(R.id.tobacco).isPressed();
        boolean exercise = findViewById(R.id.exercise).isPressed();
        boolean daily_activity = findViewById(R.id.light_physical_activity).isPressed();
        boolean wake_up = findViewById(R.id.woke_up).isPressed();
        boolean about_to_sleep = findViewById(R.id.going_to_bed).isPressed();

        data.put("food_intake",food_intake);
        data.put("caffeine",caffeine);
        data.put("non_caffeine",non_caffeine);
        data.put("tobacco",tobacco);
        data.put("exercise",exercise);
        data.put("daily_activity",daily_activity);
        data.put("wake_up",wake_up);
        data.put("about_to_sleep",about_to_sleep);

        return data;
    }


    public HashMap<String,String> getMoodData(){
        HashMap<String,String> data = new HashMap<String,String>();

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
        data.put("mood",mood);
        return data;
    }
}
