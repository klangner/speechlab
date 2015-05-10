package com.klangner.speechlab;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Signal processing publisher.
 * Connects to audio and process incoming data.
 * Allow subscribing to different audio features.
 * This class works in it own thread. You can't update UI in callback from this class.
 */
public class SignalPublisher implements Runnable{

    private static final String TAG = "SignalPublisher";
    private static final int SAMPLE_SIZE = 2048;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public interface PowerSubscriber{
        void onChanged(double power);
    }

    private final AudioRecord recorder;
    private boolean isRunning = false;
    private PowerSubscriber powerSubscriber = null;


    public SignalPublisher(){
        recorder = initAudioCapture();
    }

    @Override
    public void run() {
        short[] sample = new short[SAMPLE_SIZE];
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        isRunning = true;
        recorder.startRecording();
        while (isRunning) {
            recorder.read(sample, 0, SAMPLE_SIZE);
            if(powerSubscriber != null){
                double power = SignalPublisher.logPower(sample);
                powerSubscriber.onChanged(power);
            }
        }
        releaseAudioCapture();
    }

    public void stop(){
        isRunning = false;
    }

    public void addPowerSubscriber(PowerSubscriber subscriber){
        powerSubscriber = subscriber;
    }

    private AudioRecord initAudioCapture() {
        Log.v(TAG, "Init audio");
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        return new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
    }

    private void releaseAudioCapture() {
        Log.v(TAG, "Destroy audio");
        if(recorder != null) {
            recorder.release();
        }
    }

    /** Signal power for the whole sample */
    private static double power(short[] data){
        double sum = 0;
        for(short x: data) {
            sum += Math.pow(x, 2);
        }
        return sum/(2*(data.length+1));
    }


    /** Signal power int logarithmic scale. */
    private static double logPower(short[] data){
        return Math.log10(power(data));
    }

}
