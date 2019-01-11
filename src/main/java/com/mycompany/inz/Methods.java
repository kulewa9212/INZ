package com.mycompany.inz;

import controllers.MainController;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Ewa Skrzypek
 */
public class Methods {

    public static double assertValue(Double t, Double t0, Double t1, Double ft0,
            Double ft1) {
        return ((ft0 - ft1) / (t0 - t1)) * t + ft0 - ((t * (ft0 - ft1)) / (t0 - t1));
    }

    public static void removeSignal(List<ObservableList<String>> Arg, String S) {
        for (List L : Arg) {
            L.remove(S);
        }
    }

    public static void addSignal(List<ObservableList<String>> Arg, String S) {
        for (List L : Arg) {
            L.add(S);
        }
    }

    public static void setChartSize(XYChart Arg, int horizontal, int vertical) {
        Arg.setMinWidth(horizontal);
        Arg.setMaxWidth(horizontal);
        Arg.setMinHeight(vertical);
        Arg.setMaxHeight(vertical);

    }

    public static void setNodeCoordinates(Node Arg, int x, int y) {
        Arg.setLayoutX(x);
        Arg.setLayoutY(y);
    }

    public static void setPreviewChart(LineChart<Number, Number> SignalChart,
            XYChart.Series<Number, Number> SeriesArg) {
        SignalChart.setCreateSymbols(false);
        SignalChart.setLegendVisible(false);
        SignalChart.getData().add(SeriesArg);
    }

    public static void addToPreviewWindowSimpleAdd(MainController MC,
            LineChart<Number, Number> SignalChart, String Signal1ToFind) {
        Signal FoundSignal;
        FoundSignal = MC.signals.get(Signal1ToFind);

        //Add points to diagram
        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.getData().clear();
        series.setName(Signal1ToFind);
        Methods.addPointToSeries(series, FoundSignal);
        Methods.setPreviewChart(SignalChart, series);
    }

    public static Double setSimpleActiom(ComboBox<String> ComboArg, Double ValueX,
            Double ValueY) {
        switch (ComboArg.getSelectionModel().getSelectedItem()) {
            case "+":
                return ValueX + ValueY;
            case "-":
                return ValueX - ValueY;
            case "*":
                return ValueX * ValueY;
            default:
                return 0.0;
        }
    }

    public static void addPointToSeries(XYChart.Series series,
            Signal SignalArg) {
        series.getData().clear();
        Set<Map.Entry<Double, Double>> entrySet = SignalArg.samples.entrySet();
        for (Map.Entry<Double, Double> entry : entrySet) {
            series.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue()));
        }
    }

    public static void clearCharts(List<XYChart> Charts) {
        for (XYChart Elem : Charts) {
            Elem.getData().clear();
        }

    }

}
