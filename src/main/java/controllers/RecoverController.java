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
import javafx.scene.chart.LineChart;
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
public class RecoverController {

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
    ComboBox<String> MethodsList;

    private LineChart<Number, Number> SampledSignalChart;
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
        Pane.getChildren().add(OriginalSignalChart);
        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        SampledSignalChart = new LineChart<>(xAxis2, yAxis2);
        SampledSignalChart.setCreateSymbols(false);
        SampledSignalChart.setMinWidth(850);
        SampledSignalChart.setMinHeight(340);
        SampledSignalChart.setLayoutX(340);
        SampledSignalChart.setMaxHeight(340);
        SampledSignalChart.setLayoutY(400);
        Pane.getChildren().add(SampledSignalChart);
        MainController = aThis;
        MethodsList.getItems().addAll("r0", "r1");
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
    void recoverSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Signal Result = new Signal();
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String SignalName = this.resultName.getText();
        switch (MethodsList.getSelectionModel().getSelectedItem()) {
            case "r0":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                        Result.samples.put(i, FoundSignal.samples.get(i));
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i))));
                        Result.samples.put(i, FoundSignal.samples.
                                get(FoundSignal.samples.lowerKey(i)));
                    }
                }
                break;
            case "r1":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                    } else {
                        Double Res = Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)));
                        System.out.println("X = " + i + ", Y = " + Res);
                        series.getData().add(new XYChart.Data(i,
                                Res));
                        Result.samples.put(i, Res);
                    }
                }
                break;
        }
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
            places.add(MainController.recoverController.signalList.getItems());
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
    void previewRecoveredSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> series = new XYChart.Series();

        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String SignalName = this.resultName.getText();
        switch (MethodsList.getSelectionModel().getSelectedItem()) {
            case "r0":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i))));
                    }
                }
                break;
            case "r1":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        series.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i)));
                    } else {
                        Double Res = Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)));
                        System.out.println("X = " + i + ", Y = " + Res);
                        series.getData().add(new XYChart.Data(i,
                                Res));
                    }
                }
                break;
        }
        series.setName(SignalName);
        SampledSignalChart.getData().clear();
        SampledSignalChart.getData().add(series);
    }

}
