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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Ewa Skrzypek
 */
public class QuantiController {

    MainController MainController;

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

    void init(MainController aThis) {
        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        OriginalSignalChart = new ScatterChart<>(xAxis1, yAxis1);
        OriginalSignalChart.setMinWidth(850);
        OriginalSignalChart.setMinHeight(370);
        OriginalSignalChart.setMaxHeight(370);
        OriginalSignalChart.setLayoutX(330);
        OriginalSignalChart.setLayoutY(40);
        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new LineChart<>(xAxis2, yAxis2);
        SampledSignalChart.setCreateSymbols(false);
        SampledSignalChart.setMinWidth(850);
        SampledSignalChart.setMinHeight(370);
        SampledSignalChart.setMaxHeight(370);
        SampledSignalChart.setLayoutX(330);
        SampledSignalChart.setLayoutY(400);
        Pane.getChildren().add(OriginalSignalChart);
        Pane.getChildren().add(SampledSignalChart);
        MainController = aThis;
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
        Signal Result1 = new Signal();
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String method = methodsList.getSelectionModel().
                getSelectedItem();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                series.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i)));
                Result1.samples.put(i, FoundSignal.samples.get(i));
            } else {
                series.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)))));
                Result1.samples.put(i, Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                        FoundSignal.samples.higherKey(i),
                        FoundSignal.samples.get(FoundSignal.samples.
                                lowerKey(i)),
                        FoundSignal.samples.get(FoundSignal.samples.
                                higherKey(i))));
            }
        }
        series.getData().add(new XYChart.Data(b,
                FoundSignal.samples.get(b)));
        Result1.samples.put(b, FoundSignal.samples.get(b));
        Signal Result = new Signal();
        Double a1 = Result1.samples.firstKey();
        Double b1 = Result1.samples.lastKey();
        Double MSE = 0.0;
        Double SNRhelp = 0.0;
        Double MD = 0.0;
        //   String SignalName = this.resultName.getText();
        Iterator<Double> iterator = FoundSignal.samples.keySet().iterator();
        while (iterator.hasNext()) {
            Double i = iterator.next();
            if (Result1.samples.containsKey(i)) {
                series.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i)));
                Result.samples.put(i, Result1.samples.get(i));
            } else {
                if ("decrease".equals(method)) {
                    series.getData().add(new XYChart.Data(i,
                            Result1.samples.get(Result1.samples.
                                    lowerKey(i))));
                    Result.samples.put(i, Result1.samples.
                            get(Result1.samples.lowerKey(i)));
                } else if ("round".equals(method)) {
                    if (i - Result1.samples.lowerKey(i) <= 0.5 * interval) {
                        series.getData().add(new XYChart.Data(i,
                                Result1.samples.get(Result1.samples.
                                        lowerKey(i))));
                        Result.samples.put(i, Result1.samples.
                                get(Result1.samples.lowerKey(i)));
                    } else {
                        try {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.
                                            higherKey(i))));
                            Result.samples.put(i, Result1.samples.
                                    get(Result1.samples.higherKey(i)));
                        } catch (Exception e) {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.lowerKey(i))));
                            Result.samples.put(i, Result1.samples.
                                    get(Result1.samples.lowerKey(i)));
                        }

                    }
                }
            }
            MSE += Math.abs(FoundSignal.samples.get(i) - Result.samples.get(i))
                    * Math.abs(FoundSignal.samples.get(i) - Result.samples.get(i));
            SNRhelp += Math.abs(FoundSignal.samples.get(i)) * Math.abs(FoundSignal.samples.get(i));
            if (Math.abs(FoundSignal.samples.get(i) - Result.samples.get(i)) > MD) {
                MD = Math.abs(FoundSignal.samples.get(i) - Result.samples.get(i));
            }
        }
        Double SNR = 10 * Math.log10(SNRhelp / MSE);
        MSE = MSE / Result.samples.size();
        Double PSNR = 10 * Math.log10(FoundSignal.samples.lastKey() / MSE);
        /*
        for (double i = a1; i <= b; i += (b1 - a1) / 100) {
            if (Result1.samples.containsKey(i)) {
                series.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i)));
                Result.samples.put(i, Result1.samples.get(i));
            } else {
                series.getData().add(new XYChart.Data(i,
                        Result1.samples.get(Result1.samples.
                                lowerKey(i))));
                Result.samples.put(i, Result1.samples.
                        get(Result1.samples.lowerKey(i)));
            }
        }
         */
        String SignalName = this.resultName.getText();
        series.setName(SignalName);
        boolean B = Methods.addSignalToMainBase(this.MainController.signals,
                Result, SignalName);
        if (B == true) {
            ArrayList<ObservableList<String>> places = new ArrayList<>();
            places.add(MainController.displayController.SignalList.getItems());
            places.add(MainController.simpleAddController.SignalAdd1.
                    getItems());
            places.add(MainController.simpleAddController.SignalAdd2.
                    getItems());
            places.add(MainController.filterController.SignalAdd1.
                    getItems());
            places.add(MainController.filterController.SignalAdd2.
                    getItems());
            places.add(MainController.startController.SignalList.getItems());
            places.add(MainController.samplingController.signalList.
                    getItems());
            places.add(MainController.recoverController.signalList.
                    getItems());
            places.add(MainController.quantiController.signalList.
                    getItems());
            Methods.addSignal(places, SignalName);
            parameters.setVisible(true);
            parameters.getItems().clear();
            parameters.getItems().add("Signal name                 :      " + series.getName());
            parameters.getItems().add("Original signal name    :      " + SignalToFind);
            parameters.getItems().add("MSE                              :      " + Methods.round(MSE, 2));
            parameters.getItems().add("SNR                              :      " + Methods.round(SNR, 2));
            parameters.getItems().add("PSNR                            :      " + Methods.round(PSNR, 2));
            parameters.getItems().add("MD                               :      " + Methods.round(MD, 2));
            SampledSignalChart.getData().clear();
            SampledSignalChart.getData().add(series);
        } else {
            Stage stage = new Stage();
            Parent root;
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
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String method = methodsList.getSelectionModel().
                getSelectedItem();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                series.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i)));
                Result1.samples.put(i, FoundSignal.samples.get(i));
            } else {
                series.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)))));
                Result1.samples.put(i, Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                        FoundSignal.samples.higherKey(i),
                        FoundSignal.samples.get(FoundSignal.samples.
                                lowerKey(i)),
                        FoundSignal.samples.get(FoundSignal.samples.
                                higherKey(i))));
            }
        }
        series.getData().add(new XYChart.Data(b,
                FoundSignal.samples.get(b)));
        Result1.samples.put(b, FoundSignal.samples.get(b));
        Iterator<Double> iterator = FoundSignal.samples.keySet().iterator();
        while (iterator.hasNext()) {
            Double i = iterator.next();
            if (Result1.samples.containsKey(i)) {
                series.getData().add(new XYChart.Data(i,
                        Result1.samples.get(i)));
            } else {
                if ("decrease".equals(method)) {
                    series.getData().add(new XYChart.Data(i,
                            Result1.samples.get(Result1.samples.
                                    lowerKey(i))));
                } else if ("round".equals(method)) {
                    if (i - Result1.samples.lowerKey(i) <= 0.5 * interval) {
                        series.getData().add(new XYChart.Data(i,
                                Result1.samples.get(Result1.samples.
                                        lowerKey(i))));
                    } else {
                        try {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.
                                            higherKey(i))));
                        } catch (Exception e) {
                            series.getData().add(new XYChart.Data(i,
                                    Result1.samples.get(Result1.samples.lowerKey(i))));
                        }

                    }
                }
            }
        }
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);
    }
    
    @FXML
    void clean(){
    parameters.setVisible(false);
    OriginalSignalChart.getData().clear();
    SampledSignalChart.getData().clear();
    }


}
