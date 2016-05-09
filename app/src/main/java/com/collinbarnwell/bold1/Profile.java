package com.collinbarnwell.bold1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Now automatically get the info from local storage and fill in the text fields.
        try{
            // This is where we stored the user info
            String temp=utilClass.loadJSONStringFromFile(this,utilClass.UserInfoFile);
            Toast.makeText(getBaseContext(),temp,Toast.LENGTH_SHORT).show();
            JSONObject saved_user_info = new JSONObject(utilClass.loadJSONStringFromFile(this,utilClass.UserInfoFile));
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try{
                String temp=utilClass.loadJSONStringFromFile(this,utilClass.UserInfoFile);
                Toast.makeText(getBaseContext(),temp,Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
            }

        } catch (Exception e) {

        }
    }
}
