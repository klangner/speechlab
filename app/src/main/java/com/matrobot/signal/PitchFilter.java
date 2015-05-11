package com.matrobot.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Extract pitch (F0) from FFT data
 */
public class PitchFilter implements IFrequencyListener {

    private List<IPitchListener> listeners = new ArrayList<>();

    public PitchFilter(FourierFilter filter){
        filter.addListener(this);
    }

    /** Add subscriber */
    public void addListener(IPitchListener l){
        listeners.add(l);
    }

    @Override
    public void onFrequencyData(int rate, Complex[] data) {
        float pitch = 0;
        for(IPitchListener l : listeners){
            l.onPitchValue(pitch);
        }
    }
}
