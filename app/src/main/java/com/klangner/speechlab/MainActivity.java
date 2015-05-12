package com.klangner.speechlab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.matrobot.signal.FourierFilter;
import com.matrobot.signal.IPitchListener;
import com.matrobot.signal.IPowerListener;
import com.matrobot.signal.IRawAudioListener;
import com.matrobot.signal.LogPowerFilter;
import com.matrobot.signal.PitchFilter;
import com.matrobot.signal.PowerFilter;
import com.matrobot.signal.SignalSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int MESSAGE_POWER = 1;
    private static final int MESSAGE_PITCH = 2;

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

    private SignalSource signalSource = null;
    private Handler handler;
    private TextView powerView = null;
    private TextView pitchView = null;

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
        signalSource = new SignalSource();
        subscribeToPowerValuesStream();
        subscribeToPitchValuesStream();
        Thread thread = new Thread(signalSource);
        thread.start();
    }

    private void subscribeTimeDomain(){
        final LineChart chart = (LineChart)findViewById(R.id.timeChart);
        signalSource.addListener(new IRawAudioListener() {
            public void onAudioData(int rate, short[] data) {
                setData(chart, 100, 100);
//                LineData dataset = new LineData(data, "DataSet 1");
//                chart.setData(dataset);
            }
        });
    }

    private void setData(LineChart chart, int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            yVals.add(new Entry(val, i));
        }

        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets
        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
    }

    private void subscribeToPowerValuesStream(){
        PowerFilter powerFilter = new PowerFilter(signalSource);
        LogPowerFilter logPowerFilter = new LogPowerFilter(powerFilter);
        logPowerFilter.addPowerSubscriber(new IPowerListener() {
            @Override
            public void onPowerValue(double power) {
                Message msg = handler.obtainMessage(MESSAGE_POWER, power);
                msg.sendToTarget();
            }
        });
    }
    private void subscribeToPitchValuesStream(){
        FourierFilter fftFilter = new FourierFilter(signalSource);
        PitchFilter pitchFilter = new PitchFilter(fftFilter);
        pitchFilter.addListener(new IPitchListener() {
            public void onPitchValue(float pitch) {
                Message msg = handler.obtainMessage(MESSAGE_PITCH, pitch);
                msg.sendToTarget();
            }
        });
    }

    private void stopRecording(){
        Log.v(TAG, "Stop recording");
        signalSource.stop();
    }

    private void handleMessage(Message msg) {
        if(msg.what == MESSAGE_POWER && powerView != null) {
            powerView.setText(msg.obj.toString());
        }
        else if(msg.what == MESSAGE_PITCH && pitchView != null) {
            pitchView.setText(msg.obj.toString());
        }
    }
}
