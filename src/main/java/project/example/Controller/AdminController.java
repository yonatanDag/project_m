package project.example.Controller;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import project.example.Model.Schedule;
import project.example.Model.ScheduleGeneticAlgorithm;
import project.example.Model.Task;
import project.example.Model.Technician;
import project.example.View.App;

public class AdminController {
    @FXML
    private Button Btn1;

    private static int populationSize = 100;
    private static int maxGenerations = 1500;
    private static double mutationRate = 0.05;

    @FXML
    void clickBtn(ActionEvent event) throws IOException {
        try {
            DB db = new DB();
            db.connectSql();

            // Run the genetic algorithm
            ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(populationSize, db, maxGenerations, mutationRate);
            sga.evolutionCycle();

            // Get the best schedule
            Schedule bestSchedule = sga.getPopulation().getFittest();

            // Update the database with the new schedule
            updateScheduledTasks(db, bestSchedule);

            db.disconnectSql();

            // Redirect back to primary view on successful task addition
            App.setRoot("primary");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to run the algorithm or update the database.");
        }
    }

    private void updateScheduledTasks(DB db, Schedule schedule) throws SQLException {
        // Retrieve all scheduled tasks from the schedule
        Map<Technician, ArrayList<Task>> allScheduledTasks = schedule.getScheduling();

        // Iterate over each entry in the map
        for (Map.Entry<Technician, ArrayList<Task>> entry : allScheduledTasks.entrySet()) {
            Technician tech = entry.getKey();
            ArrayList<Task> tasks = entry.getValue();

            for (Task task : tasks) {
                if (task.getScheduledTime() != null) { // Ensure we only update tasks that are scheduled
                    String updateSQL = "INSERT INTO scheduled_task (taskID, technicianID, scheduledTime, rating) VALUES (?, ?, ?, 9) ON DUPLICATE KEY UPDATE technicianID = ?, scheduledTime = ?, rating = 9;";

                    try (PreparedStatement pstmt = db.getConnection().prepareStatement(updateSQL)) {
                        pstmt.setInt(1, task.getIdT());
                        pstmt.setInt(2, tech.getIdT());
                        pstmt.setTimestamp(3, Timestamp.valueOf(task.getScheduledTime()));
                        pstmt.setInt(4, tech.getIdT());
                        pstmt.setTimestamp(5, Timestamp.valueOf(task.getScheduledTime()));
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }
}
