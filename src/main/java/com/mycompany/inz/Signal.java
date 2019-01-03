/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.inz;

import java.util.TreeMap;

/**
 *
 * @author Ewa Skrzypek
 */
public class Signal {

    public TreeMap<Number, Number> samples;

    public Signal() {
        this.samples = new TreeMap();
    }
    
    public Signal(TreeMap samples) {
        this.samples = samples;
    }

}
