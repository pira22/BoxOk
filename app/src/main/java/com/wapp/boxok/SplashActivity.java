package com.wapp.boxok;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.wapp.boxok.filechooser.Constants;
import com.wapp.boxok.filechooser.FileChooserActivity;
import com.wapp.boxok.filechooser.adapters.ListAdapter;
import com.wapp.boxok.model.Advertise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;


public class SplashActivity extends Activity {
    private AlarmReceiver alarm;
    NumberPicker np_minute,np_hour;
    public static  ListAdapter adapter;
    RealmResults<Advertise> result;
     ArrayList<String> list;
    EditText interval;
    Realm realm;
    int min;
    int hour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        realm= Realm.getInstance(this);

        // This schedule a runnable task every 2 minutes
        interval=(EditText)findViewById(R.id.interval);

        Button bStart = (Button)findViewById(R.id.button_start);
        bStart.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                alarm = new AlarmReceiver();

                if(canStart()){
                    String i= String.valueOf(interval.getText());
                    startRepeatingTimer(Integer.parseInt(i));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();

                }


            }
        });

        Button bStop = (Button)findViewById(R.id.button_stop);
        bStop.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                cancelRepeatingTimer();
            }
        });

        Button bChoose = (Button)findViewById(R.id.button_choose);
        bChoose.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), FileChooserActivity.class));
                startActivityForResult(new Intent(getApplicationContext(), FileChooserActivity.class),0);
            }
        });



        initLisView();
        //initPicker();



    }

    void initLisView(){

        /*if(result.isEmpty()){
            list.add("test 1");
            list.add("test 2");
        }*/
        result = realm.where(Advertise.class).findAll();
        list = new ArrayList<String>();
        for (Advertise u : result) {
            list.add(u.getPath());
        }
        ListView listview = (ListView) findViewById(R.id.listview);
        adapter = new ListAdapter(this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Advertise ad= realm.where(Advertise.class)
                        .equalTo("path", list.get(i))
                        .findFirst();
                realm.beginTransaction();
                Log.d("REALM FOUND",ad.toString());
                ad.removeFromRealm();
                // result.clear();
                realm.commitTransaction();

                list.remove(i);
                adapter.notifyDataSetChanged();
            }
        });
    }

    void getProc(){
        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> alltasks = am
                            .getRunningTasks(1);
                    //Log.i("RUNNING",am.getRunningTasks(1).);
                    for (ActivityManager.RunningTaskInfo aTask : alltasks) {
                         Log.i("RUNNING",aTask.topActivity.getClassName());
                        runOnUiThread(new Runnable() {
                            public void run() {
                               // Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_LONG).show();

                            }
                        });


                    }

                } catch (Throwable t) {
                    Log.i("Zxczpt", "Throwable caught: "
                            + t.getMessage(), t);
                }
            }
        }, 0, 10L, TimeUnit.SECONDS);
    }
/*
    void initPicker(){
        np_minute = (NumberPicker) findViewById(R.id.picker_interval);
        np_minute.setMinValue(0);
        np_minute.setMaxValue(59);
        np_minute.setWrapSelectorWheel(false);
        np_minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                min= newVal;
                Log.d("min",newVal+"");


            }
        });

        np_hour = (NumberPicker) findViewById(R.id.picker_interval);
        np_hour.setMinValue(0);
        np_hour.setMaxValue(24);
        np_hour.setWrapSelectorWheel(false);
        np_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                hour= newVal;
                Log.d("min",hour+"");

            }
        });
    }*/
    String msg;
    boolean canStart(){

        if(result.isEmpty()){ msg="No Ads selected!";return false;}
        if(interval.getText().length() == 0){msg="No repeating time choosed!"; return false;}
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra(Constants.KEY_FILE_SELECTED);
                Log.d("RESULT F",result);
                list.add(result);
                addAdvertising(result);
                adapter.notifyDataSetChanged();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        //}
    }

    boolean addAdvertising(final String path){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Advertise ad = realm.createObject(Advertise.class);
                ad.setPath(path);
            }
        });
        result = realm.where(Advertise.class).findAll();
        list.clear();

        for (Advertise u : result) {
            list.add(u.getPath());
        }
        adapter.setValues(list);
        Log.d("ADAPTER",adapter.getValues().size()+"");
        return true;
    }

    public void startRepeatingTimer(int time) {
        Context context = this.getApplicationContext();
        SharedPreferences sharedPref = this.getSharedPreferences("BOX",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.putInt(getString(R.string.time_repeating), time);
        editor.commit();

        if(alarm != null){

            alarm.SetAlarm(context,time);
        }else{

            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.CancelAlarm(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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


    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+ "/myFolder/");
        Uri uri = Uri.parse("D:/mnt/sda/sda1");
        //intent.setDataAndType(uri, "text/csv");

        startActivity(Intent.createChooser(intent, "Open folder"));
    }

}

