package project.example.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int idT;
    private Technician assignedTechnician;
    private Client client;
    private Fault fault;
    private LocalDateTime reportedTime; // Time when the fault was reported
    private LocalDateTime scheduledTime; // Time when the fault is scheduled

    // Constructor for tasks reported by a client without a technician assigned yet
    public Task(int idT, Client client, Fault fault, LocalDateTime reportedTime) {
        this.idT = idT;
        this.assignedTechnician = null;
        this.client = client;
        this.fault = fault;
        this.reportedTime = reportedTime;
        this.scheduledTime = null;
    }

    public int getIdT() {
        return idT;
    }

    public void setIdT(int idT) {
        this.idT = idT;
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

    public void setClient(Client client) {
        this.client = client;
    }

    public Specialization getRequiredSpecialization() {
        return this.fault.getCfSpecialization();
    }

    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }

    public LocalDateTime getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(LocalDateTime reportedTime) {
        this.reportedTime = reportedTime;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String scheduledTimeString = scheduledTime != null ? scheduledTime.format(formatter) : "Not Scheduled";
        return client.getName() + ", " + scheduledTimeString;
    }

    // return true if Technician is scheduled and there is a scheduledTime
    public boolean status() {
        if (this.assignedTechnician == null || this.scheduledTime == null) {
            return false;
        }
        return true;
    }

    // Method to check for time conflicts with another Fault
    public boolean hasTimeConflict(Task otherFault) {
        // If either fault is not scheduled, there is no conflict
        if (this.scheduledTime == null || otherFault.scheduledTime == null) {
            return false;
        }

        // Check if the scheduled times overlap
        LocalDateTime endThisFault = this.scheduledTime.plusHours(1);
        LocalDateTime endOtherFault = otherFault.scheduledTime.plusHours(1);

        return this.scheduledTime.isBefore(endOtherFault) && endThisFault.isAfter(otherFault.scheduledTime);
    }

}
