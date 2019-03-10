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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ewa Skrzypek
 */
public class FilterController extends AbstractMain {

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
    @Override
    void init(MainController aThis) {
        super.init(aThis);

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
            Signal Result = new Signal();
            prepareResult(Result);
            XYChart.Series<Number, Number> series = new XYChart.Series();
            Methods.addPointToSeries(series, Result);
            ResultChart.setCreateSymbols(false);
            ResultChart.setLegendVisible(true);
            ResultChart.getData().clear();
            ResultChart.getData().add(series);
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }

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
        Signal Result = new Signal();
        prepareResult(Result);
        XYChart.Series<Number, Number> series = new XYChart.Series();
        String addedSignalName = resultNameField.getText();
        series.setName(addedSignalName);
        Methods.addPointToSeries(series, Result);
        if (Methods.addSignalToMainBase(this.MainController.signals, Result, addedSignalName)) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
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

    private void prepareResult(Signal Result) {
        addToPreviewChart(SignalAdd1, Signal1Chart);
        addToPreviewChart(SignalAdd2, Signal2Chart);
        String SignalXName = Methods.readComboBoxValue(SignalAdd1);
        String SignalYName = this.SignalAdd2.getSelectionModel().
                getSelectedItem();
        Signal SignalX = MainController.signals.get(SignalXName);
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
    }

    private void addToPreviewChart(ComboBox<String> CB, LineChart<Number, Number> LC) {
        LC.getData().clear();
        Methods.addToPreviewWindowSimpleAdd(MainController, LC,
                Methods.readComboBoxValue(CB));
    }

}
