package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author Ewa Skrzypek
 */
public class SamplingController extends AbstractMain {

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField fField;

    @FXML
    private TextField resultName;

    @FXML
    ComboBox<String> signalList;

    private XYChart OriginalReSignalChart;
    private XYChart OriginalImSignalChart;

    private XYChart sampledReSignalChart;
    private XYChart sampledImSignalChart;

    @Override
    void init(MainController aThis) {

        OriginalReSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        OriginalReSignalChart.setTitle("Original signal - Re");
        Methods.setChartSize(OriginalReSignalChart, 710, 450);
        Methods.setNodeCoordinates(OriginalReSignalChart, 350, 40);

        OriginalImSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        OriginalImSignalChart.setTitle("Original signal - Im");
        Methods.setChartSize(OriginalImSignalChart, 710, 450);
        Methods.setNodeCoordinates(OriginalImSignalChart, 1110, 40);

        sampledReSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        sampledReSignalChart.setTitle("Sampled signal - Re");
        Methods.setChartSize(sampledReSignalChart, 710, 450);
        Methods.setNodeCoordinates(sampledReSignalChart, 350, 490);

        sampledImSignalChart = new ScatterChart<>(new NumberAxis(),
                new NumberAxis());
        sampledImSignalChart.setTitle("Sampled signal - Im");
        Methods.setChartSize(sampledImSignalChart, 710, 450);
        Methods.setNodeCoordinates(sampledImSignalChart, 1110, 490);

        Pane.getChildren().add(OriginalReSignalChart);
        Pane.getChildren().add(OriginalImSignalChart);
        Pane.getChildren().add(sampledReSignalChart);
        Pane.getChildren().add(sampledImSignalChart);

        super.init(aThis);
    }

    @FXML
    void showOriginalSignal() {
        String SignalToFind = Methods.readComboBoxValue(signalList);
        Signal FoundSignal = MainController.signals.get(SignalToFind);

        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        reSeries.setName(SignalToFind);
        reSeries.getData().clear();
        Set<Map.Entry<Double, Complex>> entrySet = FoundSignal.samples.entrySet();
        for (Map.Entry<Double, Complex> entry : entrySet) {
            reSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getReal()));
        }
        OriginalReSignalChart.getData().clear();
        OriginalReSignalChart.getData().add(reSeries);

        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        imSeries.setName(SignalToFind);
        imSeries.getData().clear();
        for (Map.Entry<Double, Complex> entry : entrySet) {
            imSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().getImaginary()));
        }
        OriginalImSignalChart.getData().clear();
        OriginalImSignalChart.getData().add(imSeries);
    }

    @FXML
    void sampleSignal() {
        String SignalToFind = Methods.readComboBoxValue(signalList);
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        Signal Result = new Signal();
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        previewAndOrSampleSignal(reSeries, imSeries, Result, FoundSignal, true);
        String SignalName = this.resultName.getText();
        reSeries.setName(SignalName);
        boolean B = Methods.addSignalToMainBase(this.MainController.signals,
                Result, SignalName);
        if (B == true) {
            ArrayList<ObservableList<String>> places = super.getSignalLists();
            Methods.addSignal(places, SignalName);
        } else {
            try {
                Methods M = new Methods();
                M.showErrorBox("/fxml/SaveSignalError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(StartController.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    void previewSampledSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
         XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        previewAndOrSampleSignal(reSeries, imSeries, FoundSignal, FoundSignal, false);
    }

    private void previewAndOrSampleSignal(XYChart.Series<Number, Number>
            reSeries, XYChart.Series<Number, Number> imSeries,
            Signal Result, Signal FoundSignal, boolean loadFlag) {
        Double interval = Double.parseDouble(fField.getText());
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        for (double i = a; i <= b; i += interval) {
            if (FoundSignal.samples.keySet().contains(i)) {
                reSeries.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i).getReal()));
                imSeries.getData().add(new XYChart.Data(i,
                        FoundSignal.samples.get(i).getImaginary()));
                if (loadFlag) {
                    Result.samples.put(i, FoundSignal.samples.get(i));
                }
            } else {
                reSeries.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        higherKey(i))).getReal()));
                imSeries.getData().add(new XYChart.Data(i,
                        Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.
                                        higherKey(i))).getImaginary()));
                if (loadFlag) {
                    Result.samples.put(i, Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                            FoundSignal.samples.higherKey(i),
                            FoundSignal.samples.get(FoundSignal.samples.
                                    lowerKey(i)),
                            FoundSignal.samples.get(FoundSignal.samples.
                                    higherKey(i))));
                }
            }
        }
        String SignalName = this.resultName.getText();
        
        reSeries.setName(SignalName);
        imSeries.setName(SignalName);
        
        sampledReSignalChart.getData().clear();
        sampledReSignalChart.getData().add(reSeries);
        
        sampledImSignalChart.getData().clear();
        sampledImSignalChart.getData().add(imSeries);

    }
}
