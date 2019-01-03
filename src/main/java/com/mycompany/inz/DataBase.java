/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.inz;

import java.util.TreeMap;

/**
 *
 * @author Ewa Kulesza
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
