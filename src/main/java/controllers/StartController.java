package controllers;

import com.mycompany.inz.Signal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StartController {

    MainController MainController;

   // @FXML
    //TextField Logs;

    void init(MainController aThis) {
        MainController = aThis;
      //  Logs.setEditable(false);
    }

    @FXML
    public void readFromFile() throws IOException {
        try {
            FileChooser fc = new FileChooser();         //fileChooser opening
            File sF = fc.showOpenDialog(new Stage());   //chosen file
            String SignalName = sF.getName().substring(0,
                    sF.getName().length() - 4);
            System.out.println(SignalName);

            Scanner sR = new Scanner(sF);
            Signal S = new Signal();
            while (sR.hasNextLine()) {
                String next = sR.nextLine();
                S.samples.put(Double.parseDouble((String) next.split(";")[0]),
                        Double.parseDouble((String) next.split(";")[1]));
            }
            MainController.signals.put(SignalName, S);
            MainController.displayController.SignalList.getItems().
                    add(SignalName);
            MainController.simpleAddController.SignalAdd1.getItems().
                    add(SignalName);
             MainController.simpleAddController.SignalAdd2.getItems().
                    add(SignalName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ErrorBox.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            stage.setResizable(false);
            stage.setTitle("Error");
            stage.setScene(scene);
            stage.show();
          //  Logs.setText("StartView error:" + e.toString() + "/n");
        }

    }
}
