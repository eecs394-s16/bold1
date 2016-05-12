package com.collinbarnwell.bold1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;




public class MainActivity extends AppCompatActivity {
    public static final UtilClass utilClass = new UtilClass();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Button prof_button = (Button) findViewById(R.id.profile_button);
        prof_button.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddDataPoint.class));
            }
        });

        Button pdf_button = (Button) findViewById(R.id.pdf_create);
        pdf_button.setOnClickListener(new OnClickListener(){
            public void onClick(View v){

                // Opening document
                Document document = new Document();

                // File shit
                String filename = "doctor.pdf";
                File gpxfile = new File("sdcard/", filename); // Where to save. Currently trying external storage. Save in
                                                                // "/data/data/com.collinbarnwell.bold1" to get it to save in internal storage

                // Checking to see if Android Manifest actually gave me permission to save to external storage
                // Right now, it's being a bitch.
                if(Environment.getExternalStorageDirectory().canWrite()){
                    Log.i("hip hi", "hooray!");
                }
                else{
                    Log.i("damn:", "you suck");
                }



                // Writing to PDF
                try{
                    PdfWriter.getInstance(document, new FileOutputStream(gpxfile));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }


                Paragraph p3 = new Paragraph();
                p3.add("Yay");

                document.open();

                try
                {
                    document.add(p3);
                }
                catch (DocumentException e)
                {
                    e.printStackTrace();
                }

                document.close();

                // Done writing stuff




                try {

                    // File mailed_file = copyFile(gpxfile, dst);
                    // Opening up mail app to send with...

                    String[] TO = {"nour.alharithi@gmail.com"};
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.setType("application/pdf");


                    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(gpxfile));
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."),  1);
                    finish();
                }
                catch(Exception e){
                    Log.i("didn't work", "damn");
                }


                // Don't worry about the following until later.

//                try {
//
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(MainActivity.this,
//                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
//                }


            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();
        // Whenever we go back to main activity, the name of the user may have changed (very unlikely)
        // No matter whether that happens, refresh welcome message.

        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String count = "SELECT count(*) FROM data_point";
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.removeAllSeries();

            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(4);

            // Get one day ago
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date oneDayAgo = cal.getTime();

            graph.getViewport().setMinX(oneDayAgo.getTime());
            graph.getViewport().setMaxX(now.getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getGridLabelRenderer().setNumVerticalLabels(9);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(200);
            graph.getViewport().setYAxisBoundsManual(true);

            graph.getViewport().setScrollable(true);
            // graph.getViewport().setScalable(true);

            // legend
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

            LineGraphSeries<DataPoint> systolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "systolic_pressure"));
            graph.addSeries(systolic_series);
            systolic_series.setTitle("Systolic Pressure (mmHg)");

            LineGraphSeries<DataPoint> diastolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "diastolic_pressure"));
            graph.addSeries(diastolic_series);
            diastolic_series.setColor(Color.GREEN);
            diastolic_series.setTitle("Diastolic Pressure (mmHg)");

            LineGraphSeries<DataPoint> heart_rate_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "heart_rate"));
            graph.addSeries(heart_rate_series);
            heart_rate_series.setColor(Color.RED);
            heart_rate_series.setTitle("Pulse Rate (/min)");
        }
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

    public static File copyFile(File src, File dst) throws IOException
    {
        //if folder does not exist
        if (!dst.exists()) {
            dst.mkdir();
        }

        File expFile = new File(dst.getPath() + File.separator + "doctor.pdf");
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }
}
