package com.matrobot.signal;

/**
 * Subscriber for audio power data
 */
public interface IFrequencyListener {
    void onFrequencyData(int rate, Complex[] data);
}
