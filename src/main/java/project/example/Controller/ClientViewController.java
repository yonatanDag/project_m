package project.example.Controller;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import project.example.Model.Task;

public class ClientViewController {

    @FXML
    private TextField inputClientID;
    
    @FXML
    private TableView<Task> taskTableView;

    @FXML
    private TableColumn<Task, String> durationColumn;

    @FXML
    private TableColumn<Task, String> technicianColumn;

    @FXML
    private TableColumn<Task, String> timeColumn;

    @FXML
    private TableColumn<Task, Double> priceColumn;

    @FXML
    public void initialize() {
        // Initialize table columns using PropertyValueFactory
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        technicianColumn.setCellValueFactory(new PropertyValueFactory<>("technicianName"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedScheduledTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("visitPrice"));

    }

    @FXML
    void inputOnAction(ActionEvent event) throws IOException {
        // Fetch client ID from text field and validate
        int clientId = validateClientId(inputClientID.getText());
        if (clientId != -1) {
            fetchAndDisplayTasks(clientId);
        } else {
            System.out.println("Invalid Client ID.");
        }
    }

    private void fetchAndDisplayTasks(int clientId) {
        DB db = new DB();
        try {
            db.connectSql();
            ArrayList<Task> clientTasks = db.getTasksForClientScheduledTomorrow(clientId);
            // Update the TableView with the tasks
            taskTableView.setItems(FXCollections.observableArrayList(clientTasks));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("the ClientID is invalid.");
        } finally {
            db.disconnectSql();
        }
    }

    private int validateClientId(String clientIdText) {
        try {
            return Integer.parseInt(clientIdText);
        } catch (NumberFormatException e) {
            return -1;  // Return -1 if the client ID is invalid
        }
    }
}
