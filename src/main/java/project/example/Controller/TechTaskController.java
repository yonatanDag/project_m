package project.example.Controller;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.example.Model.Schedule;
import project.example.Model.ScheduleGeneticAlgorithm;
import project.example.Model.Task;
import project.example.Model.Technician;

public class TechTaskController {

    @FXML
    private TableColumn<Task, String> cityColumn;

    @FXML
    private TableColumn<Task, String> clientNameColumn;

    @FXML
    private TableColumn<Task, String> faultColumn;

    @FXML
    private TableView<Task> tasksTableView;

    @FXML
    private TableColumn<Task, String> timeColumn;

    @FXML
    private TableColumn<Task, Integer> durationColumn;

    public void initialize() {
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        faultColumn.setCellValueFactory(new PropertyValueFactory<>("faultDescription"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedScheduledTime"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("faultDuration"));
    }

    // A method to display the given tasks in the TableView
    private void displayTasks(ArrayList<Task> tasks) {
        tasksTableView.setItems(FXCollections.observableArrayList(tasks));
    }

    // A method to run the algorithm and display tasks
    public void runAlgorithmAndDisplayTasks(int technicianId) {
        try {
            // Connect to the database
            DB db = new DB();
            db.connectSql();

            // Create GA object/ Generate Population
            ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(100, db, 1000, 0.05);

            // Initialize population and run evolution cycle
            sga.evolutionCycle();

            // Get the fittest schedule (the best schedule found by the GA)
            Schedule fittestSchedule = sga.getPopulation().getFittest();

            // Use the method getTechnicianByID to get the Technician object
            Technician technician = fittestSchedule.getTechnicianByID(technicianId);

            ArrayList<Task> technicianTasks;
            // Check if technician is not null to avoid NullPointerException
            if (technician != null) {
                // Get the tasks for the specific technician after the algorithm has run
                technicianTasks = fittestSchedule.getTaskAssignedToTechnician(technician);
            } else {
                // Handle the case where the technician with the given ID was not found
                technicianTasks = new ArrayList<>(); // return an empty list or handle accordingly
            }

            displayTasks(technicianTasks);

            // Close the database connection
            db.disconnectSql();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle exceptions, maybe show an alert to the user
        }
    }
}
