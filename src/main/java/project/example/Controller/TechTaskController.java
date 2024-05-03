package project.example.Controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.example.Model.Task;

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

    public void setTechnicianId(int technicianId) {
        try {
            displayTasksForTechnician(technicianId); // Load tasks immediately
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Failed to load tasks for technician.");
        }
    }

    public void initialize() {
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        faultColumn.setCellValueFactory(new PropertyValueFactory<>("faultDescription"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("formattedScheduledTime"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("faultDuration"));
    }

    public void displayTasksForTechnician(int technicianId) throws SQLException {
        DB db = new DB();
        try {
            db.connectSql();
            ArrayList<Task> technicianTasks = db.getTasksForTechnicianScheduledTomorrow(technicianId);
            // Sort tasks by ScheduledTime
            Collections.sort(technicianTasks, new Comparator<Task>() {
                @Override
                public int compare(Task t1, Task t2) {
                    return t1.getScheduledTime().compareTo(t2.getScheduledTime());
                }
            });
            tasksTableView.setItems(FXCollections.observableArrayList(technicianTasks));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC Driver not found.");
        } finally {
            db.disconnectSql();
        }
    }
}
