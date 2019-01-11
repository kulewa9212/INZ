package controllers;

import com.mycompany.inz.Signal;
import java.util.TreeMap;
import javafx.fxml.FXML;

public class MainController {

    public TreeMap<String, Signal> signals;

    @FXML
    StartController startController;

    @FXML
    DisplayController displayController;

    @FXML
    SimpleAddController simpleAddController;

    @FXML
    public void initialize() {
        this.signals = new TreeMap();
        this.startController.init(this);
        displayController.init(this);
        simpleAddController.init(this);
    }
}
