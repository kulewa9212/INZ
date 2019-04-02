package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;

public class FourierController extends AbstractMain {

    MainController MainController;

    @FXML
    private AnchorPane DisplayPane;

    @FXML
    protected ComboBox<String> SignalList;

    @FXML
    private LineChart<Number, Number> imChart;

    @FXML
    private LineChart<Number, Number> reChart;

    @FXML
    private LineChart<Number, Number> ampChart;

    @FXML
    private LineChart<Number, Number> phaseChart;

    @FXML
    private TextField filterName;

    @FXML
    private TextField nField;

    @Override
    void init(MainController aThis) {
        super.init(aThis);
        MainController = aThis;

        reChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        reChart.setCreateSymbols(false);
        reChart.setTitle("Re");
        Methods.setChartSize(reChart, 710, 450);
        Methods.setNodeCoordinates(reChart, 350, 40);
        DisplayPane.getChildren().add(reChart);

        imChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        imChart.setTitle("Im");
        imChart.setCreateSymbols(false);
        Methods.setChartSize(imChart, 710, 450);
        Methods.setNodeCoordinates(imChart, 1110, 40);
        DisplayPane.getChildren().add(imChart);

        ampChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        ampChart.setCreateSymbols(false);
        ampChart.setTitle("Amplitude");
        Methods.setChartSize(ampChart, 710, 450);
        Methods.setNodeCoordinates(ampChart, 350, 490);
        DisplayPane.getChildren().add(ampChart);

        phaseChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        phaseChart.setCreateSymbols(false);
        phaseChart.setTitle("Phase");
        Methods.setChartSize(phaseChart, 710, 450);
        Methods.setNodeCoordinates(phaseChart, 1100, 490);
        DisplayPane.getChildren().add(phaseChart);
    }

    @FXML
    void addToChart() {
        try {
            addToChartAndOrLoad(false, false);
        } catch (IOException ex) {
            Logger.getLogger(FourierController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void addToChartAndOrLoad(Boolean loadFlag, Boolean fileFlag) throws FileNotFoundException, IOException {
        String SignalToFind = this.SignalList.getSelectionModel().
                getSelectedItem();
        Signal FoundSignal = MainController.signals.get(SignalToFind);

        XYChart.Series<Number, Number> series = new XYChart.Series();
        XYChart.Series<Number, Number> imSeries = new XYChart.Series();
        XYChart.Series<Number, Number> ampSeries = new XYChart.Series();
        XYChart.Series<Number, Number> phaseSeries = new XYChart.Series();

        Signal fourierTranform = new Signal();

        series.setName(SignalToFind);
        imSeries.setName(SignalToFind);
        ampSeries.setName(SignalToFind);
        phaseSeries.setName(SignalToFind);

        Integer N = Integer.parseInt(nField.getText());

        ArrayList<Complex> values = new ArrayList<>();

        for (Complex d : FoundSignal.samples.values()) {
            values.add(d);
        }

        for (int k = 0; k <= N; k++) {
            Complex counter = new Complex(0, 0);
            for (int i = 0; i <= N; i++) {
                Complex c = new Complex(Math.cos(2 * Math.PI * i * k / N),
                        (-1) * Math.sin(2 * Math.PI * i * k / N));
                counter = counter.add(values.get(i).multiply(c));

            }
            Double fx = k * (FoundSignal.samples.size() / (FoundSignal.samples.lastKey() - FoundSignal.samples.firstKey())) / N;
            series.getData().add(new XYChart.Data(fx,
                    counter.getReal() / N));
            imSeries.getData().add(new XYChart.Data(fx,
                    counter.getImaginary() / N));
            ampSeries.getData().add(new XYChart.Data(fx,
                    counter.abs() / N));
            phaseSeries.getData().add(new XYChart.Data(fx,
                    counter.getArgument() / N));
            fourierTranform.samples.put(fx, counter);
        }

        reChart.getData().add(series);
        imChart.getData().add(imSeries);
        ampChart.getData().add(ampSeries);
        phaseChart.getData().add(phaseSeries);

        if (loadFlag) {
            Signal filter = new Signal();
            String filterNameString = filterName.getText();
            for (int k = 1; k <= fourierTranform.samples.size() - 1; k++) {
                Complex counter = new Complex(0, 0);
                int i = 0;
                for (Complex arg : fourierTranform.samples.values()) {
                    Complex c = new Complex(Math.cos(2 * Math.PI * i * k / N),
                            Math.sin(2 * Math.PI * i * k / N));
                    counter = counter.add(arg.multiply(c));
                    i++;

                }
                filter.samples.put(Double.valueOf(k), counter);
            }

            MainController.filterList.put(filterNameString, filter);
            MainController.filtersController.filters.getItems().add(filterNameString);
            MainController.newFilterController.FilterList.getItems().add(filterNameString);

            if (fileFlag == true) {
                Iterator<Double> Keys = fourierTranform.samples.keySet().iterator();
                FileChooser chooser = new FileChooser();
                chooser.setTitle("JavaFX Projects");
                File defaultDirectory = new File("C:/");
                chooser.setInitialDirectory(defaultDirectory);
                File selectedDirectory = chooser.showSaveDialog(new Stage());
                while (Keys.hasNext()) {
                    Double key = Keys.next();
                    ComplexFormat format = new ComplexFormat();
                    Complex h = fourierTranform.samples.get(key);
                    FileOutputStream fop = new FileOutputStream(selectedDirectory, true);
                    String line = format.format(key) + ";" + format.format(h) + "\n";

                    fop.write(line.getBytes());
                    fop.flush();
                    fop.close();
                }
            }
        }

    }

    @FXML
    void cleanSignalChart() {
        imChart.getData().clear();
        reChart.getData().clear();
        ampChart.getData().clear();
        phaseChart.getData().clear();
    }

    @FXML
    void loadToFile() {
        try {
            addToChartAndOrLoad(true, true);
        } catch (IOException ex) {
            Logger.getLogger(FourierController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void saveFilter() {
        try {
            addToChartAndOrLoad(true, false);
        } catch (IOException ex) {
            Logger.getLogger(FourierController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
