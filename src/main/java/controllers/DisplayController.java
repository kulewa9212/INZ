package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.math3.complex.Complex;

public class DisplayController extends AbstractMain {

    //  MainController MainController;
    @FXML
    private AnchorPane DisplayPane;

    @FXML
    protected ComboBox<String> SignalList;

    @FXML
    private XYChart imChart;

    @FXML
    private XYChart reChart;

    @FXML
    private XYChart ampChart;

    @FXML
    private XYChart phaseChart;

    @FXML
    protected ListView<String> parameters;

    @Override
    void init(MainController aThis) {
        super.init(aThis);
        MainController = aThis;

        reChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        reChart.setTitle("Re");
        Methods.setChartSize(reChart, 710, 450);
        Methods.setNodeCoordinates(reChart, 350, 40);
        DisplayPane.getChildren().add(reChart);

        imChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        imChart.setTitle("Im");
        Methods.setChartSize(imChart, 710, 450);
        Methods.setNodeCoordinates(imChart, 1110, 40);
        DisplayPane.getChildren().add(imChart);

        ampChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        ampChart.setTitle("Amplitude");
        Methods.setChartSize(ampChart, 710, 450);
        Methods.setNodeCoordinates(ampChart, 350, 490);
        DisplayPane.getChildren().add(ampChart);

        phaseChart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        phaseChart.setTitle("Phase");
        Methods.setChartSize(phaseChart, 710, 450);
        Methods.setNodeCoordinates(phaseChart, 1100, 490);
        DisplayPane.getChildren().add(phaseChart);

        parameters.setVisible(false);
    }

    @FXML
    void addToChart() {
        String SignalToFind = this.SignalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        //Add points to diagram
        XYChart.Series<Double, Double> reSeries = new XYChart.Series();
        reSeries.setName(SignalToFind);

        
        Set<Map.Entry<Double, Complex>> entrySet = FoundSignal.samples.entrySet();
        
         for (Map.Entry<Double, Complex> entry : entrySet) {
            reSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getReal()));

        }

        XYChart.Series<Double, Double> imSeries = new XYChart.Series();
        imSeries.getData().clear();
        
        imSeries.setName(SignalToFind);
        for (Map.Entry<Double, Complex> entry : entrySet) {
            imSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getImaginary()));

        }

        XYChart.Series<Double, Double> ampSeries = new XYChart.Series();
        ampSeries.getData().clear();
        ampSeries.setName(SignalToFind);
        for (Map.Entry<Double, Complex> entry : entrySet) {
            ampSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().abs()));

        }

        XYChart.Series<Double, Double> phaseSeries = new XYChart.Series();
        phaseSeries.getData().clear();
        phaseSeries.setName(SignalToFind);
        for (Map.Entry<Double, Complex> entry : entrySet) {
            phaseSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getArgument()));

        }

        reChart.getData().add(reSeries);
        imChart.getData().add(imSeries);
        ampChart.getData().add(ampSeries);
        phaseChart.getData().add(phaseSeries);

        Double averageCounter = 0.0;
        Double powerCounter = 0.0;
        for (XYChart.Data<Double, Double> j : ampSeries.getData()) {
            averageCounter += j.getYValue();
            powerCounter += j.getYValue() * j.getYValue();
        }
        Double average = Methods.round(averageCounter / (reSeries.getData().size()),
                2);
        Double power = Methods.round(powerCounter / (reSeries.getData().size()),
                2);
        Double varianceCounter = 0.0;
        for (XYChart.Data<Double, Double> j : reSeries.getData()) {
            varianceCounter += (j.getYValue() - average) * (j.getYValue() - average);
        }
        Double variance = Methods.round(varianceCounter / (reSeries.getData().size()),
                2);
        Double rms = Methods.round(Math.sqrt(power), 2);
        parameters.setVisible(true);
        parameters.getItems().clear();
        parameters.getItems().add("Signal name             :      " + reSeries.getName());
        parameters.getItems().add("Average value          :     " + average);
        parameters.getItems().add("Average power        :     " + power);
        parameters.getItems().add("RMS                         :     " + rms);
        parameters.getItems().add("Variance                  :      " + variance);

    }

    @FXML
    void cleanSignalChart() {
        imChart.getData().clear();
        reChart.getData().clear();
        ampChart.getData().clear();
        phaseChart.getData().clear();
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
