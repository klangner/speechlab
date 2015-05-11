package com.matrobot.signal;

/**
 * Subscriber for audio power data
 */
public interface IPowerListener {
    void onPowerValue(double power);
}
