package com.mycompany.inz;

import java.util.TreeMap;

/**
 *
 * @author Ewa Skrzypek
 */
public class DataBase {
    
     TreeMap signals;

    public DataBase() {
        this.signals = new TreeMap();
    }
    
    public DataBase(TreeMap signals) {
        this.signals = signals;
    }
}
