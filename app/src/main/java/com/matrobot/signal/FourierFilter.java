package com.matrobot.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Signal processing publisher.
 * Connects to audio and process incoming data.
 * Allow subscribing to different audio features.
 * This class works in it own thread. You can't update UI in callback from this class.
 */
public class FourierFilter implements IRawAudioListener {

    private List<IFrequencyListener> listeners = new ArrayList<>();

    public FourierFilter(SignalSource signalSource){
        signalSource.addListener(this);
    }

    /** Addd subscriber */
    public void addPowerSubscriber(IFrequencyListener l){
        listeners.add(l);
    }

    @Override
    public void onAudioData(int rate, short[] data) {
        Complex[] samples = new Complex[data.length];
        for (int i=0; i<data.length; i++){
            samples[i] = new Complex(data[i], 0);
        }
        Complex[] fft = FFT.fft(samples);
        for(IFrequencyListener l : listeners){
            l.onFrequencyData(rate, fft);
        }
    }
}
