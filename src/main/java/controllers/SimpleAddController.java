package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author Ewa Skrzypek
 */
public class SimpleAddController extends AbstractMain {

    @FXML
    private AnchorPane SimpleAddPane;

    @FXML
    ComboBox<String> AddChoice;

    @FXML
    ComboBox<String> SignalAdd1;

    @FXML
    ComboBox<String> SignalAdd2;

    @FXML
    TextField resultNameField;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private LineChart<Number, Number> Signal1ReChart;
    private LineChart<Number, Number> Signal1ImChart;
    private LineChart<Number, Number> Signal2ReChart;
    private LineChart<Number, Number> Signal2ImChart;

    private LineChart<Number, Number> resultReChart;
    private LineChart<Number, Number> resultImChart;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    @Override
    void init(MainController aThis) {

        super.init(aThis);

        this.AddChoice.getItems().add("+");
        this.AddChoice.getItems().add("*");
        this.AddChoice.getItems().add("-");

        Signal1ReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal1ReChart.setTitle("Signal 1 - Re");
        Methods.setChartSize(Signal1ReChart, 390, 300);
        Methods.setNodeCoordinates(Signal1ReChart, 25, 30);
        Methods.setNodeCoordinates(SignalAdd1, 50, 350);

        Signal1ImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal1ImChart.setTitle("Signal 1 - Im");
        Methods.setChartSize(Signal1ImChart, 390, 300);
        Methods.setNodeCoordinates(Signal1ImChart, 390, 30);

        Signal2ReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal2ReChart.setTitle("Signal 2 - Re");
        Methods.setChartSize(Signal2ReChart, 390, 300);
        Methods.setNodeCoordinates(Signal2ReChart, 820, 30);
        Methods.setNodeCoordinates(SignalAdd2, 850, 350);

        Signal2ImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal2ImChart.setTitle("Signal 2 - Im");
        Methods.setChartSize(Signal2ImChart, 390, 300);
        Methods.setNodeCoordinates(Signal2ImChart, 1185, 30);

        resultReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        resultReChart.setTitle("Result signal - Re");
        Methods.setChartSize(resultReChart, 740, 500);
        Methods.setNodeCoordinates(resultReChart, 30, 440);

        resultImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        resultImChart.setTitle("Result signal - Im");
        Methods.setChartSize(resultImChart, 740, 500);
        Methods.setNodeCoordinates(resultImChart, 800, 440);

        SimpleAddPane.getChildren().add(Signal1ReChart);
        SimpleAddPane.getChildren().add(Signal1ImChart);
        SimpleAddPane.getChildren().add(Signal2ReChart);
        SimpleAddPane.getChildren().add(Signal2ImChart);
        SimpleAddPane.getChildren().add(resultReChart);
        SimpleAddPane.getChildren().add(resultImChart);
    }

    public void preview() throws IOException {
        try {
            //-----------------for SX signal----------------------------
            previewSignal(SignalAdd1, Signal1ReChart, Signal1ImChart);
            previewSignal(SignalAdd2, Signal2ReChart, Signal2ImChart);
            //-----------------for SY signal----------------------------
            String SignalYName = Methods.readComboBoxValue(SignalAdd2);
            String SignalXName = Methods.readComboBoxValue(SignalAdd1);
            Signal SignalX = MainController.signals.get(SignalXName);
            Signal SignalY = MainController.signals.get(SignalYName);
            Signal Result = new Signal();
            prepareResultSignal(SignalX, SignalY, Result);
            XYChart.Series<Number, Number> reSeries = new XYChart.Series();
            String addedSignalName = resultNameField.getText();
            reSeries.setName(addedSignalName);
            Methods.addRePointToSeries(reSeries, Result);
            resultReChart.setCreateSymbols(false);
            resultReChart.setLegendVisible(true);
            resultReChart.getData().clear();
            resultReChart.getData().add(reSeries);

            XYChart.Series<Number, Number> imSeries = new XYChart.Series();
            imSeries.setName(addedSignalName);
            Methods.addImPointToSeries(imSeries, Result);
            resultImChart.setCreateSymbols(false);
            resultImChart.setLegendVisible(true);
            resultImChart.getData().clear();
            resultImChart.getData().add(imSeries);
        } catch (NullPointerException e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/OperationError.fxml");
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }

    }

    public void previewSignals() throws IOException {
        previewSignal(SignalAdd1, Signal1ReChart, Signal1ImChart);
        previewSignal(SignalAdd2, Signal2ReChart, Signal2ImChart);
    }

