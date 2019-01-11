package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class DisplayController {

    MainController MainController;

    @FXML
    private AnchorPane DisplayPane;

    @FXML
    protected ComboBox<String> SignalList;

    @FXML
    private XYChart DisplayChart;

    void init(MainController aThis) {
        MainController = aThis;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        DisplayChart = new ScatterChart<>(xAxis, yAxis);
        DisplayChart.setMinWidth(920);
        DisplayChart.setMinHeight(750);
        DisplayChart.setLayoutX(340);
        DisplayPane.getChildren().add(DisplayChart);
    }

    @FXML
    void addToChart() {
        String SignalToFind = this.SignalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        //Add points to diagram
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName(SignalToFind);
        Methods.addPointToSeries(series, FoundSignal);
        DisplayChart.getData().add(series);
    }

    @FXML
    void cleanSignalChart() {
        DisplayChart.getData().clear();
    }

    @FXML
    void removeSignal() {
        String SignalToRemove
                = this.SignalList.getSelectionModel().getSelectedItem();
        ArrayList<ObservableList<String>> SignalLists =
                new ArrayList<>();
        SignalLists.add(this.SignalList.getItems());
        SignalLists.add(this.MainController.simpleAddController.SignalAdd1.
                getItems());
        SignalLists.add(this.MainController.simpleAddController.SignalAdd2.
                getItems());
                
        this.MainController.signals.remove(SignalToRemove);
        Methods.removeSignal(SignalLists, SignalToRemove);
    }
}
