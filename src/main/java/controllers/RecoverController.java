/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ewa Skrzypek
 */
public class RecoverController extends AbstractMain{

    @FXML
    private AnchorPane Pane;
   
    @FXML
    private TextField resultName;

    @FXML
    ComboBox<String> signalList;

    @FXML
    ComboBox<String> MethodsList;

    private LineChart<Number, Number> SampledSignalChart;
    private XYChart OriginalSignalChart;

    @Override
    void init(MainController aThis) {

        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        OriginalSignalChart = new ScatterChart<>(xAxis1, yAxis1);
        Methods.setNodeCoordinates(OriginalSignalChart, 340, 60);
        Methods.setChartSize(OriginalSignalChart, 850, 340);
        

        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new LineChart<>(xAxis2, yAxis2);
        SampledSignalChart.setCreateSymbols(false);
        Methods.setNodeCoordinates(SampledSignalChart, 340, 400);
        Methods.setChartSize(SampledSignalChart, 850, 340);

        Pane.getChildren().add(OriginalSignalChart);
        Pane.getChildren().add(SampledSignalChart);
        super.init(aThis);
        MethodsList.getItems().addAll("r0", "r1");

    }

    @FXML
    void showOriginalSignal() {
        try {
            String SignalToFind = this.signalList.getSelectionModel().
                    getSelectedItem();
            Signal FoundSignal = MainController.signals.get(SignalToFind);
            //Add points to diagram
            XYChart.Series<Number, Number> series = new XYChart.Series();
            series.setName(SignalToFind);
            Methods.addPointToSeries(series, FoundSignal);
            OriginalSignalChart.getData().clear();
            OriginalSignalChart.getData().add(series);
        } catch (Exception e) {
            Methods M = new Methods();
            try {
                M.showErrorBox("/fxml/CheckDataError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(RecoverController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

        }
    }

    @FXML
    void recoverSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Signal Result = new Signal();
        String SignalName = this.resultName.getText();
        previewAndOrRecoverSignal(series, Result, FoundSignal, true);
        boolean B = Methods.addSignalToMainBase(this.MainController.signals,
                Result, SignalName);
        if (B == true) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
            Methods.addSignal(places, SignalName);
        } else {
            try {
                Methods M = new Methods();
                M.showErrorBox("/fxml/SaveSignalError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    void previewRecoveredSignal() {
        try {
            String SignalToFind = this.signalList.getSelectionModel().
                    getSelectedItem();
            Signal FoundSignal = MainController.signals.get(SignalToFind);
            XYChart.Series<Number, Number> series = new XYChart.Series();
            previewAndOrRecoverSignal(series, FoundSignal, FoundSignal, false);
        } catch (Exception e) {
            try {
                Methods M = new Methods();
                M.showErrorBox("/fxml/CheckDataError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(RecoverController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

        }
    }

    private void previewAndOrRecoverSignal(XYChart.Series<Number, Number> series,
            Signal Result, Signal FoundSignal, boolean loadFlag) {
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String SignalName = this.resultName.getText();
        switch (MethodsList.getSelectionModel().getSelectedItem()) {
            case "r0":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.get(i));
                        }
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i))));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.
                                    get(FoundSignal.samples.lowerKey(i)));
                        }
                    }
                }
                break;
            case "r1":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.get(i));
                        }
                    } else {
                        Double Res = Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)));
                        series.getData().add(new XYChart.Data(i,
                                Res));
                        if (loadFlag) {
                            Result.samples.put(i, Res);
                        }
                    }
                }
                break;
        }
        series.setName(SignalName);
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);
    }
}
