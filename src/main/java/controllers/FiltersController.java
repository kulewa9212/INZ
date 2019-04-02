package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;

/**
 *
 * @author Ewa Skrzypek
 */
public class FiltersController extends AbstractMain {

    @FXML
    private AnchorPane SimpleAddPane;

    @FXML
    ComboBox<String> SignalAdd1;

    @FXML
    ComboBox<String> filters;

    @FXML
    TextField resultNameField;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    private LineChart<Number, Number> Signal1ReChart;
    private LineChart<Number, Number> Signal1ImChart;
    private LineChart<Number, Number> filterReChart;
    private LineChart<Number, Number> filterImChart;
    private LineChart<Number, Number> resultReChart;
    private LineChart<Number, Number> resultImChart;

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    @Override
    void init(MainController aThis) {
        super.init(aThis);

        Signal1ReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal1ReChart.setTitle("Signal  - Re");
        Methods.setChartSize(Signal1ReChart, 390, 300);
        Methods.setNodeCoordinates(Signal1ReChart, 25, 30);
        Methods.setNodeCoordinates(SignalAdd1, 50, 350);

        Signal1ImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        Signal1ImChart.setTitle("Signal  - Im");
        Methods.setChartSize(Signal1ImChart, 390, 300);
        Methods.setNodeCoordinates(Signal1ImChart, 390, 30);

        filterReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        filterReChart.setTitle("Filter - Re");
        Methods.setChartSize(filterReChart, 390, 300);
        Methods.setNodeCoordinates(filterReChart, 820, 30);
        Methods.setNodeCoordinates(filters, 850, 350);

        filterImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        filterImChart.setTitle("Filter - amplitude");
        Methods.setChartSize(filterImChart, 390, 300);
        Methods.setNodeCoordinates(filterImChart, 1185, 30);

        resultReChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        resultReChart.setTitle("Filtered signal - Re");
        Methods.setChartSize(resultReChart, 740, 500);
        Methods.setNodeCoordinates(resultReChart, 30, 440);

        resultImChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        resultImChart.setTitle("Filtered signal - Im");
        Methods.setChartSize(resultImChart, 740, 500);
        Methods.setNodeCoordinates(resultImChart, 800, 440);

        SimpleAddPane.getChildren().add(Signal1ReChart);
        SimpleAddPane.getChildren().add(Signal1ImChart);
        SimpleAddPane.getChildren().add(filterReChart);
        SimpleAddPane.getChildren().add(filterImChart);
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
        ChartsToClear.add(filterReChart);
        ChartsToClear.add(filterImChart);
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
        Methods.addAbsPointToSeries(imSeries, Result);

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
        addToPreviewFilterChart(filters, filterReChart, filterImChart);
        String SignalXName = Methods.readComboBoxValue(SignalAdd1);
        String SignalYName = this.filters.getSelectionModel().
                getSelectedItem();
        Signal SignalX = MainController.signals.get(SignalXName);
        Signal filter = MainController.filterList.get(SignalYName);
        
        Signal SXDFT = makeDFT(SignalX);
        Signal filterDFT = makeDFT(filter);
        
        Signal filteredDFT=doProduct(SXDFT, filterDFT);
        
        Signal resSignal = makeRevertDFT(filteredDFT);
        
        for(Double res: resSignal.samples.keySet()){
            Result.samples.put(res, resSignal.samples.get(res));    
        }
       
    }

    private void addToPreviewChart(ComboBox<String> CB, LineChart<Number, Number> reLC,
            LineChart<Number, Number> imLC) {
        reLC.getData().clear();
        imLC.getData().clear();
        Methods.addToPreviewWindowSimpleAdd(MainController, reLC, imLC,
                Methods.readComboBoxValue(CB));
    }

