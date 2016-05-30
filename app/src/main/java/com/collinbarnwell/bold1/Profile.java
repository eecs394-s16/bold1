package com.collinbarnwell.bold1;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import java.io.IOException;
import java.io.InputStream;



import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by Jerry on 5/4/2016.
 */
public class Profile extends AppCompatActivity {

    public static final UtilClass utilClass = new UtilClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.profile_toolbar);
        myChildToolbar.setTitle("");
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        // Now automatically get the info from local storage and fill in the text fields.
        try{
            // This is where we stored the user info
            JSONObject saved_user_info = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            for (int i=0; i<utilClass.UserInfoStrings.length;i++) {
                if (saved_user_info.has(utilClass.UserInfoStrings[i])){
                    ((EditText) findViewById(utilClass.UserInfoIds[i])).setText(saved_user_info.getString(utilClass.UserInfoStrings[i]));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
        }
    }

    public void saveUserInfo(View button) {
        JSONObject user_info = new JSONObject();
        try {
            for (int i=0; i<utilClass.UserInfoStrings.length;i++) {
                user_info.put(utilClass.UserInfoStrings[i], ((EditText) findViewById(utilClass.UserInfoIds[i])).getText().toString());
            }
            String user_info_string = user_info.toString();
            try {
                FileOutputStream fos = openFileOutput(utilClass.UserInfoFile, Context.MODE_PRIVATE);
                fos.write(user_info_string.getBytes());
                fos.close();
                Toast.makeText(getBaseContext(),"Successfully saved info.",Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),"Some error occurred when saving user info.",Toast.LENGTH_LONG).show();
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




