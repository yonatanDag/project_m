package project.example.View;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import project.example.Controller.DB;
import project.example.Model.Technician;

public class TechController {
    
    @FXML
    private Button finishBtn;

    @FXML
    private TextField techIDtxt;

    private ArrayList<Technician> technicians;

    private int selectedTechnicianId = -1;

    @FXML
    void finishOnAction(ActionEvent event) throws IOException {
        loadTechniciansData();
        String enteredId = techIDtxt.getText();
        if (isTechnicianIdValid(enteredId)) {
            selectedTechnicianId = Integer.parseInt(enteredId);
            // Pass the selectedTechnicianId to the TechTaskController
            App.setRootTechTasks("TechTasks", selectedTechnicianId);
        } else {
            System.out.println("Invalid ID. Please try again.");
        }
    }

    private void loadTechniciansData() {
        DB database = new DB();
        try {
            technicians = database.loadTechnicians(); // Load technicians from the database
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            database.disconnectSql();
        }
    }

    private boolean isTechnicianIdValid(String id) {
        if (id == null || technicians == null) {
            return false;
        }
        try {
            int technicianId = Integer.parseInt(id);
            // Check if the ID belongs to any technician in the list using binary search
            return binarySearchTechnicianId(technicianId);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean binarySearchTechnicianId(int technicianId) {
        int low = 0;
        int high = this.technicians.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Technician midTechnician = technicians.get(mid);

            if (midTechnician.getIdT() == technicianId) {
                return true;
            } else if (midTechnician.getIdT() < technicianId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }
}
