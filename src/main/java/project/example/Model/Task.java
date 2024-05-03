package project.example.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    // the ID of the Task
    private int idT;
    // the Technician that assigned to fix this Task
    private Technician assignedTechnician;
    // the Client of the Task
    private Client client;
    // the Fault of the Task
    private Fault fault;
    // the Time when the fault was reported
    private LocalDateTime reportedTime; 
    // the Time when the fault is scheduled
    private LocalDateTime scheduledTime; 

    // Constructor for tasks reported by a client without a technician assigned yet
    public Task(int idT, Client client, Fault fault, LocalDateTime reportedTime) {
        this.idT = idT;
        this.assignedTechnician = null;
        this.client = client;
        this.fault = fault;
        this.reportedTime = reportedTime;
        this.scheduledTime = null;
    }

    // Copy constructor
    public Task(Task other) {
        this.idT = other.idT;
        this.assignedTechnician = other.assignedTechnician;
        this.client = other.client;
        this.fault = other.fault;
        this.reportedTime = other.reportedTime;
        this.scheduledTime = other.scheduledTime;
    }

    public int getIdT() {
        return idT;
    }

    // Getters and Setters
    public Technician getAssignedTechnician() {
        return assignedTechnician;
    }

    public void setAssignedTechnician(Technician assignedTechnician) {
        this.assignedTechnician = assignedTechnician;
    }

    public Client getClient() {
        return client;
    }

    public Specialization getRequiredSpecialization() {
        return this.fault.getCfSpecialization();
    }

    public Fault getFault() {
        return fault;
    }

    public LocalDateTime getReportedTime() {
        return reportedTime;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    // Method to get the city from the task's client
    public String getCity() {
        return this.client.getCity().getCityName();
    }

    // Method to get the client name
    public String getClientName() {
        return this.client.getName();
    }

    // Method to get the fault description
    public String getFaultDescription() {
        return this.fault.getfDescription();
    }

    // Method to get the formatted scheduled time
    public String getFormattedScheduledTime() {
        if (this.scheduledTime != null) {
            return this.scheduledTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            return "Not Scheduled";
        }
    }

    public String getTechnicianName() {
        return this.assignedTechnician.getName(); 
    }

    // If duration is stored as an integer or long, make sure to return a String
    public String getDuration() {
        if (this.fault != null) {
            return String.valueOf(this.fault.getDuration());
        } else {
            return ""; // or some other default string
        }
    }

    public Double getVisitPrice() {
        if (assignedTechnician != null) {
            return assignedTechnician.getVisitPrice();
        } else {
            return null;  // or another appropriate default value
        }
    }

    public int getFaultDuration() {
        return this.fault.getDuration();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String scheduledTimeString = scheduledTime != null ? scheduledTime.format(formatter) : "Not Scheduled";
        return client.getName() + ", " + scheduledTimeString;
    }
}
