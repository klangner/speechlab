package com.klangner.speechlab;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.klangner.speechlab.signal.SignalProcessor;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int SAMPLE_SIZE = 2048;

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate");
    }

    @Override
    protected void onStart(){
        super.onStart();
        initAudioCapture();
    }

    @Override
    protected void onStop(){
        super.onStart();
        releaseAudioCapture();
    }

    @Override
    protected void onResume(){
        super.onResume();
        startRecording();
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopRecording();
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

    private void initAudioCapture() {
        Log.v(TAG, "Init audio");
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
    }

    private void releaseAudioCapture() {
        Log.v(TAG, "Destroy audio");
        if(recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    private void startRecording() {
        Log.v(TAG, "Start recording");
        if(recorder != null) {
            recorder.startRecording();
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    processAudio();
                }
            }, "AudioRecorder Thread");
            isRecording = true;
            recordingThread.start();
        }
    }

    private void stopRecording(){
        Log.v(TAG, "Stop recording");
        isRecording = false;
    }

    private void processAudio() {
        short[] sample = new short[SAMPLE_SIZE];
        while (isRecording) {
            recorder.read(sample, 0, SAMPLE_SIZE);
            double power = SignalProcessor.logPower(sample);
            Log.v(TAG, "Power: " + power);
        }
    }
}
