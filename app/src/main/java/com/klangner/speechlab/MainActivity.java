package com.klangner.speechlab;

import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int MESSAGE_POWER = 1;

    private Handler handler;
    private SignalPublisher signalPublisher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView powerView = (TextView)findViewById(R.id.powerView);
        handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                powerView.setText(msg.obj.toString());
            }
        };
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
}
