package com.matrobot.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Power in log units publisher
 */
public class LogPowerFilter implements IPowerListener {

    private List<IPowerListener> listeners = new ArrayList<>();

    public LogPowerFilter(PowerFilter powerFilter){
        powerFilter.addPowerSubscriber(this);
    }

    /** Addd subscriber */
    public void addPowerSubscriber(IPowerListener l){
        listeners.add(l);
    }

    @Override
    public void onPowerValue(double power) {
        double logPower = Math.log10(power);
        for(IPowerListener l : listeners){
            l.onPowerValue(logPower);
        }
    }
}
