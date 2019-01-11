package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ewa Skrzypek
 */
public class SimpleAddController {

    MainController MainController;

    @FXML
    private AnchorPane SimpleAddPane;

    @FXML
    ComboBox<String> AddChoice;

    @FXML
    ComboBox<String> SignalAdd1;

    @FXML
    ComboBox<String> SignalAdd2;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private LineChart<Number, Number> Signal1Chart;
    private LineChart<Number, Number> Signal2Chart;
    private LineChart<Number, Number> ResultChart;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    void init(MainController aThis) {
        MainController = aThis;
        this.AddChoice.getItems().add("+");
        this.AddChoice.getItems().add("*");
        this.AddChoice.getItems().add("-");
        final NumberAxis x1Axis = new NumberAxis();
        final NumberAxis y1Axis = new NumberAxis();
        Signal1Chart = new LineChart<>(x1Axis, y1Axis);
        Methods.setChartSize(Signal1Chart, 400, 280);
        Methods.setNodeCoordinates(Signal1Chart, 25, 30);
        Methods.setNodeCoordinates(SignalAdd1, 50, 300);
        final NumberAxis x2Axis = new NumberAxis();
        final NumberAxis y2Axis = new NumberAxis();
        Signal2Chart = new LineChart<>(x2Axis, y2Axis);
        Methods.setChartSize(Signal2Chart, 400, 280);
        Methods.setNodeCoordinates(Signal2Chart, 500, 30);
        Methods.setNodeCoordinates(SignalAdd2, 520, 300);
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
            Methods.addToPreviewWindowSimpleAdd(MainController, Signal1Chart,
                    Signal1ToFind);
            //-----------------for SY signal----------------------------
            String Signal2ToFind = this.SignalAdd2.getSelectionModel().
                    getSelectedItem();
            Methods.addToPreviewWindowSimpleAdd(MainController, Signal2Chart,
                    Signal2ToFind);
        } catch (Exception e) {
            System.err.println("Wrong Choice");
        }
    }

    @FXML
    private void clean() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(Signal1Chart);
        ChartsToClear.add(Signal2Chart);
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
            Signal Result = new Signal();
            for (Object elem : ResultKeysTab) {
                Double ValueX;
                Double ValueY;
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

                    Double Value = Methods.setSimpleActiom(AddChoice,
                            ValueX, ValueY);
                    Result.samples.put(entry, Value);
                }
            }
            XYChart.Series<Number, Number> series = new XYChart.Series();
            series.setName("Result");         
            Methods.addPointToSeries(series, Result);
            ResultChart.setCreateSymbols(false);
            ResultChart.setLegendVisible(false);
            ResultChart.getData().add(series);

        }

    }
}
