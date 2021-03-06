package com.collinbarnwell.bold1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.codec.wmf.Point;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.ns.developer.tagview.widget.TagCloudLinkView;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import android.util.Pair;

import static com.collinbarnwell.bold1.R.color.graph_red;
import static com.collinbarnwell.bold1.R.color.yellow;


public class MainActivity extends AppCompatActivity {
    public static final UtilClass utilClass = new UtilClass();
    private static final NotificationHelper notifHelper = new NotificationHelper();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private boolean flag;
    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AddDataPoint.class));
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.graph_table_tabber);
        TabLayout.TabLayoutOnPageChangeListener tabLayoutOnPageChangeListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewFlipper vFlip = (ViewFlipper) findViewById(R.id.tab_flipper);
                vFlip.setDisplayedChild(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Whenever we go back to main activity, the name of the user may have changed (very unlikely)
        // No matter whether that happens, refresh welcome message.

        setupGraph();
        setupPieCharts();
        getDayNightBP();
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
            case R.id.user_profile_menu:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(MainActivity.this, Profile.class));
                return true;

            case R.id.gen_pdf:
                generatePdfReport();
                return true;

            // case R.id.download:
            //    downloadPDF();
            //    return true;

//            case R.id.notification_menu:
//                notifHelper.alarmMethod(this);
//                return true;
//            case R.id.notification_cancel_menu:
//                notifHelper.cancelAlarmMethod(this);
//                return true;
//            case R.id.gen_data:
//                // Generate data for debugging
//                DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
//                SQLiteDatabase db = mDbHelper.getWritableDatabase();
//                String sqlCommands=getString(R.string.sql_query_for_debug);
//                String[] sqlCommandList=sqlCommands.split(";");
//                for (int i=0; i<sqlCommandList.length;i++) {
//                    db.execSQL(sqlCommandList[i] + ";");
//                }
//                Toast.makeText(getBaseContext(),"Database command executed",Toast.LENGTH_LONG).show();
//                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.i("whoop!","Permission is granted");
                return true;
            } else {

                Log.i("fuck","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.i("dope","Permission is granted");
            return true;
        }


    }

    private void generatePdfReport() {

        // Initilizaing stuff
        String filename = "";

        try{
            String firstLastName;
            // This is where we stored the user info
            JSONObject saved_user_info = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            if (saved_user_info.has(utilClass.UserInfoStrings[4]) && saved_user_info.has(utilClass.UserInfoStrings[5])){
                firstLastName=saved_user_info.getString(utilClass.UserInfoStrings[4])+" "+saved_user_info.getString(utilClass.UserInfoStrings[5]);
            }else{
                firstLastName="";
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+10);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
            String timeString=  calendar.get(Calendar.HOUR)+":"
                    + calendar.get(Calendar.MINUTE)+":"+ calendar.get(Calendar.SECOND);
            if (+calendar.get(Calendar.AM_PM)==Calendar.AM){
                timeString=timeString+"AM";
            }
            else{
                timeString=timeString+"PM";
            }
            filename=firstLastName+" "+timeString+" Blood Pressure Report.pdf";

        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
            filename="Blood Pressure Report.pdf";
        }


        // Opening document
        // Starting new Document instance
        Document document = new Document();

        // File shit
        File gpxfile = new File(Environment.getExternalStorageDirectory(), filename);

        // Checking to see if Android Manifest actually gave me permission to save to external storage
        // Right now, it's being a bitch.
        isStoragePermissionGranted();

        // Opening PDF Instance
        try{
            PdfWriter.getInstance(document, new FileOutputStream(gpxfile));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Initializing database helper for this function
        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        // Going to now Populate top of the PDF with Patient info
        Paragraph paragraph = new Paragraph();

        try{
            JSONObject user_profile_info = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            paragraph.add("Patient Last Name: " + user_profile_info.getString(utilClass.UserInfoStrings[5]) + "\n");
            paragraph.add("Patient First Name: " + user_profile_info.getString(utilClass.UserInfoStrings[4]) + "\n");
            paragraph.add("Patient Email: " + user_profile_info.getString(utilClass.UserInfoStrings[1]) + "\n");
            paragraph.add("Patient Age: " + user_profile_info.getString(utilClass.UserInfoStrings[6]) + "\n");
            paragraph.add("ID: " + user_profile_info.getString(utilClass.UserInfoStrings[0]) + "\n");
            paragraph.add("DOB: " + user_profile_info.getString(utilClass.UserInfoStrings[7]) + "\n");
            paragraph.add("Affiliated Medical Institution: " + user_profile_info.getString(utilClass.UserInfoStrings[2]) + "\n");
            paragraph.add("Physician: " + user_profile_info.getString(utilClass.UserInfoStrings[3]) + "\n");
            paragraph.add("\n");

            double avg_day_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",8,20);
            double avg_day_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",8,20);
            double avg_night_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",20,8);
            double avg_night_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",20,8);

            avg_day_diastolic = Math.round(avg_day_diastolic);
            avg_day_systolic = Math.round(avg_day_systolic);
            avg_night_diastolic = Math.round(avg_night_diastolic);
            avg_night_systolic = Math.round(avg_night_systolic);

            paragraph.add("Daily average (last 7 days): " + String.valueOf(avg_day_systolic) + " / " + String.valueOf(avg_day_diastolic));
            paragraph.add("Night time average (last 7 days): " + String.valueOf(avg_night_systolic) + " / " + String.valueOf(avg_night_diastolic));

            paragraph.add("\n");
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        // Getting the BOLD logo -- This will be set to the logo variable below
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.bold_white_transparent_background, null);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();


        // Getting info from the database
        Cursor cursor = db.rawQuery("SELECT SYSTOLIC_PRESSURE, DIASTOLIC_PRESSURE, HEART_RATE, MEAN_ARTERIAL_PRESSURE, TIMESTAMP FROM data_point",null);

        PdfPTable table = new PdfPTable(5);


        table.addCell("Systolic Pressure");
        table.addCell("Diastolic Pressure");
        table.addCell("Heart Rate");
        table.addCell("Mean Arterial Pressure");
        table.addCell("Timestamp");

        cursor.moveToFirst();
        int count = cursor.getCount();

        for (int j = 0; j < count; j++)
        {
            table.addCell(cursor.getString(cursor.getColumnIndex("systolic_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("diastolic_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("heart_rate")));
            table.addCell(cursor.getString(cursor.getColumnIndex("mean_arterial_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("timestamp")));

            cursor.moveToNext();
        }


        document.open();

        try
        {
//            Image logo = Image.getInstance(bitmapData);
//            document.add(logo);

            document.add(paragraph);
            document.add(table);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        document.close();


        // Copying file to downloads folder
        //String download = "/sdcard/Download/";
        //copyFile(Environment.getExternalStorageDirectory().getAbsolutePath() +"/", filename, download);
        //Log.i(download, Environment.getExternalStorageDirectory().getAbsolutePath());


        try {

            // Getting doctor email to put in TO for email generation

            JSONObject saved_user_info1 = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            String[] TO = {saved_user_info1.getString(utilClass.UserInfoStrings[8])};

            //String[] TO = {"nour.alharithi@gmail.com"};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("application/pdf");

            if(TO != null) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            }

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BOLD DIAGNOSTICS: Blood Pressure Summary");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Attached is my blood pressure summary report courtesy of BOLD Diagnostics");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(gpxfile));
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."),  1);
            finish();
        }
        catch(Exception e){
            Log.i("didn't work", "damn");
        }
    }

    private void downloadPDF(){
        // Initilizaing stuff
        String filename = "";

        try{
            String firstLastName;
            // This is where we stored the user info
            JSONObject saved_user_info = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            if (saved_user_info.has(utilClass.UserInfoStrings[4]) && saved_user_info.has(utilClass.UserInfoStrings[5])){
                firstLastName=saved_user_info.getString(utilClass.UserInfoStrings[4])+" "+saved_user_info.getString(utilClass.UserInfoStrings[5]);
            }else{
                firstLastName="";
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+10);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
            String timeString=  calendar.get(Calendar.HOUR)+":"
                    + calendar.get(Calendar.MINUTE)+":"+ calendar.get(Calendar.SECOND);
            if (+calendar.get(Calendar.AM_PM)==Calendar.AM){
                timeString=timeString+"AM";
            }
            else{
                timeString=timeString+"PM";
            }
            filename=firstLastName+" "+timeString+" Blood Pressure Report.pdf";

        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
            filename="Blood Pressure Report.pdf";
        }



        // Opening document
        // Starting new Document instance
        Document document = new Document();

        // File shit
        File gpxfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

        // Checking to see if Android Manifest actually gave me permission to save to external storage
        // Right now, it's being a bitch.
        isStoragePermissionGranted();

        // Opening PDF Instance
        try{
            PdfWriter.getInstance(document, new FileOutputStream(gpxfile));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Initializing database helper for this function
        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        // Going to now Populate top of the PDF with Patient info
        Paragraph paragraph = new Paragraph();

        try{
            JSONObject user_profile_info = utilClass.loadJSONFromFile(this,utilClass.UserInfoFile);
            paragraph.add("Patient Last Name: " + user_profile_info.getString(utilClass.UserInfoStrings[5]) + "\n");
            paragraph.add("Patient First Name: " + user_profile_info.getString(utilClass.UserInfoStrings[4]) + "\n");
            paragraph.add("Patient Email: " + user_profile_info.getString(utilClass.UserInfoStrings[1]) + "\n");
            paragraph.add("Patient Age: " + user_profile_info.getString(utilClass.UserInfoStrings[6]) + "\n");
            paragraph.add("ID: " + user_profile_info.getString(utilClass.UserInfoStrings[0]) + "\n");
            paragraph.add("DOB: " + user_profile_info.getString(utilClass.UserInfoStrings[7]) + "\n");
            paragraph.add("Affiliated Medical Institution: " + user_profile_info.getString(utilClass.UserInfoStrings[2]) + "\n");
            paragraph.add("Physician: " + user_profile_info.getString(utilClass.UserInfoStrings[3]) + "\n");
            paragraph.add("\n");

            double avg_day_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",8,20);
            double avg_day_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",8,20);
            double avg_night_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",20,8);
            double avg_night_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",20,8);

            avg_day_diastolic = Math.round(avg_day_diastolic);
            avg_day_systolic = Math.round(avg_day_systolic);
            avg_night_diastolic = Math.round(avg_night_diastolic);
            avg_night_systolic = Math.round(avg_night_systolic);

            paragraph.add("Daily average (last 7 days): " + String.valueOf(avg_day_systolic) + " / " + String.valueOf(avg_day_diastolic));
            paragraph.add("Night time average (last 7 days): " + String.valueOf(avg_night_systolic) + " / " + String.valueOf(avg_night_diastolic));

            paragraph.add("\n");
        }
        catch(Exception e){
            Toast.makeText(getBaseContext(),"Something went wrong while fetching user info.",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        // Getting the BOLD logo -- This will be set to the logo variable below
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.bold_white_transparent_background, null);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();


        // Getting info from the database
        Cursor cursor = db.rawQuery("SELECT SYSTOLIC_PRESSURE, DIASTOLIC_PRESSURE, HEART_RATE, MEAN_ARTERIAL_PRESSURE, TIMESTAMP FROM data_point",null);

        PdfPTable table = new PdfPTable(5);


        table.addCell("Systolic Pressure");
        table.addCell("Diastolic Pressure");
        table.addCell("Heart Rate");
        table.addCell("Mean Arterial Pressure");
        table.addCell("Timestamp");

        cursor.moveToFirst();
        int count = cursor.getCount();

        for (int j = 0; j < count; j++)
        {
            table.addCell(cursor.getString(cursor.getColumnIndex("systolic_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("diastolic_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("heart_rate")));
            table.addCell(cursor.getString(cursor.getColumnIndex("mean_arterial_pressure")));
            table.addCell(cursor.getString(cursor.getColumnIndex("timestamp")));

            cursor.moveToNext();
        }


        document.open();

        try
        {
//            Image logo = Image.getInstance(bitmapData);
//            document.add(logo);

            document.add(paragraph);
            document.add(table);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        document.close();
        Log.i("yes", "success");
    }

    private void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void setupPieCharts () {
        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int[] counts = mDbHelper.getHypertensionRiskLevelCounts(db);

        PieChart allTimePieChart = (PieChart) findViewById(R.id.all_time_pie_chart);
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        int[] palette = new int[]{R.color.graph_red, R.color.graph_orange, R.color.insights_yellow, R.color.insights_green};
        List<Integer> myColors = new ArrayList<Integer>();

        if (counts[0] > 0) {
            labels.add("Hypertension Stage II");
            entries.add(new Entry(counts[0], 0));
            myColors.add(palette[0]);
        }
        if (counts[1] > 0) {
            entries.add(new Entry(counts[1], 1));
            labels.add("Hypertension Stage I");
            myColors.add(palette[1]);
        }
        if (counts[2] > 0) {
            entries.add(new Entry(counts[2], 2));
            labels.add("Pre-hypertension");
            myColors.add(palette[2]);
        }
        if (counts[3] > 0) {
            entries.add(new Entry(counts[3], 3));
            labels.add("Normal");
            myColors.add(palette[3]);
        }

        if (entries.size() > 0) {
            findViewById(R.id.no_data_pie).setVisibility(View.GONE);
        }

        PieDataSet dataset = new PieDataSet(entries, "");

        int[] myColorsArray = new int[entries.size()];
        for (int i = 0; i < myColors.size(); i++) {
            myColorsArray[i] = myColors.get(i);
        }

        dataset.setColors(myColorsArray, getBaseContext());
        dataset.setValueFormatter(new PercentFormatter());
        dataset.setValueTextSize(15);
        PieData data = new PieData(labels, dataset);
        allTimePieChart.setData(data);
        allTimePieChart.setDrawSliceText(true);
        allTimePieChart.getLegend().setEnabled(false);
        allTimePieChart.setDescription("");
        allTimePieChart.setHoleRadius(0);
        allTimePieChart.setTransparentCircleRadius(0);
        allTimePieChart.setUsePercentValues(true);
    }

    public void setupGraph() {
        mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        flag = true;

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
            cal.add(Calendar.HOUR_OF_DAY, -24);
            Date oneDayAgo = cal.getTime();

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        Date date = new Date((long)value);
                        SimpleDateFormat format = new SimpleDateFormat("h:mm a\nM/d");
                        // SimpleDateFormat format = new SimpleDateFormat("M/d");
                        return format.format(date);
                    } else {
                        // show currency for y values
                        return super.formatLabel(value, isValueX);
                    }
                }
            });

            graph.getViewport().setMinX(oneDayAgo.getTime());
            graph.getViewport().setMaxX(now.getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getGridLabelRenderer().setNumVerticalLabels(9);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(200);
            graph.getViewport().setYAxisBoundsManual(true);

            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);

            // legend
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph.getLegendRenderer().setTextSize(50);
            graph.getLegendRenderer().setSpacing(30);
            graph.getLegendRenderer().setMargin(20);
            graph.getLegendRenderer().setPadding(20);

            graph.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    flag = true;
                    return false;
                }
            });

            LineGraphSeries<DataPoint> systolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "systolic_pressure"));
            graph.addSeries(systolic_series);
            systolic_series.setTitle("Systolic (mmHg)");
            systolic_series.setColor(getResources().getColor(R.color.graph_blue));
            systolic_series.setDrawDataPoints(true);
            systolic_series.setDataPointsRadius(30);
            systolic_series.setThickness(20);
            relativeLayout = (RelativeLayout) findViewById(R.id.relative);
            systolic_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    if (!flag) {
                        return;
                    }
                    flag = false;
                    showWindow(dataPoint);
                }
            });

            LineGraphSeries<DataPoint> diastolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "diastolic_pressure"));
            graph.addSeries(diastolic_series);
            diastolic_series.setColor(Color.GREEN);
            diastolic_series.setTitle("Diastolic (mmHg)");
            diastolic_series.setColor(getResources().getColor(R.color.graph_orange));
            diastolic_series.setDrawDataPoints(true);
            diastolic_series.setDataPointsRadius(30);
            diastolic_series.setThickness(20);
            diastolic_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    if (!flag) {
                        return;
                    }
                    flag = false;
                    showWindow(dataPoint);
