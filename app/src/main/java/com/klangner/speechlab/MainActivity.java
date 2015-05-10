package com.klangner.speechlab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int MESSAGE_POWER = 1;

    /** Handler class for processing messages from outside of main thread */
    private static class ActivityHandler extends Handler {
        private final WeakReference<MainActivity> currentActivity;
        public ActivityHandler(MainActivity activity){
            currentActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message){
            currentActivity.get().handleMessage(message);
        }
    }

    private SignalPublisher signalPublisher = null;
    private Handler handler;
    private TextView powerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        powerView = (TextView)findViewById(R.id.powerView);
        handler = new ActivityHandler(this);
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
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    private void startRecording() {
        signalPublisher = new SignalPublisher();
        signalPublisher.addPowerSubscriber(new SignalPublisher.PowerSubscriber() {
            public void onChanged(double power) {
                Message msg = handler.obtainMessage(MESSAGE_POWER, power);
                msg.sendToTarget();
            }
        });
        Thread thread = new Thread(signalPublisher);
        thread.start();
    }

    private void stopRecording(){
        Log.v(TAG, "Stop recording");
        signalPublisher.stop();
    }

    private void handleMessage(Message msg) {
        if(powerView != null) {
            powerView.setText(msg.obj.toString());
        }
    }
}
