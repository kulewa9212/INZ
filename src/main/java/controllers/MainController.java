package controllers;

import com.mycompany.inz.Signal;
import java.util.TreeMap;
import javafx.fxml.FXML;

public class MainController {

    public TreeMap<String, Signal> signals;
    
    public TreeMap<String, Signal> filterList;
    
  

    @FXML
    StartController startController;

    @FXML
    DisplayController displayController;

    @FXML
    SimpleAddController simpleAddController;
    
    @FXML
    FilterController filterController;
    
    @FXML
    SamplingController samplingController;
    
    @FXML
    RecoverController recoverController;
    
    @FXML
    QuantiController quantiController;
    
    @FXML
    FourierController fourierController;
    
    @FXML
    FiltersController filtersController;
    
    @FXML
    NewFilterController newFilterController;
    
    @FXML
    public void initialize() {
        this.signals = new TreeMap();
        this.filterList = new TreeMap();
        startController.init(this);
        displayController.init(this);
        simpleAddController.init(this);
        filterController.init(this);
        samplingController.init(this);
        recoverController.init(this);
        quantiController.init(this);
        fourierController.init(this);
        filtersController.init(this);
        newFilterController.init(this);
    }
}
