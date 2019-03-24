package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.commons.math3.complex.Complex;

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
            Signal Result = new Signal();
            prepareResult(Result);
            XYChart.Series<Number, Number> reSeries = new XYChart.Series();
             XYChart.Series<Number, Number> imSeries = new XYChart.Series();
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
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/CheckDataError.fxml");
        }

    }

    @FXML
    private void cleanPreview() {
        ArrayList<XYChart> ChartsToClear = new ArrayList<>();
        ChartsToClear.add(Signal1ReChart);
        ChartsToClear.add(Signal1ImChart);
        ChartsToClear.add(Signal2ReChart);
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
    private void enter() {
        Signal Result = new Signal();
        prepareResult(Result);
        
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        String addedSignalName = resultNameField.getText();
        reSeries.setName(addedSignalName);
        Methods.addRePointToSeries(reSeries, Result);
        
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        imSeries.setName(addedSignalName);
        Methods.addImPointToSeries(imSeries, Result);
        
        if (Methods.addSignalToMainBase(this.MainController.signals, Result, addedSignalName)) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
            Methods.addSignal(places, addedSignalName);
            
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
                Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void prepareResult(Signal Result) {
        addToPreviewChart(SignalAdd1, Signal1ReChart, Signal1ImChart);
        addToPreviewChart(SignalAdd2, Signal2ReChart, Signal2ImChart);
        String SignalXName = Methods.readComboBoxValue(SignalAdd1);
        String SignalYName = this.SignalAdd2.getSelectionModel().
                getSelectedItem();
        Signal SignalX = MainController.signals.get(SignalXName);
        Signal SignalY = MainController.signals.get(SignalYName);
        Iterator<Complex> ValuesX = SignalX.samples.values().iterator();
        List<Complex> VX = new ArrayList<Complex>();
        for (int i = 0; i < SignalX.samples.size(); i++) {
            VX.add(ValuesX.next());
        }
        Iterator<Complex> ValuesY = SignalY.samples.values().iterator();
        List<Complex> VY = new ArrayList<Complex>();
        for (int i = 0; i < SignalY.samples.size(); i++) {
           VY.add(ValuesY.next());
        }
        Double ax = SignalX.samples.firstKey();
        Double bx = SignalX.samples.lastKey();
        int ResultSize = SignalX.samples.size() + SignalY.samples.size() - 1;
        for (int i = 0; i < ResultSize; i++) {
            Complex valueY = new Complex(0.0, 0.0);
            for (int j = 0; j <= i; j++) {
                if ((i - j) < 0 || j >= VX.size() || (i - j) >= VY.size()) {
                    valueY = valueY;
                } else {
                   valueY = valueY.add(VX.get(j).multiply(VY.get(i - j)));
                }
            }
            Result.samples.put(ax + i * ((bx - 1) / ResultSize), valueY);
        }
    }

    private void addToPreviewChart(ComboBox<String> CB, LineChart<Number, Number> reLC,
            LineChart<Number, Number> imLC) {
        reLC.getData().clear();
        imLC.getData().clear();
        Methods.addToPreviewWindowSimpleAdd(MainController, reLC, imLC,
                Methods.readComboBoxValue(CB));
    }

}
