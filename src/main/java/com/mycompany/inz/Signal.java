package com.mycompany.inz;

import java.util.TreeMap;

/**
 *
 * @author Ewa Skrzypek
 */
public class Signal {

    public TreeMap<Double, Double> samples;

    public Signal() {
        this.samples = new TreeMap();
    }
    
    public Signal(TreeMap samples) {
        this.samples = samples;
    }

}
