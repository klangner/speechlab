package com.klangner.speechlab.signal;

/**
 * Signal processing functions
 */
public class SignalProcessor {

    /** Signal power for the whole sample */
    public static double power(short[] data){
        double sum = 0;
        for(short x: data) {
            sum += Math.pow(x, 2);
        }
        return sum/(2*(data.length+1));
    }


    /** Signal power int logarithmic scale. */
    public static double logPower(short[] data){
        return Math.log10(power(data));
    }
}
