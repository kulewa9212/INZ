package controllers;

import com.mycompany.inz.Signal;
import java.util.Map.Entry;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class DisplayController {

    MainController MainController;

    @FXML
    private AnchorPane DisplayPane;

    @FXML
    protected ComboBox<String> SignalList;

    @FXML
    private LineChart<Number, Number> DisplayChart;

    void init(MainController aThis) {
        MainController = aThis;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //  xAxis.setMinWidth(1000);
        //  yAxis.setMinHeight(1000);
        DisplayChart = new LineChart<Number, Number>(xAxis, yAxis);
        DisplayChart.setMinWidth(920);
        DisplayChart.setMinHeight(750);
        DisplayChart.setLayoutX(340);
        DisplayPane.getChildren().add(DisplayChart);
    }

    @FXML
    void addToChart() {
        String SignalToFind = this.SignalList.getSelectionModel().getSelectedItem();
        System.out.println(SignalToFind);
        Signal FoundSignal = MainController.signals.get(SignalToFind);
        //Add points to diagram
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.getData().clear();

        series.setName(SignalToFind);
        Set<Entry<Number, Number>> entrySet = FoundSignal.samples.entrySet();
        for (Entry<Number, Number> entry : entrySet) {
            series.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue()));
        }

        //     DisplayChart.getData().clear();
        DisplayChart.getXAxis().setAutoRanging(true);
        DisplayChart.getYAxis().setAutoRanging(true);
        DisplayChart.getYAxis().setTickLabelsVisible(false);
        DisplayChart.getXAxis().setTickMarkVisible(false);
        DisplayChart.getXAxis().setLabel("Args");
        DisplayChart.getYAxis().setLabel("Values");
        DisplayChart.setCreateSymbols(false);
        DisplayChart.getData().add(series);
    }

    @FXML
    void cleanSignalChart() {
        DisplayChart.getData().clear();
    }

    @FXML
    void removeSignal() {
        String SignalToRemove
                = this.SignalList.getSelectionModel().getSelectedItem();
        this.MainController.signals.remove(SignalToRemove);
        this.SignalList.getItems().remove(SignalToRemove);
        this.MainController.simpleAddController.SignalAdd1.getItems().
                remove(SignalToRemove);
        this.MainController.simpleAddController.SignalAdd2.getItems().
                remove(SignalToRemove);
    }
}
