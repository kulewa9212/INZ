/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.mycompany.inz.Signal;
import java.util.Map;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Ewa Kulesza
 */
public class SimpleAddController {

    MainController MainController;

    @FXML
    private AnchorPane SimpleAddPane;
    @FXML
    ComboBox AddChoice;
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
        Signal1Chart = new LineChart<Number, Number>(x1Axis, y1Axis);
        Signal1Chart.setMinWidth(400);
        Signal1Chart.setMaxWidth(400);
        Signal1Chart.setMinHeight(280);
        Signal1Chart.setMaxHeight(280);
        Signal1Chart.setLayoutX(25);
        Signal1Chart.setLayoutY(30);
        SignalAdd1.setLayoutY(300);
        SignalAdd1.setLayoutX(50);
        final NumberAxis x2Axis = new NumberAxis();
        final NumberAxis y2Axis = new NumberAxis();
        Signal2Chart = new LineChart<Number, Number>(x2Axis, y2Axis);
        Signal2Chart.setMinWidth(400);
        Signal2Chart.setMaxWidth(400);
        Signal2Chart.setMinHeight(280);
        Signal2Chart.setMaxHeight(280);
        Signal2Chart.setLayoutX(500);
        SignalAdd2.setLayoutX(520);
        SignalAdd2.setLayoutY(300);
        Signal2Chart.setLayoutY(30);
        SimpleAddPane.getChildren().add(Signal1Chart);
        SimpleAddPane.getChildren().add(Signal2Chart);
        final NumberAxis xRAxis = new NumberAxis();
        final NumberAxis yRAxis = new NumberAxis();
        ResultChart = new LineChart<Number, Number>(xRAxis, yRAxis);
        ResultChart.setMinWidth(700);
        ResultChart.setMaxWidth(700);
        ResultChart.setMinHeight(405);
        ResultChart.setMaxHeight(405);
        ResultChart.setLayoutX(100);
        ResultChart.setLayoutY(370);
        SimpleAddPane.getChildren().add(ResultChart);
    }

    public void Preview() {
        
        String Signal1ToFind = this.SignalAdd1.getSelectionModel().getSelectedItem();
        System.out.println(Signal1ToFind);
        Signal FoundSignal = MainController.signals.get(Signal1ToFind);
        //Add points to diagram
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.getData().clear();
        series.setName(Signal1ToFind);
        Set<Map.Entry<Number, Number>> entrySet = FoundSignal.samples.entrySet();
        for (Map.Entry<Number, Number> entry : entrySet) {
            series.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue()));
        }
        Signal1Chart.setCreateSymbols(false);
        Signal1Chart.setLegendVisible(false);
        Signal1Chart.getData().add(series);

        //----------------------------------------------------------------------
        //----------------------------------------------------------------------
        String Signal2ToFind = this.SignalAdd2.getSelectionModel().getSelectedItem();
        System.out.println(Signal2ToFind);
        Signal Found2Signal = MainController.signals.get(Signal2ToFind);
        //Add points to diagram
        XYChart.Series<Number, Number> series2 = new XYChart.Series();
        series2.getData().clear();
        series2.setName(Signal2ToFind);
        Set<Map.Entry<Number, Number>> entry2Set = Found2Signal.samples.entrySet();
        for (Map.Entry<Number, Number> entry : entry2Set) {
            series2.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue()));
        }
        Signal2Chart.setCreateSymbols(false);
        Signal2Chart.setLegendVisible(false);
        Signal2Chart.getData().add(series2);
    }
    
    @FXML
    private void clean() {
    this.Signal1Chart.getData().clear();
    this.Signal2Chart.getData().clear();
    }
}
