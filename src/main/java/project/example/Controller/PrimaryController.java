package project.example.Controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import project.example.View.App;


public class PrimaryController {

    @FXML
    private Button adminBtn;

    @FXML
    private Button ClientViewBtn;

    @FXML
    private Button clientAddBtn;

    @FXML
    private Button techBtn;

    @FXML
    void adminClick(ActionEvent event) throws IOException {
        App.setRoot("AdminView");
    }

    @FXML
    void clickAddBtn(ActionEvent event) throws IOException {
        App.setRoot("ClientAddView");
    }

    @FXML
    void clickBtnTech(ActionEvent event) throws IOException {
        App.setRoot("TechView");
    }

    @FXML
    void clickViewBtn(ActionEvent event) throws IOException {
        App.setRoot("ClientTaskView");
    }

}
