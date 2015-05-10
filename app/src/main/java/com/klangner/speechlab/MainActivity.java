package com.klangner.speechlab;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private AudioRecord recorder = null;
    private SignalPublisher signalPublisher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");
    }

    @Override
    protected void onResume(){
        super.onResume();
        startRecording();
    }

    @Override
    protected void onPause(){
        stopRecording();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRecording() {
        signalPublisher = new SignalPublisher();
        signalPublisher.addPowerSubscriber(new SignalPublisher.PowerSubscriber() {
            public void onChanged(double power) {
                Log.v(TAG, "Power: " + power);
            }
        });
        Thread thread = new Thread(signalPublisher);
        thread.start();
    }

    private void stopRecording(){
        Log.v(TAG, "Stop recording");
        signalPublisher.stop();
    }
}
