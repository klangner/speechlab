package com.matrobot.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Signal processing publisher.
 * Connects to audio and process incoming data.
 * Allow subscribing to different audio features.
 * This class works in it own thread. You can't update UI in callback from this class.
 */
public class PowerFilter implements IRawAudioListener {

    private List<IPowerListener> listeners = new ArrayList<>();

    public PowerFilter(SignalSource signalSource){
        signalSource.addListener(this);
    }

    /** Addd subscriber */
    public void addPowerSubscriber(IPowerListener l){
        listeners.add(l);
    }

    @Override
    public void onAudioData(int rate, short[] data) {
        double sum = 0;
        for(short x: data) {
            sum += Math.pow(x, 2);
        }
        double power = sum/(2*(data.length+1));
        for(IPowerListener l : listeners){
            l.onPowerValue(power);
        }
    }
}
