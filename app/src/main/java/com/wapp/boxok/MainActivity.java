package com.wapp.boxok;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


import com.wapp.boxok.model.Advertise;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends Activity {//implements SurfaceHolder.Callback{

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;
    public static  long DURATION_INTERVAL=0;
    public static  String IP="192.168.1.9";
    int position;
    String path;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);
       //position = getIntent().getIntExtra("path",0);
        path = getIntent().getStringExtra("path");
        //Log.d("path",path+"");
        addVideoView(path);


    }



    void addVideoView(String path){
        VideoView videoHolder = new VideoView(this);
        videoHolder.setMediaController(null);
        Uri video = Uri.parse(path);//"android.resource://" + getPackageName() + "/"  + R.raw.thug);
        videoHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               // mp.setVolume(0, 0);
                // DURATION_VIDEO = mp.getDuration();
                int duration = mp.getDuration();
                Log.d("start", duration+" start $$$$");

                SharedPreferences sharedPref = getSharedPreferences("BOX",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.duration), duration);
                editor.commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        /*AlarmReceiver alarm = new AlarmReceiver();
                        SharedPreferences sharedPref = context.getSharedPreferences("BOX",Context.MODE_PRIVATE);
                        int time = sharedPref.getInt(context.getResources().getString(R.string.time_repeating), 1);
                        alarm.SetAlarm(context,time);*/
                        //Log.d("start", "end----");


                    }
                }, duration);

            }
        });
        videoHolder.setVideoURI(video);
        
        setContentView(videoHolder);
        videoHolder.start();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
       // mMediaPlayer.stop();
    }






}