    @FXML
    private void cleanPreview() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(Signal1ReChart);
        ChartsToClear.add(Signal2ReChart);
        ChartsToClear.add(Signal1ImChart);
        ChartsToClear.add(Signal2ImChart);
        Methods.clearCharts(ChartsToClear);
    }

    @FXML
    private void cleanResult() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(resultReChart);
        ChartsToClear.add(resultImChart);
        Methods.clearCharts(ChartsToClear);
    }

    @FXML
    private void enter() throws IOException {
        try {
            //-----------------SX, SY signals computation---------------------------
            String SignalXName = Methods.readComboBoxValue(SignalAdd1);
            String SignalYName = Methods.readComboBoxValue(SignalAdd2);
            Signal SignalX = MainController.signals.get(SignalXName);
            Signal SignalY = MainController.signals.get(SignalYName);
            Signal Result = new Signal();
            prepareResultSignal(SignalX, SignalY, Result);
            XYChart.Series<Number, Number> reSeries = new XYChart.Series();
            XYChart.Series<Number, Number> imSeries = new XYChart.Series();
            String addedSignalName = resultNameField.getText();
            reSeries.setName(addedSignalName);
            imSeries.setName(addedSignalName);
            if (Methods.addSignalToMainBase(this.MainController.signals, Result, addedSignalName)) {
                ArrayList<ObservableList<String>> places
                        = super.getSignalLists();
                Methods.addSignal(places, addedSignalName);
                Methods.addRePointToSeries(reSeries, Result);
                Methods.addImPointToSeries(imSeries, Result);

                resultReChart.setCreateSymbols(false);
                resultReChart.setLegendVisible(true);
                resultReChart.getData().clear();
                resultReChart.getData().add(reSeries);

                resultImChart.setCreateSymbols(false);
                resultImChart.setLegendVisible(true);
                resultImChart.getData().clear();
                resultImChart.getData().add(imSeries);
            } else {
                try {
                    Methods M = new Methods();
                    M.showErrorBox("/fxml/SaveSignalError.fxml");
                } catch (IOException ex) {
                    Logger.getLogger(StartController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (NullPointerException e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }
    }

    private void prepareResultSignal(Signal SignalX, Signal SignalY,
            Signal Result) {
        Set<Double> KeysX = SignalX.samples.keySet();
        Set<Double> KeysY = SignalY.samples.keySet();
        Double ax = SignalX.samples.firstKey();
        Double bx = SignalX.samples.lastKey();
        Double ay = SignalY.samples.firstKey();
        Double by = SignalY.samples.lastKey();
        // Double a = Math.max(ax, ay);
        //  Double b = Math.min(bx, by);

        Double a = Math.min(ax, ay);
        Double b = Math.max(bx, by);

        //---------Mathematical chosen action on chosen Signals-----------------
        //       if (by < ax || bx < ay) {
        Set<Double> ResultKeys = new TreeSet<>(KeysX);
        ResultKeys.addAll(KeysY);
        for (double j = a; j <= b; j += 0.007) {
            if (!KeysX.contains(j) && !KeysY.contains(j)) {
                ResultKeys.add(j);
            }
        };
        Object[] ResultKeysTab = ResultKeys.toArray();
        for (Object elem : ResultKeysTab) {
            Complex ValueX;
            Complex ValueY;
            Double entry = (Double) elem;
            if (entry < a || entry > b) {
                ResultKeys.remove(entry);
            } else {
                if (KeysX.contains(entry)) {
                    ValueX = SignalX.samples.get(entry);
                } else if (entry < ax || entry > bx) {
                    ValueX = new Complex(0.0, 0.0);
                } else {
                    Double PreviousKey = SignalX.samples.lowerKey(entry);
                    Double NextKey = SignalX.samples.higherKey(entry);
                    ValueX = Methods.assertValue(entry, PreviousKey,
                            NextKey, SignalX.samples.get(PreviousKey),
                            SignalX.samples.get(NextKey));
                }
                if (KeysY.contains(entry)) {
                    ValueY = SignalY.samples.get(entry);
                } else if (entry < ay || entry > by) {
                    ValueY = new Complex(0.0, 0.0);
                } else {
                    Double PreviousKey = SignalY.samples.lowerKey(entry);
                    Double NextKey = SignalY.samples.higherKey(entry);
                    ValueY = Methods.assertValue(entry, PreviousKey,
                            NextKey, SignalY.samples.get(PreviousKey),
                            SignalY.samples.get(NextKey));
                }

                Complex Value = Methods.setSimpleActiom(AddChoice,
                        ValueX, ValueY);
                Result.samples.put(entry, Value);
            }

        }
    }

    private void previewSignal(ComboBox<String> CB, LineChart<Number, Number> ReLC, LineChart<Number, Number> ImLC) throws IOException {
        try {
            //-----------------for SX signal----------------------------
            String Signal1ToFind = CB.getSelectionModel().
                    getSelectedItem();
            ReLC.getData().clear();
            ImLC.getData().clear();
            Methods.addToPreviewWindowSimpleAdd(MainController, ReLC, ImLC,
                    Signal1ToFind);
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }
    }
}
