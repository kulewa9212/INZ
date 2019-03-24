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
import java.util.Map;
import java.util.Set;
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
import org.apache.commons.math3.complex.Complex;

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

    private LineChart<Number, Number> sampledReSignalChart;
    private LineChart<Number, Number> sampledImSignalChart;

    private XYChart OriginalReSignalChart;
    private XYChart OriginalImSignalChart;

    @FXML
    protected ListView<String> parameters;

    @Override
    void init(MainController aThis) {

        OriginalReSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        OriginalReSignalChart.setTitle("Original signal - Re");
        Methods.setChartSize(OriginalReSignalChart, 710, 450);
        Methods.setNodeCoordinates(OriginalReSignalChart, 350, 40);

        OriginalImSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        OriginalImSignalChart.setTitle("Original signal - Im");
        Methods.setChartSize(OriginalImSignalChart, 710, 450);
        Methods.setNodeCoordinates(OriginalImSignalChart, 1110, 40);

        sampledReSignalChart = new LineChart<>(new NumberAxis(),
                new NumberAxis());
        sampledReSignalChart.setCreateSymbols(false);
        sampledReSignalChart.setTitle("Quanted signal - Re");
        Methods.setChartSize(sampledReSignalChart, 710, 450);
        Methods.setNodeCoordinates(sampledReSignalChart, 350, 490);

        sampledImSignalChart = new LineChart<>(new NumberAxis(),
                new NumberAxis());
        sampledImSignalChart.setCreateSymbols(false);
        sampledImSignalChart.setTitle("Quanted signal - Im");
        Methods.setChartSize(sampledImSignalChart, 710, 450);
        Methods.setNodeCoordinates(sampledImSignalChart, 1110, 490);

        Pane.getChildren().add(OriginalReSignalChart);
        Pane.getChildren().add(OriginalImSignalChart);
        Pane.getChildren().add(sampledReSignalChart);
        Pane.getChildren().add(sampledImSignalChart);

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

        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        reSeries.setName(SignalToFind);

        reSeries.getData().clear();
        Set<Map.Entry<Double, Complex>> entrySet = FoundSignal.samples.entrySet();
        for (Map.Entry<Double, Complex> entry : entrySet) {
            reSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getReal()));
        }
        OriginalReSignalChart.getData().clear();
        OriginalReSignalChart.getData().add(reSeries);

        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        imSeries.setName(SignalToFind);
        imSeries.getData().clear();
        for (Map.Entry<Double, Complex> entry : entrySet) {
            imSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getImaginary()));
        }
        OriginalImSignalChart.getData().clear();
        OriginalImSignalChart.getData().add(imSeries);

    }

    @FXML
    void sampleSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        Signal Result1 = new Signal();
        previewAndOrSampleSignal(reSeries, imSeries, Result1, FoundSignal, true);
        Signal Result = new Signal();
        quantize(reSeries, imSeries, Result, Result1, true);
        String SignalName = this.resultName.getText();
        reSeries.setName(SignalName);
        boolean B = Methods.addSignalToMainBase(this.MainController.signals,
                Result, SignalName);
        if (B == true) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
            Methods.addSignal(places, SignalName);
            sampledReSignalChart.getData().clear();
            sampledReSignalChart.getData().add(reSeries);
            
            sampledImSignalChart.getData().clear();
            sampledImSignalChart.getData().add(imSeries);
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
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        previewAndOrSampleSignal(reSeries, imSeries, Result1, FoundSignal, true);
        quantize(reSeries, imSeries, new Signal(), Result1, false);
        sampledReSignalChart.getData().clear();
        sampledReSignalChart.getData().add(reSeries);
        sampledImSignalChart.getData().clear();
        sampledImSignalChart.getData().add(imSeries);
    }

    @FXML
    void clean() {
        parameters.setVisible(false);
        OriginalReSignalChart.getData().clear();
        OriginalImSignalChart.getData().clear();
        sampledReSignalChart.getData().clear();
        sampledImSignalChart.getData().clear();
    }

    private void previewAndOrSampleSignal(XYChart.Series<Number, Number> reSeries,
            XYChart.Series<Number, Number> imSeries,
            Signal Result, Signal FoundSignal, boolean loadFlag) {
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                reSeries.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i).getReal()));
                imSeries.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i).getImaginary()));
                if (loadFlag) {
                    Result.samples.put(i, FoundSignal.samples.get(i));
                }
            } else {
                reSeries.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        higherKey(i))).getReal()));
                imSeries.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        higherKey(i))).getImaginary()));
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
        reSeries.getData().add(new XYChart.Data(b,
                FoundSignal.samples.get(b).getReal()));
        imSeries.getData().add(new XYChart.Data(b,
                FoundSignal.samples.get(b).getImaginary()));
        Result.samples.put(b, FoundSignal.samples.get(b));
        String SignalName = this.resultName.getText();
        reSeries.setName(SignalName);
        imSeries.setName(SignalName);
    }

    private void quantize(XYChart.Series<Number, Number> reSeries,
            XYChart.Series<Number, Number> imSeries,
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
                reSeries.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i).getReal()));
                imSeries.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i).getImaginary()));
                if (loadFlag) {
                    Result.samples.put(i, Result1.samples.get(i));
                }
            } else {
                if ("decrease".equals(method)) {
                    reSeries.getData().add(new XYChart.Data(i,
                            Result1.samples.get(Result1.samples.
                                    lowerKey(i)).getReal()));
                    imSeries.getData().add(new XYChart.Data(i,
                            Result1.samples.get(Result1.samples.
                                    lowerKey(i)).getImaginary()));
                    if (loadFlag) {
                        Result.samples.put(i, Result1.samples.
                                get(Result1.samples.lowerKey(i)));
                    }
                } else if ("round".equals(method)) {
                    if (i - Result1.samples.lowerKey(i) <= 0.5 * interval) {
                        reSeries.getData().add(new XYChart.Data(i,
                                Result1.samples.get(Result1.samples.
                                        lowerKey(i)).getReal()));
                        imSeries.getData().add(new XYChart.Data(i,
                                Result1.samples.get(Result1.samples.
                                        lowerKey(i)).getImaginary()));
                        if (loadFlag) {
                            Result.samples.put(i, Result1.samples.
                                    get(Result1.samples.lowerKey(i)));
                        }
                    } else {
                        try {
                            reSeries.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.
                                            higherKey(i)).getReal()));
                            imSeries.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.
                                            higherKey(i)).getImaginary()));
                            if (loadFlag) {
                                Result.samples.put(i, Result1.samples.
                                        get(Result1.samples.higherKey(i)));
                            }
                        } catch (Exception e) {
                            reSeries.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(
                                            Result1.samples.lowerKey(i)).
                                            getReal()));
                            imSeries.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(
                                            Result1.samples.lowerKey(i)).
                                            getImaginary()));
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
                    + reSeries.getName());
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
