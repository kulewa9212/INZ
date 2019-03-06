package controllers;

import com.mycompany.inz.Methods;
import com.mycompany.inz.Signal;
import com.mycompany.inz.SignalShape;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StartController {

    MainController MainController;

    @FXML
    protected ListView<String> SignalList;

    @FXML
    private RadioButton SinusButton;

    @FXML
    private RadioButton Sinus1PButton;

    @FXML
    private RadioButton Sinus2PButton;

    @FXML
    private RadioButton GaussButton;

    @FXML
    private RadioButton TriangleButton;

    @FXML
    private RadioButton RectangleButton;

    @FXML
    private RadioButton fdownButton;

    @FXML
    private RadioButton fmedButton;

    @FXML
    private RadioButton fupButton;

    @FXML
    private TextField NameField;

    @FXML
    private TextField StartField;

    @FXML
    private TextField EndField;

    @FXML
    private TextField PeriodField;

    @FXML
    private TextField AmplitudeField;

    @FXML
    private TextField CapacityField;

    @FXML
    private Label CapacityLabel;

    @FXML
    private Label PeriodLabel;

    @FXML
    private Label AmpLabel;

    @FXML
    private TextField Kfield;

    @FXML
    private Label Klabel;

    @FXML
    private LineChart<Number, Number> PreviewChart;

    @FXML
    private AnchorPane StartPane;

    private SignalShape SelectedSignal;

    void init(MainController aThis) {
        MainController = aThis;
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        PreviewChart = new LineChart<>(xAxis, yAxis);
        PreviewChart.setMinWidth(610);
        PreviewChart.setMinHeight(440);
        PreviewChart.setLayoutX(280);
        PreviewChart.setCreateSymbols(false);
        PreviewChart.setLayoutY(335);
        StartPane.getChildren().add(PreviewChart);
        SelectedSignal = SignalShape.NONSIGNAL.NONSIGNAL;
        CapacityField.setVisible(false);
        CapacityLabel.setVisible(false);
        PeriodField.setVisible(false);
        PeriodLabel.setVisible(false);
        AmplitudeField.setVisible(false);
        AmpLabel.setVisible(false);
        Kfield.setVisible(false);
        Klabel.setVisible(false);
        SinusButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.SINUS;
                setSinusFields();
            }
        });
        Sinus1PButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.SINUS1P;
                setSinusFields();
            }
        });
        Sinus2PButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.SINUS2P;
                setSinusFields();
            }
        });
        GaussButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.GAUSS;
                CapacityField.setVisible(false);
                CapacityLabel.setVisible(false);
                PeriodField.setVisible(false);
                PeriodLabel.setVisible(false);
                AmplitudeField.setVisible(false);
                AmpLabel.setVisible(false);
                Kfield.setVisible(false);
                Klabel.setVisible(false);
            }
        });
        TriangleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.TRIANGLE;
                setKwFields();
            }
        });
        RectangleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.RECTANGLE;
                setKwFields();
            }
        });
        fdownButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.FDOWN;
                setFilterFields();
            }
        });
        fmedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.FMED;
                setFilterFields();
            }
        });
        fupButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SelectedSignal = SignalShape.FUP;
                setFilterFields();
            }
        });
    }

    @FXML
    public void readFromFile() throws IOException {
        try {
            FileChooser fc = new FileChooser();         //fileChooser opening
            File sF = fc.showOpenDialog(new Stage());   //chosen file
            String SignalName = sF.getName().substring(0,
                    sF.getName().length() - 4);
            Scanner sR = new Scanner(sF);
            Signal S = new Signal();
            while (sR.hasNextLine()) {
                String next = sR.nextLine();
                S.samples.put(Double.parseDouble((String) next.split(";")[0]),
                        Double.parseDouble((String) next.split(";")[1]));
            }
            // MainController.signals.put(SignalName, S);
            if (Methods.addSignalToMainBase(MainController.signals, S,
                    SignalName) == true) {
                ArrayList<ObservableList<String>> places = new ArrayList<>();
                places.add(MainController.displayController.SignalList.
                        getItems());
                places.add(MainController.simpleAddController.SignalAdd1.
                        getItems());
                places.add(MainController.simpleAddController.SignalAdd2.
                        getItems());
                places.add(SignalList.getItems());
                places.add(MainController.filterController.SignalAdd1.
                        getItems());
                places.add(MainController.filterController.SignalAdd2.
                        getItems());
                places.add(MainController.samplingController.signalList.
                        getItems());
                places.add(MainController.recoverController.signalList.
                        getItems());
                places.add(MainController.quantiController.signalList.
                        getItems());
                Methods.addSignal(places, SignalName);
            } else {
                Methods M = new Methods();
                M.showErrorBox("/fxml/ErrorStartBox.fxml");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (Exception e) {
            Methods M = new Methods();
            M.showErrorBox("/fxml/ErrorStartBox.fxml");
        }
    }

    @FXML
    public void saveToFile() throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("C:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showSaveDialog(new Stage());
        String selectedSignal = SignalList.getSelectionModel().getSelectedItem();
        Iterator<Double> Keys = this.MainController.signals.get(selectedSignal).samples.
                keySet().iterator();
        while (Keys.hasNext()) {
            Double key = Keys.next();
            FileOutputStream fop = new FileOutputStream(selectedDirectory, true);
            String line = key + " ; " + this.MainController.signals.
                    get(selectedSignal).samples.get(key) + "\n";
            fop.write(line.getBytes());
            fop.flush();
            fop.close();
        }
    }

    @FXML
    public void loadManualSignal() {
        if (SelectedSignal != SignalShape.NONSIGNAL) {
            double a = Double.parseDouble(StartField.getText());
            double b = Double.parseDouble(EndField.getText());
            Signal S = new Signal();
            Double step = 0.007;
            if (SelectedSignal == SignalShape.FDOWN) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        S.samples.put(i,
                                2 / Double.parseDouble(Kfield.getText()));
                    } else {
                        S.samples.put(i,
                                (Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText())) / (Math.PI * i)));
                    }
                }
            } else if (SelectedSignal == SignalShape.FMED) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        S.samples.put(i,
                                2 / Double.parseDouble(Kfield.getText()));
                    } else {
                        S.samples.put(i,
                                (2 * Math.sin(Math.PI * i / 2)) * (Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText()) / (Math.PI * i))));
                    }
                }
            } else if (SelectedSignal == SignalShape.FUP) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        S.samples.put(i,
                                2 / Double.parseDouble(Kfield.getText()));
                    } else {
                        S.samples.put(i,
                                Math.pow(-1, i) * Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText())) / (Math.PI * i));
                    }
                }
            } else if (SelectedSignal == SignalShape.GAUSS) {
                Random rand = new Random();
                for (double i = a; i < b; i = i + step) {
                    S.samples.put(i,
                            rand.nextGaussian());
                }
            } else {
                double A = Double.parseDouble(AmplitudeField.getText());
                double Period = Double.parseDouble(PeriodField.getText());
                switch (SelectedSignal) {
                    case SINUS:
                        for (double i = a; i < b; i = i + step) {
                            S.samples.put(i,
                                    A * Math.sin(((2 * Math.PI) / (Period)) * (i - a)));
                        }
                        break;
                    case SINUS1P:
                        for (double i = a; i < b; i = i + step) {
                            double SinusValue = Math.sin(((2 * Math.PI) / (Period)) * (i - a));
                            S.samples.put(i,
                                    A * 0.5 * (SinusValue + Math.abs(SinusValue)));
                        }
                        break;
                    case SINUS2P:
                        for (double i = a; i < b; i = i + step) {
                            double SinusValue = Math.sin(((2 * Math.PI) / (Period)) * (i - a));
                            S.samples.put(i,
                                    A * Math.abs(SinusValue));
                        }
                        break;
                    case TRIANGLE:
                        double kw = Double.parseDouble(CapacityField.getText());
                        int k = 0;
                        for (double i = a; i < b; i = i + step) {
                            if ((i > k * Period) && (i < kw * Period + k * Period + a)) {
                                S.samples.put(i,
                                        (A / (kw * Period) * (i - k * Period - a)));
                            } else if ((i >= kw * Period + k * Period + a)
                                    && (i <= Period + k * Period + a)) {
                                S.samples.put(i,
                                        (-1) * (A / (Period * (1 - kw)))
                                        * (i - k * Period - a) + (A / (1 - kw)));
                            } else if (i > Period + k * Period + a) {
                                k++;
                            }
                        }
                        break;
                    case RECTANGLE:
                        double kwr = Double.parseDouble(CapacityField.getText());
                        int l = 0;
                        for (double i = a; i < b; i = i + step) {
                            if ((i > l * Period) && (i < kwr * Period + l * Period + a)) {
                                S.samples.put(i, A);
                            } else if ((i >= kwr * Period + l * Period + a)
                                    && (i <= Period + l * Period + a)) {
                                S.samples.put(i, -A);
                            } else if (i > Period + l * Period + a) {
                                l++;
                            }
                        }
                        break;
                }
            }
            String SignalName = NameField.getText();
            boolean B = Methods.addSignalToMainBase(this.MainController.signals, S, SignalName);
            if (B == true) {
                //    this.MainController.signals.put(SignalName, S);
                ArrayList<ObservableList<String>> places = new ArrayList<>();
                places.add(MainController.displayController.SignalList.getItems());
                places.add(MainController.simpleAddController.SignalAdd1.
                        getItems());
                places.add(MainController.simpleAddController.SignalAdd2.
                        getItems());
                places.add(MainController.filterController.SignalAdd1.
                        getItems());
                places.add(MainController.filterController.SignalAdd2.
                        getItems());
                places.add(SignalList.getItems());
                places.add(MainController.samplingController.signalList.
                        getItems());
                places.add(MainController.recoverController.signalList.
                        getItems());
                places.add(MainController.quantiController.signalList.
                        getItems());
                Methods.addSignal(places, SignalName);
            } else {
                Stage stage = new Stage();
                Parent root;
                try {
                    Methods M = new Methods();
                    M.showErrorBox("/fxml/SaveSignalError.fxml");
                } catch (IOException ex) {
                    Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    void previewManualSignal() {
        XYChart.Series<Number, Number> series = new XYChart.Series();
        double a = Double.parseDouble(StartField.getText());
        double b = Double.parseDouble(EndField.getText());
        if (SelectedSignal != SignalShape.NONSIGNAL) {
            if (SelectedSignal == SignalShape.FUP) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        series.getData().add(new XYChart.Data(i,
                                2 / Double.parseDouble(Kfield.getText())));
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                Math.pow(-1, i) * Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText())) / (Math.PI * i)));
                    }
                }
            } else if (SelectedSignal == SignalShape.FMED) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        series.getData().add(new XYChart.Data(i,
                                2 / Double.parseDouble(Kfield.getText())));
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                (2 * Math.sin(Math.PI * i / 2)) * (Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText())) / (Math.PI * i))));
                    }
                }
            } else if (SelectedSignal == SignalShape.FDOWN) {
                for (double i = a; i < b; i++) {
                    if (i == 0) {
                        series.getData().add(new XYChart.Data(i,
                                2 / Double.parseDouble(Kfield.getText())));
                    } else {
                        series.getData().add(new XYChart.Data(i,
                                (Math.sin(2 * Math.PI * i / Double.parseDouble(Kfield.getText())) / (Math.PI * i))));
                    }
                }
            } else if (SelectedSignal == SignalShape.GAUSS) {
                Random rand = new Random();

                for (double i = a; i < b; i = i + 0.05) {
                    series.getData().add(new XYChart.Data(i,
                            rand.nextGaussian()));
                }
            } else {
                double A = Double.parseDouble(AmplitudeField.getText());
                double Period = Double.parseDouble(PeriodField.getText());
                //    double a = Double.parseDouble(StartField.getText());
                //    double b = Double.parseDouble(EndField.getText());
                switch (SelectedSignal) {
                    case SINUS:
                        for (double i = a; i < b; i = i + 0.05) {
                            series.getData().add(new XYChart.Data(i,
                                    A * Math.sin(((2 * Math.PI) / (Period)) * (i - a))));
                        }
                        break;
                    case SINUS1P:
                        for (double i = a; i < b; i = i + 0.05) {
                            double SinusValue = Math.sin(((2 * Math.PI) / (Period)) * (i - a));
                            series.getData().add(new XYChart.Data(i,
                                    A * 0.5 * (SinusValue + Math.abs(SinusValue))));
                        }
                        break;
                    case SINUS2P:
                        for (double i = a; i < b; i = i + 0.01) {
                            double SinusValue = Math.sin(((2 * Math.PI) / (Period)) * (i - a));
                            series.getData().add(new XYChart.Data(i,
                                    A * Math.abs(SinusValue)));
                        }
                        break;
                    case TRIANGLE:
                        double kw = Double.parseDouble(CapacityField.getText());
                        int k = -1;
                        for (double i = a; i < b; i = i + 0.01) {
                            if ((i > k * Period) && (i < kw * Period + k * Period + a)) {
                                series.getData().add(new XYChart.Data(i,
                                        (A / (kw * Period) * (i - k * Period - a))));
                            } else if ((i >= kw * Period + k * Period + a)
                                    && (i <= Period + k * Period + a)) {
                                series.getData().add(new XYChart.Data(i,
                                        (-1) * (A / (Period * (1 - kw)))
                                        * (i - k * Period - a) + (A / (1 - kw))));
                            } else if (i > Period + k * Period + a) {
                                series.getData().add(new XYChart.Data(i,
                                        (A / (kw * Period) * (i - (k + 1) * Period - a))));
                                k++;
                            }

                        }
                        break;
                    case RECTANGLE:
                        double kwr = Double.parseDouble(CapacityField.getText());
                        int l = -1;
                        for (double i = a; i < b; i = i + 0.01) {
                            if ((i > l * Period) && (i < kwr * Period + l * Period + a)) {
                                series.getData().add(new XYChart.Data(i, A));
                            } else if ((i >= kwr * Period + l * Period + a)
                                    && (i <= Period + l * Period + a)) {
                                series.getData().add(new XYChart.Data(i, -A));
                            } else if (i > Period + l * Period + a) {
                                l++;
                            }

                        }
                        break;

                }
            }
            PreviewChart.setLegendVisible(false);
            PreviewChart.getData().clear();
            PreviewChart.getData().add(series);
        }
    }

    private void setSinusFields() {
        CapacityField.setVisible(false);
        CapacityLabel.setVisible(false);
        PeriodField.setVisible(true);
        PeriodLabel.setVisible(true);
        AmplitudeField.setVisible(true);
        AmpLabel.setVisible(true);
        Kfield.setVisible(false);
        Klabel.setVisible(false);
    }

    private void setKwFields() {
        CapacityField.setVisible(true);
        CapacityLabel.setVisible(true);
        PeriodField.setVisible(true);
        PeriodLabel.setVisible(true);
        AmplitudeField.setVisible(true);
        AmpLabel.setVisible(true);
        Kfield.setVisible(false);
        Klabel.setVisible(false);
    }

    public void setFilterFields() {
        CapacityField.setVisible(false);
        CapacityLabel.setVisible(false);
        PeriodField.setVisible(false);
        PeriodLabel.setVisible(false);
        AmplitudeField.setVisible(false);
        AmpLabel.setVisible(false);
        Kfield.setVisible(true);
        Klabel.setVisible(true);
    }
}