//                    Toast.makeText(MainActivity.this, "Series: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
                }
            });

            LineGraphSeries<DataPoint> heart_rate_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getColumnDataPoints(db, "heart_rate"));
            graph.addSeries(heart_rate_series);
            heart_rate_series.setColor(Color.RED);
            heart_rate_series.setTitle("Pulse (bpm)");
            heart_rate_series.setColor(getResources().getColor(R.color.graph_red));
            heart_rate_series.setDrawDataPoints(true);
            heart_rate_series.setDataPointsRadius(30);
            heart_rate_series.setThickness(20);
            heart_rate_series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    if (!flag) {
                        return;
                    }
                    flag = false;
                    showWindow(dataPoint);
//                    Toast.makeText(MainActivity.this, "Series: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void showWindow(DataPointInterface dataPoint) {
        final SQLiteDatabase dbr = mDbHelper.getReadableDatabase();
        Date date = new Date((long)dataPoint.getX());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat newformat = new SimpleDateFormat("EEE M/d 'at' h:mm a");
        Object[] data = mDbHelper.getDataForPopup(dbr, format.format(date));
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.data, null);
        TextView x = (TextView) container.findViewById(R.id.Timetext);
        x.setText(newformat.format(date));
        x = (TextView) container.findViewById(R.id.bp);
        double systolic = (double)((Pair)data[0]).second;
        double diastolic = (double)((Pair)data[1]).second;
        x.setText(systolic + "\n" + diastolic);

        ImageView circle = (ImageView) container.findViewById(R.id.circle_X);
        if(systolic <= 120 && diastolic <= 80){
            circle.setImageResource(R.drawable.green_circle);
            x.setTextColor(Color.parseColor("#33ff33"));
        }
        else if((systolic > 120 && systolic <= 139) || (diastolic <= 89 && diastolic > 80)){
            circle.setImageResource(R.drawable.yellow_circle);
//            Resources resources = getResources();
//            circle.setImageDrawable(resources.getDrawable(R.drawable.yellow_circle));
            x.setTextColor(Color.parseColor("#FF9A00"));
        }
        else{
            circle.setImageResource(R.drawable.red_circle);
            x.setTextColor(Color.parseColor("#ff0000"));
        }
        x = (TextView) container.findViewById(R.id.Pulsetext);


        double pulse = (double)((Pair)data[2]).second;
        x.setText(pulse + "");
        x.setTextColor(Color.parseColor("#f01515"));
        TagCloudLinkView view = (TagCloudLinkView) container.findViewById(R.id.Tags);
        for (int i = 5; i <= 12; i++) {
            String tag = (String)((Pair)data[i]).second;
            if (tag.equals("1"))
                view.add(new com.ns.developer.tagview.entity.Tag(1, (String)((Pair)data[i]).first));
        }
        view.drawTags();
        String mood = (String)((Pair)data[4]).second;
        ImageView moodcon = (ImageView) container.findViewById(R.id.moodIamge);
        if (mood.equals("good")) {
            moodcon.setImageResource(R.drawable.happy);
        } else if (mood.equals("average")) {
            moodcon.setImageResource(R.drawable.normal);
        } else {
            moodcon.setImageResource(R.drawable.sad);
        }

        x = (TextView) container.findViewById(R.id.Msg);
        x.setText((String)((Pair)data[13]).second);

        Display display = getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);

        int width = (int)(size.x * 0.8);
        int height = (int)(size.y * 0.8);
        int px = (int)(size.x * 0.1);
        int py = (int)(size.y * 0.1);
        popupWindow = new PopupWindow(container, width, height, true);
        popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, px, py);
        ImageView close = (ImageView) container.findViewById(R.id.close);
        close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return false;
            }
        });
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void getDayNightBP(){


        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        double avg_day_systolic;
        avg_day_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",8,20);
        double avg_day_diastolic;
        avg_day_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",8,20);

        double avg_night_systolic;
        avg_night_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure",20,8);
        double avg_night_diastolic;
        avg_night_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure",20,8);

        avg_day_diastolic = Math.round(avg_day_diastolic);
        avg_day_systolic = Math.round(avg_day_systolic);
        avg_night_diastolic = Math.round(avg_night_diastolic);
        avg_night_systolic = Math.round(avg_night_systolic);


        TextView bp_textview = (TextView) findViewById(R.id.dayNightAvgBP);
        ImageView circle = (ImageView) findViewById(R.id.dayNightCircleIcon);
        String formattedText= "<font color=#000000>"+avg_day_systolic+ "/" + avg_day_diastolic +
                "</font><br/><font color=#ffffff>" + avg_night_systolic+ "/" + avg_night_diastolic+"</font>";
        bp_textview.setText(Html.fromHtml(formattedText));

        if(avg_day_systolic <= 120 && avg_day_diastolic <= 80){
            if(avg_night_systolic <= 120 && avg_night_diastolic <= 80){
                circle.setImageResource(R.drawable.day_night_circle_green_top_green_bottom);
            }
            else if((avg_night_systolic > 120 && avg_night_systolic <= 139) || (avg_night_diastolic <= 89 && avg_night_diastolic > 80)){
                circle.setImageResource(R.drawable.day_night_circle_green_top_yellow_bottom);
            }
            else{
                circle.setImageResource(R.drawable.day_night_circle_green_top_red_bottom);
            }
        }

        else if((avg_day_systolic > 120 && avg_day_systolic <= 139) || (avg_day_diastolic <= 89 && avg_day_diastolic > 80)){
            if(avg_night_systolic <= 120 && avg_night_diastolic <= 80){
                circle.setImageResource(R.drawable.day_night_circle_yellow_top_green_bottom);
            }
            else if((avg_night_systolic > 120 && avg_night_systolic <= 139) || (avg_night_diastolic <= 89 && avg_night_diastolic > 80)){
                circle.setImageResource(R.drawable.day_night_circle_yellow_top_yellow_bottom);
            }
            else{
                circle.setImageResource(R.drawable.day_night_circle_yellow_top_red_bottom);
            }
        }
        else{
            if(avg_night_systolic <= 120 && avg_night_diastolic <= 80){
                circle.setImageResource(R.drawable.day_night_circle_red_top_green_bottom);
            }
            else if((avg_night_systolic > 120 && avg_night_systolic <= 139) || (avg_night_diastolic <= 89 && avg_night_diastolic > 80)){
                circle.setImageResource(R.drawable.day_night_circle_red_top_yellow_bottom);
            }
            else{
                circle.setImageResource(R.drawable.day_night_circle_red_top_red_bottom);
            }
            // bp_textview.setTextColor(Color.parseColor("#ff0000"));
        }
    }

}


