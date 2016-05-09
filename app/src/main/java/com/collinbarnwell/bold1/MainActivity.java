package com.collinbarnwell.bold1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final UtilClass utilClass = new UtilClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddDataPoint.class));
            }
        });

        refreshWelcomeMessage();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Whenever we go back to main activity, the name of the user may have changed (very unlikely)
        // No matter whether that happens, refresh welcome message.
        refreshWelcomeMessage();

        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> systolic_series =
                new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "systolic_pressure"));

        graph.addSeries(systolic_series);
        graph.getViewport().setScrollable(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(MainActivity.this, Profile.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void refreshWelcomeMessage(){
        // Refresh welcome message
        try{
            // This is where we stored the user info
            String temp=utilClass.loadJSONStringFromFile(this,utilClass.UserInfoFile);
            JSONObject saved_user_info = new JSONObject(utilClass.loadJSONStringFromFile(this,utilClass.UserInfoFile));
            if (saved_user_info.has(utilClass.UserInfoStrings[4]) && !(saved_user_info.getString(utilClass.UserInfoStrings[4]).isEmpty())){
                // If there is a user first name field in local storage and it's not empty
                ((TextView)findViewById(R.id.welcomeMessage)).setText("Welcome, "+saved_user_info.getString(utilClass.UserInfoStrings[4]));
            }else{
                ((TextView)findViewById(R.id.welcomeMessage)).setText(R.string.welcome_message_default);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
        }
    }
}
