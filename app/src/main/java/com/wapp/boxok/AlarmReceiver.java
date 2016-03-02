package com.wapp.boxok;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wapp.boxok.model.Advertise;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class AlarmReceiver extends BroadcastReceiver {
    static int position = -1;

    public AlarmReceiver() {
    }

    final public static String ONE_TIME = "onetime";
    int duration = 10000;
    Context context;
    AlarmManager am;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();
        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }


        SharedPreferences sharedPref = context.getSharedPreferences("BOX", Context.MODE_PRIVATE);
        duration = sharedPref.getInt(context.getResources().getString(R.string.duration), 1);
        Log.d("Duration", duration + "");
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));


        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("Reboot", "first reboot" + intent.getAction());

            if (sharedPref.contains(context.getResources().getString(R.string.time_repeating))) {
                int time = sharedPref.getInt(context.getResources().getString(R.string.time_repeating), 0);
                Log.d("time_prefs", "time ===>" + time);

                SetAlarm(context, time);

            }
        } else {
            Log.d("launch_video", "first reboot" + intent.getAction());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchActivity();
                    Log.d("start", "post end----");
                }
            }, duration);

        }
        Log.d("postion", position + "");
        wl.release();
    }

    void launchActivity() {
        Realm realm = Realm.getInstance(context);
        RealmResults<Advertise> result = realm.where(Advertise.class)
                .findAll();
        if (result.isEmpty()) {

            Toast.makeText(context, "No ADs availble", Toast.LENGTH_SHORT).show();
        } else {
            if (position != -1) {
                Intent i = new Intent();
                i.setAction("com.wapp.boxok.LAUNCH_IT");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("path", result.get(position).getPath());
                context.startActivity(i);
            }
            if (position < result.size() - 1)
                position++;
            else position = 0;


        }
    }

    public void SetAlarm(Context context, int time) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * time, pi);


    }


    public void CancelAlarm(final Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Toast.makeText(context, "Alarm Cancled", Toast.LENGTH_SHORT).show();
    }

    public void setOnetimeTimer(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
