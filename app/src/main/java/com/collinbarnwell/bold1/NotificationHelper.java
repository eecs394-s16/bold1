package com.collinbarnwell.bold1;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Jerry on 5/18/2016.
 */
public class NotificationHelper{
    private PendingIntent pendingIntent;
    public void alarmMethod(AppCompatActivity activity){
        Intent myIntent = new Intent(activity , NotificationService.class);
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(activity, 0, myIntent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+10);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        // Set daily notif
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),1000 * 60 * 60 * 24, pendingIntent);
        String notifString=  "Daily notification enabled at "+ calendar.get(Calendar.HOUR)+":"
                + calendar.get(Calendar.MINUTE)+":"+ calendar.get(Calendar.SECOND);
        if (+calendar.get(Calendar.AM_PM)==Calendar.AM){
            notifString=notifString+"AM";
        }
        else{
            notifString=notifString+"PM";
        }
        Toast.makeText(activity, notifString, Toast.LENGTH_LONG).show();
    }

    public void cancelAlarmMethod(AppCompatActivity activity){
        Intent myIntent = new Intent(activity , NotificationService.class);
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(activity, 0, myIntent,0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(activity, "All daily notifications have been removed.", Toast.LENGTH_LONG).show();
    }
}
