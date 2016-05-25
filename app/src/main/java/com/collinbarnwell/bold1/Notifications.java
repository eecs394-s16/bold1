package com.collinbarnwell.bold1;

import com.collinbarnwell.bold1.DatabaseHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ViewFlipper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.TimePicker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Z-Henry on 5/23/2016.
 */
public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_notifications);

        Toolbar childToolbar = (Toolbar) findViewById(R.id.notifications_toolbar);
        childToolbar.setTitle("");
        setSupportActionBar(childToolbar);

        // Get a support ActionBar corresponding to this toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_notif);


        fab.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                showTimePickerDialog(v);
            }
        });


        /*
        String[] test_items = {"red", "blue", "green"};
        ListView listView = (ListView) findViewById(R.id.list_notifs);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, test_items));
        */


    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //Sets notification on the phone
            //Creates fragment for the listview in notifications inte

        }

    }

    public void showTimePickerDialog(View v){
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getFragmentManager(), "timePicker");
    }








}
