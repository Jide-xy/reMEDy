package com.example.babajidemustapha.remedy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jide Mustapha on 4/18/2018.
 */

public class RebootAlarmReceiver extends BroadcastReceiver {
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            this.context = context;
            AppDatabase appDatabase = AppDatabase.getInstance(context);
            for (Medication medication : appDatabase.medicationDao().getAllMeds()) {
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(medication.endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!Calendar.getInstance().after(date)) {
                    Intent intent2 = new Intent(context, AlarmReceiver.class);
                    intent2.putExtra("med_id", medication.id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, medication.id, intent2, 0);
                    triggerAlarmManager(medication.startDate, medication.interval, pendingIntent);
                }
            }
        }

    }

    public void triggerAlarmManager(String startDate, int interval, PendingIntent pendingIntent) {
        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-HH-dd HH:ss").parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // add interval seconds to the calendar object
        //    cal.add(Calendar.SECOND, interval);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);//get instance of alarm manager
        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval * 60 * 60 * 1000, pendingIntent);//set alarm manager with entered timer by converting into milliseconds
        // Toast.makeText(this, "Alarm Set for " + interval + " seconds.", Toast.LENGTH_SHORT).show();
    }
}
