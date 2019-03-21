package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author Ewa Skrzypek
 */
public class SimpleAddController extends AbstractMain{

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
    private LineChart<Number, Number> Signal1Chart;
    private LineChart<Number, Number> Signal2Chart;
    private LineChart<Number, Number> ResultChart;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    @Override
    void init(MainController aThis) {
        
        super.init(aThis);

        this.AddChoice.getItems().add("+");
        this.AddChoice.getItems().add("*");
        this.AddChoice.getItems().add("-");

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

        final NumberAxis xRAxis = new NumberAxis();
        final NumberAxis yRAxis = new NumberAxis();
        ResultChart = new LineChart<>(xRAxis, yRAxis);
        Methods.setChartSize(ResultChart, 700, 405);
        Methods.setNodeCoordinates(ResultChart, 100, 370);
        
        SimpleAddPane.getChildren().add(Signal1Chart);
        SimpleAddPane.getChildren().add(Signal2Chart);
        SimpleAddPane.getChildren().add(ResultChart);
    }

    public void preview() throws IOException {
        try {
            //-----------------for SX signal----------------------------
            previewSignal(SignalAdd1, Signal1Chart);
            previewSignal(SignalAdd2, Signal2Chart);
            //-----------------for SY signal----------------------------
            String SignalYName = Methods.readComboBoxValue(SignalAdd2);
            String SignalXName = Methods.readComboBoxValue(SignalAdd1);
            Signal SignalX = MainController.signals.get(SignalXName);
            Signal SignalY = MainController.signals.get(SignalYName);
            Signal Result = new Signal();
            prepareResultSignal(SignalX, SignalY, Result);
            XYChart.Series<Number, Number> series = new XYChart.Series();
            String addedSignalName = resultNameField.getText();
            series.setName(addedSignalName);
            Methods.addPointToSeries(series, Result);
            ResultChart.setCreateSymbols(false);
            ResultChart.setLegendVisible(true);
            ResultChart.getData().clear();
            ResultChart.getData().add(series);
        } catch (NullPointerException e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/OperationError.fxml");
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }

    }

    public void previewSignals() throws IOException {
        previewSignal(SignalAdd1, Signal1Chart);
        previewSignal(SignalAdd2, Signal2Chart);
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
    private void enter() throws IOException {
        try {
            //-----------------SX, SY signals computation---------------------------
            String SignalXName = Methods.readComboBoxValue(SignalAdd1);
            String SignalYName = Methods.readComboBoxValue(SignalAdd2);
            Signal SignalX = MainController.signals.get(SignalXName);
            Signal SignalY = MainController.signals.get(SignalYName);
            Signal Result = new Signal();
            prepareResultSignal(SignalX, SignalY, Result);
            XYChart.Series<Number, Number> series = new XYChart.Series();
            String addedSignalName = resultNameField.getText();
            series.setName(addedSignalName);
            if (Methods.addSignalToMainBase(this.MainController.signals, Result, addedSignalName)) {
                ArrayList<ObservableList<String>> places = 
                        super.getSignalLists();
                Methods.addSignal(places, addedSignalName);
                Methods.addPointToSeries(series, Result);
                ResultChart.setCreateSymbols(false);
                ResultChart.setLegendVisible(true);
                ResultChart.getData().clear();
                ResultChart.getData().add(series);
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
        Double a = Math.max(ax, ay);
        Double b = Math.min(bx, by);

        //---------Mathematical chosen action on chosen Signals-----------------
        if (by < ax || bx < ay) {
            System.out.println("Cannot handle!");
        } else {
            Set<Double> ResultKeys = new TreeSet<>(KeysX);
            ResultKeys.addAll(KeysY);
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
                    } else {
                        Double PreviousKey = SignalX.samples.lowerKey(entry);
                        Double NextKey = SignalX.samples.higherKey(entry);
                        ValueX = Methods.assertValue(entry, PreviousKey,
                                NextKey, SignalX.samples.get(PreviousKey),
                                SignalX.samples.get(NextKey));
                    }
                    if (KeysY.contains(entry)) {
                        ValueY = SignalY.samples.get(entry);
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
    }

    private void previewSignal(ComboBox<String> CB, LineChart<Number, Number> LC) throws IOException {
        try {
            //-----------------for SX signal----------------------------
            String Signal1ToFind = CB.getSelectionModel().
                    getSelectedItem();
            LC.getData().clear();
            Methods.addToPreviewWindowSimpleAdd(MainController, LC,
                    Signal1ToFind);
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }
    }
}
