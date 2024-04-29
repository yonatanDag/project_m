package project.example.Model;

public class Fault {
    // the ID of the Fault
    private int fID;
    // the sepcialization of the fault
    private Specialization fSpecialization;
    // the description of the Fault
    private String fDescription;
    // the amount of minutes it's take to fix the Fault in minutes
    private int duration;
    // Urgency level of the fault(1 is the most urgent)
    private int urgencyLevel;

    public Fault(int fID, Specialization fSpecialization, String fDescription, int duration, int urgencyLevel) {
        this.fID = fID;
        this.fSpecialization = fSpecialization;
        this.fDescription = fDescription;
        this.duration = duration;
        this.urgencyLevel = urgencyLevel;// the smaller the number, the more urgent it is(1 is the most urgent)
    }

    public int getfID() {
        return fID;
    }

    public Specialization getCfSpecialization() {
        return fSpecialization;
    }

    public String getfDescription() {
        return fDescription;
    }

    public int getDuration() {
        return duration;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }

}
