package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Ewa Skrzypek
 */
public class FilterController {

    MainController MainController;

    @FXML
    private AnchorPane SimpleAddPane;
    @FXML
    ComboBox<String> SignalAdd1;

    @FXML
    ComboBox<String> SignalAdd2;

    @FXML
    TextField resultNameField;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private LineChart<Number, Number> Signal1Chart;
    private LineChart<Number, Number> Signal2Chart;
    private LineChart<Number, Number> ResultChart;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    void init(MainController aThis) {
        MainController = aThis;
        final NumberAxis x1Axis = new NumberAxis();
        final NumberAxis y1Axis = new NumberAxis();
        Signal1Chart = new LineChart<>(x1Axis, y1Axis);
        Methods.setChartSize(Signal1Chart, 400, 280);
        Methods.setNodeCoordinates(Signal1Chart, 25, 30);
        Methods.setNodeCoordinates(SignalAdd1, 50, 320);
        final NumberAxis x2Axis = new NumberAxis();
        final NumberAxis y2Axis = new NumberAxis();
        Signal2Chart = new LineChart<>(x2Axis, y2Axis);
        Methods.setChartSize(Signal2Chart, 400, 280);
        Methods.setNodeCoordinates(Signal2Chart, 500, 30);
        Methods.setNodeCoordinates(SignalAdd2, 520, 320);
        SimpleAddPane.getChildren().add(Signal1Chart);
        SimpleAddPane.getChildren().add(Signal2Chart);
        final NumberAxis xRAxis = new NumberAxis();
        final NumberAxis yRAxis = new NumberAxis();
        ResultChart = new LineChart<>(xRAxis, yRAxis);
        Methods.setChartSize(ResultChart, 700, 405);
        Methods.setNodeCoordinates(ResultChart, 100, 370);
        SimpleAddPane.getChildren().add(ResultChart);
    }

    public void Preview() {
        try {
            //-----------------for SX signal----------------------------
            String Signal1ToFind = this.SignalAdd1.getSelectionModel().
                    getSelectedItem();
            Signal1Chart.getData().clear();
            Methods.addToPreviewWindowSimpleAdd(MainController, Signal1Chart,
                    Signal1ToFind);
            //-----------------for SY signal----------------------------
            String Signal2ToFind = this.SignalAdd2.getSelectionModel().
                    getSelectedItem();
            Signal2Chart.getData().clear();
            Methods.addToPreviewWindowSimpleAdd(MainController, Signal2Chart,
                    Signal2ToFind);
        } catch (Exception e) {
            System.err.println("Wrong Choice");
        }
        String SignalXName = this.SignalAdd1.getSelectionModel().
                getSelectedItem();
        Signal SignalX = MainController.signals.get(SignalXName);
        String SignalYName = this.SignalAdd2.getSelectionModel().
                getSelectedItem();
        Signal SignalY = MainController.signals.get(SignalYName);
        Iterator<Double> ValuesX = SignalX.samples.values().iterator();
        List<Double> VX = new ArrayList<Double>();
        for (int i = 0; i < SignalX.samples.size(); i++) {
            VX.add(ValuesX.next());
        }
        Iterator<Double> ValuesY = SignalY.samples.values().iterator();
        List<Double> VY = new ArrayList<Double>();
        for (int i = 0; i < SignalY.samples.size(); i++) {
            VY.add(ValuesY.next());
        }
        Signal Result = new Signal();
        Double ax = SignalX.samples.firstKey();
        Double bx = SignalX.samples.lastKey();
        int ResultSize = SignalX.samples.size() + SignalY.samples.size() - 1;
        for (int i = 0; i < ResultSize; i++) {
            double valueY = 0;
            for (int j = 0; j <= i; j++) {
                if ((i - j) < 0 || j >= VX.size() || (i - j) >= VY.size()) {
                    valueY += 0.0;
                } else {
                    valueY += VX.get(j) * VY.get(i - j);
                }
            }
            Result.samples.put(ax + i * ((bx - 1) / ResultSize), valueY);
        }
        XYChart.Series<Number, Number> series = new XYChart.Series();
        Methods.addPointToSeries(series, Result);
        ResultChart.setCreateSymbols(false);
        ResultChart.setLegendVisible(true);
        ResultChart.getData().clear();
        ResultChart.getData().add(series);

    }

    @FXML
    private void cleanPreview() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(Signal1Chart);
        ChartsToClear.add(Signal2Chart);
        Methods.clearCharts(ChartsToClear);
    }

    @FXML
    private void cleanResult() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(ResultChart);
        Methods.clearCharts(ChartsToClear);
    }

    @FXML
    private void enter() {
        //-----------------SX, SY signals computation---------------------------
        String SignalXName = this.SignalAdd1.getSelectionModel().
                getSelectedItem();
        Signal SignalX = MainController.signals.get(SignalXName);
        String SignalYName = this.SignalAdd2.getSelectionModel().
                getSelectedItem();
        Signal SignalY = MainController.signals.get(SignalYName);
        Iterator<Double> ValuesX = SignalX.samples.values().iterator();
        List<Double> VX = new ArrayList<Double>();
        for (int i = 0; i < SignalX.samples.size(); i++) {
            VX.add(ValuesX.next());
        }
        Iterator<Double> ValuesY = SignalY.samples.values().iterator();
        List<Double> VY = new ArrayList<Double>();
        for (int i = 0; i < SignalY.samples.size(); i++) {
            VY.add(ValuesY.next());
        }
        Signal Result = new Signal();
        Double ax = SignalX.samples.firstKey();
        Double bx = SignalX.samples.lastKey();
        int ResultSize = SignalX.samples.size() + SignalY.samples.size() - 1;
        for (int i = 0; i < ResultSize; i++) {
            double valueY = 0;
            for (int j = 0; j <= i; j++) {
                if ((i - j) < 0 || j >= VX.size() || (i - j) >= VY.size()) {
                    valueY += 0.0;
                } else {
                    valueY += VX.get(j) * VY.get(i - j);
                }
            }
            Result.samples.put(ax + i * ((bx - 1) / ResultSize), valueY);
        }
        XYChart.Series<Number, Number> series = new XYChart.Series();
        String addedSignalName = resultNameField.getText();
        series.setName(addedSignalName);
        Methods.addPointToSeries(series, Result);
        if (Methods.addSignalToMainBase(this.MainController.signals, Result, addedSignalName)) {
            ArrayList<ObservableList<String>> places = new ArrayList<>();
            places.add(MainController.displayController.SignalList.getItems());
            places.add(MainController.simpleAddController.SignalAdd1.
                    getItems());
            places.add(MainController.simpleAddController.SignalAdd2.
                    getItems());
            places.add(MainController.startController.SignalList.getItems());
            places.add(MainController.filterController.SignalAdd1.
                    getItems());
            places.add(MainController.filterController.SignalAdd2.
                    getItems());
            places.add(MainController.samplingController.signalList.
                    getItems());
            places.add(MainController.recoverController.signalList.
                    getItems());
            places.add(MainController.quantiController.signalList.
                    getItems());
            Methods.addSignal(places, addedSignalName);
            ResultChart.setCreateSymbols(false);
            ResultChart.setLegendVisible(true);
            ResultChart.getData().clear();
            ResultChart.getData().add(series);
        } else {
            try {
                Methods M = new Methods();
                M.showErrorBox("/fxml/SaveSignalError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
