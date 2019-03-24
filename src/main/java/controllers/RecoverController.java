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
import javafx.scene.chart.LineChart;
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
public class RecoverController extends AbstractMain {
    
    @FXML
    private AnchorPane Pane;
    
    @FXML
    private TextField resultName;
    
    @FXML
    ComboBox<String> signalList;
    
    @FXML
    ComboBox<String> MethodsList;
    
    private XYChart OriginalReSignalChart;
    private XYChart OriginalImSignalChart;
    
    private LineChart<Number, Number> recoveredReSignalChart;
    private LineChart<Number, Number> recoveredImSignalChart;
    
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
        
        recoveredReSignalChart = new LineChart(new NumberAxis(),
                new NumberAxis());
        recoveredReSignalChart.setTitle("Recovered signal - Re");
        recoveredReSignalChart.setCreateSymbols(false);
        Methods.setChartSize(recoveredReSignalChart, 710, 450);
        Methods.setNodeCoordinates(recoveredReSignalChart, 350, 490);
        
        recoveredImSignalChart = new LineChart<>(new NumberAxis(),
                new NumberAxis());
        recoveredImSignalChart.setTitle("Recovered signal - Im");
        recoveredImSignalChart.setCreateSymbols(false);
        Methods.setChartSize(recoveredImSignalChart, 710, 450);
        Methods.setNodeCoordinates(recoveredImSignalChart, 1110, 490);
        
        Pane.getChildren().add(OriginalReSignalChart);
        Pane.getChildren().add(OriginalImSignalChart);
        Pane.getChildren().add(recoveredReSignalChart);
        Pane.getChildren().add(recoveredImSignalChart);
        
        super.init(aThis);
        MethodsList.getItems().addAll("r0", "r1");
        
    }
    
    @FXML
    void showOriginalSignal() {
        try {
            String SignalToFind = this.signalList.getSelectionModel().
                    getSelectedItem();
            Signal FoundSignal = MainController.signals.get(SignalToFind);
            //Add points to diagram
            XYChart.Series<Number, Number> reSeries = new XYChart.Series();
            reSeries.setName(SignalToFind);
            //      imSeries.setName(SignalToFind);
            Set<Map.Entry<Double, Complex>> entrySet = FoundSignal.samples.entrySet();
            for (Map.Entry<Double, Complex> entry : entrySet) {
                reSeries.getData().add(new XYChart.Data(entry.getKey(),
                        entry.getValue().getReal()));
            }
            
            XYChart.Series<Number, Number> imSeries = new XYChart.Series();
            imSeries.getData().clear();
            imSeries.setName(SignalToFind);
            //         Set<Map.Entry<Double, Complex>> entrySet = FoundSignal.samples.entrySet();
            for (Map.Entry<Double, Complex> entry : entrySet) {
                imSeries.getData().add(new XYChart.Data(entry.getKey(),
                        entry.getValue().getImaginary()));
            }
            
            OriginalReSignalChart.getData().clear();
            OriginalReSignalChart.getData().add(reSeries);
            
            OriginalImSignalChart.getData().clear();
            OriginalImSignalChart.getData().add(imSeries);
        } catch (Exception e) {
            Methods M = new Methods();
            try {
                M.showErrorBox("/fxml/CheckDataError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(RecoverController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    @FXML
    void recoverSignal() {
        String SignalToFind = this.signalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        XYChart.Series<Number, Number> reSeries = new XYChart.Series();
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        Signal Result = new Signal();
        String SignalName = this.resultName.getText();
        previewAndOrRecoverSignal(reSeries, imSeries, Result, FoundSignal,
                true);
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
                Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    @FXML
    void previewRecoveredSignal() {
        try {
            String SignalToFind = this.signalList.getSelectionModel().
                    getSelectedItem();
            Signal FoundSignal = MainController.signals.get(SignalToFind);
            XYChart.Series<Number, Number> reSeries = new XYChart.Series();
            XYChart.Series<Number, Number> imSeries = new XYChart.Series();
            previewAndOrRecoverSignal(reSeries, imSeries, FoundSignal,
                    FoundSignal, false);
        } catch (Exception e) {
            try {
                Methods M = new Methods();
                M.showErrorBox("/fxml/CheckDataError.fxml");
            } catch (IOException ex) {
                Logger.getLogger(RecoverController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    private void previewAndOrRecoverSignal(XYChart.Series<Number, Number> reSeries, XYChart.Series<Number, Number> imSeries,
            Signal Result, Signal FoundSignal, boolean loadFlag) {
        Double a = FoundSignal.samples.firstKey();
        Double b = FoundSignal.samples.lastKey();
        String SignalName = this.resultName.getText();
        switch (MethodsList.getSelectionModel().getSelectedItem()) {
            case "r0":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        reSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i).getReal()));
                        imSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i).getImaginary()));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.get(i));
                        }
                    } else {
                        reSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)).getReal()));
                        imSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(FoundSignal.samples.
                                        lowerKey(i)).getImaginary()));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.
                                    get(FoundSignal.samples.lowerKey(i)));
                        }
                    }
                }
                break;
            case "r1":
                for (double i = a; i <= b; i += (b - a) / 100) {
                    if (FoundSignal.samples.containsKey(i)) {
                        reSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i).getReal()));
                        imSeries.getData().add(new XYChart.Data(i,
                                FoundSignal.samples.get(i).getImaginary()));
                        if (loadFlag) {
                            Result.samples.put(i, FoundSignal.samples.get(i));
                        }
                    } else {
                        Complex Res = Methods.assertValue(i, FoundSignal.samples.lowerKey(i),
                                FoundSignal.samples.higherKey(i),
                                FoundSignal.samples.get(FoundSignal.samples.lowerKey(i)),
                                FoundSignal.samples.get(FoundSignal.samples.higherKey(i)));
                        reSeries.getData().add(new XYChart.Data(i,
                                Res.getReal()));
                        imSeries.getData().add(new XYChart.Data(i,
                                Res.getImaginary()));
                        if (loadFlag) {
                            Result.samples.put(i, Res);
                        }
                    }
                }
                break;
        }
        reSeries.setName(SignalName);
        recoveredReSignalChart.getData().clear();
        recoveredReSignalChart.getData().add(reSeries);
        
        imSeries.setName(SignalName);
        recoveredImSignalChart.getData().clear();
        recoveredImSignalChart.getData().add(imSeries);
    }
}
