package com.collinbarnwell.bold1;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;

import java.io.FileInputStream;
import java.io.FileOutputStream;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Jerry on 5/4/2016.
 */
public class Profile extends AppCompatActivity {

    public static final String UserInfoFile = "User_Info_File";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();


        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


    }



    public void saveUserInfo(View button) {
        final String first_name = ((EditText) findViewById(R.id.user_first_name)).getText().toString();
        final String last_name = ((EditText) findViewById(R.id.user_last_name)).getText().toString();
        final String age = ((EditText) findViewById(R.id.user_age)).getText().toString();
        final String date_of_birth = ((EditText) findViewById(R.id.user_dob)).getText().toString();



//        final String user_id = ((EditText) findViewById(R.id.user_id)).getText().toString();
//        final String email = ((EditText) findViewById(R.id.dia_press)).getText().toString();
//        final String institution_affiliation = ((EditText) findViewById(R.id.institution_affiliation)).getText().toString();
//        final String respective_physician = ((EditText) findViewById(R.id.user_respective_physician)).getText().toString();

        try {
            JSONObject user_info = new JSONObject();
            user_info.put("first_name", first_name);
            user_info.put("last_name", last_name);
            user_info.put("age", age);
            user_info.put("date_of_birth", date_of_birth);
            String user_info_string = user_info.toString();

            // String string = "asdf";
            try {
                FileOutputStream fos = openFileOutput(UserInfoFile, Context.MODE_PRIVATE);
                fos.write(user_info_string.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileInputStream fin = openFileInput(UserInfoFile);
                int c;
                String temp = "";

                while ((c = fin.read()) != -1) {
                    temp = temp + Character.toString((char) c);
                }
                Toast.makeText(getBaseContext(), temp, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }

        } catch (Exception e) {

        }

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar, this, year, month, day);


        }


        public void onDateSet(DatePicker view, int year, int month, int day) {

            EditText editText = (EditText) this.getActivity().findViewById(R.id.user_dob);
            final String date = (month+1) + "/" + day + "/" + year;
            editText.setText(date, TextView.BufferType.EDITABLE);

        }

    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");

    }

}




