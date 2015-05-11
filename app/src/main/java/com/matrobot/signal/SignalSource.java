package com.matrobot.signal;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Signal processing publisher.
 * Connects to audio and process incoming data.
 * Allow subscribing to different audio features.
 * This class works in it own thread. You can't update UI in callback from this class.
 */
public class SignalSource implements Runnable{

    private static final String TAG = "SignalSource";
    private static final int SAMPLE_SIZE = 2048;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private final AudioRecord recorder;
    private boolean isRunning = false;
    private List<IRawAudioListener> listeners = new ArrayList<>();

    public SignalSource(){
        recorder = initAudioCapture();
    }

    @Override
    public void run() {
        short[] data = new short[SAMPLE_SIZE];
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        isRunning = true;
        recorder.startRecording();
        while (isRunning) {
            recorder.read(data, 0, SAMPLE_SIZE);
            for(IRawAudioListener l : listeners){
                l.onAudioData(RECORDER_SAMPLERATE, data);
            }
        }
        releaseAudioCapture();
    }

    public void stop(){
        isRunning = false;
    }

    public void addListener(IRawAudioListener listener){
        listeners.add(listener);
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
}
