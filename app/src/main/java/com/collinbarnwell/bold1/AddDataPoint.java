package com.collinbarnwell.bold1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddDataPoint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data_point);
    }

    public void savePressureNumbers(View button) {
        final EditText dpress_field = (EditText) findViewById(R.id.dia_press);
        int dia_press = Integer.parseInt(dpress_field.getText().toString());

        final EditText spress_field = (EditText) findViewById(R.id.sys_press);
        int sys_press = Integer.parseInt(spress_field.getText().toString());

        final EditText mean_art_press_field = (EditText) findViewById(R.id.art_press);
        int mean_art_pres = Integer.parseInt(mean_art_press_field.getText().toString());

        final EditText heart_rate_field = (EditText) findViewById(R.id.heart_rate);
        int heart_rate = Integer.parseInt(heart_rate_field.getText().toString());

    }


}
