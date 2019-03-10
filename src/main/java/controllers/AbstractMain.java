/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.mycompany.inz.Methods;
import java.util.ArrayList;
import javafx.collections.ObservableList;

/**
 *
 * @author Ewa Kulesza
 */
public abstract class AbstractMain {

    MainController MainController;

    void init(MainController aThis) {
        MainController = aThis;
    }

    protected ArrayList<ObservableList<String>> getSignalLists() {
        ArrayList<ObservableList<String>> places = new ArrayList<>();
        places.add(MainController.displayController.SignalList.
                getItems());
        places.add(MainController.simpleAddController.SignalAdd1.
                getItems());
        places.add(MainController.simpleAddController.SignalAdd2.
                getItems());
        places.add(MainController.startController.SignalList.getItems());
        places.add(MainController.filterController.SignalAdd1.
                getItems());
        places.add(MainController.filterController.SignalAdd2.
                getItems());
        places.add(MainController.samplingController.signalList.
                getItems());
        places.add(MainController.recoverController.signalList.
                getItems());
        places.add(MainController.quantiController.signalList.
                getItems());
        return places;
    }
}
