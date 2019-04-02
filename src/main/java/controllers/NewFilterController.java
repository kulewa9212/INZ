package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
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
public class NewFilterController extends AbstractMain {

    protected ListView<String> FilterList;

    @FXML
    private AnchorPane Pane;

    @FXML
    private TextField startField;

    @FXML
    private TextField endField;

    @FXML
    private TextField resultName;

    @FXML
    private TextField stepField;

    private LineChart OriginalReSignalChart;

    private LineChart sampledReSignalChart;

    @Override
    void init(MainController aThis) {

        OriginalReSignalChart = new LineChart<>(new NumberAxis(),
                new NumberAxis());
        OriginalReSignalChart.setCreateSymbols(false);
        OriginalReSignalChart.setTitle("Original signal - Re");
        Methods.setChartSize(OriginalReSignalChart, 750, 450);
        Methods.setNodeCoordinates(OriginalReSignalChart, 550, 40);

        sampledReSignalChart = new LineChart<>(new NumberAxis(),
                new NumberAxis());
        sampledReSignalChart.setTitle("Sampled signal - Re");
        sampledReSignalChart.setCreateSymbols(false);
        Methods.setChartSize(sampledReSignalChart, 750, 450);
        Methods.setNodeCoordinates(sampledReSignalChart, 550, 490);

        FilterList = new ListView<>();
        Methods.setNodeCoordinates(FilterList, 1500, 80);
        FilterList.setMinHeight(820);
        FilterList.setMinWidth(350);

        Pane.getChildren().add(OriginalReSignalChart);
        Pane.getChildren().add(sampledReSignalChart);
        Pane.getChildren().add(FilterList);

        super.init(aThis);
    }

    @FXML
    private void previewFilter() {
        try {
            previewAndOrLoadFilter(false, false);
        } catch (IOException ex) {
            Logger.getLogger(NewFilterController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void previewAndOrLoadFilter(Boolean loadFlag, Boolean fileFlag) throws FileNotFoundException, IOException {

        Double start = Double.parseDouble(startField.getText());
        Double end = Double.parseDouble(endField.getText());
        Signal filter = new Signal();
        Double step = Double.parseDouble(stepField.getText());

        for (double i = 0; i < end * 3; i += 1) {
            if (i < start || i > end) {
                filter.samples.put(i, new Complex(0, 0));
            } else {
                filter.samples.put(i, new Complex(1, 0));
            }
        }

        Signal DFTfilter = makeRevertDFT(filter);

        XYChart.Series<Double, Double> filterSeries = new XYChart.Series();
        filterSeries.getData().clear();
        filterSeries.setName(this.resultName.getText());
        for (Map.Entry<Double, Complex> entry : filter.samples.entrySet()) {
            filterSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().abs()));

        }

        XYChart.Series<Double, Double> filterDFTSeries = new XYChart.Series();
        filterDFTSeries.getData().clear();
        filterDFTSeries.setName(this.resultName.getText());
        for (Map.Entry<Double, Complex> entry : DFTfilter.samples.entrySet()) {
            filterDFTSeries.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().abs()));

        }

        OriginalReSignalChart.getData().add(filterDFTSeries);
        sampledReSignalChart.getData().add(filterSeries);

        if (loadFlag) {
            MainController.filterList.put(resultName.getText(), DFTfilter);
            MainController.filtersController.filters.getItems().add(resultName.getText());
            MainController.newFilterController.FilterList.getItems().add(resultName.getText());
        }
        
        if (fileFlag == true) {
                Iterator<Double> Keys = filter.samples.keySet().iterator();
                FileChooser chooser = new FileChooser();
                chooser.setTitle("JavaFX Projects");
                File defaultDirectory = new File("C:/");
                chooser.setInitialDirectory(defaultDirectory);
                File selectedDirectory = chooser.showSaveDialog(new Stage());
                while (Keys.hasNext()) {
                    Double key = Keys.next();
                    ComplexFormat format = new ComplexFormat();
                    Complex h = filter.samples.get(key);
                    FileOutputStream fop = new FileOutputStream(selectedDirectory, true);
                    String line = format.format(key) + ";" + format.format(h) + "\n";

                    fop.write(line.getBytes());
                    fop.flush();
                    fop.close();
                }
            }
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

    @FXML
    private void loadFilter() {
        try {
            previewAndOrLoadFilter(true, false);
        } catch (IOException ex) {
            Logger.getLogger(NewFilterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     @FXML
    private void saveFilterToFile() {
        try {
            previewAndOrLoadFilter(false, true);
        } catch (IOException ex) {
            Logger.getLogger(NewFilterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
