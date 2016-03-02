package com.wapp.boxok;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends Activity {
    EditText login,password,suspend;
    private AlarmReceiver alarm;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context= this.getApplicationContext();
        login=(EditText)findViewById(R.id.editText_login);
        password=(EditText)findViewById(R.id.editText_pass);
        suspend=(EditText)findViewById(R.id.editText_suspend);
        Button bStop = (Button)findViewById(R.id.button_stop);
        Button bLogin = (Button)findViewById(R.id.button_login);

        SharedPreferences sharedPref = context.getSharedPreferences("BOX", Context.MODE_PRIVATE);
        if(sharedPref.contains(context.getResources().getString(R.string.time_repeating))) {
            int time = sharedPref.getInt(context.getResources().getString(R.string.time_repeating), 1);
            Log.d("time_prefs","time ===>"+time);
            try {
                boolean alarmUp = (
                        PendingIntent.getBroadcast(context, 0,  new Intent(context, AlarmReceiver.class),PendingIntent.FLAG_NO_CREATE) != null);
                alarm.SetAlarm(context, time);
                bStop.setVisibility(View.VISIBLE);
                suspend.setVisibility(View.VISIBLE);
                Toast.makeText(context, "not empty" + time, Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e){

            }
        }
        bLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkForm()){
                    startActivity(new Intent(LoginActivity.this,SplashActivity.class));
                    finish();
                }else
                    Toast.makeText(getApplicationContext(), "Wrong username or password. ", Toast.LENGTH_LONG).show();
            }
        });

        //PendingIntent sender = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),PendingIntent.FLAG_NO_CREAT);
        boolean alarmUp = (
        PendingIntent.getBroadcast(context, 0,  new Intent(context, AlarmReceiver.class),PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            bStop.setVisibility(View.VISIBLE);
            suspend.setVisibility(View.VISIBLE);
        }
        else{ bStop.setVisibility(View.GONE);
            suspend.setVisibility(View.GONE);
        }

        bStop.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

             if(canStop())
              cancelRepeatingTimer();
                else
                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


            }
        });

    }

    public void cancelRepeatingTimer(){
        if(alarm != null){
            alarm.CancelAlarm(context);
        }else{
            alarm= new AlarmReceiver();
            alarm.CancelAlarm(context);
        }


        //Log.d("time",time+"");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = context.getSharedPreferences("BOX",Context.MODE_PRIVATE);
                int time = sharedPref.getInt(context.getResources().getString(R.string.time_repeating), 1);
                alarm.SetAlarm(context,time);

            }
        }, 1000*60*Integer.parseInt(String.valueOf(suspend.getText())));
    }
    boolean checkForm(){
        String log =login.getText().toString();
        String pass =password.getText().toString();
        if(log.length() == 0) return false;
        if(pass.length() == 0) return false;

        boolean b= ((log.equals("admin")) && (pass.equals("admin")));//(log != "admin" || pass != "admin");
        if (!b) return false;
        //else return false;
        //Log.d("login",log.length()+"->"+ (log.equals("admin")));
        //Log.d("pass",pass+"->"+ (pass.equals("admin")));
        //Log.d("result",b+"");
        return true;
    }
    String msg;
    boolean canStop(){
       // String suspend;
        if(suspend.getText().length() == 0){msg="No repeating time choosed!"; return false;}
        if(suspend.getText().length() > 120){msg="The suspension time is too long !"; return false;}

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
