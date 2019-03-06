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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Ewa Skrzypek
 */
public class SamplingController {

    MainController MainController;

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

    void init(MainController aThis) {
        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        OriginalSignalChart = new ScatterChart<>(xAxis1, yAxis1);
        OriginalSignalChart.setMinWidth(850);
        OriginalSignalChart.setMinHeight(340);
        OriginalSignalChart.setMaxHeight(340);
        OriginalSignalChart.setLayoutX(340);
        OriginalSignalChart.setLayoutY(60);
        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new ScatterChart<>(xAxis2, yAxis2);
        SampledSignalChart.setMinWidth(850);
        SampledSignalChart.setMinHeight(340);
        SampledSignalChart.setMaxHeight(340);
        SampledSignalChart.setLayoutX(340);
        SampledSignalChart.setLayoutY(400);
        Pane.getChildren().add(OriginalSignalChart);
        Pane.getChildren().add(SampledSignalChart);
        MainController = aThis;
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
        Signal Result = new Signal();
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                series.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i)));
                Result.samples.put(i, FoundSignal.samples.get(i));
            } else {
                series.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)))));
                Result.samples.put(i, Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                        FoundSignal.samples.higherKey(i),
                        FoundSignal.samples.get(FoundSignal.samples.
                                lowerKey(i)),
                        FoundSignal.samples.get(FoundSignal.samples.
                                higherKey(i))));
            }
        }
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
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String SignalName = this.resultName.getText();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                series.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i)));
            } else {
                series.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)))));
            }
        }
        series.setName(SignalName);
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);

    }
}
