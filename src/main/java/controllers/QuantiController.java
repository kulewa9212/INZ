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
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ewa Skrzypek
 */
public class QuantiController extends AbstractMain {

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField fField;

    @FXML
    private TextField resultName;

    @FXML
    ComboBox<String> signalList;

    @FXML
    ComboBox<String> methodsList;

    private LineChart<Number, Number> SampledSignalChart;
    private XYChart OriginalSignalChart;

    @FXML
    protected ListView<String> parameters;

    @Override
    void init(MainController aThis) {

        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        OriginalSignalChart = new ScatterChart<>(xAxis1, yAxis1);
        Methods.setChartSize(OriginalSignalChart, 850, 370);
        Methods.setNodeCoordinates(OriginalSignalChart, 330, 40);

        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new LineChart<>(xAxis2, yAxis2);
        SampledSignalChart.setCreateSymbols(false);
        Methods.setChartSize(SampledSignalChart, 850, 370);
        Methods.setNodeCoordinates(SampledSignalChart, 330, 400);

        Pane.getChildren().add(OriginalSignalChart);
        Pane.getChildren().add(SampledSignalChart);
        
        super.init(aThis);
        
        methodsList.getItems().addAll("decrease", "round");
        parameters.setVisible(false);
    }

    @FXML
    void showOriginalSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        //Add points to diagram
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName(SignalToFind);
        Methods.addPointToSeries(series, FoundSignal);
        OriginalSignalChart.getData().clear();
        OriginalSignalChart.getData().add(series);
    }

    @FXML
    void sampleSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Signal Result1 = new Signal();
        previewAndOrSampleSignal(series, Result1, FoundSignal, true);
        Signal Result = new Signal();
        quantize(series, Result, Result1, true);
        String SignalName = this.resultName.getText();
        series.setName(SignalName);
        boolean B = Methods.addSignalToMainBase(this.MainController.signals,
                Result, SignalName);
        if (B == true) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
            Methods.addSignal(places, SignalName);
            SampledSignalChart.getData().clear();
            SampledSignalChart.getData().add(series);
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
    void previewSampledSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        Signal Result1 = new Signal();
        XYChart.Series<Number, Number> series = new XYChart.Series();
        previewAndOrSampleSignal(series, Result1, FoundSignal, true);
        quantize(series, new Signal(), Result1, false);
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);
    }

    @FXML
    void clean() {
        parameters.setVisible(false);
        OriginalSignalChart.getData().clear();
        SampledSignalChart.getData().clear();
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
        series.getData().add(new XYChart.Data(b,
                FoundSignal.samples.get(b)));
        Result.samples.put(b, FoundSignal.samples.get(b));
        String SignalName = this.resultName.getText();
        series.setName(SignalName);
    }

    private void quantize(XYChart.Series<Number, Number> series,
            Signal Result, Signal Result1, boolean loadFlag) {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Double interval = Double.parseDouble(fField.getText());
        String method = Methods.readComboBoxValue(methodsList);
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        Iterator<Double> iterator = FoundSignal.samples.keySet().iterator();
        Double MSE = 0.0;
        Double SNRhelp = 0.0;
        Double MD = 0.0;
        while (iterator.hasNext()) {
            Double i = iterator.next();
            if (Result1.samples.containsKey(i)) {
                series.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i)));
                if (loadFlag) {
                    Result.samples.put(i, Result1.samples.get(i));
                }
            } else {
                if ("decrease".equals(method)) {
                    series.getData().add(new XYChart.Data(i,
                            Result1.samples.get(Result1.samples.
                                    lowerKey(i))));
                    if (loadFlag) {
                        Result.samples.put(i, Result1.samples.
                                get(Result1.samples.lowerKey(i)));
                    }
                } else if ("round".equals(method)) {
                    if (i - Result1.samples.lowerKey(i) <= 0.5 * interval) {
                        series.getData().add(new XYChart.Data(i,
                                Result1.samples.get(Result1.samples.
                                        lowerKey(i))));
                        if (loadFlag) {
                            Result.samples.put(i, Result1.samples.
                                    get(Result1.samples.lowerKey(i)));
                        }
                    } else {
                        try {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.
                                            higherKey(i))));
                            if (loadFlag) {
                                Result.samples.put(i, Result1.samples.
                                        get(Result1.samples.higherKey(i)));
                            }
                        } catch (Exception e) {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.lowerKey(i))));
                            if (loadFlag) {
                                Result.samples.put(i, Result1.samples.
                                        get(Result1.samples.lowerKey(i)));
                            }
                        }

                    }
                }
            }
            if (loadFlag) {
                MSE = MSE + Math.abs(FoundSignal.samples.get(i).abs() - Result.samples.get(i).abs())
                        * Math.abs(FoundSignal.samples.get(i).abs() - Result.samples.get(i).abs());
                SNRhelp = SNRhelp + Math.abs(FoundSignal.samples.get(i).abs()) * Math.abs(
                        FoundSignal.samples.get(i).abs());
                if (Math.abs(FoundSignal.samples.get(i).abs() - Result.samples.get(i).abs()) > MD) {
                    MD = Math.abs(FoundSignal.samples.get(i).abs() - Result.samples.get(i).abs());
                }
            }
        }
        if (loadFlag) {
            Double SNR = 10 * Math.log10(SNRhelp / MSE);
            MSE = MSE / Result.samples.size();
            Double PSNR = 10 * Math.log10(FoundSignal.samples.lastKey() / MSE);
            parameters.setVisible(true);
            parameters.getItems().clear();
            parameters.getItems().add("Signal name                 :      "
                    + series.getName());
            parameters.getItems().add("Original signal name    :      "
                    + SignalToFind);
            parameters.getItems().add("MSE                              :      "
                    + Methods.round(MSE, 2));
            parameters.getItems().add("SNR                              :      "
                    + Methods.round(SNR, 2));
            parameters.getItems().add("PSNR                            :      "
                    + Methods.round(PSNR, 2));
            parameters.getItems().add("MD                               :      "
                    + Methods.round(MD, 2));
        }
    }

}
