package com.mycompany.inz;

import java.util.TreeMap;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author Ewa Skrzypek
 */
public class Signal {

    public TreeMap<Double, Complex> samples;

    public Signal() {
        this.samples = new TreeMap();
    }
    
    public Signal(TreeMap samples) {
        this.samples = samples;
    }

}
