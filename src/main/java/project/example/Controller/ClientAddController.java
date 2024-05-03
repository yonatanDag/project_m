package project.example.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import project.example.Model.Client;
import project.example.Model.Fault;
import project.example.Model.Specialization;
import project.example.View.App;

public class ClientAddController {

    @FXML
    private TextField clientIDtxt;

    @FXML
    private ComboBox<String> faultsCBox;

    @FXML
    private ComboBox<String> specializationCBox;

    @FXML
    private Button finishBtn;

    private ArrayList<Client> clients;

    @FXML
    void finishOnAction(ActionEvent event) throws IOException {
        String enteredId = clientIDtxt.getText();
        String selectedFaultDescription = faultsCBox.getSelectionModel().getSelectedItem();

        if (isClientIdValid(enteredId) && selectedFaultDescription != null && !selectedFaultDescription.isEmpty()) {
            int selectedClientId = Integer.parseInt(enteredId);

            // Create a new instance of the DB class to interact with the database
            DB database = new DB();
            try {
                // try to find the fault ID by the selected fault description
                int selectedFaultId = database.getFaultIdByDescription(selectedFaultDescription);
                if (selectedFaultId != -1) {
                    // If the fault ID is valid, add a new task to the database
                    boolean taskAdded = database.addTask(selectedClientId, selectedFaultId);
                    if (taskAdded) {
                        // Redirect back to primary view on successful task addition
                        App.setRoot("primary");
                    } else {
                        System.out.println("Unable to add task. Please try again.");
                    }
                } else {
                    System.out.println("Invalid fault selected. Please try again.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions
                System.out.println("Database error. Please try again.");
            } finally {
                // Always ensure the database connection is closed
                database.disconnectSql();
            }
        } else {
            System.out.println("Invalid ID or Fault. Please try again.");
        }
    }

    public void initialize() {
        loadSpecializationNames();
        setupSpecializationChangeListener();
        loadClientsData();
    }

    private void loadSpecializationNames() {
        DB database = new DB(); // Create a new instance of DB.
        ArrayList<Specialization> specializations;
        try {
            specializations = database.loadSpecializations(); // Call the method that loads specializations from the
                                                              // database.
            // Map the Specialization objects to their names and collect to a list.
            List<String> specializationNames = specializations.stream().map(Specialization::getNameS)
                    .collect(Collectors.toList());
            // Convert the list to an observable list and set it to the ComboBox.
            specializationCBox.setItems(FXCollections.observableArrayList(specializationNames));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Handle exceptions appropriately.
        } finally {
            database.disconnectSql(); // Disconnect from the database.
        }
    }

    private void setupSpecializationChangeListener() {
        // Add a change listener to the specialization combo box
        specializationCBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadFaultsForSpecialization((String) newVal);
            }
        });
    }

    private void loadFaultsForSpecialization(String specializationName) {
        DB database = new DB();
        try {
            ArrayList<Specialization> specializations = database.loadSpecializations();
            // Find the selected specialization object based on the name
            Specialization selectedSpecialization = specializations.stream()
                    .filter(s -> s.getNameS().equals(specializationName)).findFirst().orElse(null);

            if (selectedSpecialization != null) {
                ArrayList<Fault> faults = database.loadFaults();
                // Filter faults by the selected specialization
                List<String> faultNames = faults.stream()
                        .filter(f -> f.getCfSpecialization().getIdS() == selectedSpecialization.getIdS())
                        .map(Fault::getfDescription).collect(Collectors.toList());
                faultsCBox.setItems(FXCollections.observableArrayList(faultNames));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            database.disconnectSql();
        }
    }

    private void loadClientsData() {
        DB database = new DB(); // Create a new instance of DB.
        try {
            clients = database.loadClients(); // Load clients from the database
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Handle exceptions appropriately.
        } finally {
            database.disconnectSql(); // Always disconnect from the database
        }
    }

    private boolean isClientIdValid(String id) {
        if (id == null || clients == null) {
            return false;
        }
        try {
            int clientId = Integer.parseInt(id); // Convert the entered ID to integer
            // Check if the ID belongs to any client in the list
            return binarySearchClientId(clientId);
        } catch (NumberFormatException e) {
            // Handle the case where the entered ID is not an integer
            return false;
        }
    }

    // Binary search method
    public boolean binarySearchClientId(int clientId) {
        int low = 0;
        int high = this.clients.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Client midClient = clients.get(mid);

            if (midClient.getIdC() == clientId) {
                return true; // Client ID found
            } else if (midClient.getIdC() < clientId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return false; // Client ID not found
    }
}