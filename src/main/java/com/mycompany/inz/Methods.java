package com.mycompany.inz;

import controllers.MainController;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author Ewa Skrzypek
 */
public class Methods {

    public static Complex assertValue(Double t, Double t0, Double t1,
            Complex ft0, Complex ft1) {
        Complex c1 = ft0.add(ft1.multiply(-1)).multiply(1 / (t0 - t1)).
                multiply(t).add(ft0);
        Complex c2 = ft0.add(ft1.multiply(-1)).multiply(t0).
                multiply(1 / (t0 - t1));
        return c1.add(c2.multiply(-1));
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
        SignalChart.setLegendVisible(true);
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

    public static Complex setSimpleActiom(ComboBox<String> ComboArg,
            Complex ValueX, Complex ValueY) {
        switch (ComboArg.getSelectionModel().getSelectedItem()) {
            case "+":
                return ValueX.add(ValueY);
            case "-":
                return ValueX.add(ValueY.multiply(-1));
            case "*":
                return ValueX.multiply(ValueY);
            default:
                return new Complex(0, 0);
        }
    }

    public static void addPointToSeries(XYChart.Series series,
            Signal SignalArg) {
        series.getData().clear();
        Set<Map.Entry<Double, Complex>> entrySet = SignalArg.samples.entrySet();
        for (Map.Entry<Double, Complex> entry : entrySet) {
            series.getData().add(new XYChart.Data(entry.getKey(),
                    entry.getValue().abs()));
        }
    }

    public static void clearCharts(List<XYChart> Charts) {
        for (XYChart Elem : Charts) {
            Elem.getData().clear();
        }

    }

    public static boolean addSignalToMainBase(TreeMap<String, Signal> signalBase, Signal signal, String SignalName) {
        if (signalBase.containsKey(SignalName) || SignalName.isEmpty()) {
            return false;
        } else {
            signalBase.put(SignalName, signal);
            return true;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void showErrorBox(String errorType) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().
                getResource(errorType));
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setTitle("Error");
        stage.setScene(scene);
        stage.show();
    }

    public static String readComboBoxValue(ComboBox<String> CB) {
        return CB.getSelectionModel().getSelectedItem();
    }

}
