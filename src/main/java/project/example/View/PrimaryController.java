package project.example.View;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class PrimaryController {

    @FXML
    private Button clientBtn;

    @FXML
    private Button techBtn;

    @FXML
    void clickBtnClient(ActionEvent event) throws IOException {
        App.setRoot("ClientView");
    }

    @FXML
    void clickBtnTech(ActionEvent event) throws IOException {
        App.setRoot("TechView");
    }

}
