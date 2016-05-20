package com.collinbarnwell.bold1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

import static com.collinbarnwell.bold1.R.color.graph_red;


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

        setupPieCharts();
        getAverageBP();
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
            cal.add(Calendar.HOUR_OF_DAY, -72);
            Date threeDaysAgo = cal.getTime();

            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        // show normal x values
                        Date date = new Date((long)value);
                        // SimpleDateFormat format = new SimpleDateFormat("h:mm a\nM/dd");
                        SimpleDateFormat format = new SimpleDateFormat("M/d");
                        return format.format(date);
                    } else {
                        // show currency for y values
                        return super.formatLabel(value, isValueX);
                    }
                }
            });

            graph.getViewport().setMinX(threeDaysAgo.getTime());
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

            LineGraphSeries<DataPoint> systolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getDailyAverageDataPoints(db, "systolic_pressure"));
            graph.addSeries(systolic_series);
            systolic_series.setTitle("Systolic (mmHg)");
            systolic_series.setColor(getResources().getColor(R.color.graph_blue));
            systolic_series.setDrawDataPoints(true);
            systolic_series.setDataPointsRadius(30);
            systolic_series.setThickness(20);

            LineGraphSeries<DataPoint> diastolic_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getDailyAverageDataPoints(db, "diastolic_pressure"));
            graph.addSeries(diastolic_series);
            diastolic_series.setColor(Color.GREEN);
            diastolic_series.setTitle("Diastolic (mmHg)");
            diastolic_series.setColor(getResources().getColor(R.color.graph_orange));
            diastolic_series.setDrawDataPoints(true);
            diastolic_series.setDataPointsRadius(30);
            diastolic_series.setThickness(20);

            LineGraphSeries<DataPoint> heart_rate_series =
                    new LineGraphSeries<DataPoint>(mDbHelper.getDailyAverageDataPoints(db, "heart_rate"));
            graph.addSeries(heart_rate_series);
            heart_rate_series.setColor(Color.RED);
            heart_rate_series.setTitle("Pulse (bpm)");
            heart_rate_series.setColor(getResources().getColor(R.color.graph_red));
            heart_rate_series.setDrawDataPoints(true);
            heart_rate_series.setDataPointsRadius(30);
            heart_rate_series.setThickness(20);

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
            case R.id.user_profile_menu:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(MainActivity.this, Profile.class));
                return true;

            case R.id.gen_pdf:
                generatePdfReport();
                return true;

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

        // Opening document
        Document document = new Document();

        // File shit
        String filename = "doctor.pdf";
        File gpxfile = new File("sdcard/", filename); // Where to save. Currently trying external storage. Save in
        // "/data/data/com.collinbarnwell.bold1" to get it to save in internal storage

        // Checking to see if Android Manifest actually gave me permission to save to external storage
        // Right now, it's being a bitch.
        isStoragePermissionGranted();



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


        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


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
            document.add(table);
        }
        catch (DocumentException e)
        {
            e.printStackTrace();
        }

        document.close();

        try {

            String[] TO = {"nour.alharithi@gmail.com"};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("application/pdf");

            //emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
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

    private void setupPieCharts () {

        class MyValueFormatter implements ValueFormatter {
            private DecimalFormat mFormat;
            public MyValueFormatter() {
                mFormat = new DecimalFormat("###,###");
            }
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return mFormat.format(value);
            }
        }

        PieChart allTimePieChart = (PieChart) findViewById(R.id.all_time_pie_chart);
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(4, 0));
        entries.add(new Entry(8, 1));
        entries.add(new Entry(6, 2));
        entries.add(new Entry(12, 3));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Hypertensive");
        labels.add("At Risk");
        labels.add("Normal");
        labels.add("Low");

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setValueFormatter(new MyValueFormatter());
        dataset.setValueTextSize(15);
        PieData data = new PieData(labels, dataset);
        allTimePieChart.setData(data);
        allTimePieChart.getLegend().setEnabled(false);
        allTimePieChart.setDescription("");

    }

    public void getAverageBP(){


        DatabaseHelper mDbHelper = new DatabaseHelper(getBaseContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        double avg_systolic = mDbHelper.getAverageOverPastWeek(db, "systolic_pressure");
        double avg_diastolic = mDbHelper.getAverageOverPastWeek(db, "diastolic_pressure");

        TextView bp_textview = (TextView) findViewById(R.id.avg_bp);
        ImageView circle = (ImageView) findViewById(R.id.circle);
        bp_textview.setText(avg_systolic + "\n" + avg_diastolic);

        if(avg_systolic < 120 && avg_diastolic < 80){
            circle.setImageResource(R.drawable.green_circle);
            bp_textview.setTextColor(Color.parseColor("#33ff33"));
        }
        else if((avg_systolic > 120 && avg_systolic < 139) || (avg_diastolic < 89 && avg_diastolic > 80)){
            circle.setImageResource(R.drawable.yellow_circle);
            bp_textview.setTextColor(Color.parseColor("#ffff00"));
        }
        else{
            circle.setImageResource(R.drawable.red_circle);
            bp_textview.setTextColor(Color.parseColor("#ff0000"));
        }




    }





}
