package com.matrobot.signal;

/**
 * Subscriber for audio data in raw format
 */
public interface IRawAudioListener {
    void onAudioData(int rate, short[] data);
}
