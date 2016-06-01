package com.collinbarnwell.bold1;

import com.collinbarnwell.bold1.DatabaseHelper;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
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
import java.util.Vector;


/**
 * Created by Z-Henry on 5/23/2016.
 */
public class Notifications extends AppCompatActivity {
    Vector<String> alarms = new Vector<String>();


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


        restartAlarms();
        displayAlarmsOnPage();

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

            return new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar, this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String am_or_pm;
            Notifications notifications = (Notifications)getActivity();

            if (hourOfDay > 12){
                hourOfDay -= 12;
                am_or_pm = " PM";
            } else {
                am_or_pm = " AM";
            }


            notifications.saveAlarm(hourOfDay, minute);
            ListView listView = (ListView) notifications.findViewById(R.id.list_notifs);
            ((ArrayAdapter<String>)listView.getAdapter()).add("Alarm set at " + hourOfDay + ":" + minute + am_or_pm);


            //Sets notification on the phone
            //Creates fragment for the listview in notifications interface
            //Saves information to databases


        }

    }

    public void showTimePickerDialog(View v){
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getFragmentManager(), "timePicker");
    }

    public void saveAlarm(int hourOfDay, int minute){
        int id = writeAlarmtoDatabase(hourOfDay, minute);
        setAlarm(id, hourOfDay, minute);
    }




    public void setAlarm(int id, int hourOfDay, int minute){
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        Intent myIntent = new Intent(this, Notifications.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, id, myIntent, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        String notifString=  "Daily notification enabled at "+ cal.get(Calendar.HOUR)+":"
                + cal.get(Calendar.MINUTE)+":"+ cal.get(Calendar.SECOND);
        if (+cal.get(Calendar.AM_PM)==Calendar.AM){
            notifString=notifString+"AM";
        }
        else{
            notifString=notifString+"PM";
        }
        Toast.makeText(this, notifString, Toast.LENGTH_LONG).show();



    }

    public int writeAlarmtoDatabase(int hourOfDay, int minute){
        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseContract.AlarmEntries.HOUR, hourOfDay);
        contentValues.put(DatabaseContract.AlarmEntries.MINUTE, minute);

        long id = db.insert(DatabaseContract.Alarms.TABLE_NAME, null, contentValues);
        return (int)id;

    }

    public void removeAlarmfromDatabase(int id){

        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseContract.Alarms.TABLE_NAME, DatabaseContract.AlarmEntries.ID + " = " + id, null);

    }

    public void unsetAlarm(int id){

        Intent myIntent = new Intent(this, Notifications.class);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, id, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Daily notification disabled.", Toast.LENGTH_LONG).show();

    }

    public void restartAlarms(){
        int id, hourofDay, minute;

        DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Alarms.TABLE_NAME + ";", null);

        boolean keep_going = cursor.moveToFirst();

        while (keep_going){

            id = (int)cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntries.ID));
            hourofDay = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntries.HOUR));
            minute = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntries.MINUTE));
            setAlarm(id, hourofDay, minute);
            keep_going = cursor.moveToNext();


        }

    }

    public void displayAlarmsOnPage(){
        int id, hourofDay, minute;
        Vector<String> alarms = new Vector<String>();
        String build_title, am_or_pm;


        SQLiteDatabase db = new DatabaseHelper(getBaseContext()).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.Alarms.TABLE_NAME + ";", null);

        boolean keep_going = cursor.moveToFirst();

        while (keep_going){

            id = (int)cursor.getLong(cursor.getColumnIndex(DatabaseContract.AlarmEntries.ID));
            hourofDay = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntries.HOUR));

            if (hourofDay > 12){
                hourofDay -= 12;
                am_or_pm = " PM";
            } else {
                am_or_pm = " AM";
            }

            minute = cursor.getInt(cursor.getColumnIndex(DatabaseContract.AlarmEntries.MINUTE));
            build_title = "Alarm set at " + hourofDay + ":" + minute + am_or_pm;
            alarms.add(build_title);
            keep_going = cursor.moveToNext();

        }

        ListView listView = (ListView) findViewById(R.id.list_notifs);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alarms));

    }










}
