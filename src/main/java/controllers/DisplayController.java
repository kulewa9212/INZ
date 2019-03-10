package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class DisplayController extends AbstractMain {

    //  MainController MainController;
    @FXML
    private AnchorPane DisplayPane;

    @FXML
    protected ComboBox<String> SignalList;

    @FXML
    private XYChart DisplayChart;

    @FXML
    protected ListView<String> parameters;

    @Override
    void init(MainController aThis) {
        super.init(aThis);
        MainController = aThis;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        DisplayChart = new ScatterChart<>(xAxis, yAxis);
        DisplayChart.setMinWidth(860);
        DisplayChart.setMinHeight(400);
        DisplayChart.setLayoutX(340);
        DisplayChart.setLayoutY(240);
        DisplayPane.getChildren().add(DisplayChart);
        parameters.setVisible(false);
    }

    @FXML
    void addToChart() {
        String SignalToFind = this.SignalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        //Add points to diagram
        XYChart.Series<Double, Double> series = new XYChart.Series();
        series.setName(SignalToFind);
        Methods.addPointToSeries(series, FoundSignal);
        DisplayChart.getData().add(series);
        Double averageCounter = 0.0;
        Double powerCounter = 0.0;
        for (XYChart.Data<Double, Double> j : series.getData()) {
            averageCounter += j.getYValue();
            powerCounter += j.getYValue() * j.getYValue();
        }
        Double average = Methods.round(averageCounter / (series.getData().size()),
                2);
        Double power = Methods.round(powerCounter / (series.getData().size()),
                2);
        Double varianceCounter = 0.0;
        for (XYChart.Data<Double, Double> j : series.getData()) {
            varianceCounter += (j.getYValue() - average) * (j.getYValue() - average);
        }
        Double variance = Methods.round(varianceCounter / (series.getData().size()),
                2);
        Double rms = Methods.round(Math.sqrt(power), 2);
        parameters.setVisible(true);
        parameters.getItems().clear();
        parameters.getItems().add("Signal name             :      " + series.getName());
        parameters.getItems().add("Average value          :     " + average);
        parameters.getItems().add("Average power        :     " + power);
        parameters.getItems().add("RMS                         :     " + rms);
        parameters.getItems().add("Variance                  :      " + variance);

    }

    @FXML
    void cleanSignalChart() {
        DisplayChart.getData().clear();
        parameters.setVisible(false);
    }

    @FXML
    void removeSignal() {
        String SignalToRemove
                = this.SignalList.getSelectionModel().getSelectedItem();
        ArrayList<ObservableList<String>> signalLists
                = super.getSignalLists();
        Methods.removeSignal(signalLists, SignalToRemove);
    }
}
