package project.example.View;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.example.Controller.DB;
import project.example.Model.Schedule;
import project.example.Model.ScheduleGeneticAlgorithm;
import project.example.Model.Task;

public class ClientTaskController {
    
    @FXML
    private TableView<Task> taskTableView;

    @FXML
    private TableColumn<Task, String> durationColumn;

    @FXML
    private TableColumn<Task, String> technicianColumn;

    @FXML
    private TableColumn<Task, String> timeColumn;

    private int clientId;

    @FXML
    public void initialize() {
        // Initialize table columns using PropertyValueFactory
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        technicianColumn.setCellValueFactory(new PropertyValueFactory<>("technicianName"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedScheduledTime"));
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
        runAlgorithmAndDisplayTasks();
    }

    private void runAlgorithmAndDisplayTasks() {
        DB db = new DB();
        try {
            db.connectSql();
            ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(100, db, 1000, 0.05);
            sga.evolutionCycle(); // Run the algorithm to generate the schedule

            Schedule fittestSchedule = sga.getPopulation().getFittest();
            ArrayList<Task> clientTasks = fittestSchedule.getTasksForClient(clientId);

            // Update the TableView with the tasks
            taskTableView.setItems(FXCollections.observableArrayList(clientTasks));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while generating the schedule for the client.");
        } finally {
            db.disconnectSql();
        }
    }
}