    private void addToPreviewFilterChart(ComboBox<String> CB, LineChart<Number, Number> reLC,
            LineChart<Number, Number> imLC) {
        reLC.getData().clear();
        imLC.getData().clear();
        Methods.addToPreviewFilterWindowSimpleAdd(MainController, reLC, imLC,
                Methods.readComboBoxValue(CB));
    }

    @FXML
    void loadFromFile() throws IOException {
        try {
            FileChooser fc = new FileChooser();         //fileChooser opening
            File sF = fc.showOpenDialog(new Stage());   //chosen file
            String SignalName = sF.getName().substring(0,
                    sF.getName().length() - 4);
            Scanner sR = new Scanner(sF);
            Signal S = new Signal();
            while (sR.hasNextLine()) {
                String next = sR.nextLine();
                S.samples.put(Double.parseDouble((String) next.split(";")[0].
                        replace(",", ".")),
                        new ComplexFormat().parse((String) next.split(";")[1]));
            }

            Signal filter = new Signal();
            Integer N = S.samples.size();

            for (int k = 1; k <= N - 1; k++) {
                Complex counter = new Complex(0, 0);
                int i = 0;
                for (Complex v : S.samples.values()) {
                    Complex c = new Complex(Math.cos(2 * Math.PI * i * k / N),
                            Math.sin(2 * Math.PI * i * k / N));
                    counter = counter.add(v.multiply(c));
                    i++;

                }
                filter.samples.put(Double.valueOf(k), counter);
            }

            MainController.filterList.put(SignalName, filter);
            MainController.filtersController.filters.getItems().add(SignalName);
            MainController.newFilterController.FilterList.getItems().add(SignalName);
            if (true) {
                ArrayList<ObservableList<String>> places
                        = super.getSignalLists();
                // Methods.addSignal(places, SignalName);
            } else {
                Methods M = new Methods();
                M.showErrorBox("/fxml/ErrorStartBox.fxml");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    private Signal makeDFT(Signal input) {
        Signal filter = new Signal();
        Integer N = input.samples.size();

        for (int k = 1; k <= N - 1; k++) {
            Complex counter = new Complex(0, 0);
            int i = 0;
            for (Complex v : input.samples.values()) {
                Complex c = new Complex(Math.cos(2 * Math.PI * i * k / N),
                        (-1) * Math.sin(2 * Math.PI * i * k / N));
                counter = counter.add(v.multiply(c));
                i++;

            }
            filter.samples.put(Double.valueOf(k), counter);
        }
        return filter;
    }

    private Signal makeRevertDFT(Signal input) {
        Signal filter = new Signal();
        Integer N = input.samples.size();

        for (int k = 1; k <= N - 1; k++) {
            Complex counter = new Complex(0, 0);
            int i = 0;
            for (Complex v : input.samples.values()) {
                Complex c = new Complex(Math.cos(2 * Math.PI * i * k / N),
                        Math.sin(2 * Math.PI * i * k / N));
                counter = counter.add(v.multiply(c));
                i++;

            }
            filter.samples.put(Double.valueOf(k), counter);
        }
        return filter;
    }

    private Signal doProduct(Signal SX, Signal SY) {
        Signal result = new Signal();
        Set<Double> KeysX = SX.samples.keySet();
        Set<Double> KeysY = SY.samples.keySet();
        Set<Double> ResultKeys = new TreeSet<>(KeysX);
        ResultKeys.addAll(KeysY);
        Object[] ResultKeysTab = ResultKeys.toArray();
        for (Object elem : ResultKeysTab) {
            Complex ValueX;
            Complex ValueY;
            Double entry = (Double) elem;
            if (KeysX.contains(entry)) {
                ValueX = SX.samples.get(entry);
            } else {
                ValueX = new Complex(0.0, 0.0);
            }

            if (KeysY.contains(entry)) {
                ValueY = SY.samples.get(entry);
            } else {
                ValueY = new Complex(0.0, 0.0);
            }

            Complex Value = ValueX.multiply(ValueY);
            result.samples.put(entry, Value);
        }
        return result;
    }
}
