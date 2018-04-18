package com.example.babajidemustapha.remedy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Jide Mustapha on 4/9/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM!! ALARM!!", Toast.LENGTH_SHORT).show();
        int id = intent.getExtras().getInt("med_id");
        Log.e("med id", id + "");
        Medication medication = AppDatabase.getInstance(context).medicationDao().getMed(id);
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(medication.endDate);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (Calendar.getInstance().after(date)) {
            stopAlarmManager(context, id, intent);
        } else {
            //Stop sound service to play sound for alarm
            //  context.startService(new Intent(context, AlarmSoundService.class));
            //This will send a notification message and show notification in notification tray
            ComponentName comp = new ComponentName(context.getPackageName(),
                    AlarmNotificationService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
        }


    }

    //Stop/Cancel alarm manager
    public void stopAlarmManager(Context context, int id, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent

    }
}
