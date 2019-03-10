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
public class SamplingController extends AbstractMain{

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField fField;

    @FXML
    private TextField resultName;

    @FXML
    ComboBox<String> signalList;

    private XYChart SampledSignalChart;
    private XYChart OriginalSignalChart;

    @Override
    void init(MainController aThis) {
        
        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        OriginalSignalChart = new ScatterChart<>(xAxis1, yAxis1);
        Methods.setChartSize(OriginalSignalChart, 850, 340);
        Methods.setNodeCoordinates(OriginalSignalChart, 340, 60);
        
        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new ScatterChart<>(xAxis2, yAxis2);
        Methods.setChartSize(SampledSignalChart, 850, 340);
        Methods.setNodeCoordinates(SampledSignalChart, 340, 400);
        
        Pane.getChildren().add(OriginalSignalChart);
        Pane.getChildren().add(SampledSignalChart);
        super.init(aThis);
    }

    @FXML
    void showOriginalSignal() {
        String SignalToFind = Methods.readComboBoxValue(signalList);
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName(SignalToFind);
        Methods.addPointToSeries(series, FoundSignal);
        OriginalSignalChart.getData().clear();
        OriginalSignalChart.getData().add(series);
    }

    @FXML
    void sampleSignal() {
        String SignalToFind = Methods.readComboBoxValue(signalList);
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        Signal Result = new Signal();
        XYChart.Series<Number, Number> series = new XYChart.Series();
        previewAndOrSampleSignal(series, Result, FoundSignal, true);
        String SignalName = this.resultName.getText();
        series.setName(SignalName);
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
                Logger.getLogger(StartController.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    void previewSampledSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        previewAndOrSampleSignal(series, FoundSignal, FoundSignal, false);
    }

    private void previewAndOrSampleSignal(XYChart.Series<Number, Number> series,
            Signal Result, Signal FoundSignal, boolean loadFlag) {
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                series.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i)));
                if (loadFlag) {
                    Result.samples.put(i, FoundSignal.samples.get(i));
                }
            } else {
                series.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        higherKey(i)))));
                if (loadFlag) {
                    Result.samples.put(i, Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                            FoundSignal.samples.higherKey(i),
                            FoundSignal.samples.get(FoundSignal.samples.
                                    lowerKey(i)),
                            FoundSignal.samples.get(FoundSignal.samples.
                                    higherKey(i))));
                }
            }
        }
        String SignalName = this.resultName.getText();
        series.setName(SignalName);
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);

    }
}
