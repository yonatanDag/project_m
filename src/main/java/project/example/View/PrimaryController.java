package project.example.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import project.example.Model.Schedule;
import project.example.Model.Task;
import project.example.Model.Technician;

public class PrimaryController {

    @FXML
    private Button primaryButton;

    @FXML
    private TableView<Map.Entry<Technician, List<Task>>> scheduleTable;
    
    @FXML
    private TableColumn<Map.Entry<Technician, List<Task>>, String> technicianColumn;
    
    @FXML
    private TableColumn<Map.Entry<Technician, List<Task>>, String> taskColumn;

    private Schedule schedule;

    // private void loadScheduleData() {
    //     ObservableList<Map.Entry<Technician, ArrayList<Task>>> items = FXCollections.observableArrayList(schedule.getScheduling().entrySet());
    //     scheduleTable.setItems(items);
    // }

    // private void loadScheduleData() {
    //     // Assuming ScheduleItem has properties like technician, task, etc.
    //     ObservableList<Map.Entry<Technician, ArrayList<Task>>> items = FXCollections.observableArrayList(schedule.getScheduling().entrySet());
    
    //     // Set the items to the table
    //     scheduleTable.setItems(items);
    // }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
